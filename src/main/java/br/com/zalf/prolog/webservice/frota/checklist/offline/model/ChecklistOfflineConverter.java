package br.com.zalf.prolog.webservice.frota.checklist.offline.model;

import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 16/03/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class ChecklistOfflineConverter {

    private ChecklistOfflineConverter() {
        throw new IllegalStateException(ChecklistOfflineConverter.class.getSimpleName() + " cannot be instanciated!");
    }

    @NotNull
    public static AlternativaModeloChecklistOffline createAlternativaModeloChecklistOffline(
            @NotNull final ResultSet rSet) throws SQLException {
        return new AlternativaModeloChecklistOffline(
                rSet.getLong("COD_ALTERNATIVA"),
                rSet.getString("DESCRICAO_ALTERNATIVA"),
                rSet.getBoolean("TIPO_OUTROS"),
                rSet.getInt("ALTERNATIVA_ORDEM_EXIBICAO"));
    }

    @NotNull
    public static PerguntaModeloChecklistOffline createPerguntaModeloChecklistOffline(
            @NotNull final ResultSet rSet,
            @NotNull final List<AlternativaModeloChecklistOffline> alternativas) throws SQLException {
        return new PerguntaModeloChecklistOffline(
                rSet.getLong("COD_PERGUNTA"),
                rSet.getString("DESCRICAO_PERGUNTA"),
                rSet.getLong("COD_IMAGEM"),
                rSet.getString("URL_IMAGEM") == null ? "" : rSet.getString("URL_IMAGEM"),
                rSet.getInt("PERGUNTA_ORDEM_EXIBICAO"),
                rSet.getBoolean("SINGLE_CHOICE"),
                alternativas);
    }

    @NotNull
    public static ModeloChecklistOffline createModeloChecklistOffline(
            @NotNull final ResultSet rSet,
            @NotNull final List<PerguntaModeloChecklistOffline> perguntas) throws SQLException {
        return new ModeloChecklistOffline(
                rSet.getLong("COD_MODELO_CHECKLIST"),
                rSet.getString("NOME_MODELO_CHECKLIST"),
                rSet.getLong("COD_UNIDADE_MODELO_CHECKLIST"),
                new ArrayList<>(),
                new ArrayList<>(),
                perguntas);
    }
}
