package br.com.zalf.prolog.webservice.frota.checklist.offline.model;

import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
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
                rSet.getString("URL_IMAGEM"),
                rSet.getInt("PERGUNTA_ORDEM_EXIBICAO"),
                rSet.getBoolean("SINGLE_CHOICE"),
                alternativas);
    }

    @NotNull
    public static ModeloChecklistOffline createModeloChecklistOffline(
            @NotNull final Long codUnidadeModeloChecklist,
            @NotNull final Long codModeloCheklist,
            @NotNull final String nomeModeloChecklist,
            @NotNull final List<CargoChecklistOffline> cargosLiberados,
            @NotNull final List<TipoVeiculoChecklistOffline> tiposVeiculosLiberados,
            @NotNull final List<PerguntaModeloChecklistOffline> perguntas) {
        return new ModeloChecklistOffline(
                codModeloCheklist,
                nomeModeloChecklist,
                codUnidadeModeloChecklist,
                cargosLiberados,
                tiposVeiculosLiberados,
                perguntas);
    }

    @NotNull
    public static CargoChecklistOffline createCargoChecklistOffline(
            @NotNull final ResultSet rSet) throws SQLException {
        return new CargoChecklistOffline(rSet.getLong("COD_CARGO"));
    }

    @NotNull
    public static TipoVeiculoChecklistOffline createTipoVeiculoChecklistOffline(
            @NotNull final ResultSet rSet) throws SQLException {
        return new TipoVeiculoChecklistOffline(rSet.getLong("COD_TIPO_VEICULO"));
    }

    @NotNull
    public static ColaboradorChecklistOffline createColaboradorChecklistOffline(
            @NotNull final ResultSet rSet) throws SQLException {
        return new ColaboradorChecklistOffline(
                rSet.getLong("COD_EMPRESA_COLABORADOR"),
                rSet.getLong("COD_REGIONAL_COLABORADOR"),
                rSet.getLong("COD_UNIDADE_COLABORADOR"),
                rSet.getLong("COD_COLABORADOR"),
                rSet.getString("CPF_COLABORADOR"),
                rSet.getObject("DATA_NASCIMENTO", LocalDate.class),
                rSet.getLong("COD_CARGO_COLABORADOR"),
                rSet.getInt("COD_PERMISSAO_COLABORADOR"));
    }
}
