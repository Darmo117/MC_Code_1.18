package net.darmo_creations.mccode.interpreter.annotations;

/**
 * Declares metadata for the returned value of a type method.
 */
public @interface ReturnMeta {
  /**
   * Whether the returned value may be null. Defaults to false.
   */
  boolean mayBeNull() default false;

  /**
   * Documentation string for the returned value.
   */
  String doc() default "";
}
