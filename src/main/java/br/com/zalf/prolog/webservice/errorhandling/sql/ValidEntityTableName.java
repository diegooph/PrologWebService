package br.com.zalf.prolog.webservice.errorhandling.sql;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created on 2021-06-08
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@AllArgsConstructor
@Getter
public enum ValidEntityTableName {

    AFERICAO("afericao"),
    CARGO("cargo"),
    CHECKLIST("checklist"),
    COLABORADOR("colaborador"),
    DASHBOARD("dashboard"),
    ESCALA("escala"),
    PRONTUARIO("prontuario"),
    QUIZ("quiz"),
    RELATO("relato"),
    SOCORRO("socorro"),
    TREINAMENTO("treinamento"),
    INDICADOR("indicador"),
    INTEGRACAO("integracao"),
    INTERNO("interno"),
    JORNADA("jornada"),
    MOVIMENTACAO("movimentacao"),
    PNEU("pneu"),
    PRE_CONTRACHEQUE("pre_contracheque"),
    PRODUTIVIDADE("produtividade"),
    SUPORTE("suporte"),
    TRANSFERENCIA("transferencia"),
    VEICULO("veiculo");

    private final String tableName;

    @NotNull
    public static String getTableNamesConcatenated(@NotNull final String delimiter) {
        return Stream.of(ValidEntityTableName.values())
                .map(ValidEntityTableName::getTableName)
                .collect(Collectors.joining(delimiter));
    }

    @Nullable
    public static ValidEntityTableName getTableNameFromMessage(@NotNull final String phrase) {
        final Pattern validPatternOfEntities = Pattern.compile("(" + getTableNamesConcatenated("|") + ")");
        final Matcher matcher = validPatternOfEntities.matcher(phrase);
        if (matcher.find()) {
            return fromString(matcher.group(1));
        }
        return null;
    }

    @NotNull
    public static ValidEntityTableName fromString(@NotNull final String str) {
        return Stream.of(ValidEntityTableName.values())
                .filter(e -> e.tableName.equalsIgnoreCase(str))
                .findFirst()
                .orElseThrow();
    }
}
