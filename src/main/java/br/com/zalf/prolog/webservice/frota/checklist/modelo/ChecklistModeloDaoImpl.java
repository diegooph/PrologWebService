package br.com.zalf.prolog.webservice.frota.checklist.modelo;

import br.com.zalf.prolog.webservice.colaborador.model.Cargo;
import br.com.zalf.prolog.webservice.commons.imagens.Galeria;
import br.com.zalf.prolog.webservice.commons.imagens.ImagemProLog;
import br.com.zalf.prolog.webservice.commons.util.PostgresUtils;
import br.com.zalf.prolog.webservice.commons.util.SqlType;
import br.com.zalf.prolog.webservice.commons.util.date.Now;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.frota.checklist.OLD.AlternativaChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.OLD.PerguntaRespostaChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.PrioridadeAlternativa;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.AlternativaModeloChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.ModeloChecklistListagem;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.PerguntaModeloChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.edicao.AcaoEdicaoAlternativa;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.edicao.AlternativaModeloChecklistEdicao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.edicao.ModeloChecklistEdicao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.edicao.PerguntaModeloChecklistEdicao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.insercao.ModeloChecklistInsercao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.insercao.PerguntaModeloChecklistInsercao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.realizacao.ModeloChecklistSelecao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.realizacao.VeiculoChecklistSelecao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.visualizacao.AlternativaModeloChecklistVisualizacao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.visualizacao.ModeloChecklistVisualizacao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.visualizacao.PerguntaModeloChecklistVisualizacao;
import br.com.zalf.prolog.webservice.frota.checklist.offline.DadosChecklistOfflineChangedListener;
import br.com.zalf.prolog.webservice.frota.veiculo.model.TipoVeiculo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

import static br.com.zalf.prolog.webservice.commons.util.StatementUtils.bindValueOrNull;

public final class ChecklistModeloDaoImpl extends DatabaseConnection implements ChecklistModeloDao {

    public ChecklistModeloDaoImpl() {

    }

    @Override
    public void insertModeloChecklist(
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
                    "F_DATA_HORA_ATUAL    := ?," +
                    "F_TOKEN_COLABORADOR  := ?);");
            stmt.setLong(1, modeloChecklist.getCodUnidade());
            stmt.setString(2, modeloChecklist.getNome());
            stmt.setBoolean(3, statusAtivo);
            stmt.setObject(4, Now.offsetDateTimeUtc());
            stmt.setString(5, userToken);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                final long codModeloChecklistInserido = rSet.getLong("COD_MODELO_CHECKLIST");
                final long codVersaoModeloChecklist = rSet.getLong("COD_VERSAO_MODELO_CHECKLIST");
                if (codModeloChecklistInserido <= 0 || codVersaoModeloChecklist <= 0) {
                    throw new SQLException("Erro ao inserir modelo de checklist:\n" +
                            "codModeloChecklistInserido: " + codModeloChecklistInserido);
                }

                insertModeloTipoVeiculo(conn, modeloChecklist, codModeloChecklistInserido);
                insertModeloCargo(conn, modeloChecklist, codModeloChecklistInserido);
                insertModeloPerguntas(conn, modeloChecklist, codModeloChecklistInserido, codVersaoModeloChecklist);

