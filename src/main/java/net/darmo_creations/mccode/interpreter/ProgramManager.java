package net.darmo_creations.mccode.interpreter;

import net.darmo_creations.mccode.MCCode;
import net.darmo_creations.mccode.interpreter.annotations.*;
import net.darmo_creations.mccode.interpreter.builtin_functions.*;
import net.darmo_creations.mccode.interpreter.exceptions.*;
import net.darmo_creations.mccode.interpreter.parser.ProgramParser;
import net.darmo_creations.mccode.interpreter.type_wrappers.*;
import net.darmo_creations.mccode.interpreter.types.BuiltinFunction;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.storage.LevelResource;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * A class that lets users load/unload programs.
 * <p>
 * The state of program managers can be saved and restored.
 */
public class ProgramManager implements NBTDeserializable {
  private static final Map<String, TypeBase<?>> TYPES = new HashMap<>();
  private static final Map<Class<?>, TypeBase<?>> WRAPPED_TYPES = new HashMap<>();
  private static final Map<String, BuiltinFunction> FUNCTIONS = new HashMap<>();
  private static boolean initialized;

  public static final String PROGRAMS_KEY = "Programs";
  public static final String PROGRAM_KEY = "Program";
  public static final String SCHEDULE_KEY = "ScheduleDelay";
  public static final String REPEAT_AMOUNT_KEY = "RepeatAmount";
  public static final String RUNNING_KEY = "Running";

  private final File dataDir;
  private final File programsDir;
  private final ServerLevel world;
  private final Map<String, Program> programs;
  private final Map<String, Long> programsSchedules;
  private final Map<String, Long> programsRepeats;
  private final Map<String, Boolean> runningPrograms;
  private long lastTick;

  /**
   * Create a program manager for the given world.
   *
   * @param world The world to attach.
   */
  public ProgramManager(ServerLevel world) {
    this.programs = new HashMap<>();
    this.programsSchedules = new HashMap<>();
    this.programsRepeats = new HashMap<>();
    this.runningPrograms = new HashMap<>();
    this.lastTick = -1;
    this.world = world;
    this.dataDir = new File(world.getServer().getWorldPath(LevelResource.LEVEL_DATA_FILE).toFile().getAbsolutePath(), "data");
    this.programsDir = new File(this.dataDir, "mccode_programs");
    this.loadFromFile();
  }

  /**
   * Return the dimension-specific save file path.
   */
  private File getSaveFile() {
    return new File(
        this.dataDir,
        String.format("%s_program_manager_%s.dat", MCCode.MOD_ID, this.world.dimension().getRegistryName())
    );
  }

  /**
   * Load programs from disk.
   */
  private void loadFromFile() {
    File file = this.getSaveFile();
    if (file.exists()) {
      try (FileInputStream fileinputstream = new FileInputStream(file)) {
        this.readFromNBT(NbtIo.readCompressed(fileinputstream));
      } catch (Exception e) {
        e.printStackTrace();
        if (this.world.getGameRules().getBoolean(MCCode.GR_SHOW_ERROR_MESSAGES)) {
          this.world.getServer().sendMessage(
              new TranslatableComponent("mccode.interpreter.error.exception",
                  e.getClass().getSimpleName(), e.getMessage()).setStyle(Style.EMPTY.withColor(ChatFormatting.RED)),
              Util.NIL_UUID
          );
        }
      }
    }
  }

