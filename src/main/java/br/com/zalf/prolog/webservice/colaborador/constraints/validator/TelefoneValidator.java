package br.com.zalf.prolog.webservice.colaborador.constraints.validator;

import br.com.zalf.prolog.webservice.colaborador.constraints.Telefone;
import br.com.zalf.prolog.webservice.colaborador.model.ColaboradorTelefone;
import br.com.zalf.prolog.webservice.commons.util.ValidationUtils;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Created on 2020-01-23
 *
 * @author Wellington Moraes (https://github.com/wvinim)
 */
public class TelefoneValidator implements ConstraintValidator<Telefone, ColaboradorTelefone> {
    @Override
    public void initialize(Telefone value) {
    }

    @Override
    public boolean isValid(ColaboradorTelefone colaboradorTelefone, ConstraintValidatorContext constraintValidatorContext) {
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();

        String regionCode = phoneUtil.getRegionCodeForCountryCode(colaboradorTelefone.getPrefixoPais());

        try {
            PhoneNumber numberProto = phoneUtil.parse(colaboradorTelefone.getTelefone(), regionCode);
            return phoneUtil.isValidNumber(numberProto);
        } catch (NumberParseException e) {
            System.err.println("NumberParseException was thrown: " + e.toString());
        }

        return false;
    }
}