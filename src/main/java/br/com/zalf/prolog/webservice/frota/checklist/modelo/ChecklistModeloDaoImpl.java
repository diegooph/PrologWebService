package br.com.zalf.prolog.webservice.frota.checklist.modelo;

import br.com.zalf.prolog.webservice.commons.gson.GsonUtils;
import br.com.zalf.prolog.webservice.commons.imagens.Galeria;
import br.com.zalf.prolog.webservice.commons.imagens.ImagemProlog;
import br.com.zalf.prolog.webservice.commons.util.database.PostgresUtils;
import br.com.zalf.prolog.webservice.commons.util.database.SqlType;
import br.com.zalf.prolog.webservice.commons.util.datetime.Now;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.frota.checklist.model.TipoChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.AlternativaModeloChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.ModeloChecklistListagem;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.PerguntaModeloChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.edicao.*;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.insercao.ModeloChecklistInsercao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.insercao.PerguntaModeloChecklistInsercao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.insercao.ResultInsertModeloChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.realizacao.*;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.visualizacao.ModeloChecklistVisualizacao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.visualizacao.PerguntaModeloChecklistVisualizacao;
import br.com.zalf.prolog.webservice.frota.checklist.offline.DadosChecklistOfflineChangedListener;
import br.com.zalf.prolog.webservice.frota.veiculo.model.TipoVeiculo;
import br.com.zalf.prolog.webservice.gente.colaborador.model.Cargo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static br.com.zalf.prolog.webservice.commons.util.database.StatementUtils.bindValueOrNull;

public final class ChecklistModeloDaoImpl extends DatabaseConnection implements ChecklistModeloDao {

    public ChecklistModeloDaoImpl() {

    }

    @NotNull
    @Override
    public ResultInsertModeloChecklist insertModeloChecklist(
            @NotNull final ModeloChecklistInsercao modeloChecklist,
            @NotNull final DadosChecklistOfflineChangedListener checklistOfflineListener,
            final boolean statusAtivo,
            @NotNull final String userToken) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            // Essa function insere o modelo e já cria a primeira versão (1).
            stmt = conn.prepareStatement("SELECT * FROM FUNC_CHECKLIST_INSERT_MODELO_CHECKLIST_INFOS(" +
                                                 "F_COD_UNIDADE_MODELO := ?," +
                                                 "F_NOME_MODELO        := ?," +
                                                 "F_STATUS_ATIVO       := ?," +
                                                 "F_COD_CARGOS         := ?," +
                                                 "F_COD_TIPOS_VEICULOS := ?," +
                                                 "F_DATA_HORA_ATUAL    := ?," +
                                                 "F_TOKEN_COLABORADOR  := ?);");
            stmt.setLong(1, modeloChecklist.getCodUnidade());
            stmt.setString(2, modeloChecklist.getNome());
            stmt.setBoolean(3, statusAtivo);
            stmt.setArray(4, PostgresUtils.listToArray(
                    conn,
                    SqlType.BIGINT,
                    modeloChecklist.getCargosLiberados()));
            stmt.setArray(5, PostgresUtils.listToArray(
                    conn,
                    SqlType.BIGINT,
                    modeloChecklist.getTiposVeiculoLiberados()));
            stmt.setObject(6, Now.getOffsetDateTimeUtc());
            stmt.setString(7, userToken);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                final long codModeloChecklistInserido = rSet.getLong("COD_MODELO_CHECKLIST");
                final long codVersaoModeloChecklist = rSet.getLong("COD_VERSAO_MODELO_CHECKLIST");
                if (codModeloChecklistInserido <= 0 || codVersaoModeloChecklist <= 0) {
                    throw new SQLException("Erro ao inserir modelo de checklist:\n" +
                                                   "codModeloChecklistInserido: " + codModeloChecklistInserido);
                }

                insertPerguntasEAlternativasCadastro(conn,
                                                     modeloChecklist,
                                                     codModeloChecklistInserido,
                                                     codVersaoModeloChecklist);

