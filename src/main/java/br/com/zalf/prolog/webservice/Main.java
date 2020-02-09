package br.com.zalf.prolog.webservice;


import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import org.jetbrains.annotations.NotNull;

public class Main {
    private static final int MAX_LENGTH_NOME_COLABORADOR = 20;

    public static void main(String[] args) {
        final PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        final String regionCode = phoneUtil.getRegionCodeForCountryCode(1246);

        try {
            final Phonenumber.PhoneNumber numberProto = phoneUtil.parse("246-437-2326", regionCode);
            System.out.println(phoneUtil.isValidNumber(numberProto));
        } catch (final NumberParseException ignored) {
            ignored.printStackTrace();
        }
    }

    @SuppressWarnings("Duplicates")
    @NotNull
    private static String formataNomeColaborador(@NotNull final String nomeColaboradorAbertura) {
        return verificaNomeMaxLength(String.join(" ", new String[]{}));
    }

    @SuppressWarnings("Duplicates")
    @NotNull
    private static String verificaNomeMaxLength(@NotNull final String nomeColaboradorAbertura) {
        if (nomeColaboradorAbertura.length() > MAX_LENGTH_NOME_COLABORADOR) {
            return nomeColaboradorAbertura.substring(0, MAX_LENGTH_NOME_COLABORADOR - 1).trim().concat(".");
        } else {
            return nomeColaboradorAbertura;
        }
    }
}