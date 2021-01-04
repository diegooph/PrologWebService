package br.com.zalf.prolog.webservice.frota.checklist.mudancaestrutura;

import br.com.zalf.prolog.webservice.commons.FonteDataHora;
import br.com.zalf.prolog.webservice.commons.gson.GsonUtils;
import br.com.zalf.prolog.webservice.commons.questoes.Alternativa;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.PostgresUtils;
import br.com.zalf.prolog.webservice.commons.util.date.Now;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.frota.checklist.OLD.AlternativaChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.OLD.Checklist;
import br.com.zalf.prolog.webservice.frota.checklist.OLD.ModeloChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.OLD.PerguntaRespostaChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.NovoChecklistHolder;
import br.com.zalf.prolog.webservice.frota.checklist.model.TipoChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.insercao.ChecklistAlternativaResposta;
import br.com.zalf.prolog.webservice.frota.checklist.model.insercao.ChecklistInsercao;
import br.com.zalf.prolog.webservice.frota.checklist.model.insercao.ChecklistResposta;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.AnexoMidiaChecklistEnum;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.realizacao.*;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;
import br.com.zalf.prolog.webservice.gente.colaborador.model.Colaborador;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
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
    private static final int VERSION_CODE_APP_NOVA_ESTRUTURA = 94;

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
    public static Checklist toChecklistAntigo(@NotNull final ChecklistInsercao checklist) {
        final Checklist checklistAntigo = new Checklist();
        checklistAntigo.setCodModelo(checklist.getCodModelo());
        checklistAntigo.setCodVersaoModeloChecklist(checklist.getCodVersaoModeloChecklist());
        final LocalDateTime now = Now.getLocalDateTimeUtc();
        checklistAntigo.setData(now);
        checklistAntigo.setDataHoraImportadoProLog(now);
        checklistAntigo.setPlacaVeiculo(checklist.getPlacaVeiculo());
        checklistAntigo.setTipo(checklist.getTipo().asChar());
        checklistAntigo.setKmAtualVeiculo(checklist.getKmColetadoVeiculo());
        checklistAntigo.setTempoRealizacaoCheckInMillis(checklist.getTempoRealizacaoCheckInMillis());
        final Colaborador colaborador = new Colaborador();
        colaborador.setCodigo(checklist.getCodColaborador());
        colaborador.setCpf(getCpfByCodColaborador(checklist.getCodColaborador()));
        checklistAntigo.setColaborador(colaborador);

        // Conversão das respostas.
        final List<PerguntaRespostaChecklist> respostas = new ArrayList<>();
        checklist.getRespostas()
                .forEach(novaPergunta -> {
                    final PerguntaRespostaChecklist resposta = new PerguntaRespostaChecklist();
                    resposta.setCodigo(novaPergunta.getCodPergunta());
                    final List<AlternativaChecklist> alternativas = new ArrayList<>();
                    novaPergunta
                            .getAlternativasRespostas()
                            .forEach(novaAlternativa -> {
                                final AlternativaChecklist alternativa = new AlternativaChecklist();
                                alternativa.setSelected(novaAlternativa.isAlternativaSelecionada());
                                alternativa.setCodigo(novaAlternativa.getCodAlternativa());
                                if (novaAlternativa.isTipoOutros()) {
                                    alternativa.setTipo(Alternativa.TIPO_OUTROS);
                                    alternativa.setRespostaOutros(novaAlternativa.getRespostaTipoOutros());
                                }
                                alternativas.add(alternativa);
                            });
                    resposta.setAlternativasResposta(alternativas);
                    respostas.add(resposta);
                });
        checklistAntigo.setListRespostas(respostas);

        // Antes de retornar, calcula as quantidades.
        checklistAntigo.calculaQtdOkOrNok();
        return checklistAntigo;
    }

    @NotNull
    public static ModeloChecklistRealizacao toEstruturaNovaRealizacaoModelo(@NotNull final NovoChecklistHolder novo) {
        final VeiculoChecklistRealizacao veiculo = new VeiculoChecklistRealizacao(
                // Atualmente, só é usado na integração com a Avilan, então não temos código do veículo.
                // De qualquer modo, mantemos o if, vai que acabe sendo usado em outro local depois que tenha o código.
                novo.getVeiculo().getCodigo() != null ? novo.getVeiculo().getCodigo() : -1L,
                novo.getVeiculo().getPlaca(),
                novo.getVeiculo().getKmAtual());

        final List<PerguntaRealizacaoChecklist> perguntas = new ArrayList<>();
        for (final PerguntaRespostaChecklist pAntiga : novo.getListPerguntas()) {
            final List<AlternativaRealizacaoChecklist> alternativas = pAntiga
                    .getAlternativasResposta()
                    .stream()
                    .map(a -> new AlternativaRealizacaoChecklist(
                            a.getCodigo(),
                            a.getAlternativa(),
                            a.isTipoOutros(),
                            a.getOrdemExibicao(),
                            a.getPrioridade(),
                            AnexoMidiaChecklistEnum.BLOQUEADO))
                    .collect(Collectors.toList());
            perguntas.add(new PerguntaRealizacaoChecklist(
                    pAntiga.getCodigo(),
                    pAntiga.getPergunta(),
                    pAntiga.getCodImagem(),
                    pAntiga.getUrl(),
                    pAntiga.getOrdemExibicao(),
                    pAntiga.isSingleChoice(),
                    AnexoMidiaChecklistEnum.BLOQUEADO,
                    alternativas));
        }

        return new ModeloChecklistRealizacao(
                novo.getCodigoModeloChecklist(),
                -1L,
                novo.getNomeModeloChecklist() != null ? novo.getNomeModeloChecklist() : "",
                novo.getCodUnidaedModeloChecklist() != null ? novo.getCodUnidaedModeloChecklist() : -1L,
                veiculo,
                perguntas);
    }

    @NotNull
    public static NovoChecklistHolder toEstruturaAntigaRealizacaoModelo(@NotNull final ModeloChecklistRealizacao modelo) {
        final Veiculo veiculo = new Veiculo();
        veiculo.setCodigo(modelo.getVeiculoRealizacao().getCodVeiculo());
        veiculo.setPlaca(modelo.getVeiculoRealizacao().getPlacaVeiculo());
        veiculo.setKmAtual(modelo.getVeiculoRealizacao().getKmAtualVeiculo());

        final List<PerguntaRespostaChecklist> perguntas = new ArrayList<>();
        for (final PerguntaRealizacaoChecklist pAntiga : modelo.getPerguntas()) {
            final List<AlternativaChecklist> alternativas = pAntiga
                    .getAlternativas()
                    .stream()
                    .map(a -> AlternativaChecklist.create(
                            a.getCodigo(),
                            a.getDescricao(),
                            a.isTipoOutros(),
                            a.getOrdemExibicao(),
                            a.getPrioridade()))
                    .collect(Collectors.toList());
            perguntas.add(PerguntaRespostaChecklist.create(
                    pAntiga.getCodigo(),
                    pAntiga.getDescricao(),
                    pAntiga.getCodImagem(),
                    pAntiga.getUrlImagem(),
                    pAntiga.getOrdemExibicao(),
                    pAntiga.isSingleChoice(),
                    alternativas));
        }

        final NovoChecklistHolder novo = new NovoChecklistHolder();
        novo.setCodUnidaedModeloChecklist(modelo.getCodUnidadeModelo());
        novo.setCodigoModeloChecklist(modelo.getCodModelo());
        novo.setCodigoVersaoAtualModeloChecklist(modelo.getCodVersaoModelo());
        novo.setNomeModeloChecklist(modelo.getNomeModelo());
        novo.setVeiculo(veiculo);
        novo.setListPerguntas(perguntas);
        return novo;
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
    public static ChecklistInsercao toChecklistInsercao(@NotNull final Checklist antigo,
                                                        @NotNull final LocalDateTime dataHoraRealizacao,
                                                        @NotNull final Integer versaoApp) {
        final DadosChecklistInsercaoEstrutraSuporte dados = getDadosChecklistInsercao(
                antigo.getColaborador().getCpf(),
                antigo.getPlacaVeiculo());

        return new ChecklistInsercao(
                dados.getCodUnidadeColaborador(),
                antigo.getCodModelo(),
                antigo.getCodVersaoModeloChecklist(),
                dados.getCodColaborador(),
                dados.getCodVeiculo(),
                antigo.getPlacaVeiculo(),
                TipoChecklist.fromChar(antigo.getTipo()),
                antigo.getKmAtualVeiculo(),
                null,
                antigo.getTempoRealizacaoCheckInMillis(),
                convertRespostas(antigo.getListRespostas()),
                dataHoraRealizacao,
                FonteDataHora.SERVIDOR,
                versaoApp,
                versaoApp,
                null,
                null,
                0,
                0,
                0,
                0);
    }

    @NotNull
    public static DadosChecklistInsercaoEstrutraSuporte getDadosChecklistInsercao(@NotNull final Long cpfColaborador,
                                                                                  @NotNull final String placa) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            final Long codVeiculo = internalGetCodVeiculoByPlaca(conn, placa);
            final Pair<Long, Long> pairUnidadeCodColaborador = internalGetCodColaboradorByCpf(conn, cpfColaborador);
            return new DadosChecklistInsercaoEstrutraSuporte(
                    pairUnidadeCodColaborador.getLeft(),
                    pairUnidadeCodColaborador.getRight(),
                    codVeiculo);
        } catch (final Throwable throwable) {
            Log.e(TAG, "Erro ao buscar dados de checklist inserção");
            throw new RuntimeException(throwable);
        } finally {
            DatabaseConnection.close(conn);
        }
    }

    @NotNull
    public static Long getCodVeiculoByPlaca(@NotNull final String placa) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            return internalGetCodVeiculoByPlaca(conn, placa);
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao buscar código do veículo pela placa: " + placa);
            throw new RuntimeException(t);
        } finally {
            DatabaseConnection.close(conn);
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
    private static Long getCpfByCodColaborador(@NotNull final Long codColaborador) {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.prepareStatement("SELECT CD.CPF FROM COLABORADOR_DATA CD WHERE CD.CODIGO = ?;");
            stmt.setLong(1, codColaborador);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return rSet.getLong("CPF");
            } else {
                throw new RuntimeException("Erro ao buscar CPF pelo código: " + codColaborador);
            }
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DatabaseConnection.close(conn, stmt, rSet);
        }
    }

    @NotNull
    private static Pair<Long, Long> internalGetCodColaboradorByCpf(@NotNull final Connection conn,
                                                                   @NotNull final Long cpfColaborador) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            // Como o CPF ainda é PK, o busca só por ele já basta.
            stmt = conn.prepareStatement("SELECT CD.COD_UNIDADE, CD.CODIGO FROM COLABORADOR_DATA CD WHERE CD.CPF = ?;");
            stmt.setLong(1, cpfColaborador);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return Pair.of(
                        rSet.getLong("COD_UNIDADE"),
                        rSet.getLong("CODIGO"));
            } else {
                throw new RuntimeException();
            }
        } finally {
            DatabaseConnection.close(stmt, rSet);
        }
    }

    @NotNull
    private static Long internalGetCodVeiculoByPlaca(@NotNull final Connection conn,
                                                     @NotNull final String placa) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            // Como a placa ainda é PK, a busca só pela placa já basta.
            stmt = conn.prepareStatement("SELECT VD.CODIGO FROM VEICULO_DATA VD WHERE VD.PLACA = ?;");
            stmt.setString(1, placa);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return rSet.getLong("CODIGO");
            } else {
                return -1L;
            }
        } finally {
            DatabaseConnection.close(stmt, rSet);
        }
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
            // Se a versão do modelo não for encontrada, será retornado 1.
            stmt = conn.prepareStatement("SELECT * FROM FUNC_CHECKLIST_ENCONTRA_VERSAO_MODELO(" +
                    "F_COD_MODELO_CHECKLIST   := ?, " +
                    "F_PERGUNTAS_ALTERNATIVAS := ?) AS COD_VERSAO;");
            stmt.setLong(1, codModeloChecklist);
            stmt.setObject(2, PostgresUtils.toJsonb(GsonUtils.getGson().toJson(checklistJson)));
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
        return checklist.getCodVersaoModeloChecklist() != null
                // Para os checklist offline pendentes de sincronia que existem no BD local do app quando o mesmo
                // atualizou e teve seu BD local migrado, a versão do modelo recebida será 0 e não null. Pois esse
                // foi o valor assumido como default para a coluna no App.
                && checklist.getCodVersaoModeloChecklist() > 0;
    }

    public static boolean isAppNovaEstruturaChecklist(@Nullable final Integer versaoApp) {
        return versaoApp != null && versaoApp >= VERSION_CODE_APP_NOVA_ESTRUTURA;
    }

    @NotNull
    private static List<ChecklistResposta> convertRespostas(@NotNull final List<PerguntaRespostaChecklist> antigas) {
        final List<ChecklistResposta> respostas = new ArrayList<>();
        antigas
                .forEach(respostaAntiga -> {
                    final List<ChecklistAlternativaResposta> alternativas = new ArrayList<>();
                    final ChecklistResposta resposta = new ChecklistResposta(respostaAntiga.getCodigo(), alternativas);
                    respostaAntiga
                            .getAlternativasResposta()
                            .forEach(alternativaAntiga -> {
                                alternativas.add(new ChecklistAlternativaResposta(
                                        alternativaAntiga.getCodigo(),
                                        alternativaAntiga.isSelected(),
                                        alternativaAntiga.isTipoOutros(),
                                        alternativaAntiga.getRespostaOutros()));
                            });
                    respostas.add(resposta);
                });
        return respostas;
    }

}
