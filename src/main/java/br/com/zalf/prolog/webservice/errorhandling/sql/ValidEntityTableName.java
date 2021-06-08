package br.com.zalf.prolog.webservice.errorhandling.sql;

import lombok.AllArgsConstructor;
import lombok.Getter;


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
    public static ValidEntityTableName fromString(@NotNull final String str) {
        return Stream.of(ValidEntityTableName.values())
                .filter(e -> e.tableName.equalsIgnoreCase(str))
                .findFirst()
                .orElseThrow();
    }
}
