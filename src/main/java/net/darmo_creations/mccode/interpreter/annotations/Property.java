package net.darmo_creations.mccode.interpreter.annotations;

import net.darmo_creations.mccode.interpreter.type_wrappers.TypeBase;

import java.lang.annotation.*;

/**
 * Methods annoted with this annotation, from classes extending {@link TypeBase},
 * will be accessible properties from instances of the type within programs.
 * <p>
 * Annotated methods should return a value matching any wrapped type and must feature a single parameter:
 * the instance object to query the property value from.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface Property {
  /**
   * MCCode name of the property.
   */
  String name();

  /**
   * Whether the property’s value may be null. Defaults to false.
   */
  boolean mayBeNull() default false;

  /**
   * Documentation string for the property.
   */
  String doc() default "";
}
