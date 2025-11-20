package uts.edu.java.proyecto.validacion;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validador para la anotación @ValidId
 */
public class ValidIdValidator implements ConstraintValidator<ValidId, Integer> {
    
    @Override
    public void initialize(ValidId constraintAnnotation) {
        // No se requiere inicialización
    }
    
    @Override
    public boolean isValid(Integer id, ConstraintValidatorContext context) {
        if (id == null) {
            return false;
        }
        return id > 0;
    }
}

