package br.com.zalf.prolog.webservice.colaborador.constraints.validator;

import br.com.zalf.prolog.webservice.colaborador.constraints.Pis;
import br.com.zalf.prolog.webservice.commons.util.ValidationUtils;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Created on 2020-01-22
 *
 * @author Wellington Moraes (https://github.com/wvinim)
 */
public final class PisValidator implements ConstraintValidator<Pis, CharSequence> {

    @Override
    public boolean isValid(CharSequence pis, ConstraintValidatorContext constraintValidatorContext) {
        // Pis é Nullable.
        if (pis == null) {
            return true;
        }

        return ValidationUtils.validaPIS(String.valueOf(pis));
    }
}