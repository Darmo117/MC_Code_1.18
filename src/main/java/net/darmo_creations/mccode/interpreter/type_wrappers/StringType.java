package net.darmo_creations.mccode.interpreter.type_wrappers;

import net.darmo_creations.mccode.interpreter.ProgramManager;
import net.darmo_creations.mccode.interpreter.Scope;
import net.darmo_creations.mccode.interpreter.annotations.Method;
import net.darmo_creations.mccode.interpreter.annotations.ParameterMeta;
import net.darmo_creations.mccode.interpreter.annotations.ReturnMeta;
import net.darmo_creations.mccode.interpreter.annotations.Type;
import net.darmo_creations.mccode.interpreter.exceptions.IndexOutOfBoundsException;
import net.darmo_creations.mccode.interpreter.types.MCList;
import net.minecraft.nbt.CompoundTag;

import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.Collectors;

/**
 * Wrapper type for {@link String} class.
 * <p>
 * Strings are iterable and support the __get_item__ operator.
 */
@Type(name = StringType.NAME, doc = "Type representing strings.")
public class StringType extends TypeBase<String> {
  public static final String NAME = "string";

  public static final String VALUE_KEY = "Value";

  @Override
  public Class<String> getWrappedType() {
    return String.class;
  }

  @Method(name = "lower",
      returnTypeMetadata = @ReturnMeta(doc = "The resulting `string."),
      doc = "Converts a `string to lower case.")
  public String toLowerCase(final Scope scope, final String self) {
    return self.toLowerCase();
  }

  @Method(name = "upper",
      returnTypeMetadata = @ReturnMeta(doc = "The resulting `string."),
      doc = "Converts a `string to upper case.")
  public String toUpperCase(final Scope scope, final String self) {
    return self.toUpperCase();
  }

  @Method(name = "title",
      returnTypeMetadata = @ReturnMeta(doc = "The resulting `string."),
      doc = "Converts a `string to title case.")
  public String toTitleCase(final Scope scope, final String self) {
    // Based on https://stackoverflow.com/a/1086134/3779986
    StringBuilder titleCase = new StringBuilder(self.length());
    boolean nextTitleCase = true;

    for (char c : self.toCharArray()) {
      if (Character.isSpaceChar(c)) {
        nextTitleCase = true;
      } else if (nextTitleCase) {
        c = Character.toTitleCase(c);
        nextTitleCase = false;
      } else {
        c = Character.toLowerCase(c);
      }
      titleCase.append(c);
    }

    return titleCase.toString();
  }

  @Method(name = "starts_with",
      parametersMetadata = {
          @ParameterMeta(name = "prefix", doc = "The `string to search for.")
      },
      returnTypeMetadata = @ReturnMeta(doc = "#True if the `string begins with the given prefix, #false otherwise."),
      doc = "Returns whether a `string starts with the given `string.")
  public Boolean startsWith(final Scope scope, final String self, final String prefix) {
    return self.startsWith(prefix);
  }

  @Method(name = "ends_with",
      parametersMetadata = {
          @ParameterMeta(name = "suffix", doc = "The `string to search for.")
      },
      returnTypeMetadata = @ReturnMeta(doc = "#True if the `string ends with the given suffix, #false otherwise."),
      doc = "Returns whether a `string ends with the given `string.")
  public Boolean endsWith(final Scope scope, final String self, final String suffix) {
    return self.endsWith(suffix);
  }

  @Method(name = "count",
      parametersMetadata = {
          @ParameterMeta(name = "needle", doc = "The `string to get the number of occurences of.")
      },
      returnTypeMetadata = @ReturnMeta(doc = "The number of occurences of the needle."),
      doc = "Returns the number of times the given `string is present in another.")
  public Long count(final Scope scope, final String self, final String needle) {
    if ("".equals(needle)) {
      return (long) self.length() + 1;
    }
    return (long) self.length() - self.replace(needle, "").length();
  }

  @Method(name = "index",
      parametersMetadata = {
          @ParameterMeta(name = "needle", doc = "The `string to get the index of.")
      },
      returnTypeMetadata = @ReturnMeta(doc = "The index of the first occurence of the needle or -1 if it was not found."),
      doc = "Returns the index of the first occurence of the given `string in another, " +
          "or -1 if no occurence were found.")
  public Long indexOf(final Scope scope, final String self, final String needle) {
    return (long) self.indexOf(needle);
  }

  @Method(name = "strip",
      returnTypeMetadata = @ReturnMeta(doc = "The resulting `string."),
      doc = "Removes all leading and trailing whitespace from a `string.")
  public String trim(final Scope scope, final String self) {
    return self.trim();
  }

  @Method(name = "left_strip",
      returnTypeMetadata = @ReturnMeta(doc = "The resulting `string."),
      doc = "Removes all leading whitespace from a `string.")
  public String trimLeft(final Scope scope, final String self) {
    return self.replaceFirst("^\\s+", "");
  }

  @Method(name = "right_strip", doc = "Removes all trailing whitespace from a `string.")
  public String trimRight(final Scope scope, final String self) {
    return self.replaceFirst("\\s+$", "");
  }

