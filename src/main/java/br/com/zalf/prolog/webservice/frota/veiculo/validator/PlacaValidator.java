package br.com.zalf.prolog.webservice.frota.veiculo.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * Created on 2021-03-11
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
public class PlacaValidator implements ConstraintValidator<Placa, String> {

    private static final Pattern PRE_MERCOSUL_PLACA_PATTERN = Pattern.compile("[A-Z]{3}[0-9][0-9A-Z][0-9]{2}");
    private String value;

    @Override
    public void initialize(final Placa constraintAnnotation) {
        this.value = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(final String s, final ConstraintValidatorContext constraintValidatorContext) {
        return PRE_MERCOSUL_PLACA_PATTERN.matcher(value).matches();
    }
}
