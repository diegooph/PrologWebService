package br.com.zalf.prolog.webservice.commons.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.Normalizer;

/**
 * Created on 06/06/18.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public final class StringUtils {

    private StringUtils() {
        throw new IllegalStateException(StringUtils.class.getSimpleName() + " cannot be instantiated!");
    }

    @NotNull
    public static String getOnlyNumbers(String string) {
        if (string != null && !string.isEmpty())
            return string.replaceAll("[^0-9]*", "");

        return "";
    }

    @NotNull
    public static String stripAccents(@NotNull String string) {
        string = Normalizer.normalize(string, Normalizer.Form.NFD);
        return string.replaceAll("[^\\p{ASCII}]", "");
    }

    /**
     * This method inserts a string "inside" another string at a specific position. It is 0-based.
     * If you want to add
     *
     * @param fullString       the string that will have the character inserted.
     * @param stringToInsert   the string to be inserted.
     * @param positionToInsert the position to be inserted.
     * @return the new complete string.
     */
    @NotNull
    public static String insertStringAtSpecificPosition(@NotNull final String fullString,
                                                        @NotNull final String stringToInsert,
                                                        final int positionToInsert) {
        return fullString.substring(0, positionToInsert)
                + stringToInsert
                + fullString.substring(positionToInsert, fullString.length());
    }

    /**
     * @return true if the string is null, empty string "", or the length is less than equal to 0.
     */
    public static boolean isNullOrEmpty(@Nullable final String inString) {
        return inString == null || inString.equals("") || inString.length() <= 0;
    }

    /**
     * Returns the given string if it is not-null; the empty string otherwise.
     *
     * @param string the string to test and possibly return.
     * @return {@code string} itself if it is non-null; {@code ""} if it is null
     */
    public static String nullToEmpty(@Nullable String string) {
        return (string == null) ? "" : string;
    }

    /**
     * Returns the given string if it is notempty; {@code null} otherwise.
     *
     * @param string the string to test and possibly return.
     * @return {@code string} itself if it is nonempty; {@code null} if it is empty or null
     */
    @Nullable
    public static String emptyToNull(@Nullable String string) {
        return isNullOrEmpty(string) ? null : string;
    }

    /**
     * Return whether the specified string contains only alpabets/special chars too.
     *
     * @param palavra the string to test.
     * @return true if the string contains only alpabets/special chars, false if it has numbers.
     */
    public static boolean isAlpabetsValue(String palavra) {
        return !palavra.matches(".*\\d+.*");
    }

    /**
     * Return whether the specified string contains only numbers.
     *
     * @param representacaoValor the string to test.
     * @return true if the string contains only numbers, false if it has alpabets/special chars too.
     */
    public static boolean isIntegerValue(@NotNull final String representacaoValor) {
        return representacaoValor.matches("^[+-]?\\d+$");
    }

    /**
     * Return whether the specified string contains only numbers.
     *
     * @param representacaoValor the string to test.
     * @return true if the string contains only positive numbers, false if it has alpabets/special chars too.
     */
    public static boolean isIntegerValuePositive(@NotNull final String representacaoValor) {
        return representacaoValor.matches("^?\\d+$");
    }
}