  @Method(name = "replace",
      parametersMetadata = {
          @ParameterMeta(name = "target", doc = "The substring to replace."),
          @ParameterMeta(name = "replacement", doc = "The replacement `string.")
      },
      returnTypeMetadata = @ReturnMeta(doc = "The resulting `string."),
      doc = "Replaces each substring of a `string that matches the target `string with " +
          "the specified literal replacement sequence.")
  public String replace(final Scope scope, final String self, final String target, final String replacement) {
    return self.replace(target, replacement);
  }

  @Method(name = "replace_regex",
      parametersMetadata = {
          @ParameterMeta(name = "target", doc = "The regex."),
          @ParameterMeta(name = "replacement", doc = "The replacement `string.")
      },
      returnTypeMetadata = @ReturnMeta(doc = "The resulting `string."),
      doc = "Replaces each substring of a `string that matches the regex `string with " +
          "the specified literal replacement sequence.")
  public String replaceRegex(final Scope scope, final String self, final String target, final String replacement) {
    return self.replaceAll(target, replacement);
  }

  @Method(name = "split",
      parametersMetadata = {
          @ParameterMeta(name = "separator", doc = "The delimiting regular expression.")
      },
      returnTypeMetadata = @ReturnMeta(doc = "The `list of `string objects computed by splitting the `string around matches " +
          "of the given regular expression."),
      doc = "Splits a `string around matches of the given regular expression.")
  public MCList split(final Scope scope, final String self, final String separator) {
    return new MCList(Arrays.asList(self.split(separator, -1)));
  }

  @Method(name = "join",
      parametersMetadata = {
          @ParameterMeta(name = "collection", doc = "A collection containing the values to join.")
      },
      returnTypeMetadata = @ReturnMeta(doc = "The resulting `string."),
      doc = "Joins all values from the given `list using the `string as a delimiter.")
  public String join(final Scope scope, final String self, final Object collection) {
    MCList list = ProgramManager.getTypeInstance(ListType.class).implicitCast(scope, collection);
    return list.stream().map(e -> ProgramManager.getTypeForValue(e).toString(e)).collect(Collectors.joining(self));
  }

  @Method(name = "format",
      parametersMetadata = {
          @ParameterMeta(name = "args", doc = "A `list of values to insert into the `string.")
      },
      returnTypeMetadata = @ReturnMeta(doc = "The resulting `string."),
      doc = "Formats a `string using the specified `list of values.")
  public String format(final Scope scope, final String self, final MCList args) {
    return String.format(self, args.toArray());
  }

  @Override
  protected Object __get_item__(final Scope scope, final String self, final Object key) {
    if (key instanceof Long || key instanceof Boolean) {
      Long index = ProgramManager.getTypeInstance(IntType.class).implicitCast(scope, key);
      if (index < 0 || index >= self.length()) {
        throw new IndexOutOfBoundsException(scope, index.intValue());
      }
      return String.valueOf(self.charAt(index.intValue()));
    }
    return super.__get_item__(scope, self, key);
  }

  @Override
  protected Object __add__(final Scope scope, final String self, final Object o, final boolean inPlace) {
    return self + ProgramManager.getTypeForValue(o).toString(o);
  }

  @Override
  protected Object __mul__(final Scope scope, final String self, final Object o, final boolean inPlace) {
    if (o instanceof Long || o instanceof Boolean) {
      Long nb = ProgramManager.getTypeInstance(IntType.class).implicitCast(scope, o);
      if (nb <= 0) {
        return "";
      }
      StringBuilder s = new StringBuilder(self);
      for (int i = 0; i < nb - 1; i++) {
        s.append(self);
      }
      return s.toString();
    }
    return super.__mul__(scope, self, o, inPlace);
  }

  @Override
  protected Object __eq__(final Scope scope, final String self, final Object o) {
    if (o instanceof String s) {
      return self.equals(s);
    }
    return false;
  }

  @Override
  protected Object __gt__(final Scope scope, final String self, final Object o) {
    if (o instanceof String s) {
      return self.compareTo(s) > 0;
    }
    return super.__gt__(scope, self, o);
  }

  @Override
  protected Object __in__(final Scope scope, final String self, final Object o) {
    if (o instanceof String s) {
      return self.contains(s);
    }
    return super.__in__(scope, self, o);
  }

  @Override
  protected boolean __bool__(final String self) {
    return self.length() != 0;
  }

  @Override
  protected Iterator<?> __iter__(final Scope scope, final String self) {
    char[] chars = new char[self.length()];
    self.getChars(0, self.length(), chars, 0);
    return new Iterator<>() {
      private final char[] characters = chars;
      private int i;

      @Override
      public boolean hasNext() {
        return this.i < this.characters.length;
      }

      @Override
      public Object next() {
        return String.valueOf(this.characters[this.i++]);
      }
    };
  }

  @Override
  protected long __len__(final Scope scope, final String self) {
    return self.length();
  }

  @Override
  public String implicitCast(final Scope scope, final Object o) {
    return ProgramManager.getTypeForValue(o).toString(o);
  }

  @Override
  protected CompoundTag _writeToNBT(final String self) {
    CompoundTag tag = super._writeToNBT(self);
    tag.putString(VALUE_KEY, self);
    return tag;
  }

  @Override
  public String readFromNBT(final Scope scope, final CompoundTag tag) {
    return tag.getString(VALUE_KEY);
  }
}