  /**
   * Save this manager’s programs to disk.
   */
  public void save() {
    try (FileOutputStream fileoutputstream = new FileOutputStream(this.getSaveFile())) {
      NbtIo.writeCompressed(this.writeToNBT(), fileoutputstream);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Return the world associated to this manager.
   */
  public ServerLevel getWorld() {
    return this.world;
  }

  /**
   * Execute all loaded programs. If a program raises an error,
   * it is automatically unloaded and the error is returned.
   *
   * @return A list of program errors that have occured.
   */
  public List<ProgramErrorReport> executePrograms() {
    long currentTick = this.world.getGameTime();
    // Tick event still fired when unloading world but actual tick value stays the same
    // -> Ignore them
    if (currentTick == this.lastTick) {
      return Collections.emptyList();
    }
    this.lastTick = currentTick;
    List<ProgramErrorReport> errorReports = new ArrayList<>();

    // Execute all programs
    List<Program> toRemove = new LinkedList<>();
    for (Program program : this.programs.values()) {
      if (this.runningPrograms.get(program.getName())) {
        boolean error = true;
        try {
          program.execute();
          error = false; // Not executed if an error is thrown by execute().
        } catch (ProgramFileNotFoundException e) {
          errorReports.add(new ProgramErrorReport(
              program.getScope(), -1, -1, e.getTranslationKey(), e.getProgramName()));
        } catch (SyntaxErrorException e) {
          errorReports.add(new ProgramErrorReport(
              program.getScope(), e.getLine(), e.getColumn(), e.getTranslationKey(), e.getArgs()));
        } catch (MCCodeRuntimeException e) {
          errorReports.add(new ProgramErrorReport(
              e.getScope(), e.getLine(), e.getColumn(), e.getTranslationKey(), e.getArgs()));
        } catch (WrappedException e) {
          errorReports.add(new ProgramErrorReport(
              program.getScope(), e.getLine(), e.getColumn(), e.getTranslationKey(), e.getArgs()));
        }
        // Unload programs that have terminated or failed
        if (error || program.hasTerminated() && (!this.programsSchedules.containsKey(program.getName()) || this.programsRepeats.get(program.getName()) == 0)) {
          toRemove.add(program);
        }
      }
    }
    toRemove.forEach(p -> this.unloadProgram(p.getName()));

    // Update schedules and repeats of terminated programs
    for (Map.Entry<String, Long> e : this.programsSchedules.entrySet()) {
      String programName = e.getKey();
      long delay = e.getValue();
      Program program = this.programs.get(programName);

      if (program.hasTerminated()) {
        if (delay <= 0) {
          long repeatAmount = this.programsRepeats.get(programName);
          if (repeatAmount != Long.MAX_VALUE) {
            this.programsRepeats.put(programName, repeatAmount - 1);
          }
          //noinspection OptionalGetWithoutIsPresent
          this.programsSchedules.put(programName, program.getScheduleDelay().get());
          program.reset();
        } else {
          this.programsSchedules.put(programName, delay - 1);
        }
      }
    }

    return errorReports;
  }

  /**
   * Load a program.
   *
   * @param name     Program’s name.
   * @param alias    An optional alias to use in registries instead of first argument.
   *                 Useful when loading same program multiple times.
   * @param asModule If true, the program instance is returned instead of being loaded in this manager.
   * @param args     Optional command arguments for the program. Ignored if the program is loaded as a module.
   * @return The program.
   * @throws SyntaxErrorException         If a syntax error is present in the program’s source file.
   * @throws ProgramFileNotFoundException If no .mccode file was found for the given name.
   */
  public Program loadProgram(final String name, final String alias, final boolean asModule, String... args)
      throws SyntaxErrorException, ProgramFileNotFoundException {
    String actualName = alias != null ? alias : name;
    if (!asModule && this.programs.containsKey(actualName)) {
      throw new ProgramAlreadyLoadedException(actualName);
    }
    String[] splitPath = name.split("\\.");
    splitPath[splitPath.length - 1] += ".mccode";
    File programFile = Paths.get(this.programsDir.getAbsolutePath(), splitPath).toFile();
    if (!programFile.exists()) {
      throw new ProgramFileNotFoundException(programFile.getName());
    }
    StringBuilder code = new StringBuilder();
    try (BufferedReader br = new BufferedReader(new FileReader(programFile))) {
      String line;
      while ((line = br.readLine()) != null) {
        code.append(line).append('\n');
      }
    } catch (IOException e) {
      throw new ProgramFileNotFoundException(programFile.getName());
    }

    Program program = ProgramParser.parse(this, actualName, code.toString(), asModule, args);
    if (!asModule) {
      this.loadProgram(program);
    }
    return program;
  }

  /**
   * Load a program.
   *
   * @param program The program to load.
   * @apiNote Package access level for tests.
   */
  void loadProgram(Program program) {
    String name = program.getName();
    if (this.programs.containsKey(name)) {
      throw new ProgramAlreadyLoadedException(name);
    }
    this.programs.put(name, program);
    program.getScheduleDelay().ifPresent(t -> {
      this.programsSchedules.put(program.getName(), t);
      this.programsRepeats.put(program.getName(), program.getRepeatAmount().orElse(1L));
    });
    this.runningPrograms.put(name, false);
  }

  /**
   * Unload the given program.
   *
   * @param name Program’s name.
   * @throws ProgramNotFoundException If no program with this name is loaded.
   */
  public void unloadProgram(final String name) throws ProgramNotFoundException {
    if (!this.programs.containsKey(name)) {
      throw new ProgramNotFoundException(name);
    }
    this.programs.remove(name);
    this.programsSchedules.remove(name);
    this.programsRepeats.remove(name);
    this.runningPrograms.remove(name);
  }

  /**
   * Reset the given program.
   *
   * @param name Program’s name.
   * @throws ProgramNotFoundException If no program with this name is loaded.
   */
  public void resetProgram(final String name) throws ProgramNotFoundException {
    if (!this.programs.containsKey(name)) {
      throw new ProgramNotFoundException(name);
    }
    this.programs.get(name).reset();
    this.runningPrograms.put(name, false);
  }

  /**
   * Run the given program.
   *
   * @param name Program’s name.
   * @throws ProgramNotFoundException       If no program with this name is loaded.
   * @throws ProgramAlreadyRunningException If the program is already running.
   */
  public void runProgram(final String name) throws ProgramNotFoundException, ProgramAlreadyRunningException {
    if (!this.programs.containsKey(name)) {
      throw new ProgramNotFoundException(name);
    }
    if (this.runningPrograms.get(name)) {
      throw new ProgramAlreadyRunningException(name);
    }
    this.runningPrograms.put(name, true);
  }

  /**
   * Pause the given program.
   *
   * @param name Program’s name.
   * @throws ProgramNotFoundException      If no program with this name is loaded.
   * @throws ProgramAlreadyPausedException If the program is already paused.
   */
  public void pauseProgram(final String name) throws ProgramNotFoundException, ProgramAlreadyPausedException {
    if (!this.programs.containsKey(name)) {
      throw new ProgramNotFoundException(name);
    }
    if (!this.runningPrograms.get(name)) {
      throw new ProgramAlreadyPausedException(name);
    }
    this.runningPrograms.put(name, false);
  }

  /**
   * Return the names of all loaded programs sorted alphabetically.
   */
  public List<String> getLoadedPrograms() {
    List<String> list = new ArrayList<>(this.programs.keySet());
    list.sort(Comparator.comparing(String::toLowerCase));
    return list;
  }

  /**
   * Return the program with the given name.
   *
   * @param name Program’s name.
   * @return The program.
   */
  public Optional<Program> getProgram(final String name) {
    return Optional.ofNullable(this.programs.get(name));
  }

  /**
   * Return the directory containing program files.
   */
  public File getProgramsDirectory() {
    return this.programsDir;
  }

  @Override
  public CompoundTag writeToNBT() {
    CompoundTag tag = new CompoundTag();
    ListTag programs = new ListTag();
    for (Program p : this.programs.values()) {
      CompoundTag programTag = new CompoundTag();
      programTag.put(PROGRAM_KEY, p.writeToNBT());
      String programName = p.getName();
      if (this.programsSchedules.containsKey(programName)) {
        programTag.putLong(SCHEDULE_KEY, this.programsSchedules.get(programName));
        programTag.putLong(REPEAT_AMOUNT_KEY, this.programsRepeats.get(programName));
      }
      programTag.putBoolean(RUNNING_KEY, this.runningPrograms.get(programName));
      programs.add(programTag);
    }
    tag.put(PROGRAMS_KEY, programs);
    return tag;
  }

  @Override
  public void readFromNBT(final CompoundTag tag) {
    ListTag list = tag.getList(PROGRAMS_KEY, Tag.TAG_COMPOUND);
    this.programs.clear();
    this.programsSchedules.clear();
    this.programsRepeats.clear();
    this.runningPrograms.clear();
    for (Tag t : list) {
      CompoundTag programTag = (CompoundTag) t;
      try {
        Program program = new Program(programTag.getCompound(PROGRAM_KEY), this);
        this.programs.put(program.getName(), program);
        if (programTag.contains(SCHEDULE_KEY)) {
          this.programsSchedules.put(program.getName(), programTag.getLong(SCHEDULE_KEY));
          if (programTag.contains(REPEAT_AMOUNT_KEY)) {
            this.programsRepeats.put(program.getName(), programTag.getLong(REPEAT_AMOUNT_KEY));
          }
        }
        this.runningPrograms.put(program.getName(), programTag.getBoolean(RUNNING_KEY));
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  /*
   * Static methods
   */

  /**
   * Finish setup of this interpreter.
   * Call only after all types have been declared.
   */
  public static void initialize() {
    processTypeAnnotations();
    initialized = true;
  }

  /**
   * Return all declared types.
   */
  public static List<TypeBase<?>> getTypes() {
    return new ArrayList<>(TYPES.values());
  }

  /**
   * Return the {@link TypeBase} instance for the given class.
   *
   * @param typeClass Type’s class.
   * @param <T>       Type’s wrapped type.
   * @return The type class’ instance.
   */
  public static <T extends TypeBase<?>> T getTypeInstance(final Class<T> typeClass) {
    //noinspection unchecked
    return (T) TYPES.values().stream().filter(t -> t.getClass() == typeClass).findFirst().orElse(null);
  }

  /**
   * Return the {@link TypeBase} instance for the given type name.
   *
   * @param name Type’s name.
   * @param <T>  Type’s wrapped type.
   * @return The type instance.
   */
  public static <T extends TypeBase<?>> T getTypeForName(final String name) {
    //noinspection unchecked
    return (T) TYPES.get(name);
  }

  /**
   * Return the {@link TypeBase} instance for the given wrapped class.
   *
   * @param wrappedClass Wrapped type’s class.
   * @param <T>          Type’s wrapped type.
   * @param <U>          Instance’s type.
   * @return The type instance.
   */
  public static <T, U extends TypeBase<T>> U getTypeForWrappedClass(final Class<T> wrappedClass) {
    //noinspection unchecked
    return (U) WRAPPED_TYPES.entrySet().stream()
        .min(Comparator.comparing(e -> classDistance(e.getKey(), wrappedClass)))
        .map(Map.Entry::getValue)
        .orElse(null);
  }

  /**
   * Return the class hierarchy distance between the two given classes.
   *
   * @param key          A superclass of the second argument.
   * @param wrappedClass The class to get the distance to the first one.
   * @return The distance between the two classes.
   */
  private static int classDistance(final Class<?> key, final Class<?> wrappedClass) {
    if (!key.isAssignableFrom(wrappedClass)) {
      return Integer.MAX_VALUE;
    }
    Class<?> c = wrappedClass;
    int distance = 0;
    while (c != key) {
      c = c.getSuperclass();
      distance++;
    }
    return distance;
  }

  /**
   * Return the {@link TypeBase} instance for the given object.
   *
   * @param o Object to get the type of.
   * @return The type instance.
   */
  public static TypeBase<?> getTypeForValue(final Object o) {
    return o != null ? getTypeForWrappedClass(o.getClass()) : getTypeInstance(NullType.class);
  }

  /**
   * Declare all default builtin types.
   */
  public static void declareDefaultBuiltinTypes() {
    // TODO load through reflection
    declareType(AnyType.class);
    declareType(NullType.class);
    declareType(BooleanType.class);
    declareType(IntType.class);
    declareType(FloatType.class);
    declareType(StringType.class);
    declareType(PosType.class);
    declareType(ListType.class);
    declareType(SetType.class);
    declareType(MapType.class);
    declareType(BlockType.class);
    declareType(ItemType.class);
    declareType(WorldType.class);
    declareType(FunctionType.class);
    declareType(RangeType.class);
    declareType(ModuleType.class);
  }

  /**
   * Declare a new wrapper type.
   *
   * @param typeClass Wrapper type’s class.
   * @throws TypeException If any error occurs related to {@link Property} or
   *                       {@link net.darmo_creations.mccode.interpreter.annotations.Method} annotations.
   */
  public static <T extends TypeBase<?>> void declareType(final Class<T> typeClass) throws TypeException {
    ensureNotInitialized();

    T type;
    try {
      type = typeClass.getDeclaredConstructor().newInstance();
    } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
      throw new TypeException("missing empty constructor for class %s" + typeClass);
    }

    Type typeAnnotation = typeClass.getAnnotation(Type.class);
    if (typeAnnotation == null) {
      throw new TypeException("Missing Type annotation on type class %s".formatted(typeClass.getSimpleName()));
    }

    String typeName = typeAnnotation.name();
    Class<?> wrappedType = type.getWrappedType();

    if (!ProgramParser.IDENTIFIER_PATTERN.asPredicate().test(typeName)) {
      throw new TypeException(String.format("invalid type name \"%s\" for type class %s", typeName, typeClass));
    }
    if (TYPES.containsKey(typeName)) {
      throw new TypeException(String.format("a type with the name \"%s\" already exists", typeName));
    }
    if (TYPES.values().stream().anyMatch(t -> t.getClass() == type.getClass())) {
      throw new TypeException(String.format("cannot redeclare type \"%s\"", typeName));
    }
    if (WRAPPED_TYPES.containsKey(wrappedType)) {
      throw new TypeException("a wrapper class is already declared for class %s" + wrappedType);
    }

    setPrivateField(TypeBase.class, type, "name", typeName);
    setPrivateField(TypeBase.class, type, "generateCastOperator", typeAnnotation.generateCastOperator());
    setPrivateField(TypeBase.class, type, "doc", typeAnnotation.doc().trim());

    TYPES.put(typeName, type);
    WRAPPED_TYPES.put(wrappedType, type);
  }

  /**
   * Process annotations of all declared types.
   */
  private static void processTypeAnnotations() {
    for (TypeBase<?> type : TYPES.values()) {
      setTypeProperties(type);
      setTypeMethods(type);
    }
  }

  /**
   * Set private "properties" field of the given type.
   */
  private static void setTypeProperties(TypeBase<?> type) {
    Map<String, Triple<Method, Boolean, String>> getterMethods = new HashMap<>();
    Map<String, Method> setterMethods = new HashMap<>();
    extractTypeProperties(type, getterMethods, setterMethods);
    setPrivateField(TypeBase.class, type, "properties",
        createPropertyObjects(type, getterMethods, setterMethods));
  }

  /**
   * Extract property getters and setters methods annotated with {@link Property} or {@link PropertySetter}.
   * Checks that every setter has a getter and that there are not duplicate properties.
   *
   * @param type          Type instance to extract properties from.
   * @param getterMethods Map of getter methods to populate.
   * @param setterMethods Map of setter methods to populate.
   */
  private static void extractTypeProperties(final TypeBase<?> type,
                                            Map<String, Triple<Method, Boolean, String>> getterMethods,
                                            Map<String, Method> setterMethods) {
    //noinspection unchecked
    Class<? extends TypeBase<?>> typeClass = (Class<? extends TypeBase<?>>) type.getClass();

    for (Method method : typeClass.getMethods()) {
      String methodName = method.getName();
      boolean isGetter = false;

      if (method.isAnnotationPresent(Property.class)) {
        Property propertyAnnotation = method.getAnnotation(Property.class);
        String propertyName = propertyAnnotation.name();
        Class<?>[] parameterTypes = method.getParameterTypes();

        if (!ProgramParser.IDENTIFIER_PATTERN.asPredicate().test(propertyName)) {
          throw new TypeException(String.format("invalid name \"%s\" for property in type %s", propertyName, typeClass));
        }
        if (getterMethods.containsKey(propertyName)) {
          throw new TypeException(String.format("property %s already defined for type %s", propertyName, typeClass));
        }
        if (parameterTypes.length != 1) {
          throw new TypeException(String.format("invalid number of arguments for property %s.%s: expected 1, got %s",
              typeClass, methodName, parameterTypes.length));
        }
        if (!parameterTypes[0].isAssignableFrom(type.getWrappedType())) {
          throw new TypeException(String.format("method argument type does not match wrapped type in %s.%s",
              typeClass, methodName));
        }

        getterMethods.put(propertyName, new ImmutableTriple<>(method, propertyAnnotation.mayBeNull(), propertyAnnotation.doc()));
        isGetter = true;
      }

      if (method.isAnnotationPresent(PropertySetter.class)) {
        PropertySetter propertyAnnotation = method.getAnnotation(PropertySetter.class);
        String targetPropertyName = propertyAnnotation.forProperty();

        if (isGetter) {
          throw new TypeException(String.format("annotations Property and PropertySetter both present on method %s.%s", typeClass, methodName));
        }

        setterMethods.put(targetPropertyName, method);
      }
    }

    checkPropertySetters(typeClass, getterMethods, setterMethods);
  }

  /**
   * Check that every setter has a corresponding getter
   */
  private static void checkPropertySetters(final Class<? extends TypeBase<?>> typeClass,
                                           final Map<String, Triple<Method, Boolean, String>> getterMethods,
                                           final Map<String, Method> setterMethods) {
    for (Map.Entry<String, Method> entry : setterMethods.entrySet()) {
      if (!getterMethods.containsKey(entry.getKey())) {
        throw new TypeException(String.format("no getter method for setter method %s.%s", typeClass, entry.getValue().getName()));
      }
    }
  }

  /**
   * Create {@link ObjectProperty} instances for each getter and setter. Setters validity is not checked.
   *
   * @param type          Type instance the properties belong to.
   * @param getterMethods Collection of property getters with their metadata.
   * @param setterMethods Collection of property setters.
   * @return A map containing the {@link ObjectProperty} instances.
   */
  private static Map<String, ObjectProperty> createPropertyObjects(final TypeBase<?> type,
                                                                   final Map<String, Triple<Method, Boolean, String>> getterMethods,
                                                                   final Map<String, Method> setterMethods) {
    Map<String, ObjectProperty> properties = new HashMap<>();

    for (Map.Entry<String, Triple<Method, Boolean, String>> entry : getterMethods.entrySet()) {
      String propertyName = entry.getKey();
      Method getterMethod = entry.getValue().getLeft();
      TypeBase<?> returnType = getTypeForWrappedClass(getterMethod.getReturnType());
      boolean nullable = entry.getValue().getMiddle();
      String docString = entry.getValue().getRight().trim();

      if (returnType == null) {
        throw new TypeException(String.format("method return type does not match declared Property annotation type: %s.%s",
            type.getName(), propertyName));
      }

      String doc = "Type: " + formatTypeDoc(returnType.getName(), nullable);
      if (docString.length() != 0) {
        doc = docString + "\n" + doc;
      }

      ObjectProperty property = new ObjectProperty(type, propertyName, returnType, getterMethod, setterMethods.get(propertyName), doc);
      properties.put(property.getName(), property);
    }

    return properties;
  }

  /**
   * Extract all methods annotated with {@link net.darmo_creations.mccode.interpreter.annotations.Method}
   * from the given type and set its "methods" field.
   *
   * @param type Type instance to extract methods from.
   */
  private static void setTypeMethods(TypeBase<?> type) {
    //noinspection unchecked
    Class<? extends TypeBase<?>> typeClass = (Class<? extends TypeBase<?>>) type.getClass();
    String typeName = type.getName();
    Class<?> wrappedType = type.getWrappedType();
    Map<String, MemberFunction> methods = new HashMap<>();

    for (Method method : typeClass.getMethods()) {
      if (method.isAnnotationPresent(net.darmo_creations.mccode.interpreter.annotations.Method.class)) {
        net.darmo_creations.mccode.interpreter.annotations.Method methodAnnotation =
            method.getAnnotation(net.darmo_creations.mccode.interpreter.annotations.Method.class);
        String methodName = methodAnnotation.name();
        ParameterMeta[] paramsMetadata = methodAnnotation.parametersMetadata();
        Class<?>[] parameterTypes = method.getParameterTypes();

        if (!ProgramParser.IDENTIFIER_PATTERN.asPredicate().test(methodName)) {
          throw new TypeException(String.format("invalid name \"%s\" for method in type %s", methodName, typeClass));
        }
        if (methods.containsKey(methodName)) {
          throw new TypeException(String.format("method %s already defined for type %s",
              methodName, typeName));
        }
        if (parameterTypes.length < 2) {
          throw new TypeException(String.format("not enough arguments for method %s.%s: expected at least 2, got %s",
              typeName, methodName, parameterTypes.length));
        }
        if (parameterTypes[0] != Scope.class) {
          throw new TypeException(String.format("first argument of method must be Scope object for method %s.%s",
              typeName, methodName));
        }
        if (parameterTypes[1] != wrappedType) {
          throw new TypeException(String.format("second argument of method must be of same type as wrapped type for method %s.%s: expected %s, got %s",
              typeName, methodName, wrappedType, parameterTypes[1]));
        }
        if (parameterTypes.length != paramsMetadata.length) {
          throw new TypeException(String.format("invalid number of parameter metadata elements for method %s.%s: expected %d, got %d",
              typeName, methodName, parameterTypes.length, paramsMetadata.length));
        }

        List<? extends TypeBase<?>> paramsTypes = Arrays.stream(parameterTypes)
            .skip(2) // Skip scope and instance arguments
            .map(c -> (TypeBase<?>) getTypeForWrappedClass(c))
            .collect(Collectors.toList());

        if (paramsTypes.stream().anyMatch(Objects::isNull)) {
          throw new TypeException(String.format("method argument type does not match any declared type: %s in %s.%s",
              paramsTypes, typeName, methodName));
        }
        TypeBase<?> returnType = getTypeForWrappedClass(method.getReturnType());
        if (returnType == null) {
          throw new TypeException(String.format("method return type does not match any declared type: %s in %s.%s",
              method.getReturnType(), typeName, methodName));
        }

        String doc = generateMethodDoc(typeName, methodAnnotation, paramsTypes, returnType);
        boolean mayReturnNull = methodAnnotation.returnTypeMetadata().mayBeNull();
        MemberFunction memberFunction = new MemberFunction(type, methodName, paramsTypes, returnType, mayReturnNull, method, doc);
        methods.put(memberFunction.getName(), memberFunction);
      }
    }

    setPrivateField(TypeBase.class, type, "methods", methods);
  }

  /**
   * Generate the doc string for the given method.
   *
   * @param typeName         Name of the type the method belongs to.
   * @param methodAnnotation Method’s annotation.
   * @param paramsTypes      List of method’s parameter types.
   * @param returnType       Method’s return type.
   * @return The doc string.
   */
  private static String generateMethodDoc(final String typeName,
                                          final net.darmo_creations.mccode.interpreter.annotations.Method methodAnnotation,
                                          final List<? extends TypeBase<?>> paramsTypes,
                                          final TypeBase<?> returnType) {
    ReturnMeta returnMetadata = methodAnnotation.returnTypeMetadata();
    ParameterMeta[] paramsMetadata = methodAnnotation.parametersMetadata();
    StringBuilder signatureParams = new StringBuilder();

    for (int i = 0; i < paramsTypes.size(); i++) {
      if (i > 0) {
        signatureParams.append(", ");
      }
      signatureParams.append(formatTypeDoc(paramsTypes.get(i).getName(), paramsMetadata[i].mayBeNull()))
          .append(' ')
          .append(paramsMetadata[i].name());
    }
    String methodName = methodAnnotation.name();
    StringBuilder doc = new StringBuilder("§nSignature§r: ")
        .append(String.format("%s.%s(%s) -> %s", typeName, methodName, signatureParams,
            formatTypeDoc(returnType.getName(), returnMetadata.mayBeNull())));

    // Method
    String methodDoc = methodAnnotation.doc().trim();
    if (methodDoc.length() != 0) {
      doc.insert(0, methodDoc + "\n");
    }

    Set<String> paramsNames = new HashSet<>();
    // Parameters
    for (int i = 0; i < paramsMetadata.length; i++) {
      ParameterMeta meta = paramsMetadata[i];
      String paramName = meta.name();
      if (!ProgramParser.IDENTIFIER_PATTERN.asPredicate().test(paramName)) {
        throw new TypeException(String.format("invalid name \"%s\" for parameter %d in method %s.%s", paramName, i, typeName, methodName));
      }
      if (paramsNames.contains(paramName)) {
        throw new TypeException(String.format("duplicate parameter name \"%s\" for method %s.%s", paramName, typeName, methodName));
      }
      paramsNames.add(paramName);
      String paramDoc = meta.doc().trim();
      if (paramDoc.length() != 0) {
        doc.append(paramName).append(':').append(' ').append(paramDoc).append('\n');
      }
    }

    // Return type
    String returnDoc = returnMetadata.doc().trim();
    if (returnDoc.length() != 0) {
      doc.append("Returns: ").append(returnDoc);
    }

    return doc.toString().trim();
  }

  /**
   * Return all declared builtin functions.
   */
  public static List<BuiltinFunction> getBuiltinFunctions() {
    return new LinkedList<>(FUNCTIONS.values());
  }

  /**
   * Return the function with the given name.
   *
   * @param name Function’s name.
   * @return The function or null if none matched.
   */
  public static BuiltinFunction getBuiltinFunction(final String name) {
    return FUNCTIONS.get(name);
  }

  /**
   * Declare all default builtin functions.
   */
  public static void declareDefaultBuiltinFunctions() {
    // TODO load through reflection
    declareBuiltinFunction(AbsFunction.class);
    declareBuiltinFunction(AcosFunction.class);
    declareBuiltinFunction(AsinFunction.class);
    declareBuiltinFunction(AtanFunction.class);
    declareBuiltinFunction(Atan2Function.class);
    declareBuiltinFunction(CbrtFunction.class);
    declareBuiltinFunction(CeilFunction.class);
    declareBuiltinFunction(CosFunction.class);
    declareBuiltinFunction(ErrorFunction.class);
    declareBuiltinFunction(ExpFunction.class);
    declareBuiltinFunction(FloorFunction.class);
    declareBuiltinFunction(HypotFunction.class);
    declareBuiltinFunction(IsInstanceFunction.class);
    declareBuiltinFunction(LenFunction.class);
    declareBuiltinFunction(Log10Function.class);
    declareBuiltinFunction(LogFunction.class);
    declareBuiltinFunction(MaxFunction.class);
    declareBuiltinFunction(MinFunction.class);
    declareBuiltinFunction(PrintFunction.class);
    declareBuiltinFunction(RandomFloatFunction.class);
    declareBuiltinFunction(RandomIntFunction.class);
    declareBuiltinFunction(RangeFunction.class);
    declareBuiltinFunction(ReversedFunction.class);
    declareBuiltinFunction(RoundFunction.class);
    declareBuiltinFunction(SetRandomSeedFunction.class);
    declareBuiltinFunction(SinFunction.class);
    declareBuiltinFunction(SortedFunction.class);
    declareBuiltinFunction(SqrtFunction.class);
    declareBuiltinFunction(TanFunction.class);
    declareBuiltinFunction(ToDegreesFunction.class);
    declareBuiltinFunction(ToRadiansFunction.class);
    declareBuiltinFunction(ToRelativePosFunction.class);

    // Generate cast operators for relevant types
    for (TypeBase<?> type : TYPES.values()) {
      if (type.generateCastOperator()) {
        String name = "to_" + type.getName();
        FUNCTIONS.put(name, new BuiltinFunction(name, type, false, new Parameter("o", ProgramManager.getTypeInstance(AnyType.class))) {
          @Override
          public Object apply(final Scope scope) {
            return type.explicitCast(scope, this.getParameterValue(scope, 0));
          }
        });
      }
    }
  }

  /**
   * Declare a new builtin function.
   *
   * @param functionClass Function’s class.
   */
  public static void declareBuiltinFunction(final Class<? extends BuiltinFunction> functionClass) {
    ensureNotInitialized();

    Function functionAnnotation = functionClass.getAnnotation(Function.class);
    if (functionAnnotation == null) {
      throw new MCCodeException("missing @Function annotation on builtin function class %s"
          .formatted(functionClass.getSimpleName()));
    }

    BuiltinFunction function;
    try {
      function = functionClass.getDeclaredConstructor().newInstance();
    } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
      throw new TypeException("missing empty constructor for function class %s" + functionClass);
    }

    String name = function.getName();
    if (FUNCTIONS.containsKey(name)) {
      throw new MCCodeException(String.format("a function with the name \"%s\" already exists", name));
    }

    setBuiltinFunctionDoc(function, functionAnnotation);

    FUNCTIONS.put(name, function);
  }

  /**
   * Generate and set the doc string for the given function.
   *
   * @param function           The function.
   * @param functionAnnotation The function’s annotation.
   */
  private static void setBuiltinFunctionDoc(BuiltinFunction function, final Function functionAnnotation) {
    StringBuilder paramsDoc = new StringBuilder();
    List<Parameter> parameters = function.getParameters();
    for (int i = 0; i < parameters.size(); i++) {
      if (i > 0) {
        paramsDoc.append(',').append(' ');
      }
      paramsDoc.append(formatTypeDoc(parameters.get(i).getType().getName(), parameters.get(i).isNullable()))
          .append(' ')
          .append(parameters.get(i).getName());
    }

    StringBuilder doc = new StringBuilder("§nSignature§r: ");
    doc.append(String.format("%s(%s) -> %s", function.getName(), paramsDoc,
        formatTypeDoc(function.getReturnType().getName(), function.mayReturnNull())));

    // Function
    String functionDoc = functionAnnotation.doc().trim();
    if (functionDoc.length() != 0) {
      doc.insert(0, functionDoc + "\n");
    }

    // Parameters
    for (int i = 0; i < parameters.size(); i++) {
      Parameter parameter = parameters.get(i);
      String paramDoc = functionAnnotation.parametersDoc()[i].trim();
      if (paramDoc.length() != 0) {
        doc.append(parameter.getName()).append(':').append(' ').append(paramDoc).append('\n');
      }
    }

    // Return type
    String returnDoc = functionAnnotation.returnDoc().trim();
    if (returnDoc.length() != 0) {
      doc.append("Returns: ").append(returnDoc);
    }

    setPrivateField(BuiltinFunction.class, function, "doc", doc.toString().trim());
  }

  /**
   * Format a type for method or function signature.
   *
   * @param typeName Name of the type to format.
   * @param nullable Whether the parameter associated to the type may be null.
   * @return The type name with a question mark appended at the end if nullable is true; only the type name otherwise.
   */
  private static String formatTypeDoc(final String typeName, final boolean nullable) {
    return typeName + (nullable ? "?" : "");
  }

  private static <C, I extends C> void setPrivateField(final Class<C> class_, I instance, final String fieldName, final Object fieldValue) {
    try {
      Field field = class_.getDeclaredField(fieldName);
      field.setAccessible(true);
      field.set(instance, fieldValue);
    } catch (NoSuchFieldException | IllegalAccessException e) {
      throw new TypeException("missing field '%s' for class %s".formatted(fieldName, class_.getSimpleName()));
    }
  }

  /**
   * Throw an exception if the program manager is already initialized.
   */
  private static void ensureNotInitialized() {
    if (initialized) {
      throw new MCCodeException("interpreter already initialized");
    }
  }
}
