package br.com.zalf.prolog.webservice.gente.quiz.modelo;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.TimeZoneManager;
import br.com.zalf.prolog.webservice.commons.questoes.Alternativa;
import br.com.zalf.prolog.webservice.commons.util.StringUtils;
import br.com.zalf.prolog.webservice.commons.util.date.Now;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.gente.colaborador.model.Cargo;
import br.com.zalf.prolog.webservice.gente.quiz.quiz.model.AlternativaEscolhaQuiz;
import br.com.zalf.prolog.webservice.gente.quiz.quiz.model.AlternativaOrdenamentoQuiz;
import br.com.zalf.prolog.webservice.gente.quiz.quiz.model.PerguntaQuiz;
import br.com.zalf.prolog.webservice.gente.treinamento.TreinamentoDao;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Zalf on 04/01/17.
 */
public final class QuizModeloDaoImpl extends DatabaseConnection implements QuizModeloDao {

    @NotNull
    @Override
    public Long insertModeloQuiz(@NotNull final Long codUnidade,
                                 @NotNull final ModeloQuiz modeloQuiz) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            stmt = conn.prepareStatement("INSERT INTO QUIZ_MODELO(COD_UNIDADE, NOME, DESCRICAO, DATA_HORA_ABERTURA, " +
                    "DATA_HORA_FECHAMENTO, PORCENTAGEM_APROVACAO) VALUES (?,?,?,?,?,?) RETURNING CODIGO;");
            final ZoneId unidadeZoneId = TimeZoneManager.getZoneIdForCodUnidade(codUnidade, conn);
            stmt.setLong(1, codUnidade);
            stmt.setString(2, modeloQuiz.getNome().trim());
            final String descricao = modeloQuiz.getDescricao();
            stmt.setString(3, StringUtils.isNullOrEmpty(descricao) ? null : descricao.trim());
            stmt.setObject(4, modeloQuiz.getDataHoraAbertura().atZone(unidadeZoneId).toOffsetDateTime());
            stmt.setObject(5, modeloQuiz.getDataHoraFechamento().atZone(unidadeZoneId).toOffsetDateTime());
            stmt.setDouble(6, modeloQuiz.getPorcentagemAprovacao());
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                final Long codModeloQuiz = rSet.getLong("CODIGO");
                modeloQuiz.setCodigo(codModeloQuiz);
                // Insere as perguntas e alternativas do modelo.
                if (modeloQuiz.getPerguntas() != null) {
                    insertPerguntasModeloQuiz(conn, codUnidade, codModeloQuiz, modeloQuiz.getPerguntas());
                }
                // Insere os cargos que podem acessar esse modelo de quiz.
                if (modeloQuiz.getFuncoesLiberadas() != null) {
                    insertCargosModeloQuiz(conn, codUnidade, codModeloQuiz, modeloQuiz.getFuncoesLiberadas());
                }
                // Insere o treinamento.
                if (modeloQuiz.getMaterialApoio() != null) {
                    // Associa o treinamento ao modelo de quiz.
                    insertQuizModeloTreinamento(
                            conn,
                            codUnidade,
                            codModeloQuiz,
                            modeloQuiz.getMaterialApoio().getCodigo());
                }
                conn.commit();
                return codModeloQuiz;
            } else {
                throw new SQLException("Erro ao inserir modelo de quiz");
            }
        } catch (final Throwable e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    public void updateModeloQuiz(@NotNull final Long codUnidade,
                                 @NotNull final ModeloQuiz modeloQuiz) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(
                    "UPDATE QUIZ_MODELO SET NOME = ?, DESCRICAO = ?, DATA_HORA_ABERTURA = ?, " +
                            "DATA_HORA_FECHAMENTO = ?, PORCENTAGEM_APROVACAO = ? WHERE CODIGO = ? AND COD_UNIDADE = ?");
            final ZoneId unidadeZoneId = TimeZoneManager.getZoneIdForCodUnidade(codUnidade, conn);
            stmt.setString(1, modeloQuiz.getNome());
            stmt.setString(2, modeloQuiz.getDescricao());
            stmt.setObject(3, modeloQuiz.getDataHoraAbertura().atZone(unidadeZoneId).toOffsetDateTime());
            stmt.setObject(4, modeloQuiz.getDataHoraFechamento().atZone(unidadeZoneId).toOffsetDateTime());
            stmt.setDouble(5, modeloQuiz.getPorcentagemAprovacao());
            stmt.setLong(6, modeloQuiz.getCodigo());
            stmt.setLong(7, codUnidade);
            if (stmt.executeUpdate() <= 0) {
                throw new SQLException("Erro ao atualizar modelo de quiz");
            }
        } finally {
            close(conn, stmt);
        }
    }

    @Override
    public void updateCargosModeloQuiz(@NotNull final Long codUnidade,
                                       @NotNull final Long codModeloQuiz,
                                       @NotNull final List<Cargo> funcoes) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            stmt = conn.prepareStatement(
                    "DELETE FROM QUIZ_MODELO_FUNCAO WHERE COD_UNIDADE = ? AND COD_MODELO = ?;");
            stmt.setLong(1, codUnidade);
            stmt.setLong(2, codModeloQuiz);
            if (stmt.executeUpdate() <= 0) {
                throw new SQLException("Erro ao deletar funções do modelo de quiz");
            }
            insertCargosModeloQuiz(conn, codUnidade, codModeloQuiz, funcoes);
            conn.commit();
        } catch (final Throwable e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        } finally {
            close(conn, stmt);
        }
    }

    @NotNull
    @Override
    public List<ModeloQuiz> getModelosQuizDisponiveis(@NotNull final Long codUnidade,
                                                      @NotNull final Long codFuncaoColaborador) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        final List<ModeloQuiz> modelos = new ArrayList<>();
        final TreinamentoDao treinamentoDao = Injection.provideTreinamentoDao();
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT " +
                    "QM.CODIGO AS CODIGO, " +
                    "QM.DATA_HORA_ABERTURA AT TIME ZONE ? AS DATA_HORA_ABERTURA, " +
                    "QM.DATA_HORA_FECHAMENTO AT TIME ZONE ? AS DATA_HORA_FECHAMENTO, " +
                    "QM.DESCRICAO AS DESCRICAO, " +
                    "QM.NOME AS NOME, " +
                    "QM.PORCENTAGEM_APROVACAO AS PORCENTAGEM_APROVACAO, " +
                    "QMT.COD_TREINAMENTO " +
                    "FROM QUIZ_MODELO QM JOIN QUIZ_MODELO_FUNCAO QMF " +
                    "  ON QM.COD_UNIDADE = QMF.COD_UNIDADE " +
                    "  AND QM.CODIGO = QMF.COD_MODELO " +
                    "  LEFT JOIN QUIZ_MODELO_TREINAMENTO QMT ON QMT.COD_MODELO_QUIZ = QM.CODIGO AND " +
                    "    QMT.COD_UNIDADE = QM.COD_UNIDADE " +
                    "WHERE QM.DATA_HORA_ABERTURA <= ? " +
                    "  AND QM.DATA_HORA_FECHAMENTO >= ? " +
                    "  AND QMF.COD_UNIDADE = ? " +
                    "  AND QMF.COD_FUNCAO_COLABORADOR = ?;");
            final OffsetDateTime now = OffsetDateTime.now(Clock.systemUTC());
            final ZoneId zoneId = TimeZoneManager.getZoneIdForCodUnidade(codUnidade, conn);
            stmt.setString(1, zoneId.getId());
            stmt.setString(2, zoneId.getId());
            stmt.setObject(3, now);
            stmt.setObject(4, now);
            stmt.setLong(5, codUnidade);
            stmt.setLong(6, codFuncaoColaborador);
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                final ModeloQuiz modelo = QuizModeloConverter.createModeloQuiz(rSet);
                modelo.setFuncoesLiberadas(getFuncoesLiberadas(conn, codUnidade, modelo.getCodigo()));
                modelo.setPerguntas(getPerguntasQuiz(conn, codUnidade, modelo.getCodigo()));
                final long codTreinamento = rSet.getLong("COD_TREINAMENTO");
                if (codTreinamento != 0) {
                    modelo.setMaterialApoio(
                            treinamentoDao.getTreinamentoByCod(codTreinamento, codUnidade, false));
                }
                modelos.add(modelo);
            }
        } finally {
            close(conn, stmt, rSet);
        }
        return modelos;
    }

    @NotNull
    @Override
    public List<ModeloQuizListagem> getModelosQuizzesByCodUnidade(@NotNull final Long codUnidade) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_QUIZ_GET_LISTAGEM_MODELOS(?, ?);");
            stmt.setLong(1, codUnidade);
            stmt.setObject(2, Now.getOffsetDateTimeUtc());
            rSet = stmt.executeQuery();
            final List<ModeloQuizListagem> modelos = new ArrayList<>();
            Set<String> cargosLiberados = null;
            Long codModeloAnterior = null;
            while (rSet.next()) {
                final Long codModeloAtual = rSet.getLong("COD_MODELO_QUIZ");
                if (codModeloAnterior == null || !codModeloAnterior.equals(codModeloAtual)) {
                    // Usamos um LinkedHashSet para manter a ordem dos nomes dos cargos.
                    cargosLiberados = new LinkedHashSet<>();
                    modelos.add(QuizModeloConverter.createModeloQuizListagem(rSet, cargosLiberados));
                }

                // O if abaixo é necessário para os casos onde o modelo não possua nenhum cargo liberado.
                if (rSet.getString("NOME_CARGO_LIBERADO") != null) {
                    cargosLiberados.add(rSet.getString("NOME_CARGO_LIBERADO"));
                }
                codModeloAnterior = codModeloAtual;
            }
            return modelos;
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public ModeloQuiz getModeloQuiz(@NotNull final Long codUnidade,
                                    @NotNull final Long codModeloQuiz) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        final TreinamentoDao treinamentoDao = Injection.provideTreinamentoDao();
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT " +
                    "QM.CODIGO AS CODIGO, " +
                    "QM.DATA_HORA_ABERTURA AT TIME ZONE ? AS DATA_HORA_ABERTURA, " +
                    "QM.DATA_HORA_FECHAMENTO AT TIME ZONE ? AS DATA_HORA_FECHAMENTO, " +
                    "QM.DESCRICAO AS DESCRICAO, " +
                    "QM.NOME AS NOME, " +
                    "QM.PORCENTAGEM_APROVACAO AS PORCENTAGEM_APROVACAO, " +
                    "QMT.COD_TREINAMENTO FROM QUIZ_MODELO QM JOIN QUIZ_MODELO_FUNCAO QMF " +
                    "  ON QM.COD_UNIDADE = QMF.COD_UNIDADE " +
                    "  AND QM.CODIGO = QMF.COD_MODELO " +
                    "  LEFT JOIN QUIZ_MODELO_TREINAMENTO QMT ON QMT.COD_MODELO_QUIZ = QM.CODIGO AND " +
                    "    QMT.COD_UNIDADE = QM.COD_UNIDADE " +
                    " WHERE " +
                    "  QMF.COD_UNIDADE = ? AND QM.CODIGO = ?;");
            final ZoneId zoneId = TimeZoneManager.getZoneIdForCodUnidade(codUnidade, conn);
            stmt.setString(1, zoneId.getId());
            stmt.setString(2, zoneId.getId());
            stmt.setLong(3, codUnidade);
            stmt.setLong(4, codModeloQuiz);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                final ModeloQuiz modelo = QuizModeloConverter.createModeloQuiz(rSet);
                modelo.setFuncoesLiberadas(getFuncoesLiberadas(conn, codUnidade, modelo.getCodigo()));
                modelo.setPerguntas(getPerguntasQuiz(conn, codUnidade, modelo.getCodigo()));
                final long codTreinamento = rSet.getLong("COD_TREINAMENTO");
                if (codTreinamento != 0) {
                    modelo.setMaterialApoio(
                            treinamentoDao.getTreinamentoByCod(codTreinamento, codUnidade, false));
                }
                return modelo;
            } else {
                throw new SQLException("Erro ao buscar modelo de quiz");
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    private List<Cargo> getFuncoesLiberadas(@NotNull final Connection conn,
                                            @NotNull final Long codUnidade,
                                            @NotNull final Long codModeloQuiz) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        final List<Cargo> funcoes = new ArrayList<>();
        try {
            stmt = conn.prepareStatement("SELECT F.CODIGO, F.NOME " +
                    "FROM FUNCAO F " +
                    "  JOIN QUIZ_MODELO_FUNCAO QMF ON F.CODIGO = QMF.COD_FUNCAO_COLABORADOR " +
                    "WHERE QMF.COD_UNIDADE = ? AND QMF.COD_MODELO = ?;");
            stmt.setLong(1, codUnidade);
            stmt.setLong(2, codModeloQuiz);
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                funcoes.add(new Cargo(rSet.getLong("CODIGO"), rSet.getString("NOME")));
            }
        } finally {
            close(stmt, rSet);
        }
        return funcoes;
    }

    @NotNull
    private List<PerguntaQuiz> getPerguntasQuiz(@NotNull final Connection conn,
                                                @NotNull final Long codUnidade,
                                                @NotNull final Long codModeloQuiz) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        final List<PerguntaQuiz> perguntas = new ArrayList<>();
        try {
            stmt = conn.prepareStatement("SELECT * FROM QUIZ_PERGUNTAS QP " +
                    "WHERE QP.COD_MODELO = ? AND QP. COD_UNIDADE = ? " +
                    "ORDER BY QP.ORDEM;");
            stmt.setLong(1, codModeloQuiz);
            stmt.setLong(2, codUnidade);
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                final PerguntaQuiz pergunta = QuizModeloConverter.createPerguntaQuiz(rSet);
                pergunta.setAlternativas(
                        getAlternativasPerguntaQuiz(
                                conn,
                                codUnidade,
                                codModeloQuiz,
                                pergunta.getCodigo(),
                                pergunta.getTipo()));
                perguntas.add(pergunta);
            }
        } finally {
            close(stmt, rSet);
        }
        return perguntas;
    }

    private List<Alternativa> getAlternativasPerguntaQuiz(@NotNull final Connection conn,
                                                          @NotNull final Long codUnidade,
                                                          @NotNull final Long codModeloQuiz,
                                                          @NotNull final Long codPergunta,
                                                          @NotNull final String tipoPergunta) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        final List<Alternativa> alternativas = new ArrayList<>();
        try {
            stmt = conn.prepareStatement("SELECT *, NULL AS SELECIONADA, NULL AS ORDEM_SELECIONADA " +
                    "FROM QUIZ_ALTERNATIVA_PERGUNTA WHERE COD_MODELO = ? AND COD_UNIDADE = ? AND COD_PERGUNTA = ? " +
                    "ORDER BY ORDEM;");
            stmt.setLong(1, codModeloQuiz);
            stmt.setLong(2, codUnidade);
            stmt.setLong(3, codPergunta);
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                alternativas.add(QuizModeloConverter.createAlternativa(rSet, tipoPergunta));
            }
        } finally {
            close(stmt, rSet);
        }
        return alternativas;
    }

    private void insertPerguntasModeloQuiz(@NotNull final Connection conn,
                                           @NotNull final Long codUnidade,
                                           @NotNull final Long codModeloQuiz,
                                           @NotNull final List<PerguntaQuiz> perguntas) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("INSERT INTO QUIZ_PERGUNTAS(COD_MODELO, COD_UNIDADE, PERGUNTA, ORDEM, " +
                    "TIPO, URL_IMAGEM) VALUES (?, ?, ?, ?, ?, ?) RETURNING CODIGO;");
            stmt.setLong(1, codModeloQuiz);
            stmt.setLong(2, codUnidade);
            for (final PerguntaQuiz pergunta : perguntas) {
                stmt.setString(3, pergunta.getPergunta());
                stmt.setInt(4, pergunta.getOrdemExibicao());
                stmt.setString(5, pergunta.getTipo());
                stmt.setString(6, pergunta.getUrlImagem());
                rSet = stmt.executeQuery();
                if (rSet.next()) {
                    pergunta.setCodigo(rSet.getLong("CODIGO"));
                    insertAlternativasPerguntaModeloQuiz(
                            conn,
                            codUnidade,
                            codModeloQuiz,
                            pergunta.getCodigo(),
                            pergunta.getTipo(),
                            pergunta.getAlternativas());
                } else {
                    throw new SQLException("Erro ao inserir a pergunta");
                }
            }
        } finally {
            close(stmt, rSet);
        }
    }

    private void insertAlternativasPerguntaModeloQuiz(@NotNull final Connection conn,
                                                      @NotNull final Long codUnidade,
                                                      @NotNull final Long codModeloQuiz,
                                                      @NotNull final Long codPergunta,
                                                      @NotNull final String tipoPergunta,
                                                      @NotNull final List<Alternativa> alternativas) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement(
                    "INSERT INTO QUIZ_ALTERNATIVA_PERGUNTA(COD_MODELO, COD_UNIDADE, COD_PERGUNTA, " +
                    "ALTERNATIVA, ORDEM, CORRETA) VALUES (?, ?, ?, ?, ?, ?) RETURNING CODIGO");
            stmt.setLong(1, codModeloQuiz);
            stmt.setLong(2, codUnidade);
            stmt.setLong(3, codPergunta);
            for (final Alternativa alternativa : alternativas) {
                stmt.setString(4, alternativa.alternativa);
                if (tipoPergunta.equals(PerguntaQuiz.TIPO_ORDERING)) {
                    final AlternativaOrdenamentoQuiz alternativaOrdenamentoQuiz =
                            (AlternativaOrdenamentoQuiz) alternativa;
                    stmt.setInt(5, alternativaOrdenamentoQuiz.getOrdemCorreta());
                    stmt.setNull(6, Types.BOOLEAN);
                } else {
                    final AlternativaEscolhaQuiz alternativaEscolhaQuiz = (AlternativaEscolhaQuiz) alternativa;
                    stmt.setInt(5, alternativaEscolhaQuiz.getOrdemExibicao());
                    stmt.setBoolean(6, alternativaEscolhaQuiz.isCorreta());
                }
                rSet = stmt.executeQuery();
                if (rSet.next()) {
                    alternativa.setCodigo(rSet.getLong("CODIGO"));
                } else {
                    throw new SQLException("Erro ao inserir a alternativa");
                }
            }
        } finally {
            close(stmt, rSet);
        }
    }

    private void insertCargosModeloQuiz(@NotNull final Connection conn,
                                        @NotNull final Long codUnidade,
                                        @NotNull final Long codModeloQuiz,
                                        @NotNull final List<Cargo> funcoesLiberadas) throws Throwable {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(
                    "INSERT INTO QUIZ_MODELO_FUNCAO(COD_UNIDADE, COD_MODELO, COD_FUNCAO_COLABORADOR) " +
                            "VALUES (?, ?, ?);");
            stmt.setLong(1, codUnidade);
            stmt.setLong(2, codModeloQuiz);
            for (final Cargo cargo : funcoesLiberadas) {
                stmt.setLong(3, cargo.getCodigo());
                if (stmt.executeUpdate() <= 0) {
                    throw new SQLException("Erro ao vincular o cargo ao modelo de quiz");
                }
            }
        } finally {
            close(stmt);
        }
    }

    private void insertQuizModeloTreinamento(@NotNull final Connection conn,
                                             @NotNull final Long codUnidade,
                                             @NotNull final Long codModeloQuiz,
                                             @NotNull final Long codTreinamento) throws Throwable {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(
                    "INSERT INTO QUIZ_MODELO_TREINAMENTO(COD_MODELO_QUIZ, COD_UNIDADE, COD_TREINAMENTO) " +
                    "VALUES (?, ?, ?)");
            stmt.setLong(1, codModeloQuiz);
            stmt.setLong(2, codUnidade);
            stmt.setLong(3, codTreinamento);
            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Erro ao inserir o treinamento do Quiz");
            }
        } finally {
            close(stmt);
        }
    }
}