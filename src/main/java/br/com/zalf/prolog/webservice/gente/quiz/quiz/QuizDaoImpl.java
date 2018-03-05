package br.com.zalf.prolog.webservice.gente.quiz.quiz;

import br.com.zalf.prolog.webservice.DatabaseConnection;
import br.com.zalf.prolog.webservice.TimeZoneManager;
import br.com.zalf.prolog.webservice.commons.questoes.Alternativa;
import br.com.zalf.prolog.webservice.gente.quiz.quiz.model.AlternativaEscolhaQuiz;
import br.com.zalf.prolog.webservice.gente.quiz.quiz.model.AlternativaOrdenamentoQuiz;
import br.com.zalf.prolog.webservice.gente.quiz.quiz.model.PerguntaQuiz;
import br.com.zalf.prolog.webservice.gente.quiz.quiz.model.Quiz;
import br.com.zalf.prolog.webservice.gente.quiz.quizModelo.QuizModeloConverter;

import java.sql.*;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zalf on 05/01/17.
 */
public class QuizDaoImpl extends DatabaseConnection implements QuizDao {

    public QuizDaoImpl() {

    }

    @Override
    public boolean insert(Quiz quiz) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            stmt = conn.prepareStatement("INSERT INTO quiz " +
                    "(cod_modelo, cod_unidade, cpf_colaborador, data_hora, tempo_realizacao, qt_corretas, qt_erradas) "+
                    "VALUES (?, (SELECT COD_UNIDADE FROM COLABORADOR WHERE CPF = ?), ?, ?, ?, ?, ?) RETURNING CODIGO;");
            stmt.setLong(1, quiz.getCodModeloQuiz());
            stmt.setLong(2, quiz.getColaborador().getCpf());
            stmt.setLong(3, quiz.getColaborador().getCpf());
            stmt.setObject(4, OffsetDateTime.now(Clock.systemUTC()));
            stmt.setLong(5, quiz.getTempoRealizacaoInMillis());
            stmt.setInt(6, quiz.calculaQtdRespostasCorretas());
            stmt.setInt(7, quiz.calculaQtdRespostasErradas());
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                quiz.setCodigo(rSet.getLong("CODIGO"));
                insertRespostas(quiz, conn);
                conn.commit();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            conn.rollback();
            return false;
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return true;
    }

    @Override
    public List<Quiz> getRealizadosByColaborador(Long cpf, int limit, int offset) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        final List<Quiz> quizes = new ArrayList<>();
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT " +
                    "Q.CODIGO AS CODIGO, " +
                    "Q.COD_MODELO AS COD_MODELO, " +
                    "Q.DATA_HORA AT TIME ZONE ? AS DATA_HORA, " +
                    "QM.NOME AS NOME, " +
                    "Q.QT_CORRETAS AS QT_CORRETAS, " +
                    "Q.QT_ERRADAS AS QT_ERRADAS, " +
                    "Q.TEMPO_REALIZACAO AS TEMPO_REALIZACAO, " +
                    "(CASE WHEN Q.QT_CORRETAS >= ((Q.QT_CORRETAS + Q.QT_ERRADAS) * QM.PORCENTAGEM_APROVACAO) " +
                    "THEN TRUE ELSE FALSE END) AS APROVADO " +
                    "FROM QUIZ Q JOIN QUIZ_MODELO QM ON Q.COD_MODELO = QM.CODIGO " +
                    "AND Q.COD_UNIDADE = QM.COD_UNIDADE WHERE CPF_COLABORADOR = ? " +
                    "ORDER BY DATA_HORA DESC " +
                    "LIMIT ? OFFSET ?");
            stmt.setString(1, TimeZoneManager.getZoneIdForCpf(cpf, conn).getId());
            stmt.setLong(2, cpf);
            stmt.setInt(3, limit);
            stmt.setInt(4, offset);
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                quizes.add(createQuiz(rSet));
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return quizes;
    }

    @Override
    public Quiz getByCod(Long codUnidade, Long codQuiz, Long codModeloQuiz) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        Quiz quiz = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT Q.CODIGO AS CODIGO, " +
                    "Q.COD_MODELO AS COD_MODELO, " +
                    "Q.DATA_HORA AT TIME ZONE ? AS DATA_HORA, " +
                    "QM.NOME AS NOME, " +
                    "Q.QT_CORRETAS AS QT_CORRETAS, " +
                    "Q.QT_ERRADAS AS QT_ERRADAS, " +
                    "Q.TEMPO_REALIZACAO AS TEMPO_REALIZACAO, " +
                    "(CASE WHEN Q.QT_CORRETAS >= ((Q.qt_corretas + Q.qt_erradas) * QM.PORCENTAGEM_APROVACAO) " +
                    "THEN TRUE ELSE FALSE END) AS APROVADO " +
                    "FROM QUIZ q join quiz_modelo QM ON Q.cod_modelo = QM.codigo\n" +
                    "AND Q.cod_unidade = QM.cod_unidade " +
                    "WHERE Q.CODIGO = ? and Q.cod_modelo = ? and Q.cod_unidade = ?");
            stmt.setString(1, TimeZoneManager.getZoneIdForCodUnidade(codUnidade, conn).getId());
            stmt.setLong(2, codQuiz);
            stmt.setLong(3, codModeloQuiz);
            stmt.setLong(4, codUnidade);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                quiz = createQuiz(rSet);
                quiz.setPerguntas(getPerguntasByCodQuiz(codQuiz, codUnidade, codModeloQuiz, conn));
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return quiz;
    }

    private void insertRespostas(Quiz quiz, Connection conn) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("INSERT INTO quiz_respostas(cod_modelo, cod_unidade, cod_quiz, " +
                    "cod_pergunta, cod_alternativa, ordem_selecionada, selecionada) " +
                    "VALUES(?, (SELECT COD_UNIDADE FROM COLABORADOR WHERE CPF = ?), ?, ?, ?, ?, ?);");
            stmt.setLong(1, quiz.getCodModeloQuiz());
            stmt.setLong(2, quiz.getColaborador().getCpf());
            stmt.setLong(3, quiz.getCodigo());
            for (final PerguntaQuiz pergunta : quiz.getPerguntas()) {
                stmt.setLong(4, pergunta.getCodigo());
                for (Alternativa alternativa : pergunta.getAlternativas()) {
                    stmt.setLong(5, alternativa.getCodigo());
                    if (pergunta.getTipo().equals(PerguntaQuiz.TIPO_ORDERING)) {
                        final AlternativaOrdenamentoQuiz alternativaOrdenamentoQuiz = (AlternativaOrdenamentoQuiz)
                                alternativa;
                        stmt.setInt(6, alternativaOrdenamentoQuiz.getOrdemSelecionada());
                        stmt.setNull(7, Types.BOOLEAN);
                    } else {
                        final AlternativaEscolhaQuiz alternativaEscolhaQuiz = (AlternativaEscolhaQuiz) alternativa;
                        stmt.setNull(6, Types.INTEGER);
                        stmt.setBoolean(7, alternativaEscolhaQuiz.isSelecionada());
                    }
                    stmt.executeUpdate();
                }
            }
        } finally {
            closeConnection(null, stmt, null);
        }
    }

    private Quiz createQuiz(ResultSet rSet) throws SQLException {
        final Quiz quiz = new Quiz();
        quiz.setCodigo(rSet.getLong("CODIGO"));
        quiz.setCodModeloQuiz(rSet.getLong("COD_MODELO"));
        quiz.setDataHoraRealizacao(rSet.getObject("DATA_HORA", LocalDateTime.class));
        quiz.setNome(rSet.getString("NOME"));
        quiz.setQtdRespostasCorretas(rSet.getInt("QT_CORRETAS"));
        quiz.setQtdRespostasErradas(rSet.getInt("QT_ERRADAS"));
        quiz.setTempoRealizacaoInMillis(rSet.getLong("TEMPO_REALIZACAO"));
        quiz.setAprovado(rSet.getBoolean("APROVADO"));
        return quiz;
    }

    private List<PerguntaQuiz> getPerguntasByCodQuiz(Long codQuiz, Long codUnidade, Long codModeloQuiz, Connection
            conn) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        final List<PerguntaQuiz> perguntas = new ArrayList<>();
        try {
            stmt = conn.prepareStatement("SELECT DISTINCT QP.* FROM quiz_respostas QR\n" +
                    "JOIN quiz_perguntas QP ON QP.cod_unidade = QR.cod_unidade AND QP.CODIGO = QR.cod_pergunta " +
                    "AND QP.cod_modelo = QR.cod_modelo\n" +
                    "WHERE QR.cod_unidade = ? AND QR.cod_quiz = ? and QR.cod_modelo = ?\n" +
                    "ORDER BY QP.ordem");
            stmt.setLong(1, codUnidade);
            stmt.setLong(2, codQuiz);
            stmt.setLong(3, codModeloQuiz);
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                final PerguntaQuiz pergunta = QuizModeloConverter.createPerguntaQuiz(rSet);
                pergunta.setAlternativas(getAlternativasPergunta(pergunta.getCodigo(), codUnidade, codQuiz,
                        codModeloQuiz,
                        pergunta.getTipo(), conn));
                perguntas.add(pergunta);
            }
        } finally {
            closeConnection(null, stmt, rSet);
        }
        return perguntas;
    }

    private List<Alternativa> getAlternativasPergunta(Long codPergunta, Long codUnidade, Long codQuiz,
                                                      Long codModeloQuiz, String tipoPergunta, Connection conn)
            throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        final List<Alternativa> alternativas = new ArrayList<>();
        try {
            stmt = conn.prepareStatement("SELECT QAP.*, QR.ordem_selecionada, QR.selecionada FROM " +
                    "quiz_alternativa_pergunta QAP JOIN quiz_respostas QR ON " +
                    "QAP.cod_modelo = QR.cod_modelo AND QAP.cod_unidade = QR.cod_unidade AND QAP.cod_pergunta = QR.cod_pergunta " +
                    "AND QAP.codigo = QR.cod_alternativa " +
                    "WHERE QR.COD_MODELO = ? AND QR.cod_unidade = ? AND QR.cod_quiz = ? AND QR.cod_pergunta = ? " +
                    "ORDER BY QAP.ordem;");
            stmt.setLong(1, codModeloQuiz);
            stmt.setLong(2, codUnidade);
            stmt.setLong(3, codQuiz);
            stmt.setLong(4, codPergunta);
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                alternativas.add(QuizModeloConverter.createAlternativa(rSet, tipoPergunta));
            }
        } finally {
            closeConnection(null, stmt, rSet);
        }
        return alternativas;
    }
}