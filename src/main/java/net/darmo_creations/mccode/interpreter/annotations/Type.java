package net.darmo_creations.mccode.interpreter.annotations;

import net.darmo_creations.mccode.interpreter.type_wrappers.TypeBase;

import java.lang.annotation.*;

/**
 * This annotation declares metadata of a type wrapper.
 * <p>
 * Classes annotated with this annotation must be concrete classes inheriting from {@link TypeBase}
 * and feature constructor with no arguments.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface Type {
  /**
   * The type’s MCCode name.
   */
  String name();

  /**
   * Whether to generate an explicit cast operator.
   */
  boolean generateCastOperator() default true;

  /**
   * Documentation string for the type.
   */
  String doc() default "";
}