                // Devemos notificar que uma inserção de modelo de checklist foi realizada.
                checklistOfflineListener.onInsertModeloChecklist(conn, codModeloChecklistInserido);
                conn.commit();
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
            final boolean  sobrescreverDescricaoPerguntasAlternativas,
            @NotNull final String userToken) throws Throwable {
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            atualizaModeloChecklist(conn, codUnidade, codModelo, modeloChecklist);
            // Caso nenhuma pergunta tenha sido editada, o servidor não receberá nada.
            if (modeloChecklist.getPerguntas() != null && modeloChecklist.getPerguntas().size() > 0) {
                atualizaPerguntasModeloChecklist(
                        conn,
                        codUnidade,
                        codModelo,
                        modeloChecklist,
                         sobrescreverDescricaoPerguntasAlternativas);
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

    private void criarNovaVersaoModelo(@NotNull final Connection conn,
                                       @NotNull final ModeloChecklistEdicao modeloChecklist,
                                       @NotNull final String userToken) {

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
                modeloChecklistListagem =
                        createModeloChecklistListagem(rSet, codModeloChecklistAnterior, setCargos, setTiposVeiculos);
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
            stmt = conn.prepareStatement("SELECT DISTINCT CM.NOME AS MODELO, CM.CODIGO AS COD_MODELO "
                    + "FROM CHECKLIST_MODELO CM "
                    + "WHERE CM.COD_UNIDADE = ? AND CM.CODIGO = ? "
                    + "ORDER BY MODELO");
            stmt.setLong(1, codUnidade);
            stmt.setLong(2, codModelo);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                final ModeloChecklistVisualizacao modeloChecklist = new ModeloChecklistVisualizacao();
                modeloChecklist.setCodigo(rSet.getLong("COD_MODELO"));
                modeloChecklist.setNome(rSet.getString("MODELO"));
                modeloChecklist.setCodUnidade(codUnidade);
                modeloChecklist.setPerguntas(getPerguntasModeloChecklist(codUnidade, codModelo));
                modeloChecklist.setTiposVeiculoLiberados(getTipoVeiculoByCodModeloChecklist(codUnidade, codModelo));
                modeloChecklist.setCargosLiberados(getCargosByCodModelo(codUnidade, codModelo));
                return modeloChecklist;
            } else {
                throw new SQLException("Não foi possível buscar o modelo de checklist: "
                        + "unidade: " + codUnidade + "\n"
                        + "modelo: " + codModelo);
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    @Deprecated
    public List<PerguntaRespostaChecklist> getPerguntas(@NotNull final Long codUnidadeModelo,
                                                        @NotNull final Long codModelo) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT " +
                            "  CP.CODIGO                   AS COD_PERGUNTA," +
                            "  CGI.COD_IMAGEM              AS COD_IMAGEM," +
                            "  CGI.URL_IMAGEM              AS URL_IMAGEM," +
                            "  CP.PERGUNTA                 AS PERGUNTA," +
                            "  CP.ORDEM                    AS ORDEM_PERGUNTA," +
                            "  CP.SINGLE_CHOICE            AS SINGLE_CHOICE," +
                            "  CAP.CODIGO                  AS COD_ALTERNATIVA," +
                            "  CAP.ALTERNATIVA             AS ALTERNATIVA," +
                            "  CAP.PRIORIDADE              AS PRIORIDADE," +
                            "  CAP.ORDEM                   AS ORDEM_ALTERNATIVA," +
                            "  CAP.ALTERNATIVA_TIPO_OUTROS AS ALTERNATIVA_TIPO_OUTROS " +
                            "FROM CHECKLIST_PERGUNTAS CP " +
                            "  JOIN CHECKLIST_ALTERNATIVA_PERGUNTA CAP " +
                            "    ON CP.CODIGO = CAP.COD_PERGUNTA " +
                            "       AND CAP.COD_UNIDADE = CP.COD_UNIDADE " +
                            "       AND CAP.COD_CHECKLIST_MODELO = CP.COD_CHECKLIST_MODELO " +
                            "       AND CAP.STATUS_ATIVO = TRUE " +
                            "  LEFT JOIN CHECKLIST_GALERIA_IMAGENS CGI " +
                            "    ON CGI.COD_IMAGEM = CP.COD_IMAGEM " +
                            "WHERE CP.COD_UNIDADE = ? " +
                            "      AND CP.COD_CHECKLIST_MODELO = ?" +
                            "      AND CP.STATUS_ATIVO = TRUE " +
                            "ORDER BY CP.ORDEM, Cp.PERGUNTA, CAP.ORDEM;",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            stmt.setLong(1, codUnidadeModelo);
            stmt.setLong(2, codModelo);
            rSet = stmt.executeQuery();
            return createPerguntasAlternativas(rSet);
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    private List<PerguntaModeloChecklistVisualizacao> getPerguntasModeloChecklist(
            @NotNull final Long codUnidade,
            @NotNull final Long codModelo) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT " +
                            "  CP.CODIGO                    AS COD_PERGUNTA," +
                            "  CGI.COD_IMAGEM               AS COD_IMAGEM," +
                            "  CGI.URL_IMAGEM               AS URL_IMAGEM," +
                            "  CP.PERGUNTA                  AS PERGUNTA," +
                            "  CP.ORDEM                     AS ORDEM_PERGUNTA," +
                            "  CP.SINGLE_CHOICE             AS SINGLE_CHOICE," +
                            "  CAP.CODIGO                   AS COD_ALTERNATIVA," +
                            "  CAP.ALTERNATIVA              AS ALTERNATIVA," +
                            "  CAP.PRIORIDADE               AS PRIORIDADE," +
                            "  CAP.ORDEM                    AS ORDEM_ALTERNATIVA," +
                            "  CAP.DEVE_ABRIR_ORDEM_SERVICO AS DEVE_ABRIR_ORDEM_SERVICO," +
                            "  CAP.ALTERNATIVA_TIPO_OUTROS  AS ALTERNATIVA_TIPO_OUTROS " +
                            "FROM CHECKLIST_PERGUNTAS CP " +
                            "  JOIN CHECKLIST_ALTERNATIVA_PERGUNTA CAP " +
                            "    ON CP.CODIGO = CAP.COD_PERGUNTA " +
                            "       AND CAP.COD_UNIDADE = CP.COD_UNIDADE " +
                            "       AND CAP.COD_CHECKLIST_MODELO = CP.COD_CHECKLIST_MODELO " +
                            "       AND CAP.STATUS_ATIVO = TRUE " +
                            "  LEFT JOIN CHECKLIST_GALERIA_IMAGENS CGI " +
                            "    ON CGI.COD_IMAGEM = CP.COD_IMAGEM " +
                            "WHERE CP.COD_UNIDADE = ? " +
                            "      AND CP.COD_CHECKLIST_MODELO = ?" +
                            "      AND CP.STATUS_ATIVO = TRUE " +
                            "ORDER BY CP.ORDEM, Cp.PERGUNTA, CAP.ORDEM;",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            stmt.setLong(1, codUnidade);
            stmt.setLong(2, codModelo);
            rSet = stmt.executeQuery();
            return createPerguntaAlternativaModeloChecklist(rSet);
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
            conn.setAutoCommit(false);
            stmt = conn.prepareStatement("UPDATE CHECKLIST_MODELO " +
                    "SET STATUS_ATIVO = ? " +
                    "WHERE COD_UNIDADE  = ? AND CODIGO = ?;");
            stmt.setBoolean(1, statusAtivo);
            stmt.setLong(2, codUnidade);
            stmt.setLong(3, codModelo);
            if (stmt.executeUpdate() != 0) {
                checklistOfflineListener.onUpdateStatusModeloChecklist(conn, codModelo);
                conn.commit();
            } else {
                throw new SQLException("Erro ao atualizar o status do modelo de checklist:\n"
                        + "codUnidade: " + codUnidade + "\n"
                        + "codModelo: " + codModelo);
            }
        } catch (final Throwable t) {
            if (conn != null) {
                conn.rollback();
            }
            throw t;
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
                final ModeloChecklistVisualizacao modelo = new ModeloChecklistVisualizacao();
                modelo.setCodigo(rSet.getLong("CODIGO"));
                modelo.setNome(rSet.getString("NOME"));
                modelo.setPerguntas(getPerguntasAlternativasProLog(conn));
                modelos.add(modelo);
            }
            return modelos;
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
            stmt = conn.prepareStatement("SELECT DISTINCT CGI.URL_IMAGEM FROM CHECKLIST_MODELO_FUNCAO CMF " +
                    "  JOIN CHECKLIST_PERGUNTAS CP ON CP.COD_UNIDADE = CMF.COD_UNIDADE " +
                    "                                 AND CP.COD_CHECKLIST_MODELO = CMF.COD_CHECKLIST_MODELO " +
                    "  JOIN CHECKLIST_GALERIA_IMAGENS CGI ON CP.COD_IMAGEM = CGI.COD_IMAGEM " +
                    "  JOIN CHECKLIST_MODELO CM ON CP.COD_CHECKLIST_MODELO = CM.CODIGO " +
                    "WHERE CMF.COD_UNIDADE = ? " +
                    "      AND CMF.COD_FUNCAO = ? " +
                    "      AND CM.STATUS_ATIVO = TRUE" +
                    "      AND CP.STATUS_ATIVO = TRUE;");
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
    public Long insertImagem(@NotNull final Long codEmpresa,
                             @NotNull final ImagemProLog imagemProLog) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
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
        } catch (final Throwable t) {
            if (conn != null) {
                conn.rollback();
            }
            throw t;
        } finally {
            close(conn, stmt, rSet);
        }
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
                            "F_COD_UNIDADE := ? "+
                            "F_COD_CARGO   := ?);");
            stmt.setLong(1, codUnidade);
            stmt.setLong(2, codCargo);
            rSet = stmt.executeQuery();
            ModeloChecklistSelecao modeloSelecao = null;
            if (rSet.next()) {
                final List<ModeloChecklistSelecao> dispositivos = new ArrayList<>();
                List<VeiculoChecklistSelecao> veiculosSelecao = new ArrayList<>();
                do {
                    if (modeloSelecao == null) {
                        modeloSelecao = ChecklistModeloConverter.createModeloChecklistSelecao(rSet, veiculosSelecao);
                        veiculosSelecao.add(ChecklistModeloConverter.createVeiculoChecklistSelecao(rSet));
                    } else {
                        if (modeloSelecao.getCodModelo() == rSet.getLong("COD_MODELO")) {
                            veiculosSelecao.add(ChecklistModeloConverter.createVeiculoChecklistSelecao(rSet));
                        } else {
                            dispositivos.add(modeloSelecao);
                            veiculosSelecao = new ArrayList<>();
                            veiculosSelecao.add(ChecklistModeloConverter.createVeiculoChecklistSelecao(rSet));
                            modeloSelecao = ChecklistModeloConverter.createModeloChecklistSelecao(rSet, veiculosSelecao);
                        }
                    }
                } while (rSet.next());
                dispositivos.add(modeloSelecao);
                return dispositivos;
            } else {
                return Collections.emptyList();
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }

    private void atualizaPerguntasModeloChecklist(
            @NotNull final Connection conn,
            @NotNull final Long codUnidade,
            @NotNull final Long codModelo,
            @NotNull final ModeloChecklistEdicao modeloChecklist,
            final boolean  sobrescreverDescricaoPerguntasAlternativas) throws SQLException {
        for (final PerguntaModeloChecklistEdicao pergunta : modeloChecklist.getPerguntas()) {
            switch (pergunta.getAcaoEdicao()) {
                case CRIADA:
                    // Apenas adicionamos uma nova entrada no banco.
                    // Adicionamos as alternativas também.
                    // TODO:
//                    insertPerguntaAlternativaModeloChecklist(conn, codUnidade, codModelo, pergunta);
                    break;
                case ALTERADA_NOME:
                    Long codPergunta = pergunta.getCodigo();
                    if ( sobrescreverDescricaoPerguntasAlternativas) {
                        // Para manter o código original da pergunta, apenas atualizamos os dados.
                        atualizaPerguntaChecklist(conn, codUnidade, codModelo, pergunta);
                    } else {
                        // Inativa a Pergunta atual.
                        // Cria uma nova entrada no banco de dados e retorna o código.
                        // Insere as alternativas (NÃO DELETADAS) da pergunta no código novo.
                        inativarPerguntaChecklist(conn, codUnidade, codModelo, pergunta);
                        inativarTodasAlternativasPerguntaChecklist(conn, codUnidade, codModelo, pergunta.getCodigo());
                        codPergunta = insertApenasPerguntaChecklist(conn, codUnidade, codModelo, pergunta);
                    }

                    // Se nenhuma alternativa tiver sido alterada, a lista será nula e precisamos instanciá-la.
                    if (pergunta.getAlternativas() == null) {
                        pergunta.setAlternativas(new ArrayList<>());
                    }

                    for (final AlternativaModeloChecklist alternativa : pergunta.getAlternativas()) {
                        if (alternativa instanceof AlternativaModeloChecklistEdicao) {
                            final AlternativaModeloChecklistEdicao alternativaEdicao =
                                    (AlternativaModeloChecklistEdicao) alternativa;
                            if ( sobrescreverDescricaoPerguntasAlternativas) {
                                if (alternativaEdicao.getAcaoEdicao().equals(AcaoEdicaoAlternativa.CRIADA)
                                        && alternativaEdicao.getCodigo() != null) {
                                    // Significa que é uma alternativa nova adicionada na pergunta. Para esse caso
                                    // podemos criar a alternativa pois ela não existe no banco de dados ainda.
                                    insertAlternativaChecklist(conn, codUnidade, codModelo, codPergunta, alternativa);
                                } else if (alternativaEdicao.getAcaoEdicao().equals(AcaoEdicaoAlternativa.ALTERADA)) {
                                    // Significa que ouve uma alteração na alternativa, neste caso apenas atualizamos.
                                    atualizaAlternativaChecklist(
                                            conn,
                                            codUnidade,
                                            codModelo,
                                            codPergunta,
                                            alternativaEdicao);
                                } else if (alternativaEdicao.getAcaoEdicao().equals(AcaoEdicaoAlternativa.ALTERADA_ORDENACAO)) {
                                    // Apenas mudou a ordem, precisamos somente atualizar a ordem da alternativa.
                                    updateOrdemExibicaoAlternativa(
                                            conn,
                                            alternativa.getCodigo(),
                                            alternativa.getOrdemExibicao());
                                } else if (alternativaEdicao.getAcaoEdicao().equals(AcaoEdicaoAlternativa.DELETADA)) {
                                    // Se a alternativa foi deletada, precisamos inativar para manter o histórico.
                                    inativarAlternativaModeloChecklist(
                                            conn,
                                            codUnidade,
                                            codModelo,
                                            codPergunta,
                                            alternativa);
                                }
                            } else {
                                // Se não for para sobrescrever, todas as alternativas antigas da pergunta já foram
                                // inativadas, por isso precisamos apenas realizar criações. Se for DELETADA, nada
                                // precisa ser feito.
                                if (!alternativaEdicao.getAcaoEdicao().equals(AcaoEdicaoAlternativa.DELETADA)) {
                                    insertAlternativaChecklist(
                                            conn,
                                            codUnidade,
                                            codModelo,
                                            codPergunta,
                                            alternativaEdicao);
                                }
                            }
                        } else {
                            throw new IllegalStateException("Apenas alternativa do tipo "
                                    + AlternativaModeloChecklistEdicao.class.getSimpleName() + " pode ser atualizada!");
                        }
                    }
                    break;
                case ALTERADA_INFOS:
                    // Apenas atualizamos a pergunta atual.
                    // Devemos fazer uma verificação em cima das alternativas para tratar
                    // os casos de ALTERACAO/CRIACAO/DELECAO.
                    atualizaPerguntaChecklist(conn, codUnidade, codModelo, pergunta);

                    // Se as alternaticas não foram alteradas, pula.
                    if (pergunta.getAlternativas() != null && !pergunta.getAlternativas().isEmpty()) {
                        for (final AlternativaModeloChecklist alternativa : pergunta.getAlternativas()) {
                            if (alternativa instanceof AlternativaModeloChecklistEdicao) {
                                final AlternativaModeloChecklistEdicao alternativaEdicao =
                                        (AlternativaModeloChecklistEdicao) alternativa;
                                if (alternativaEdicao.getAcaoEdicao().equals(AcaoEdicaoAlternativa.CRIADA)) {
                                    if ( sobrescreverDescricaoPerguntasAlternativas && alternativaEdicao.getCodigo() != null) {
                                        // Se o código da alternativa não é null, significa que a alternativa já
                                        // existia, assim, só precisamos atualizar os dados dela.
                                        atualizaAlternativaChecklist(
                                                conn,
                                                codUnidade,
                                                codModelo,
                                                pergunta.getCodigo(),
                                                alternativaEdicao);
                                    } else {
                                        // Caso contrário, ela não existia, então precisamos inserir ela no banco.
                                        insertAlternativaChecklist(
                                                conn,
                                                codUnidade,
                                                codModelo,
                                                pergunta.getCodigo(),
                                                alternativaEdicao);
                                    }
                                } else if (alternativaEdicao.getAcaoEdicao().equals(AcaoEdicaoAlternativa.ALTERADA)) {
                                    if ( sobrescreverDescricaoPerguntasAlternativas) {
                                        // Se a alternativa foi alterada, precisamos atualizar e manter o código.
                                        atualizaAlternativaChecklist(
                                                conn,
                                                codUnidade,
                                                codModelo,
                                                pergunta.getCodigo(),
                                                alternativaEdicao);
                                    } else {
                                        // Devemos passar o código da pergunta que foi alterada,
                                        // para podermos desativar as suas alternativas.
                                        inativarAlternativaModeloChecklist(
                                                conn,
                                                codUnidade,
                                                codModelo,
                                                pergunta.getCodigo(),
                                                alternativaEdicao);
                                        insertAlternativaChecklist(
                                                conn,
                                                codUnidade,
                                                codModelo,
                                                pergunta.getCodigo(),
                                                alternativaEdicao);
                                    }
                                } else if (alternativaEdicao.getAcaoEdicao().equals(AcaoEdicaoAlternativa.ALTERADA_ORDENACAO)) {
                                    updateOrdemExibicaoAlternativa(
                                            conn,
                                            alternativaEdicao.getCodigo(),
                                            alternativa.getOrdemExibicao());
                                } else {
                                    // Devemos passar o código da pergunta que foi desativada,
                                    // para podermos desativar as suas alternativas também.
                                    inativarAlternativaModeloChecklist(
                                            conn,
                                            codUnidade,
                                            codModelo,
                                            pergunta.getCodigo(),
                                            alternativaEdicao);
                                }
                            } else {
                                throw new IllegalStateException("Apenas alternativa do tipo "
                                        + AlternativaModeloChecklistEdicao.class.getSimpleName()
                                        + " pode ser atualizada!");
                            }
                        }
                    }
                    break;
                case DELETADA:
                    // Vamos inativar a pergunta
                    inativarPerguntaChecklist(conn, codUnidade, codModelo, pergunta);
                    inativarTodasAlternativasPerguntaChecklist(conn, codUnidade, codModelo, pergunta.getCodigo());
                    break;
            }
        }
    }

    private void atualizaAlternativaChecklist(
            @NotNull final Connection conn,
            @NotNull final Long codUnidade,
            @NotNull final Long codModelo,
            @NotNull final Long codPergunta,
            @NotNull final AlternativaModeloChecklistEdicao alternativa) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("UPDATE CHECKLIST_ALTERNATIVA_PERGUNTA " +
                    "SET" +
                    "  ALTERNATIVA = ?," +
                    "  ORDEM = ?," +
                    "  ALTERNATIVA_TIPO_OUTROS = ?," +
                    "  PRIORIDADE = ?," +
                    "  DEVE_ABRIR_ORDEM_SERVICO = ? " +
                    "WHERE COD_UNIDADE = ?" +
                    "      AND COD_CHECKLIST_MODELO = ?" +
                    "      AND COD_PERGUNTA = ?" +
                    "      AND CODIGO = ?;");
            stmt.setString(1, alternativa.getDescricao());
            stmt.setInt(2, alternativa.getOrdemExibicao());
            stmt.setBoolean(3, alternativa.isTipoOutros());
            stmt.setString(4, alternativa.getPrioridade().asString());
            stmt.setBoolean(5, alternativa.isDeveAbrirOrdemServico());
            stmt.setLong(6, codUnidade);
            stmt.setLong(7, codModelo);
            stmt.setLong(8, codPergunta);
            stmt.setLong(9, alternativa.getCodigo());
            if (stmt.executeUpdate() <= 0) {
                throw new SQLException("Não foi possível atualizar a alternativa da pergunta:\n" +
                        "codUnidade: " + codUnidade + "\n" +
                        "codModelo: " + codModelo + "\n" +
                        "codPergunta: " + codPergunta);
            }
        } finally {
            close(stmt);
        }
    }

    private void insertPerguntaAlternativaModeloChecklist(
            @NotNull final Connection conn,
            @NotNull final Long codUnidade,
            @NotNull final Long codModelo,
            @NotNull final Long codVersaoModelo,
            @NotNull final PerguntaModeloChecklist pergunta) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("INSERT INTO CHECKLIST_PERGUNTAS ("
                    + "COD_UNIDADE, COD_CHECKLIST_MODELO, COD_VERSAO_CHECKLIST_MODELO, ORDEM, PERGUNTA, COD_IMAGEM, "
                    + "STATUS_ATIVO, SINGLE_CHOICE) VALUES (?, ?, ?, ?, ?, ?, ?, ?) RETURNING CODIGO");
            stmt.setLong(1, codUnidade);
            stmt.setLong(2, codModelo);
            stmt.setLong(3, codVersaoModelo);
            stmt.setInt(4, pergunta.getOrdemExibicao());
            stmt.setString(5, pergunta.getDescricao());
            bindValueOrNull(stmt, 6, pergunta.getCodImagem(), SqlType.BIGINT);
            stmt.setBoolean(7, true);
            stmt.setBoolean(8, pergunta.isSingleChoice());
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                pergunta.setCodigo(rSet.getLong("CODIGO"));

                // Se nenhuma alternativa tiver sido criada, a lista será nula e precisamos instanciá-la.
                if (pergunta.getAlternativas() == null) {
                    pergunta.setAlternativas(new ArrayList<>());
                }

                for (final AlternativaModeloChecklist alternativa : pergunta.getAlternativas()) {
                    insertAlternativaChecklist(conn, codUnidade, codModelo, pergunta.getCodigo(), alternativa);
                }
            } else {
                throw new SQLException("Erro ao inserir a pergunta do checklist:\n"
                        + "unidade: " + codUnidade + "\n"
                        + "modelo: " + codModelo);
            }
        } finally {
            close(stmt, rSet);
        }
    }

    private long insertApenasPerguntaChecklist(@NotNull final Connection conn,
                                               @NotNull final Long codUnidade,
                                               @NotNull final Long codModelo,
                                               @NotNull final PerguntaModeloChecklist pergunta) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("INSERT INTO CHECKLIST_PERGUNTAS ("
                    + "COD_CHECKLIST_MODELO, COD_UNIDADE, ORDEM, PERGUNTA, COD_IMAGEM, "
                    + "STATUS_ATIVO, SINGLE_CHOICE) VALUES (?,?,?,?,?,?,?) RETURNING CODIGO");
            stmt.setLong(1, codModelo);
            stmt.setLong(2, codUnidade);
            stmt.setInt(3, pergunta.getOrdemExibicao());
            stmt.setString(4, pergunta.getDescricao());
            bindValueOrNull(stmt, 5, pergunta.getCodImagem(), SqlType.BIGINT);
            stmt.setBoolean(6, true);
            stmt.setBoolean(7, pergunta.isSingleChoice());
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return rSet.getLong("CODIGO");
            } else {
                throw new SQLException("Não foi possível inserir a pergunta do checklist:\n"
                        + "unidade: " + codUnidade + "\n"
                        + "modelo: " + codModelo);
            }
        } finally {
            close(stmt, rSet);
        }
    }

    private void atualizaPerguntaChecklist(@NotNull final Connection conn,
                                           @NotNull final Long codUnidade,
                                           @NotNull final Long codModelo,
                                           @NotNull final PerguntaModeloChecklist pergunta) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("UPDATE CHECKLIST_PERGUNTAS " +
                    "SET ORDEM = ?, PERGUNTA = ?, SINGLE_CHOICE = ?, COD_IMAGEM = ? " +
                    "WHERE COD_UNIDADE = ? AND COD_CHECKLIST_MODELO = ? AND CODIGO = ?;");
            stmt.setInt(1, pergunta.getOrdemExibicao());
            stmt.setString(2, pergunta.getDescricao());
            stmt.setBoolean(3, pergunta.isSingleChoice());
            bindValueOrNull(stmt, 4, pergunta.getCodImagem(), SqlType.BIGINT);
            stmt.setLong(5, codUnidade);
            stmt.setLong(6, codModelo);
            stmt.setLong(7, pergunta.getCodigo());
            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Não foi possível atualizar a pergunta: " + pergunta.getCodigo());
            }
        } finally {
            close(stmt);
        }
    }

    private void insertAlternativaChecklist(@NotNull final Connection conn,
                                            @NotNull final Long codUnidade,
                                            @NotNull final Long codModelo,
                                            @NotNull final Long codPergunta,
                                            @NotNull final AlternativaModeloChecklist alternativa) throws SQLException {
        // Garante que alternativas do TIPO_OUTROS tenham setado o texto "Outros".
        if (alternativa.isTipoOutros()) {
            alternativa.setDescricao("Outros");
        }

        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("INSERT INTO CHECKLIST_ALTERNATIVA_PERGUNTA ( "
                    + "COD_CHECKLIST_MODELO, COD_UNIDADE, COD_PERGUNTA, ALTERNATIVA, PRIORIDADE, ORDEM, "
                    + "STATUS_ATIVO, ALTERNATIVA_TIPO_OUTROS, DEVE_ABRIR_ORDEM_SERVICO) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);");
            stmt.setLong(1, codModelo);
            stmt.setLong(2, codUnidade);
            stmt.setLong(3, codPergunta);
            stmt.setString(4, alternativa.getDescricao());
            stmt.setString(5, alternativa.getPrioridade().asString());
            stmt.setInt(6, alternativa.getOrdemExibicao());
            stmt.setBoolean(7, true);
            stmt.setBoolean(8, alternativa.isTipoOutros());
            stmt.setBoolean(9, alternativa.isDeveAbrirOrdemServico());
            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Não foi possível inserir a alternativa da pergunta: " + codPergunta);
            }
        } finally {
            close(stmt);
        }
    }

    private void updateOrdemExibicaoAlternativa(@NotNull final Connection conn,
                                                @NotNull final Long codAlternativa,
                                                final int ordemExibicao) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("UPDATE CHECKLIST_ALTERNATIVA_PERGUNTA " +
                    "SET ORDEM = ? WHERE CODIGO = ?;");
            stmt.setInt(1, ordemExibicao);
            stmt.setLong(2, codAlternativa);
            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Não foi possível atualizar a ordem de exibição da alternativa: " + codAlternativa);
            }
        } finally {
            close(stmt);
        }
    }

    private void inativarAlternativaModeloChecklist(
            @NotNull final Connection conn,
            @NotNull final Long codUnidade,
            @NotNull final Long codModelo,
            @NotNull final Long codPergunta,
            @NotNull final AlternativaModeloChecklist alternativa) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("UPDATE CHECKLIST_ALTERNATIVA_PERGUNTA" +
                    " SET STATUS_ATIVO = FALSE" +
                    " WHERE COD_UNIDADE = ? AND COD_CHECKLIST_MODELO = ? AND COD_PERGUNTA = ? AND CODIGO = ?;");
            stmt.setLong(1, codUnidade);
            stmt.setLong(2, codModelo);
            stmt.setLong(3, codPergunta);
            stmt.setLong(4, alternativa.getCodigo());
            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Não foi possível inativar a alternativa: " + alternativa.getCodigo());
            }
        } finally {
            close(stmt);
        }
    }

    private void inativarTodasAlternativasPerguntaChecklist(@NotNull final Connection conn,
                                                            @NotNull final Long codUnidade,
                                                            @NotNull final Long codModelo,
                                                            @NotNull final Long codPergunta) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("UPDATE CHECKLIST_ALTERNATIVA_PERGUNTA " +
                    "SET STATUS_ATIVO = FALSE " +
                    "WHERE COD_UNIDADE = ? AND COD_CHECKLIST_MODELO = ? AND COD_PERGUNTA = ?;");
            stmt.setLong(1, codUnidade);
            stmt.setLong(2, codModelo);
            stmt.setLong(3, codPergunta);
            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Não foi possível inativar as alternativas da pergunta: " + codPergunta);
            }
        } finally {
            close(stmt);
        }
    }

    private void inativarPerguntaChecklist(@NotNull final Connection conn,
                                           @NotNull final Long codUnidade,
                                           @NotNull final Long codModelo,
                                           @NotNull final PerguntaModeloChecklist pergunta) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("UPDATE CHECKLIST_PERGUNTAS " +
                    "SET STATUS_ATIVO = FALSE " +
                    "WHERE COD_UNIDADE = ? AND COD_CHECKLIST_MODELO = ? AND CODIGO = ?;");
            stmt.setLong(1, codUnidade);
            stmt.setLong(2, codModelo);
            stmt.setLong(3, pergunta.getCodigo());
            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Não foi possível inativar a pergunta: " + pergunta.getCodigo());
            }
        } finally {
            close(stmt);
        }
    }

    private void atualizaModeloChecklist(@NotNull final Connection conn,
                                         @NotNull final Long codUnidade,
                                         @NotNull final Long codModelo,
                                         @NotNull final ModeloChecklistEdicao modeloChecklist) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("SELECT * FROM FUNC_CHECKLIST_UPDATE_MODELO_CHECKLIST(?, ?, ?, ?, ?);");
            stmt.setString(1, modeloChecklist.getNome());
            stmt.setLong(2, codUnidade);
            stmt.setLong(3, codModelo);
            stmt.setArray(4, PostgresUtils.listToArray(
                    conn,
                    SqlType.BIGINT,
                    modeloChecklist.getCargosLiberados()));
            stmt.setArray(5, PostgresUtils.listToArray(
                    conn,
                    SqlType.BIGINT,
                    modeloChecklist.getTiposVeiculoLiberados()));
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                if (!rSet.getBoolean(1)) {
                    throw new SQLException("Erro ao atualizar as informações gerais do modelo de checklist: "
                            + "unidade: " + codUnidade
                            + "modelo: " + codModelo);
                }
            } else {
                throw new SQLException("Erro ao atualizar as informações gerais do modelo de checklist: "
                        + "unidade: " + codUnidade
                        + "modelo: " + codModelo);
            }
        } finally {
            close(stmt, rSet);
        }
    }

    @NotNull
    private ModeloChecklistListagem createModeloChecklistListagem(
            @NotNull final ResultSet rSet,
            @NotNull final Long codModeloChecklistAtual,
            @NotNull final Set<String> setCargos,
            @NotNull final Set<String> setTiposVeiculos) throws SQLException {
        final ModeloChecklistListagem modeloChecklist = new ModeloChecklistListagem();
        modeloChecklist.setCodigo(codModeloChecklistAtual);
        modeloChecklist.setCodUnidade(rSet.getLong("COD_UNIDADE"));
        modeloChecklist.setNome(rSet.getString("MODELO"));
        modeloChecklist.setCargosLiberados(setCargos);
        modeloChecklist.setTiposVeiculoLiberados(setTiposVeiculos);
        modeloChecklist.setQtdPerguntas(rSet.getInt("TOTAL_PERGUNTAS"));
        modeloChecklist.setAtivo(rSet.getBoolean("STATUS_ATIVO"));
        return modeloChecklist;
    }

    @NotNull
    private List<PerguntaModeloChecklistVisualizacao> getPerguntasAlternativasProLog(
            @NotNull final Connection conn) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("SELECT " +
                    "  CPP.CODIGO AS COD_PERGUNTA, " +
                    "  CPP.PERGUNTA AS PERGUNTA, " +
                    "  CPP.ORDEM AS ORDEM_PERGUNTA, " +
                    "  CPP.SINGLE_CHOICE AS SINGLE_CHOICE, " +
                    "  CAPP.CODIGO AS COD_ALTERNATIVA, " +
                    "  CAPP.ALTERNATIVA AS ALTERNATIVA, " +
                    "  CAPP.PRIORIDADE AS PRIORIDADE, " +
                    "  CAPP.ORDEM AS ORDEM_ALTERNATIVA, " +
                    "  CAPP.DEVE_ABRIR_ORDEM_SERVICO AS DEVE_ABRIR_ORDEM_SERVICO, " +
                    "  CGI.URL_IMAGEM AS URL_IMAGEM, " +
                    "  CGI.COD_IMAGEM AS COD_IMAGEM, " +
                    "  CAPP.ALTERNATIVA_TIPO_OUTROS AS ALTERNATIVA_TIPO_OUTROS " +
                    "FROM CHECKLIST_PERGUNTAS_PROLOG CPP " +
                    "  JOIN CHECKLIST_ALTERNATIVA_PERGUNTA_PROLOG CAPP " +
                    "    ON CPP.CODIGO = CAPP.COD_PERGUNTA_PROLOG " +
                    "  LEFT JOIN CHECKLIST_GALERIA_IMAGENS CGI " +
                    "    ON CGI.COD_IMAGEM = CPP.COD_IMAGEM " +
                    "ORDER BY CPP.ORDEM, CPP.PERGUNTA, CAPP.ORDEM;");
            rSet = stmt.executeQuery();
            return createPerguntaAlternativaModeloChecklist(rSet);
        } finally {
            close(stmt, rSet);
        }
    }

    @NotNull
    private Galeria getGaleria(@Nullable final Long codEmpresa) throws SQLException {
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
            final List<ImagemProLog> imagensProLog = new ArrayList<>();
            while (rSet.next()) {
                final ImagemProLog imagemProLog = new ImagemProLog();
                imagemProLog.setCodImagem(rSet.getLong("COD_IMAGEM"));
                imagemProLog.setUrlImagem(rSet.getString("URL_IMAGEM"));
                imagemProLog.setDataHoraCadastro(rSet.getObject("DATA_HORA_CADASTRO", LocalDateTime.class));
                imagemProLog.setStatusImagem(rSet.getBoolean("STATUS_ATIVO"));
                imagensProLog.add(imagemProLog);
            }
            return new Galeria(imagensProLog);
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    private List<TipoVeiculo> getTipoVeiculoByCodModeloChecklist(@NotNull final Long codUnidade,
                                                                 @NotNull final Long codModelo) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
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
                final TipoVeiculo tipo = new TipoVeiculo();
                tipo.setCodigo(rSet.getLong("CODIGO"));
                tipo.setNome(rSet.getString("TIPO_VEICULO"));
                tipos.add(tipo);
            }
            return tipos;
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @SuppressWarnings("Duplicates")
    @NotNull
    private List<Cargo> getCargosByCodModelo(@NotNull final Long codUnidade,
                                             @NotNull final Long codModelo) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
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
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    private List<PerguntaModeloChecklistVisualizacao> createPerguntaAlternativaModeloChecklist(
            @NotNull final ResultSet rSet) throws SQLException {
        final List<PerguntaModeloChecklistVisualizacao> perguntas = new ArrayList<>();
        List<AlternativaModeloChecklist> alternativas = new ArrayList<>();
        PerguntaModeloChecklistVisualizacao pergunta = new PerguntaModeloChecklistVisualizacao();
        AlternativaModeloChecklistVisualizacao alternativa;
        if (rSet.first()) {
            pergunta = createPerguntaModeloChecklist(rSet);
            alternativa = createAlternativaModeloChecklist(rSet);
            alternativas.add(alternativa);
        }
        while (rSet.next()) {
            if (rSet.getLong("COD_PERGUNTA") == pergunta.getCodigo()) {
                alternativa = createAlternativaModeloChecklist(rSet);
                alternativas.add(alternativa);
            } else {
                pergunta.setAlternativas(alternativas);
                perguntas.add(pergunta);
                alternativas = new ArrayList<>();
                pergunta = createPerguntaModeloChecklist(rSet);
                alternativa = createAlternativaModeloChecklist(rSet);
                alternativas.add(alternativa);
            }
        }
        pergunta.setAlternativas(alternativas);
        perguntas.add(pergunta);
        return perguntas;
    }

    @NotNull
    private PerguntaModeloChecklistVisualizacao createPerguntaModeloChecklist(
            @NotNull final ResultSet rSet) throws SQLException {
        final PerguntaModeloChecklistVisualizacao pergunta = new PerguntaModeloChecklistVisualizacao();
        pergunta.setCodigo(rSet.getLong("COD_PERGUNTA"));
        pergunta.setOrdemExibicao(rSet.getInt("ORDEM_PERGUNTA"));
        pergunta.setDescricao(rSet.getString("PERGUNTA"));
        pergunta.setSingleChoice(rSet.getBoolean("SINGLE_CHOICE"));
        pergunta.setCodImagem(rSet.getLong("COD_IMAGEM"));
        pergunta.setUrlImagem(rSet.getString("URL_IMAGEM"));
        return pergunta;
    }

    @NotNull
    private AlternativaModeloChecklistVisualizacao createAlternativaModeloChecklist(
            @NotNull final ResultSet rSet) throws SQLException {
        final AlternativaModeloChecklistVisualizacao alternativa = new AlternativaModeloChecklistVisualizacao();
        alternativa.setCodigo(rSet.getLong("COD_ALTERNATIVA"));
        alternativa.setDescricao(rSet.getString("ALTERNATIVA"));
        alternativa.setPrioridade(PrioridadeAlternativa.fromString(rSet.getString("PRIORIDADE")));
        alternativa.setTipoOutros(rSet.getBoolean("ALTERNATIVA_TIPO_OUTROS"));
        alternativa.setDeveAbrirOrdemServico(rSet.getBoolean("DEVE_ABRIR_ORDEM_SERVICO"));
        return alternativa;
    }

    private void insertModeloTipoVeiculo(@NotNull final Connection conn,
                                         @NotNull final ModeloChecklistInsercao modeloChecklist,
                                         @NotNull final Long codModeloInserido) throws Throwable {
        PreparedStatement stmt = null;
        try {
            for (final Long codTipoVeiculo : modeloChecklist.getTiposVeiculoLiberados()) {
                stmt = conn.prepareStatement("INSERT INTO CHECKLIST_MODELO_VEICULO_TIPO VALUES (?,?,?);");
                stmt.setLong(1, modeloChecklist.getCodUnidade());
                stmt.setLong(2, codModeloInserido);
                stmt.setLong(3, codTipoVeiculo);
                if (stmt.executeUpdate() == 0) {
                    throw new SQLException("Erro ao vincular o tipo de veículo ao modelo de checklist:\n"
                            + "unidade: " + modeloChecklist.getCodUnidade() + "\n"
                            + "tipo do veículo: " + codTipoVeiculo);
                }
            }
        } finally {
            close(stmt);
        }
    }

    private void insertModeloCargo(@NotNull final Connection conn,
                                   @NotNull final ModeloChecklistInsercao modeloChecklist,
                                   @NotNull final Long codModeloInserido) throws Throwable {
        PreparedStatement stmt = null;
        try {
            for (final Long codCargo : modeloChecklist.getCargosLiberados()) {
                stmt = conn.prepareStatement(
                        "INSERT INTO CHECKLIST_MODELO_FUNCAO VALUES (?,?,?);");
                stmt.setLong(1, modeloChecklist.getCodUnidade());
                stmt.setLong(2, codModeloInserido);
                stmt.setLong(3, codCargo);
                if (stmt.executeUpdate() == 0) {
                    throw new SQLException("Erro ao vincular o cargo ao modelo de checklist:\n"
                            + "unidade: " + modeloChecklist.getCodUnidade() + "\n"
                            + "cargo: " + codCargo);
                }
            }
        } finally {
            close(stmt);
        }
    }

    private void insertModeloPerguntas(@NotNull final Connection conn,
                                       @NotNull final ModeloChecklistInsercao modeloChecklist,
                                       @NotNull final Long codModelo,
                                       @NotNull final Long codVersaoModelo) throws Throwable {
        for (final PerguntaModeloChecklistInsercao pergunta : modeloChecklist.getPerguntas()) {
            insertPerguntaAlternativaModeloChecklist(
                    conn,
                    modeloChecklist.getCodUnidade(),
                    codModelo,
                    codVersaoModelo,
                    pergunta);
        }
    }

    @NotNull
    @Deprecated
    private List<PerguntaRespostaChecklist> createPerguntasAlternativas(
            @NotNull final ResultSet rSet) throws SQLException {
        final List<PerguntaRespostaChecklist> perguntas = new ArrayList<>();
        List<AlternativaChecklist> alternativas = new ArrayList<>();
        PerguntaRespostaChecklist pergunta = new PerguntaRespostaChecklist();
        AlternativaChecklist alternativa;
        if (rSet.first()) {
            pergunta = createPergunta(rSet);
            alternativa = createAlternativa(rSet);
            alternativas.add(alternativa);
        }
        while (rSet.next()) {
            if (rSet.getLong("COD_PERGUNTA") == pergunta.getCodigo()) {
                alternativa = createAlternativa(rSet);
                alternativas.add(alternativa);
            } else {
                pergunta.setAlternativasResposta(alternativas);
                perguntas.add(pergunta);
                alternativas = new ArrayList<>();
                pergunta = createPergunta(rSet);
                alternativa = createAlternativa(rSet);
                alternativas.add(alternativa);
            }
        }
        pergunta.setAlternativasResposta(alternativas);
        perguntas.add(pergunta);
        return perguntas;
    }

    @NotNull
    @Deprecated
    private PerguntaRespostaChecklist createPergunta(@NotNull final ResultSet rSet) throws SQLException {
        final PerguntaRespostaChecklist pergunta = new PerguntaRespostaChecklist();
        pergunta.setCodigo(rSet.getLong("COD_PERGUNTA"));
        pergunta.setOrdemExibicao(rSet.getInt("ORDEM_PERGUNTA"));
        pergunta.setPergunta(rSet.getString("PERGUNTA"));
        pergunta.setSingleChoice(rSet.getBoolean("SINGLE_CHOICE"));
        pergunta.setCodImagem(rSet.getLong("COD_IMAGEM"));
        pergunta.setUrl(rSet.getString("URL_IMAGEM"));
        return pergunta;
    }

    @NotNull
    @Deprecated
    private AlternativaChecklist createAlternativa(@NotNull final ResultSet rSet) throws SQLException {
        final AlternativaChecklist alternativa = new AlternativaChecklist();
        alternativa.codigo = rSet.getLong("COD_ALTERNATIVA");
        alternativa.alternativa = rSet.getString("ALTERNATIVA");
        alternativa.prioridade = PrioridadeAlternativa.fromString(rSet.getString("PRIORIDADE"));
        if (rSet.getBoolean("ALTERNATIVA_TIPO_OUTROS")) {
            alternativa.tipo = AlternativaChecklist.TIPO_OUTROS;
        }
        return alternativa;
    }
}