                // Devemos notificar que uma inserção de modelo de checklist foi realizada.
                checklistOfflineListener.onInsertModeloChecklist(conn, codModeloChecklistInserido);
                conn.commit();
                return new ResultInsertModeloChecklist(
                        codModeloChecklistInserido,
                        codVersaoModeloChecklist);
            } else {
                throw new SQLException("Não foi possível inserir o modelo de checklist para a unidade: "
                                               + modeloChecklist.getCodUnidade());
            }
        } catch (final Throwable t) {
            if (conn != null) {
                conn.rollback();
            }
            throw t;
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    public void updateModeloChecklist(
            @NotNull final Long codUnidade,
            @NotNull final Long codModelo,
            @NotNull final ModeloChecklistEdicao modeloChecklist,
            @NotNull final DadosChecklistOfflineChangedListener checklistOfflineListener,
            final boolean podeMudarCodigoContextoPerguntasEAlternativas,
            @NotNull final String userToken) throws Throwable {
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);

            final AnaliseMudancaModeloChecklist analiseModelo = realizaAnaliseMudancaModeloChecklist(
                    conn,
                    codModelo,
                    modeloChecklist);
            if (analiseModelo.isAlgoMudouNoModelo()) {
                if (analiseModelo.isDeveCriarNovaVersaoModelo()) {
                    criaNovaVersaoModelo(
                            conn,
                            modeloChecklist,
                            analiseModelo,
                            // Se o código de contexto não pode mudar, significa que iremos mudar apenas o variável e
                            // manter o mesmo de contexto em diferentes versões.
                            !podeMudarCodigoContextoPerguntasEAlternativas,
                            userToken);
                } else {
                    atualizaModeloChecklistInfosGerais(conn, codUnidade, codModelo, modeloChecklist);
                    for (final PerguntaModeloChecklistEdicao pergunta : modeloChecklist.getPerguntas()) {
                        atualizaPergunta(conn, codModelo, pergunta);
                        for (final AlternativaModeloChecklist alternativa : pergunta.getAlternativas()) {
                            atualizaAlternativa(conn, codModelo, alternativa);
                        }
                    }
                }
            } else {
                atualizaModeloChecklistInfosGerais(conn, codUnidade, codModelo, modeloChecklist);
            }

            // Notificamos o Listener que ouve atualização no modelo de checklist.
            checklistOfflineListener.onUpdateModeloChecklist(conn, codModelo);
            conn.commit();
        } catch (final Throwable t) {
            if (conn != null) {
                conn.rollback();
            }
            throw t;
        } finally {
            close(conn);
        }
    }

    @NotNull
    @Override
    public List<ModeloChecklistListagem> getModelosChecklistListagemByCodUnidade(@NotNull final Long codUnidade)
            throws Throwable {
        final List<ModeloChecklistListagem> modelosChecklistListagem = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_CHECKLIST_GET_LISTAGEM_MODELOS_CHECKLIST(" +
                                                 "F_COD_UNIDADE := ?);");
            stmt.setLong(1, codUnidade);
            rSet = stmt.executeQuery();
            Set<String> setCargos = new HashSet<>();
            Set<String> setTiposVeiculos = new HashSet<>();
            ModeloChecklistListagem modeloChecklistListagem = null;
            Long codModeloChecklistAnterior = null;
            while (rSet.next()) {
                final Long codModeloChecklistAtual = rSet.getLong("COD_MODELO");
                if (codModeloChecklistAnterior == null) {
                    codModeloChecklistAnterior = codModeloChecklistAtual;
                }
                if (!codModeloChecklistAtual.equals(codModeloChecklistAnterior)) {
                    modelosChecklistListagem.add(modeloChecklistListagem);
                    codModeloChecklistAnterior = codModeloChecklistAtual;
                    setCargos = new HashSet<>();
                    setTiposVeiculos = new HashSet<>();
                }
                setCargos.add(rSet.getString("NOME_CARGO"));
                setTiposVeiculos.add(rSet.getString("TIPO_VEICULO"));
                modeloChecklistListagem = ChecklistModeloConverter.createModeloChecklistListagem(
                        rSet,
                        codModeloChecklistAnterior,
                        setCargos,
                        setTiposVeiculos);
            }
            if (codModeloChecklistAnterior != null) {
                modelosChecklistListagem.add(modeloChecklistListagem);
            }
        } finally {
            close(conn, stmt, rSet);
        }
        return modelosChecklistListagem;
    }

    @NotNull
    @Override
    public ModeloChecklistVisualizacao getModeloChecklist(@NotNull final Long codUnidade,
                                                          @NotNull final Long codModelo) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT DISTINCT CM.NOME AS MODELO, "
                                                 + "CM.CODIGO AS COD_MODELO, "
                                                 + "CM.COD_VERSAO_ATUAL AS COD_VERSAO_ATUAL, "
                                                 + "CM.STATUS_ATIVO AS STATUS_ATIVO "
                                                 + "FROM CHECKLIST_MODELO CM "
                                                 + "WHERE CM.COD_UNIDADE = ? AND CM.CODIGO = ? "
                                                 + "ORDER BY MODELO");
            stmt.setLong(1, codUnidade);
            stmt.setLong(2, codModelo);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return new ModeloChecklistVisualizacao(
                        rSet.getLong("COD_MODELO"),
                        rSet.getLong("COD_VERSAO_ATUAL"),
                        codUnidade,
                        rSet.getString("MODELO"),
                        getTipoVeiculoByCodModeloChecklist(conn, codUnidade, codModelo),
                        getCargosByCodModelo(conn, codUnidade, codModelo),
                        getPerguntasModeloChecklist(conn, codUnidade, codModelo, rSet.getLong("COD_VERSAO_ATUAL")),
                        rSet.getBoolean("STATUS_ATIVO"));
            } else {
                throw new SQLException("Não foi possível buscar o modelo de checklist: "
                                               + "unidade: " + codUnidade + "\n"
                                               + "modelo: " + codModelo);
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    public void updateStatusAtivo(
            @NotNull final Long codUnidade,
            @NotNull final Long codModelo,
            final boolean statusAtivo,
            @NotNull final DadosChecklistOfflineChangedListener checklistOfflineListener) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.prepareCall("{CALL FUNC_CHECKLIST_UPDATE_STATUS_MODELO(" +
                                            "F_COD_UNIDADE  := ?," +
                                            "F_COD_MODELO   := ?," +
                                            "F_STATUS_ATIVO := ?)}");
            stmt.setLong(1, codUnidade);
            stmt.setLong(2, codModelo);
            stmt.setBoolean(3, statusAtivo);
            stmt.execute();
        } finally {
            close(conn, stmt);
        }
    }

    @NotNull
    @Override
    public List<ModeloChecklistVisualizacao> getModelosChecklistProLog() throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT " +
                                                 "  CMP.CODIGO, " +
                                                 "  CMP.NOME " +
                                                 "FROM CHECKLIST_MODELO_PROLOG CMP " +
                                                 "WHERE CMP.STATUS_ATIVO = TRUE;");
            rSet = stmt.executeQuery();
            final List<ModeloChecklistVisualizacao> modelos = new ArrayList<>();
            while (rSet.next()) {
                // TODO:
            }
            return modelos;
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public Long insertImagem(@NotNull final Long codEmpresa,
                             @NotNull final ImagemProlog imagemProLog) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("INSERT INTO CHECKLIST_GALERIA_IMAGENS(URL_IMAGEM, COD_EMPRESA) " +
                                                 "VALUES (?, ?) RETURNING COD_IMAGEM;");
            stmt.setString(1, imagemProLog.getUrlImagem());
            stmt.setLong(2, codEmpresa);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return rSet.getLong("COD_IMAGEM");
            } else {
                throw new SQLException("Erro ao inserir imagem na galeria da empresa: " + codEmpresa);
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    public List<String> getUrlImagensPerguntas(@NotNull final Long codUnidade,
                                               @NotNull final Long codFuncao) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT " +
                                                 "       DISTINCT CGI.URL_IMAGEM AS URL_IMAGEM " +
                                                 "FROM CHECKLIST_MODELO CM " +
                                                 "         JOIN CHECKLIST_PERGUNTAS CP ON CP" +
                                                 ".COD_VERSAO_CHECKLIST_MODELO = CM.COD_VERSAO_ATUAL " +
                                                 "         JOIN CHECKLIST_GALERIA_IMAGENS CGI ON CP.COD_IMAGEM = CGI" +
                                                 ".COD_IMAGEM " +
                                                 "         JOIN CHECKLIST_MODELO_FUNCAO CMF ON CM.CODIGO = CMF" +
                                                 ".COD_CHECKLIST_MODELO " +
                                                 "WHERE CM.COD_UNIDADE = ? " +
                                                 "  AND CMF.COD_FUNCAO = ? " +
                                                 "  AND CM.STATUS_ATIVO = TRUE;");
            stmt.setLong(1, codUnidade);
            stmt.setLong(2, codFuncao);
            rSet = stmt.executeQuery();
            final List<String> urls = new ArrayList<>();
            while (rSet.next()) {
                urls.add(rSet.getString("URL_IMAGEM"));
            }
            return urls;
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public Galeria getGaleriaImagensPublicas() throws Throwable {
        return getGaleria(null);
    }

    @NotNull
    @Override
    public Galeria getGaleriaImagensEmpresa(@NotNull final Long codEmpresa) throws Throwable {
        return getGaleria(codEmpresa);
    }

    @NotNull
    @Override
    public List<ModeloChecklistSelecao> getModelosSelecaoRealizacao(@NotNull final Long codUnidade,
                                                                    @NotNull final Long codCargo) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_CHECKLIST_GET_MODELOS_SELECAO_REALIZACAO(" +
                                                 "F_COD_UNIDADE := ?, " +
                                                 "F_COD_CARGO   := ?);");
            stmt.setLong(1, codUnidade);
            stmt.setLong(2, codCargo);
            rSet = stmt.executeQuery();
            ModeloChecklistSelecao modeloSelecao = null;
            if (rSet.next()) {
                final List<ModeloChecklistSelecao> modelos = new ArrayList<>();
                List<VeiculoChecklistSelecao> veiculosSelecao = new ArrayList<>();
                do {
                    if (modeloSelecao == null) {
                        modeloSelecao = ChecklistModeloConverter.createModeloChecklistSelecao(rSet, veiculosSelecao);
                        veiculosSelecao.add(ChecklistModeloConverter.createVeiculoChecklistSelecao(rSet));
                    } else {
                        if (modeloSelecao.getCodModelo().equals(rSet.getLong("COD_MODELO"))) {
                            veiculosSelecao.add(ChecklistModeloConverter.createVeiculoChecklistSelecao(rSet));
                        } else {
                            modelos.add(modeloSelecao);
                            veiculosSelecao = new ArrayList<>();
                            veiculosSelecao.add(ChecklistModeloConverter.createVeiculoChecklistSelecao(rSet));
                            modeloSelecao =
                                    ChecklistModeloConverter.createModeloChecklistSelecao(rSet, veiculosSelecao);
                        }
                    }
                } while (rSet.next());
                modelos.add(modeloSelecao);
                return modelos;
            } else {
                return Collections.emptyList();
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public ModeloChecklistRealizacao getModeloChecklistRealizacao(
            final @NotNull Long codModeloChecklist,
            final @NotNull Long codVeiculo,
            final @NotNull String placaVeiculo,
            final @NotNull TipoChecklist tipoChecklist) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_CHECKLIST_GET_MODELO_REALIZACAO(" +
                                                 "F_COD_MODELO_CHECKLIST   := ?," +
                                                 "F_COD_VEICULO_REALIZACAO := ?);");
            stmt.setLong(1, codModeloChecklist);
            stmt.setLong(2, codVeiculo);
            rSet = stmt.executeQuery();
            ModeloChecklistRealizacao modelo = null;
            PerguntaRealizacaoChecklist pergunta = null;
            final List<PerguntaRealizacaoChecklist> perguntas = new ArrayList<>();
            List<AlternativaRealizacaoChecklist> alternativas = new ArrayList<>();
            if (rSet.next()) {
                do {
                    if (pergunta == null) {
                        modelo = ChecklistModeloConverter.createModeloChecklistRealizacao(
                                rSet.getLong("COD_UNIDADE_MODELO_CHECKLIST"),
                                rSet.getLong("COD_MODELO_CHECKLIST"),
                                rSet.getLong("COD_VERSAO_MODELO_CHECKLIST"),
                                rSet.getString("NOME_MODELO_CHECKLIST"),
                                ChecklistModeloConverter.createVeiculoChecklistRealizacao(codVeiculo,
                                                                                          placaVeiculo,
                                                                                          rSet),
                                perguntas);
                        pergunta = ChecklistModeloConverter.createPerguntaRealizacaoChecklist(rSet, alternativas);
                        alternativas.add(ChecklistModeloConverter.createAlternativaRealizacaoChecklist(rSet));
                        perguntas.add(pergunta);
                    } else {
                        if (pergunta.getCodigo().equals(rSet.getLong("COD_PERGUNTA"))) {
                            // Mesma pergunta.
                            // Precisamos processar apenas a nova alternativa.
                            alternativas.add(ChecklistModeloConverter.createAlternativaRealizacaoChecklist(rSet));
                        } else {
                            // Trocou de pergunta.
                            alternativas = new ArrayList<>();
                            alternativas.add(ChecklistModeloConverter.createAlternativaRealizacaoChecklist(rSet));
                            pergunta = ChecklistModeloConverter.createPerguntaRealizacaoChecklist(rSet, alternativas);
                            perguntas.add(pergunta);
                        }
                    }
                } while (rSet.next());
                return modelo;
            } else {
                throw new IllegalStateException("Modelo de checklist não encontrado para o código: " + codModeloChecklist);
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }

    private void criaNovaVersaoModelo(@NotNull final Connection conn,
                                      @NotNull final ModeloChecklistEdicao modeloChecklist,
                                      @NotNull final AnaliseMudancaModeloChecklist analiseModelo,
                                      final boolean usarMesmoCodigoDeContexto,
                                      @NotNull final String userToken) throws Throwable {
        // 1 -> Geramos o novo código de versão do modelo.
        final Long novaVersaoModelo = geraNovoCodigoVersaoModelo(conn, modeloChecklist, userToken);
        for (final PerguntaModeloChecklistEdicao pergunta : modeloChecklist.getPerguntas()) {
            // 2 -> Agora iremos tratar cada caso de uma pergunta.
            if (pergunta instanceof PerguntaModeloChecklistEdicaoInsere) {
                // 2.1 -> Quando uma pergunta é nova, inserimos a pergunta, sem usar um código de contexto existente,
                // pois não temos um.
                final Long codPergunta = insertPerguntaChecklist(
                        conn,
                        modeloChecklist.getCodUnidade(),
                        modeloChecklist.getCodModelo(),
                        novaVersaoModelo,
                        pergunta,
                        false);
                for (final AlternativaModeloChecklist alternativa : pergunta.getAlternativas()) {
                    // 2.1.1 -> E então inserimos todas as alterantivas, também sem um código de contexto,
                    // pelo mesmo motivo.
                    insertAlternativaChecklist(
                            conn,
                            modeloChecklist.getCodUnidade(),
                            modeloChecklist.getCodModelo(),
                            novaVersaoModelo,
                            codPergunta,
                            alternativa,
                            false);
                }
            } else {
                // Pergunta está sendo atualizada.
                final AnaliseItemModeloChecklist analisePergunta = analiseModelo.getPergunta(pergunta.getCodigo());
                // 2.2 -> Se pergunta mudou de contexto, troca o código de contexto.
                final Long codPergunta = insertPerguntaChecklist(
                        conn,
                        modeloChecklist.getCodUnidade(),
                        modeloChecklist.getCodModelo(),
                        novaVersaoModelo,
                        pergunta,
                        usarMesmoCodigoDeContexto);
                for (final AlternativaModeloChecklist alternativa : pergunta.getAlternativas()) {
                    if (alternativa instanceof AlternativaModeloChecklistEdicaoInsere) {
                        insertAlternativaChecklist(
                                conn,
                                modeloChecklist.getCodUnidade(),
                                modeloChecklist.getCodModelo(),
                                novaVersaoModelo,
                                codPergunta,
                                alternativa,
                                false);
                    } else {
                        // 2.2.1 -> As alternativas de uma pergunta que muda de contexto podem tanto ser novas ou terem
                        // mudado de contexto. Alternativas deletadas nem serão recebidas.
                        final AnaliseItemModeloChecklist analiseAlternativa =
                                analiseModelo.getAlternativa(alternativa.getCodigo());
                        // O contexto da alternativa é mantido se ela for uma atualização e tivermos forçando manter o
                        // contexto ou se ela mesmo editada não mudou de contexto.
                        final boolean manterContextoAlternativa =
                                alternativa instanceof AlternativaModeloChecklistEdicaoAtualiza
                                        && (usarMesmoCodigoDeContexto || !analiseAlternativa.isItemMudouContexto());

                        insertAlternativaChecklist(
                                conn,
                                modeloChecklist.getCodUnidade(),
                                modeloChecklist.getCodModelo(),
                                novaVersaoModelo,
                                codPergunta,
                                alternativa,
                                manterContextoAlternativa);
                    }
                }
            }
        }
    }

    @NotNull
    private AnaliseMudancaModeloChecklist realizaAnaliseMudancaModeloChecklist(
            @NotNull final Connection conn,
            @NotNull final Long codModelo,
            @NotNull final ModeloChecklistEdicao modeloChecklist)
            throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("SELECT * FROM FUNC_CHECKLIST_ANALISA_MUDANCAS_MODELO(" +
                                                 "F_COD_MODELO                  := ?," +
                                                 "F_COD_VERSAO_MODELO           := ?," +
                                                 "F_NOME_MODELO                 := ?," +
                                                 "F_COD_CARGOS                  := ?," +
                                                 "F_COD_TIPOS_VEICULOS          := ?," +
                                                 "F_PERGUNTAS_ALTERNATIVAS_JSON := ?);");
            stmt.setLong(1, codModelo);
            stmt.setLong(2, modeloChecklist.getCodVersaoModelo());
            stmt.setString(3, modeloChecklist.getNome());
            stmt.setArray(4, PostgresUtils.listToArray(
                    conn,
                    SqlType.BIGINT,
                    modeloChecklist.getCargosLiberados()));
            stmt.setArray(5, PostgresUtils.listToArray(
                    conn,
                    SqlType.BIGINT,
                    modeloChecklist.getTiposVeiculoLiberados()));
            final String json = GsonUtils.getGson().toJson(modeloChecklist.getPerguntas());
            stmt.setObject(6, PostgresUtils.toJsonb(json));
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                final boolean algoMudouNoModelo = rSet.getBoolean("ALGO_MUDOU_NO_MODELO");
                final boolean algoMudouNoContexto = rSet.getBoolean("ALGO_MUDOU_NO_CONTEXTO");
                final boolean deveCriarNovaVersaoModelo = rSet.getBoolean("DEVE_CRIAR_NOVA_VERSAO_MODELO");
                if (deveCriarNovaVersaoModelo) {
                    final Map<Long, AnaliseItemModeloChecklist> analisePerguntas = new HashMap<>();
                    final Map<Long, AnaliseItemModeloChecklist> analiseAlternativas = new HashMap<>();
                    do {
                        if (rSet.getBoolean("ITEM_TIPO_PERGUNTA")) {
                            analisePerguntas.put(
                                    rSet.getLong("CODIGO_ITEM"),
                                    new AnaliseItemModeloChecklist(
                                            rSet.getLong("CODIGO_ITEM"),
                                            rSet.getBoolean("ITEM_NOVO"),
                                            rSet.getBoolean("ITEM_MUDOU_CONTEXTO")));
                        } else {
                            analiseAlternativas.put(
                                    rSet.getLong("CODIGO_ITEM"),
                                    new AnaliseItemModeloChecklist(
                                            rSet.getLong("CODIGO_ITEM"),
                                            rSet.getBoolean("ITEM_NOVO"),
                                            rSet.getBoolean("ITEM_MUDOU_CONTEXTO")));
                        }
                    } while (rSet.next());
                    return new AnaliseMudancaModeloChecklist(
                            algoMudouNoModelo,
                            algoMudouNoContexto,
                            true,
                            analisePerguntas,
                            analiseAlternativas);
                } else {
                    return new AnaliseMudancaModeloChecklist(
                            algoMudouNoModelo,
                            algoMudouNoContexto,
                            false,
                            null,
                            null);
                }
            } else {
                throw new IllegalStateException("Erro ao análise mudanças no modelo de checklist: " + codModelo);
            }
        } finally {
            close(stmt, rSet);
        }
    }

    @NotNull
    private List<PerguntaModeloChecklistVisualizacao> getPerguntasModeloChecklist(
            @NotNull final Connection conn,
            @NotNull final Long codUnidade,
            @NotNull final Long codModelo,
            @NotNull final Long codVersaoModelo) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("SELECT * FROM FUNC_CHECKLIST_GET_PERGUNTAS_MODELOS_CHECKLIST(" +
                                                 "F_COD_UNIDADE                 := ?," +
                                                 "F_COD_MODELO                  := ?," +
                                                 "F_COD_VERSAO_MODELO           := ?);");
            stmt.setLong(1, codUnidade);
            stmt.setLong(2, codModelo);
            stmt.setLong(3, codVersaoModelo);
            rSet = stmt.executeQuery();
            return ChecklistModeloConverter.createPerguntaAlternativaModeloChecklistVisualizacao(rSet);
        } finally {
            close(stmt, rSet);
        }
    }

    private void atualizaModeloChecklistInfosGerais(@NotNull final Connection conn,
                                                    @NotNull final Long codUnidade,
                                                    @NotNull final Long codModelo,
                                                    @NotNull final ModeloChecklistEdicao modeloChecklist)
            throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareCall("{CALL FUNC_CHECKLIST_UPDATE_MODELO_CHECKLIST_INFOS(" +
                                            "F_COD_UNIDADE        := ?," +
                                            "F_COD_MODELO         := ?," +
                                            "F_NOME_MODELO        := ?," +
                                            "F_COD_CARGOS         := ?," +
                                            "F_COD_TIPOS_VEICULOS := ?)}");
            stmt.setLong(1, codUnidade);
            stmt.setLong(2, codModelo);
            stmt.setString(3, modeloChecklist.getNome());
            stmt.setArray(4, PostgresUtils.listToArray(
                    conn,
                    SqlType.BIGINT,
                    modeloChecklist.getCargosLiberados()));
            stmt.setArray(5, PostgresUtils.listToArray(
                    conn,
                    SqlType.BIGINT,
                    modeloChecklist.getTiposVeiculoLiberados()));
            stmt.execute();
        } finally {
            close(stmt);
        }
    }

    @NotNull
    private Galeria getGaleria(@Nullable final Long codEmpresa) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            if (codEmpresa != null) {
                stmt = conn.prepareStatement("SELECT * FROM CHECKLIST_GALERIA_IMAGENS " +
                                                     "WHERE COD_EMPRESA = ? AND STATUS_ATIVO = TRUE;");
                stmt.setLong(1, codEmpresa);
            } else {
                stmt = conn.prepareStatement("SELECT * FROM CHECKLIST_GALERIA_IMAGENS " +
                                                     "WHERE COD_EMPRESA IS NULL AND STATUS_ATIVO = TRUE;");
            }
            rSet = stmt.executeQuery();
            final List<ImagemProlog> imagensProLog = new ArrayList<>();
            while (rSet.next()) {
                imagensProLog.add(ChecklistModeloConverter.createImagemProLog(rSet));
            }
            return new Galeria(imagensProLog);
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    private List<TipoVeiculo> getTipoVeiculoByCodModeloChecklist(@NotNull final Connection conn,
                                                                 @NotNull final Long codUnidade,
                                                                 @NotNull final Long codModelo) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("SELECT VT.NOME AS TIPO_VEICULO, VT.CODIGO "
                                                 + "FROM CHECKLIST_MODELO_VEICULO_TIPO CM "
                                                 + "JOIN VEICULO_TIPO VT ON CM.COD_TIPO_VEICULO = VT.CODIGO "
                                                 + "WHERE CM.COD_UNIDADE = ? "
                                                 + "AND CM.COD_MODELO = ? "
                                                 + "ORDER BY VT.NOME");
            stmt.setLong(1, codUnidade);
            stmt.setLong(2, codModelo);
            rSet = stmt.executeQuery();
            final List<TipoVeiculo> tipos = new ArrayList<>();
            while (rSet.next()) {
                tipos.add(ChecklistModeloConverter.createTipoVeiculo(rSet));
            }
            return tipos;
        } finally {
            close(stmt, rSet);
        }
    }

    @NotNull
    private List<Cargo> getCargosByCodModelo(@NotNull final Connection conn,
                                             @NotNull final Long codUnidade,
                                             @NotNull final Long codModelo) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("SELECT F.CODIGO, F.NOME "
                                                 + "FROM CHECKLIST_MODELO_FUNCAO CM "
                                                 + "JOIN FUNCAO F ON F.CODIGO = CM.COD_FUNCAO "
                                                 + "WHERE CM.COD_UNIDADE = ? "
                                                 + "AND CM.COD_CHECKLIST_MODELO = ? "
                                                 + "ORDER BY F.NOME");
            stmt.setLong(1, codUnidade);
            stmt.setLong(2, codModelo);
            rSet = stmt.executeQuery();
            final List<Cargo> cargos = new ArrayList<>();
            while (rSet.next()) {
                final Cargo cargo = new Cargo();
                cargo.setCodigo(rSet.getLong("CODIGO"));
                cargo.setNome(rSet.getString("NOME"));
                cargos.add(cargo);
            }
            return cargos;
        } finally {
            close(stmt, rSet);
        }
    }

    private void insertPerguntasEAlternativasCadastro(@NotNull final Connection conn,
                                                      @NotNull final ModeloChecklistInsercao modeloChecklist,
                                                      @NotNull final Long codModelo,
                                                      @NotNull final Long codVersaoModelo) throws Throwable {
        for (final PerguntaModeloChecklistInsercao pergunta : modeloChecklist.getPerguntas()) {
            final Long codPergunta = insertPerguntaChecklist(
                    conn,
                    modeloChecklist.getCodUnidade(),
                    codModelo,
                    codVersaoModelo,
                    pergunta,
                    false);
            for (final AlternativaModeloChecklist alternativa : pergunta.getAlternativas()) {
                insertAlternativaChecklist(
                        conn,
                        modeloChecklist.getCodUnidade(),
                        codModelo,
                        codVersaoModelo,
                        codPergunta,
                        alternativa,
                        false);
            }
        }
    }

    @NotNull
    private Long insertPerguntaChecklist(@NotNull final Connection conn,
                                         @NotNull final Long codUnidade,
                                         @NotNull final Long codModelo,
                                         @NotNull final Long codVersaoModelo,
                                         @NotNull final PerguntaModeloChecklist pergunta,
                                         final boolean usarMesmoCodigoDeContexto) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            if (usarMesmoCodigoDeContexto) {
                stmt = conn.prepareStatement("INSERT INTO CHECKLIST_PERGUNTAS ("
                                                     + "COD_UNIDADE, COD_CHECKLIST_MODELO, " +
                                                     "COD_VERSAO_CHECKLIST_MODELO, ORDEM, PERGUNTA, COD_IMAGEM, "
                                                     + "SINGLE_CHOICE, ANEXO_MIDIA_RESPOSTA_OK, CODIGO_CONTEXTO) "
                                                     + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING CODIGO;");
            } else {
                stmt = conn.prepareStatement("INSERT INTO CHECKLIST_PERGUNTAS ("
                                                     + "COD_UNIDADE, COD_CHECKLIST_MODELO, " +
                                                     "COD_VERSAO_CHECKLIST_MODELO, ORDEM, PERGUNTA, COD_IMAGEM, "
                                                     + "SINGLE_CHOICE, ANEXO_MIDIA_RESPOSTA_OK) VALUES (?, ?, ?, ?, " +
                                                     "?, ?, ?, ?) RETURNING CODIGO");
            }
            stmt.setLong(1, codUnidade);
            stmt.setLong(2, codModelo);
            stmt.setLong(3, codVersaoModelo);
            stmt.setInt(4, pergunta.getOrdemExibicao());
            stmt.setString(5, pergunta.getDescricao());
            bindValueOrNull(stmt, 6, pergunta.getCodImagem(), SqlType.BIGINT);
            stmt.setBoolean(7, pergunta.isSingleChoice());
            stmt.setString(8, pergunta.getAnexoMidiaRespostaOk().asString());
            if (usarMesmoCodigoDeContexto) {
                stmt.setLong(9, pergunta.getCodigoContexto());
            }
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return rSet.getLong("CODIGO");
            } else {
                throw new SQLException("Erro ao inserir a pergunta do checklist:\n"
                                               + "unidade: " + codUnidade + "\n"
                                               + "modelo: " + codModelo);
            }
        } finally {
            close(stmt, rSet);
        }
    }

    private void insertAlternativaChecklist(@NotNull final Connection conn,
                                            @NotNull final Long codUnidade,
                                            @NotNull final Long codModelo,
                                            @NotNull final Long codVersaoModelo,
                                            @NotNull final Long codPergunta,
                                            @NotNull final AlternativaModeloChecklist alternativa,
                                            final boolean usarMesmoCodigoDeContexto) throws Throwable {
        PreparedStatement stmt = null;
        try {
            if (usarMesmoCodigoDeContexto) {
                stmt = conn.prepareStatement("INSERT INTO CHECKLIST_ALTERNATIVA_PERGUNTA ( "
                                                     + "COD_UNIDADE, COD_CHECKLIST_MODELO, " +
                                                     "COD_VERSAO_CHECKLIST_MODELO, COD_PERGUNTA, ALTERNATIVA, " +
                                                     "PRIORIDADE, ORDEM, "
                                                     + "ALTERNATIVA_TIPO_OUTROS, DEVE_ABRIR_ORDEM_SERVICO, " +
                                                     "ANEXO_MIDIA, COD_AUXILIAR, CODIGO_CONTEXTO) "
                                                     + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
            } else {
                stmt = conn.prepareStatement("INSERT INTO CHECKLIST_ALTERNATIVA_PERGUNTA ( "
                                                     + "COD_UNIDADE, COD_CHECKLIST_MODELO, " +
                                                     "COD_VERSAO_CHECKLIST_MODELO, COD_PERGUNTA, ALTERNATIVA, " +
                                                     "PRIORIDADE, ORDEM, "
                                                     + "ALTERNATIVA_TIPO_OUTROS, DEVE_ABRIR_ORDEM_SERVICO, " +
                                                     "ANEXO_MIDIA, COD_AUXILIAR) "
                                                     + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
            }
            stmt.setLong(1, codUnidade);
            stmt.setLong(2, codModelo);
            stmt.setLong(3, codVersaoModelo);
            stmt.setLong(4, codPergunta);
            stmt.setString(5, alternativa.getDescricao());
            stmt.setString(6, alternativa.getPrioridade().asString());
            stmt.setInt(7, alternativa.getOrdemExibicao());
            stmt.setBoolean(8, alternativa.isTipoOutros());
            stmt.setBoolean(9, alternativa.isDeveAbrirOrdemServico());
            stmt.setString(10, alternativa.getAnexoMidia().asString());
            stmt.setString(11, alternativa.getCodAuxiliar());
            if (usarMesmoCodigoDeContexto) {
                stmt.setLong(12, alternativa.getCodigoContexto());
            }
            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Não foi possível inserir a alternativa da pergunta: " + codPergunta);
            }
        } finally {
            close(stmt);
        }
    }

    private void atualizaPergunta(
            @NotNull final Connection conn,
            @NotNull final Long codModelo,
            @NotNull final PerguntaModeloChecklistEdicao pergunta) throws Throwable {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("UPDATE CHECKLIST_PERGUNTAS " +
                                                 "SET PERGUNTA = ?, ORDEM = ?, SINGLE_CHOICE = ?, COD_IMAGEM = ? " +
                                                 "WHERE COD_CHECKLIST_MODELO = ? AND CODIGO = ?;");
            stmt.setString(1, pergunta.getDescricao());
            stmt.setInt(2, pergunta.getOrdemExibicao());
            stmt.setBoolean(3, pergunta.isSingleChoice());
            bindValueOrNull(stmt, 4, pergunta.getCodImagem(), SqlType.BIGINT);
            stmt.setLong(5, codModelo);
            stmt.setLong(6, pergunta.getCodigo());
            if (stmt.executeUpdate() <= 0) {
                throw new SQLException("Não foi possível atualizar a pergunta: " + pergunta.getCodigo());
            }
        } finally {
            close(stmt);
        }
    }

    private void atualizaAlternativa(
            @NotNull final Connection conn,
            @NotNull final Long codModelo,
            @NotNull final AlternativaModeloChecklist alternativa) throws Throwable {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("UPDATE CHECKLIST_ALTERNATIVA_PERGUNTA " +
                                                 "SET ALTERNATIVA = ?, ORDEM = ?, PRIORIDADE = ?, " +
                                                 "DEVE_ABRIR_ORDEM_SERVICO = ?, COD_AUXILIAR = ? " +
                                                 "WHERE COD_CHECKLIST_MODELO = ? AND CODIGO = ?;");
            stmt.setString(1, alternativa.getDescricao());
            stmt.setInt(2, alternativa.getOrdemExibicao());
            stmt.setString(3, alternativa.getPrioridade().asString());
            stmt.setBoolean(4, alternativa.isDeveAbrirOrdemServico());
            stmt.setString(5, alternativa.getCodAuxiliar());
            stmt.setLong(6, codModelo);
            stmt.setLong(7, alternativa.getCodigo());
            if (stmt.executeUpdate() <= 0) {
                throw new SQLException("Não foi possível atualizar a alternativa:\n" +
                                               "codModelo: " + codModelo + "\n" +
                                               "codigo: " + alternativa.getCodigo());
            }
        } finally {
            close(stmt);
        }
    }

    @NotNull
    private Long geraNovoCodigoVersaoModelo(@NotNull final Connection conn,
                                            @NotNull final ModeloChecklistEdicao modeloChecklist,
                                            @NotNull final String userToken) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("SELECT * FROM FUNC_CHECKLIST_INSERT_NOVA_VERSAO_MODELO(" +
                                                 "F_COD_UNIDADE_MODELO := ?," +
                                                 "F_COD_MODELO         := ?," +
                                                 "F_NOME_MODELO        := ?," +
                                                 "F_STATUS_ATIVO       := ?," +
                                                 "F_DATA_HORA_ATUAL    := ?," +
                                                 "F_TOKEN_COLABORADOR  := ?);");
            stmt.setLong(1, modeloChecklist.getCodUnidade());
            stmt.setLong(2, modeloChecklist.getCodModelo());
            stmt.setString(3, modeloChecklist.getNome());
            stmt.setBoolean(4, modeloChecklist.isAtivo());
            stmt.setObject(5, Now.getOffsetDateTimeUtc());
            stmt.setString(6, userToken);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                final long codVersaoModeloChecklist = rSet.getLong("COD_VERSAO_MODELO_CHECKLIST");
                if (codVersaoModeloChecklist <= 0) {
                    throw new SQLException("Erro ao criar nova versão do modelo de checklist: "
                                                   + modeloChecklist.getCodModelo());
                }
                return codVersaoModeloChecklist;
            } else {
                throw new SQLException("Erro ao criar nova versão do modelo de checklist: "
                                               + modeloChecklist.getCodModelo());
            }
        } finally {
            close(stmt, rSet);
        }
    }
}