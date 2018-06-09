package br.com.zalf.prolog.webservice.gente.quiz.quizModelo;

import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.TimeZoneManager;
import br.com.zalf.prolog.webservice.colaborador.model.Cargo;
import br.com.zalf.prolog.webservice.commons.questoes.Alternativa;
import br.com.zalf.prolog.webservice.gente.quiz.quiz.model.AlternativaEscolhaQuiz;
import br.com.zalf.prolog.webservice.gente.quiz.quiz.model.AlternativaOrdenamentoQuiz;
import br.com.zalf.prolog.webservice.gente.quiz.quiz.model.PerguntaQuiz;
import br.com.zalf.prolog.webservice.gente.treinamento.TreinamentoDao;

import java.sql.*;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zalf on 04/01/17.
 */
public class QuizModeloDaoImpl extends DatabaseConnection implements QuizModeloDao {

    @Override
    public List<ModeloQuiz> getModelosQuizDisponiveis(Long codUnidade, Long codFuncaoColaborador) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        List<ModeloQuiz> modelos = new ArrayList<>();
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
                    "WHERE QM.DATA_HORA_ABERTURA <= (? AT TIME ZONE ?) " +
                    "  AND QM.DATA_HORA_FECHAMENTO >= (? AT TIME ZONE ?) " +
                    "  AND QMF.COD_UNIDADE = ? " +
                    "  AND QMF.COD_FUNCAO_COLABORADOR = ?;");
            final OffsetDateTime now = OffsetDateTime.now(Clock.systemUTC());
            final ZoneId zoneId = TimeZoneManager.getZoneIdForCodUnidade(codUnidade, conn);
            stmt.setString(1, zoneId.getId());
            stmt.setString(2, zoneId.getId());
            stmt.setObject(3, now);
            stmt.setString(4, zoneId.getId());
            stmt.setObject(5, now);
            stmt.setString(6, zoneId.getId());
            stmt.setLong(7, codUnidade);
            stmt.setLong(8, codFuncaoColaborador);
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                final ModeloQuiz modelo = QuizModeloConverter.createModeloQuiz(rSet);
                modelo.setFuncoesLiberadas(getFuncoesLiberadas(modelo.getCodigo(), codUnidade, conn));
                modelo.setPerguntas(getPerguntasQuiz(modelo.getCodigo(), codUnidade, conn));
                final long codTreinamento = rSet.getLong("COD_TREINAMENTO");
                if (codTreinamento != 0) {
                    modelo.setMaterialApoio(treinamentoDao.getTreinamentoByCod(codTreinamento, codUnidade, false));
                }
                modelos.add(modelo);
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return modelos;
    }

    @Override
    public ModeloQuiz getModeloQuiz(Long codUnidade, Long codModeloQuiz) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        ModeloQuiz modelo = null;
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
                modelo = QuizModeloConverter.createModeloQuiz(rSet);
                modelo.setFuncoesLiberadas(getFuncoesLiberadas(modelo.getCodigo(), codUnidade, conn));
                modelo.setPerguntas(getPerguntasQuiz(modelo.getCodigo(), codUnidade, conn));
                final long codTreinamento = rSet.getLong("COD_TREINAMENTO");
                if (codTreinamento != 0) {
                    modelo.setMaterialApoio(treinamentoDao.getTreinamentoByCod(codTreinamento, codUnidade, false));
                }
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return modelo;
    }

    @Override
    public Long insertModeloQuiz(ModeloQuiz modeloQuiz, Long codUnidade) throws SQLException {
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
            stmt.setString(2, modeloQuiz.getNome());
            stmt.setString(3, modeloQuiz.getDescricao());
            stmt.setObject(4, modeloQuiz.getDataHoraAbertura().atZone(unidadeZoneId).toOffsetDateTime());
            stmt.setObject(5, modeloQuiz.getDataHoraFechamento().atZone(unidadeZoneId).toOffsetDateTime());
            stmt.setDouble(6, modeloQuiz.getPorcentagemAprovacao());
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                final Long codModeloQuiz = rSet.getLong("CODIGO");
                modeloQuiz.setCodigo(codModeloQuiz);
                // Insere as perguntas e alternativas do modelo.
                if (modeloQuiz.getPerguntas() != null) {
                    insertPerguntasModeloQuiz(modeloQuiz.getPerguntas(), codModeloQuiz, codUnidade, conn);
                }
                // Insere os cargos que podem acessar esse modelo de quiz.
                if (modeloQuiz.getFuncoesLiberadas() != null) {
                    insertCargosModeloQuiz(modeloQuiz.getFuncoesLiberadas(), codModeloQuiz, codUnidade, conn);
                }
                // Insere o treinamento.
                if (modeloQuiz.getMaterialApoio() != null) {
                    // Associa o treinamento ao modleo de quiz.
                    insertQuizModeloTreinamento(modeloQuiz.getMaterialApoio().getCodigo(), codModeloQuiz, codUnidade, conn);
                }
                conn.commit();
                return codModeloQuiz;
            }
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return null;
    }

    @Override
    public List<ModeloQuiz> getModelosQuizByCodUnidade(Long codUnidade) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        final List<ModeloQuiz> quizzes = new ArrayList<>();
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT CODIGO, NOME FROM QUIZ_MODELO WHERE COD_UNIDADE = ? ORDER BY 1;");
            stmt.setLong(1, codUnidade);
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                final ModeloQuiz modeloQuiz = new ModeloQuiz();
                modeloQuiz.setCodigo(rSet.getLong("CODIGO"));
                modeloQuiz.setNome(rSet.getString("NOME"));
                quizzes.add(modeloQuiz);
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return quizzes;
    }

    @Override
    public boolean updateModeloQuiz(ModeloQuiz modeloQuiz, Long codUnidade) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("UPDATE QUIZ_MODELO SET NOME = ?, DESCRICAO = ?, DATA_HORA_ABERTURA = " +
                    "?, DATA_HORA_FECHAMENTO = ?, PORCENTAGEM_APROVACAO = ? WHERE CODIGO = ? AND COD_UNIDADE = ?");
            final ZoneId unidadeZoneId = TimeZoneManager.getZoneIdForCodUnidade(codUnidade, conn);
            stmt.setString(1, modeloQuiz.getNome());
            stmt.setString(2, modeloQuiz.getDescricao());
            stmt.setObject(3, modeloQuiz.getDataHoraAbertura().atZone(unidadeZoneId).toOffsetDateTime());
            stmt.setObject(4, modeloQuiz.getDataHoraFechamento().atZone(unidadeZoneId).toOffsetDateTime());
            stmt.setDouble(5, modeloQuiz.getPorcentagemAprovacao());
            stmt.setLong(6, modeloQuiz.getCodigo());
            stmt.setLong(7, codUnidade);
            return stmt.executeUpdate() > 0;
        } finally {
            closeConnection(conn, stmt, null);
        }
    }

    @Override
    public boolean updateCargosModeloQuiz(List<Cargo> funcoes, Long codModeloQuiz, Long codUnidade) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("DELETE FROM QUIZ_MODELO_FUNCAO WHERE COD_UNIDADE = ? AND COD_MODELO = ?;");
            stmt.setLong(1, codUnidade);
            stmt.setLong(2, codModeloQuiz);
            stmt.executeUpdate();
            insertCargosModeloQuiz(funcoes, codModeloQuiz, codUnidade, conn);
            return true;
        } finally {
            closeConnection(conn, stmt, null);
        }
    }

    private List<Cargo> getFuncoesLiberadas(Long codModeloQuiz, Long codUnidade, Connection conn) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        List<Cargo> funcoes = new ArrayList<>();
        try {
            stmt = conn.prepareStatement("SELECT F.* FROM QUIZ_MODELO QM JOIN QUIZ_MODELO_FUNCAO QMF " +
                    "  ON QM.COD_UNIDADE = QMF.COD_UNIDADE " +
                    "  AND QM.CODIGO = QMF.COD_MODELO " +
                    "    JOIN UNIDADE_FUNCAO UF ON UF.COD_FUNCAO = QMF.COD_FUNCAO_COLABORADOR AND UF.COD_UNIDADE = QMF.COD_UNIDADE " +
                    "    JOIN FUNCAO F ON F.CODIGO = UF.COD_FUNCAO " +
                    "WHERE QMF.COD_UNIDADE = ? " +
                    "  AND QMF.COD_MODELO = ?;");
            stmt.setLong(1, codUnidade);
            stmt.setLong(2, codModeloQuiz);
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                final Cargo cargo = new Cargo();
                cargo.setCodigo(rSet.getLong("CODIGO"));
                cargo.setNome(rSet.getString("NOME"));
                funcoes.add(cargo);
            }
        } finally {
            closeConnection(null, stmt, rSet);
        }
        return funcoes;
    }

    private List<PerguntaQuiz> getPerguntasQuiz(Long codModeloQuiz, Long codUnidade, Connection conn) throws SQLException {
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
                pergunta.setAlternativas(getAlternativasPerguntaQuiz(codModeloQuiz, codUnidade, pergunta.getCodigo(),
                        pergunta.getTipo(), conn));
                perguntas.add(pergunta);
            }
        } finally {
            closeConnection(null, stmt, rSet);
        }
        return perguntas;
    }

    private List<Alternativa> getAlternativasPerguntaQuiz(Long codModeloQuiz, Long codUnidade, Long codPergunta,
                                                          String tipoPergunta, Connection conn) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        final List<Alternativa> alternativas = new ArrayList<>();
        try {
            stmt = conn.prepareStatement("SELECT *, NULL AS SELECIONADA, NULL AS ORDEM_SELECIONADA FROM QUIZ_ALTERNATIVA_PERGUNTA " +
                    "WHERE COD_MODELO = ? AND COD_UNIDADE = ? AND COD_PERGUNTA = ? " +
                    "ORDER BY ORDEM;");
            stmt.setLong(1, codModeloQuiz);
            stmt.setLong(2, codUnidade);
            stmt.setLong(3, codPergunta);
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                alternativas.add(QuizModeloConverter.createAlternativa(rSet, tipoPergunta));
            }
        } finally {
            closeConnection(null, stmt, rSet);
        }
        return alternativas;
    }

    private void insertPerguntasModeloQuiz(List<PerguntaQuiz> perguntas, Long codModeloQuiz, Long codUnidade,
                                           Connection conn) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("INSERT INTO QUIZ_PERGUNTAS(COD_MODELO, COD_UNIDADE, PERGUNTA, ORDEM, " +
                    "TIPO, URL_IMAGEM) VALUES (?,?,?,?,?,?) RETURNING CODIGO;");
            stmt.setLong(1, codModeloQuiz);
            stmt.setLong(2, codUnidade);
            for (PerguntaQuiz pergunta : perguntas) {
                stmt.setString(3, pergunta.getPergunta());
                stmt.setInt(4, pergunta.getOrdemExibicao());
                stmt.setString(5, pergunta.getTipo());
                stmt.setString(6, pergunta.getUrlImagem());
                rSet = stmt.executeQuery();
                if (rSet.next()) {
                    pergunta.setCodigo(rSet.getLong("CODIGO"));
                    insertAlternativasPerguntaModeloQuiz(pergunta.getAlternativas(), codModeloQuiz, pergunta.getCodigo(),
                            codUnidade, pergunta.getTipo(), conn);
                } else {
                    throw new SQLException("Erro ao inserir a pergunta");
                }
            }
        } finally {
            closeConnection(null, stmt, rSet);
        }
    }

    private void insertAlternativasPerguntaModeloQuiz(List<Alternativa> alternativas, Long codModeloQuiz, Long codPergunta,
                                                      Long codUnidade, String tipoPergunta, Connection conn) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("INSERT INTO QUIZ_ALTERNATIVA_PERGUNTA(COD_MODELO, COD_UNIDADE, COD_PERGUNTA, " +
                    "ALTERNATIVA, ORDEM, CORRETA) VALUES (?,?,?,?,?,?) RETURNING CODIGO");
            stmt.setLong(1, codModeloQuiz);
            stmt.setLong(2, codUnidade);
            stmt.setLong(3, codPergunta);
            for (Alternativa alternativa : alternativas) {
                stmt.setString(4, alternativa.alternativa);
                if (tipoPergunta.equals(PerguntaQuiz.TIPO_ORDERING)) {
                    AlternativaOrdenamentoQuiz alternativaOrdenamentoQuiz = (AlternativaOrdenamentoQuiz) alternativa;
                    stmt.setInt(5, alternativaOrdenamentoQuiz.getOrdemCorreta());
                    stmt.setNull(6, Types.BOOLEAN);
                } else {
                    AlternativaEscolhaQuiz alternativaEscolhaQuiz = (AlternativaEscolhaQuiz) alternativa;
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
            closeConnection(null, stmt, rSet);
        }
    }

    private void insertCargosModeloQuiz(List<Cargo> funcoesLiberadas, Long codModeloQuiz, Long codUnidade,
                                        Connection conn) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("INSERT INTO QUIZ_MODELO_FUNCAO(COD_UNIDADE, COD_MODELO, COD_FUNCAO_COLABORADOR) VALUES (?,?,?);");
            stmt.setLong(1, codUnidade);
            stmt.setLong(2, codModeloQuiz);
            for (Cargo cargo : funcoesLiberadas) {
                stmt.setLong(3, cargo.getCodigo());
                int count = stmt.executeUpdate();
                if (count == 0) {
                    throw new SQLException("Erro ao vincular o cargo ao modelo de quiz");
                }
            }
        } finally {
            closeStatement(stmt);
        }
    }

    private void insertQuizModeloTreinamento(Long codTreinamento, Long codModeloQuiz, Long codUnidade, Connection conn)
            throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("INSERT INTO QUIZ_MODELO_TREINAMENTO(COD_MODELO_QUIZ, COD_UNIDADE, COD_TREINAMENTO) VALUES (?,?,?)");
            stmt.setLong(1, codModeloQuiz);
            stmt.setLong(2, codUnidade);
            stmt.setLong(3, codTreinamento);
            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Erro ao inserir o treinamento do Quiz");
            }
        } finally {
            closeStatement(stmt);
        }
    }
}