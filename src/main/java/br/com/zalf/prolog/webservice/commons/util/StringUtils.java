package br.com.zalf.prolog.webservice.commons.util;

import com.google.common.base.CharMatcher;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 06/06/18.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public final class StringUtils {
    /**
     * The empty String {@code ""}.
     * @since 2.0
     */
    public static final String EMPTY = "";
    private static final CharMatcher ALPHA_AND_DIGITS_MATCHER = CharMatcher
            .inRange('a', 'z')
            .or(CharMatcher.inRange('A', 'Z'))
            .or(CharMatcher.inRange('0', '9'))
            .precomputed();

    private StringUtils() {
        throw new IllegalStateException(StringUtils.class.getSimpleName() + " cannot be instantiated!");
    }

    /**
     * <p>Removes control characters (char &lt;= 32) from both
     * ends of this String returning {@code null} if the String is
     * empty ("") after the trim or if it is {@code null}.
     *
     * <p>The String is trimmed using {@link String#trim()}.
     * Trim removes start and end characters &lt;= 32.
     *
     * <pre>
     * StringUtils.trimToNull(null)          = null
     * StringUtils.trimToNull("")            = null
     * StringUtils.trimToNull("     ")       = null
     * StringUtils.trimToNull("abc")         = "abc"
     * StringUtils.trimToNull("    abc    ") = "abc"
     * </pre>
     *
     * @param str  the String to be trimmed, may be null
     * @return the trimmed String,
     *  {@code null} if only chars &lt;= 32, empty or null String input
     */
    public static String trimToNull(final String str) {
        final String ts = trim(str);
        return isNullOrEmpty(ts) ? null : ts;
    }

    /**
     * <p>Removes control characters (char &lt;= 32) from both
     * ends of this String returning an empty String ("") if the String
     * is empty ("") after the trim or if it is {@code null}.
     *
     * <p>The String is trimmed using {@link String#trim()}.
     * Trim removes start and end characters &lt;= 32.
     *
     * <pre>
     * StringUtils.trimToEmpty(null)          = ""
     * StringUtils.trimToEmpty("")            = ""
     * StringUtils.trimToEmpty("     ")       = ""
     * StringUtils.trimToEmpty("abc")         = "abc"
     * StringUtils.trimToEmpty("    abc    ") = "abc"
     * </pre>
     *
     * @param str  the String to be trimmed, may be null
     * @return the trimmed String, or an empty String if {@code null} input
     */
    public static String trimToEmpty(final String str) {
        return str == null ? EMPTY : str.trim();
    }

    /**
     * <p>Removes control characters (char &lt;= 32) from both
     * ends of this String, handling {@code null} by returning
     * {@code null}.</p>
     *
     * <p>The String is trimmed using {@link String#trim()}.
     * Trim removes start and end characters &lt;= 32.
     *
     * <p>To trim your choice of characters, use the
     *
     * <pre>
     * StringUtils.trim(null)          = null
     * StringUtils.trim("")            = ""
     * StringUtils.trim("     ")       = ""
     * StringUtils.trim("abc")         = "abc"
     * StringUtils.trim("    abc    ") = "abc"
     * </pre>
     *
     * @param str  the String to be trimmed, may be null
     * @return the trimmed string, {@code null} if null String input
     */
    public static String trim(final String str) {
        return str == null ? null : str.trim();
    }

    /**
     * Return string with only numbers
     *
     * @param string the string to test.
     * @return String with only numbers.
     */
    @NotNull
    public static String getOnlyNumbers(@Nullable final String string) {
        if (string != null && !string.isEmpty())
            return string.replaceAll("[^0-9]*", "");
        return "";
    }

    /**
     * Return string with only letters
     *
     * @param string the string to test.
     * @return String with only letters.
     */
    @NotNull
    public static String getOnlyLetters(@Nullable final String string) {
        if (string != null && !string.isEmpty())
            return stripAccents(string).replaceAll("[^A-Z]*", "");
        return "";
    }

    /**
     * Returns only alpha and digits from the string.
     *
     * @param string a string.
     * @return String with only alpha and digits.
     */
    @NotNull
    public static String getOnlyAlphaAndDigits(@NotNull final String string) {
        return ALPHA_AND_DIGITS_MATCHER.retainFrom(string);
    }

    /**
     * Remove accents.
     *
     * @param string the string to test.
     * @return String without accents.
     */
    @NotNull
    public static String stripAccents(@NotNull final String string) {
        return org.apache.commons.lang3.StringUtils.stripAccents(string);
    }

    /**
     * Remove letters that contains accents.
     *
     * @param string the string to test.
     * @return String without letters that contain accents.
     */
    @NotNull
    public static String stripCharactersWithAccents(@NotNull String string) {
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
