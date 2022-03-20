package net.darmo_creations.mccode.interpreter.builtin_functions;

import net.darmo_creations.mccode.interpreter.Parameter;
import net.darmo_creations.mccode.interpreter.ProgramManager;
import net.darmo_creations.mccode.interpreter.Scope;
import net.darmo_creations.mccode.interpreter.annotations.Function;
import net.darmo_creations.mccode.interpreter.type_wrappers.AnyType;
import net.darmo_creations.mccode.interpreter.type_wrappers.PosType;
import net.darmo_creations.mccode.interpreter.type_wrappers.StringType;
import net.darmo_creations.mccode.interpreter.types.BuiltinFunction;
import net.darmo_creations.mccode.interpreter.types.Position;

/**
 * Function that casts a value into a relative {@link Position} object.
 */
@Function(parametersDoc = {
    "The absolute position to make relative.",
    "The prefix to apply to the x coordinate. May be #null.",
    "The prefix to apply to the y coordinate. May be #null.",
    "The prefix to apply to the z coordinate. May be #null."},
    returnDoc = "A new `pos object.",
    doc = "Casts an absolute position into a relative position.")
public class ToRelativePosFunction extends BuiltinFunction {
  /**
   * Create a function that casts a value into a relative {@link Position} object.
   */
  public ToRelativePosFunction() {
    super("to_relative_pos", ProgramManager.getTypeInstance(PosType.class), false,
        new Parameter("pos", ProgramManager.getTypeInstance(AnyType.class)),
        new Parameter("x_prefix", ProgramManager.getTypeInstance(StringType.class), true),
        new Parameter("y_prefix", ProgramManager.getTypeInstance(StringType.class), true),
        new Parameter("z_prefix", ProgramManager.getTypeInstance(StringType.class), true));
  }

  @Override
  public Object apply(final Scope scope) {
    Object posObject = this.getParameterValue(scope, 0);
    Position.Relativity xRelativity = Position.Relativity.fromString(this.getParameterValue(scope, 1));
    Position.Relativity yRelativity = Position.Relativity.fromString(this.getParameterValue(scope, 2));
    Position.Relativity zRelativity = Position.Relativity.fromString(this.getParameterValue(scope, 3));
    Position pos = ProgramManager.getTypeInstance(PosType.class).explicitCast(scope, posObject);
    return new Position(pos, xRelativity, yRelativity, zRelativity);
  }
}
