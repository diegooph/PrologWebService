package br.com.zalf.prolog.webservice.frota.veiculo.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created on 2021-03-11
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PlacaValidator.class)
public @interface Placa {
    String message() default "A placa fornecida não está no padrão da Resolução 780/2019 CONTRAN, de 26 de junho de 2019";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    String value() default "";
}
