package net.darmo_creations.mccode.interpreter.builtin_functions;

import net.darmo_creations.mccode.interpreter.Parameter;
import net.darmo_creations.mccode.interpreter.ProgramManager;
import net.darmo_creations.mccode.interpreter.Scope;
import net.darmo_creations.mccode.interpreter.annotations.Function;
import net.darmo_creations.mccode.interpreter.type_wrappers.AnyType;
import net.darmo_creations.mccode.interpreter.type_wrappers.BooleanType;
import net.darmo_creations.mccode.interpreter.type_wrappers.PosType;
import net.darmo_creations.mccode.interpreter.types.BuiltinFunction;
import net.darmo_creations.mccode.interpreter.types.Position;

/**
 * Function that casts a value into a relative {@link Position} object.
 */
@Function(parametersDoc = {
    "The absolute position to make relative.",
    "Whether the x coordinate should be relative.",
    "Whether the y coordinate should be relative.",
    "Whether the z coordinate should be relative."},
    returnDoc = "A new position object.",
    doc = "Casts an absolute position into a relative position.")
public class ToRelativePosFunction extends BuiltinFunction {
  /**
   * Create a function that casts a value into a relative {@link Position} object.
   */
  public ToRelativePosFunction() {
    super("to_relative_pos", ProgramManager.getTypeInstance(PosType.class), false,
        new Parameter("pos", ProgramManager.getTypeInstance(AnyType.class)),
        new Parameter("x_relative", ProgramManager.getTypeInstance(BooleanType.class)),
        new Parameter("y_relative", ProgramManager.getTypeInstance(BooleanType.class)),
        new Parameter("z_relative", ProgramManager.getTypeInstance(BooleanType.class)));
  }

  @Override
  public Object apply(final Scope scope) {
    Object posObject = this.getParameterValue(scope, 0);
    Boolean xRelative = this.getParameterValue(scope, 1);
    Boolean yRelative = this.getParameterValue(scope, 2);
    Boolean zRelative = this.getParameterValue(scope, 3);
    Position pos = ProgramManager.getTypeInstance(PosType.class).explicitCast(scope, posObject);
    return new Position(pos, xRelative, yRelative, zRelative);
  }
}
