package net.darmo_creations.mccode.interpreter.annotations;

import java.lang.annotation.*;

/**
 * This annotation declares metadata of a builtin function.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface Function {
  /**
   * Documentation string for the function.
   */
  String doc() default "";

  /**
   * Documentation strings for each parameter.
   */
  String[] parametersDoc() default {};

  /**
   * Documentation string for the returned value.
   */
  String returnDoc() default "";
}
