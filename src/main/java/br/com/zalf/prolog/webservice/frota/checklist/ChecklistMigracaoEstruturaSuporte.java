package br.com.zalf.prolog.webservice.frota.checklist;

import br.com.zalf.prolog.webservice.commons.gson.GsonUtils;
import br.com.zalf.prolog.webservice.commons.questoes.Alternativa;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.frota.checklist.OLD.Checklist;
import br.com.zalf.prolog.webservice.frota.checklist.OLD.ModeloChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.NovoChecklistHolder;
import br.com.zalf.prolog.webservice.frota.checklist.model.insercao.ChecklistAlternativaResposta;
import br.com.zalf.prolog.webservice.frota.checklist.model.insercao.ChecklistInsercao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.realizacao.ModeloChecklistRealizacao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.realizacao.ModeloChecklistSelecao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.realizacao.VeiculoChecklistSelecao;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created on 2019-08-15
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class ChecklistMigracaoEstruturaSuporte {
    private static final String TAG = ChecklistMigracaoEstruturaSuporte.class.getSimpleName();
    private static final int VERSION_CODE_APP_NOVA_ESTRUTURA = 83;

    @NotNull
    public static Long getCodVersaoAtualModeloChecklist(@NotNull final Long codModelo)
            throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            return internalGetCodVersaoAtualModeloChecklist(conn, codModelo);
        } finally {
            DatabaseConnection.close(conn);
        }
    }

    @NotNull
    public static ModeloChecklistRealizacao toEstruturaNovaRealizacaoModelo(@NotNull final NovoChecklistHolder novo) {
        // TODO: Implementar converter.
        return null;
    }

    @NotNull
    public static NovoChecklistHolder toEstruturaAntigaRealizacaoModelo(@NotNull final ModeloChecklistRealizacao modelo) {
        // TODO: Implementar converter.
        return null;
    }

    @NotNull
    public static List<ModeloChecklistSelecao> toEstruturaNovaSelecaoModelo(
            @NotNull final Map<ModeloChecklist, List<String>> map) {
        final List<ModeloChecklistSelecao> modelos = new ArrayList<>(map.size());
        map.forEach((modeloOld, placas) -> {
            final List<VeiculoChecklistSelecao> veiculos = placas
                    .stream()
                    .map(p -> new VeiculoChecklistSelecao(-1L, p, -1))
                    .collect(Collectors.toList());
            final ModeloChecklistSelecao modeloNew = new ModeloChecklistSelecao(
                    modeloOld.getCodigo(),
                    -1L,
                    modeloOld.getCodUnidade(),
                    modeloOld.getNome(),
                    veiculos);
            modelos.add(modeloNew);
        });
        return modelos;
    }

    @NotNull
    public static Map<ModeloChecklist, List<String>> toEstruturaAntigaSelecaoModelo(
            @NotNull final List<ModeloChecklistSelecao> modelos) {
        final Map<ModeloChecklist, List<String>> map = new LinkedHashMap<>();
        modelos.forEach(modeloNew -> {
            final ModeloChecklist modeloOld = new ModeloChecklist();
            modeloOld.setCodigo(modeloNew.getCodModelo());
            modeloOld.setCodUnidade(modeloNew.getCodUnidadeModelo());
            modeloOld.setNome(modeloNew.getNomeModelo());
            final List<String> placas = modeloNew
                    .getVeiculosVinculadosModelo()
                    .stream()
                    .map(VeiculoChecklistSelecao::getPlacaVeiculo)
                    .collect(Collectors.toList());
            map.put(modeloOld, placas);
        });
        return map;
    }

    @NotNull
    public static Long getCodVeiculoByPlaca(@NotNull final String placa) {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            // Como a placa ainda é PK, a busca só pela placa já basta.
            stmt = conn.prepareStatement("SELECT VD.CODIGO FROM VEICULO_DATA VD WHERE VD.PLACA = ?;");
            stmt.setString(1, placa);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return rSet.getLong("COD");
            } else {
                throw new RuntimeException();
            }
        } catch (final Throwable throwable) {
            Log.e(TAG, "Erro ao buscar código do veículo pela placa: " + placa);
            throw new RuntimeException(throwable);
        } finally {
            DatabaseConnection.close(conn, stmt, rSet);
        }
    }

    @NotNull
    public static Long encontraCodVersaoModeloChecklist(@NotNull final Connection conn,
                                                        @NotNull final ChecklistInsercao checklist) throws Throwable {
        final List<ChecklistJson> checklistJson = createChecklistJson(checklist);
        return interalEncontraCodVersaoModeloChecklist(conn, checklist.getCodModelo(), checklistJson);
    }

    @NotNull
    public static Long encontraCodVersaoModeloChecklist(@NotNull final Connection conn,
                                                        @NotNull final Checklist checklist) throws Throwable {
        final List<ChecklistJson> checklistJson = createChecklistJson(checklist);
        return interalEncontraCodVersaoModeloChecklist(conn, checklist.getCodModelo(), checklistJson);
    }

    @NotNull
    private static Long internalGetCodVersaoAtualModeloChecklist(@NotNull final Connection conn,
                                                                 @NotNull final Long codModelo) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("SELECT CM.COD_VERSAO_ATUAL " +
                    "FROM CHECKLIST_MODELO_DATA CM WHERE CM.CODIGO = ?;");
            stmt.setLong(1, codModelo);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return rSet.getLong("COD_VERSAO_ATUAL");
            } else {
                throw new SQLException("Erro ao buscar versão atual do modelo de checklist para o modelo: "
                        + codModelo);
            }
        } finally {
            DatabaseConnection.close(stmt, rSet);
        }
    }

    @NotNull
    private static Long interalEncontraCodVersaoModeloChecklist(
            @NotNull final Connection conn,
            @NotNull final Long codModeloChecklist,
            @NotNull final List<ChecklistJson> checklistJson) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("SELECT * FROM FUNC_CHECKLIST_ENCONTRA_VERSAO_MODELO(" +
                    "F_COD_MODELO_CHECKLIST   := ?, " +
                    "F_PERGUNTAS_ALTERNATIVAS := ?) AS COD_VERSAO;");
            stmt.setLong(1, codModeloChecklist);
            stmt.setString(2, GsonUtils.getGson().toJson(checklistJson));
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return rSet.getLong("COD_VERSAO");
            } else {
                throw new SQLException("Erro ao buscar versão do modelo de checklist para o modelo: "
                        + codModeloChecklist);
            }
        } finally {
            DatabaseConnection.close(stmt, rSet);
        }
    }

    @NotNull
    private static List<ChecklistJson> createChecklistJson(@NotNull final ChecklistInsercao checklist) {
        final List<ChecklistJson> jsons = new ArrayList<>();
        checklist
                .getRespostas()
                .forEach(pergunta -> {
                    final List<Long> alternativasPergunta = pergunta
                            .getAlternativasRespostas()
                            .stream()
                            .map(ChecklistAlternativaResposta::getCodAlternativa)
                            .collect(Collectors.toList());
                    jsons.add(new ChecklistJson(checklist.getCodModelo(), pergunta.getCodPergunta(), alternativasPergunta));
                });
        return jsons;
    }

    @NotNull
    private static List<ChecklistJson> createChecklistJson(@NotNull final Checklist checklist) {
        final List<ChecklistJson> jsons = new ArrayList<>();
        checklist
                .getListRespostas()
                .forEach(pergunta -> {
                    final List<Long> alternativasPergunta = pergunta
                            .getAlternativasResposta()
                            .stream()
                            .map(Alternativa::getCodigo)
                            .collect(Collectors.toList());
                    jsons.add(new ChecklistJson(checklist.getCodModelo(), pergunta.getCodigo(), alternativasPergunta));
                });
        return jsons;
    }

    public static boolean isAppNovaEstruturaChecklist(@NotNull final Checklist checklist) {
        return checklist.getCodVersaoModeloChecklist() != null;
    }

    public static boolean isAppNovaEstruturaChecklist(@NotNull final ChecklistInsercao checklist) {
        return checklist.getCodVersaoModeloChecklist() != null;
    }

    public static boolean isAppNovaEstruturaChecklist(@Nullable final Integer versaoApp) {
        return versaoApp != null && versaoApp >= VERSION_CODE_APP_NOVA_ESTRUTURA;
    }
}
