package net.darmo_creations.mccode.interpreter.annotations;

/**
 * Declares metadata for a parameter of a type method.
 */
public @interface ParameterMeta {
  /**
   * Name of the parameter as it should appear in the documentation string.
   */
  String name();

  /**
   * Whether the parameter may be null. Defaults to false.
   */
  boolean mayBeNull() default false;

  /**
   * Documentation string for the parameter.
   */
  String doc() default "";
}
