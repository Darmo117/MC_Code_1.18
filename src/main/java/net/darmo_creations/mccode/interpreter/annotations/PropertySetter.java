package net.darmo_creations.mccode.interpreter.annotations;

import net.darmo_creations.mccode.interpreter.type_wrappers.TypeBase;

import java.lang.annotation.*;

/**
 * Methods annoted with this annotation, from classes extending {@link TypeBase},
 * will be accessible property setters from instances of the type within programs.
 * <p>
 * Annotated methods must feature a two parameters:
 * the instance object to set the property on and the new property value.
 * <p>
 * {@link #forProperty()} must refer to an existing {@link Property} annotation in the same class.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface PropertySetter {
  /**
   * MCCode name of the property it is the setter for.
   * <p>
   * Must refer to a {@link Property} annotation in the same class.
   */
  String forProperty();
}
