package uts.edu.java.proyecto.validacion;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotación de validación para asegurar que un ID sea un número positivo válido
 */
@Documented
@Constraint(validatedBy = ValidIdValidator.class)
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidId {
    String message() default "El ID debe ser un número positivo válido";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

