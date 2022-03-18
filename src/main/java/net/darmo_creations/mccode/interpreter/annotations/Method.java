package net.darmo_creations.mccode.interpreter.annotations;

import net.darmo_creations.mccode.interpreter.Scope;
import net.darmo_creations.mccode.interpreter.type_wrappers.TypeBase;

import java.lang.annotation.*;

/**
 * Methods annoted with this annotation, from classes extending {@link TypeBase},
 * will be accessible from instances of the type within programs.
 * <p>
 * Annotated methods should return a value matching the declared wrapping type and must feature at least two parameters:
 * <li>the {@link Scope} the method is called from;
 * <li>the instance object;
 * <li>optional additional parameters.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface Method {
  /**
   * MCCode name of the method.
   */
  String name();

  /**
   * Metadata for all parameters. Array length must be the same as the number of parameters.
   */
  ParameterMeta[] parametersMetadata() default {};

  /**
   * Metadata for the returned value.
   */
  ReturnMeta returnTypeMetadata() default @ReturnMeta;

  /**
   * Documentation string for the method.
   */
  String doc() default "";
}
