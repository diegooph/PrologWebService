package br.com.zalf.prolog.webservice.gente.colaborador.constraints.validator;

import br.com.zalf.prolog.webservice.gente.colaborador.constraints.Telefone;
import br.com.zalf.prolog.webservice.gente.colaborador.model.ColaboradorTelefone;
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
public final class TelefoneValidator implements ConstraintValidator<Telefone, ColaboradorTelefone> {

    @Override
    public boolean isValid(ColaboradorTelefone colaboradorTelefone,
                           ConstraintValidatorContext constraintValidatorContext) {
        if (colaboradorTelefone == null) {
            return true;
        }

        final PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        final String regionCode = phoneUtil.getRegionCodeForCountryCode(colaboradorTelefone.getPrefixoPais());

        try {
            final PhoneNumber numberProto = phoneUtil.parse(colaboradorTelefone.getNumero(), regionCode);
            return phoneUtil.isValidNumber(numberProto);
        } catch (final NumberParseException ignored) {}

        return false;
    }
}