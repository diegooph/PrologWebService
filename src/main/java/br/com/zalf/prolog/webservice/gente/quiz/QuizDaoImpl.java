package br.com.zalf.prolog.webservice.gente.quiz;

import br.com.zalf.prolog.commons.questoes.Alternativa;
import br.com.zalf.prolog.commons.util.DateUtils;
import br.com.zalf.prolog.gente.quiz.AlternativaEscolhaQuiz;
import br.com.zalf.prolog.gente.quiz.AlternativaOrdenamentoQuiz;
import br.com.zalf.prolog.gente.quiz.PerguntaQuiz;
import br.com.zalf.prolog.gente.quiz.Quiz;
import br.com.zalf.prolog.webservice.DatabaseConnection;
import br.com.zalf.prolog.webservice.util.GsonUtils;
import br.com.zalf.prolog.webservice.util.L;

import java.sql.*;
import java.util.Date;

/**
 * Created by Zalf on 05/01/17.
 */
public class QuizDaoImpl extends DatabaseConnection {

    private static final String TAG = QuizDaoImpl.class.getSimpleName();

    public boolean insert (Quiz quiz) throws SQLException{
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try{
            conn = getConnection();
            conn.setAutoCommit(false);
            // salvar dados do quiz e setar o codigo gerado pelo BD
            stmt = conn.prepareStatement("INSERT INTO quiz" +
                    "(cod_modelo, cod_unidade, cpf_colaborador, data_hora, tempo_realizacao, qt_corretas, qt_erradas) " +
                    "VALUES (?, (SELECT COD_UNIDADE FROM COLABORADOR WHERE CPF = ?), ?, ?, ?, ?, ?) RETURNING CODIGO;");
            stmt.setLong(1, quiz.getCodModeloQuiz());
            stmt.setLong(2, quiz.getColaborador().getCpf());
            stmt.setLong(3, quiz.getColaborador().getCpf());
            stmt.setTimestamp(4, DateUtils.toTimestamp(new Date(System.currentTimeMillis())));
            stmt.setLong(5, quiz.getTempoRealizacaoInMillis());
            stmt.setInt(6, quiz.getQtdCorretas());
            stmt.setInt(7, quiz.getQtdErradas());
            rSet = stmt.executeQuery();
            if(rSet.next()){
                quiz.setCodigo(rSet.getLong("CODIGO"));
                insertRespostas(quiz, conn);
                conn.commit();
            }
        }catch (SQLException e){
            e.printStackTrace();
            conn.rollback();
            return false;
        }
        finally {
            closeConnection(conn, stmt, rSet);
        }
        return true;
    }

    private void insertRespostas(Quiz quiz, Connection conn) throws SQLException{
        PreparedStatement stmt = null;
        try{
            stmt = conn.prepareStatement("INSERT INTO quiz_respostas(cod_modelo, cod_unidade, cod_quiz, cod_pergunta, cod_alternativa, ordem_selecionada, selecionada)\n" +
                    "    VALUES(?, (SELECT COD_UNIDADE FROM COLABORADOR WHERE CPF = ?), ?, ?, ?, ?, ?)");
            stmt.setLong(1, quiz.getCodModeloQuiz());
            stmt.setLong(2, quiz.getColaborador().getCpf());
            stmt.setLong(3, quiz.getCodigo());
            for(PerguntaQuiz pergunta : quiz.getPerguntas()){
                stmt.setLong(4, pergunta.getCodigo());
                for(Alternativa alternativa : pergunta.getAlternativas()){
                    stmt.setLong(5, alternativa.getCodigo());
                    L.d(TAG, "Inserindo a alternativa:" + GsonUtils.getGson().toJson(alternativa));
                    if(pergunta.getTipo().equals(PerguntaQuiz.TIPO_ORDERING)){
                        AlternativaOrdenamentoQuiz alternativaOrdenamentoQuiz = (AlternativaOrdenamentoQuiz) alternativa;
                        stmt.setInt(6, alternativaOrdenamentoQuiz.getOrdemSelecionada());
                        stmt.setNull(7, Types.BOOLEAN);
                    }else{
                        AlternativaEscolhaQuiz alternativaEscolhaQuiz = (AlternativaEscolhaQuiz) alternativa;
                        stmt.setNull(6, Types.INTEGER);
                        stmt.setBoolean(7, alternativaEscolhaQuiz.isSelecionada());
                    }
                    stmt.executeUpdate();
                }
            }
        }finally {
            closeConnection(null, stmt, null);
        }
    }
}
