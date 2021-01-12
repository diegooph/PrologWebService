package br.com.zalf.prolog.webservice.gente.colaborador.constraints.validator;

import br.com.zalf.prolog.webservice.commons.util.validators.PisPasepValidator;
import br.com.zalf.prolog.webservice.gente.colaborador.constraints.Pis;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Created on 2020-01-22
 *
 * @author Wellington Moraes (https://github.com/wvinim)
 */
public final class PisValidator implements ConstraintValidator<Pis, CharSequence> {

    @Override
    public boolean isValid(final CharSequence pis, final ConstraintValidatorContext constraintValidatorContext) {
        // Pis é Nullable.
        if (pis == null) {
            return true;
        }

        return PisPasepValidator.isPisPasepValid(String.valueOf(pis));
    }
}