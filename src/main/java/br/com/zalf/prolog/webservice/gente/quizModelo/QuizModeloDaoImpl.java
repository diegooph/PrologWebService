package br.com.zalf.prolog.webservice.gente.quizModelo;

import br.com.zalf.prolog.commons.colaborador.Funcao;
import br.com.zalf.prolog.commons.questoes.Alternativa;
import br.com.zalf.prolog.commons.util.DateUtils;
import br.com.zalf.prolog.gente.quiz.ModeloQuiz;
import br.com.zalf.prolog.gente.quiz.PerguntaQuiz;
import br.com.zalf.prolog.webservice.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Zalf on 04/01/17.
 */
public class QuizModeloDaoImpl extends DatabaseConnection implements QuizModeloDao{

    private static final String TAG = QuizModeloDaoImpl.class.getSimpleName();

    public List<ModeloQuiz> getModelosQuizDisponiveisByCodUnidadeByCodFuncao(Long codUnidade, Long codFuncaoColaborador) throws SQLException{
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        List<ModeloQuiz> modelos = new ArrayList<>();
        try{
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT QM.* FROM quiz_modelo QM JOIN quiz_modelo_funcao QMF \n" +
                    "  ON QM.cod_unidade = QMF.cod_unidade \n" +
                    "  AND QM.codigo = QMF.cod_modelo\n" +
                    "WHERE QM.data_hora_abertura <= ? \n" +
                    "  AND data_hora_fechamento >= ?\n" +
                    "  AND QMF.cod_unidade = ?\n" +
                    "  AND QMF.cod_funcao_colaborador = ?");
            stmt.setTimestamp(1, DateUtils.toTimestamp(new Date(System.currentTimeMillis())));
            stmt.setTimestamp(2, DateUtils.toTimestamp(new Date(System.currentTimeMillis())));
            stmt.setLong(3, codUnidade);
            stmt.setLong(4, codFuncaoColaborador);
            rSet = stmt.executeQuery();
            while(rSet.next()){
                ModeloQuiz modelo = QuizModeloConverter.createModeloQuiz(rSet);
                modelo.setFuncoesLiberadas(getFuncoesLiberadasByCodModeloByCodUnidade(modelo.getCodigo(), codUnidade, conn));
                modelo.setPerguntas(getPerguntasAlternativasQuizByCodModeloByCodUnidade(modelo.getCodigo(), codUnidade, conn));
                //TODO: Setar material de apoio
                modelos.add(modelo);
            }
        }finally {
            closeConnection(conn, stmt, rSet);
        }
        return modelos;
    }

    private List<Funcao> getFuncoesLiberadasByCodModeloByCodUnidade(Long codModeloQuiz, Long codUnidade, Connection conn) throws SQLException{
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        List<Funcao> funcoes = new ArrayList<>();
        try{
            stmt = conn.prepareStatement("SELECT F.* FROM quiz_modelo QM JOIN quiz_modelo_funcao QMF\n" +
                    "  ON QM.cod_unidade = QMF.cod_unidade\n" +
                    "  AND QM.codigo = QMF.cod_modelo\n" +
                    "    JOIN UNIDADE_FUNCAO UF ON UF.cod_funcao = QMF.cod_funcao_colaborador AND UF.cod_unidade = QMF.cod_unidade\n" +
                    "    JOIN FUNCAO F ON F.codigo = UF.cod_funcao\n" +
                    "WHERE QMF.cod_unidade = ?\n" +
                    "  AND QMF.cod_modelo = ?");
            stmt.setLong(1, codUnidade);
            stmt.setLong(2, codModeloQuiz);
            rSet = stmt.executeQuery();
            while (rSet.next()){
                Funcao funcao = new Funcao();
                funcao.setCodigo(rSet.getLong("CODIGO"));
                funcao.setNome(rSet.getString("NOME"));
                funcoes.add(funcao);
            }
        }finally {
            closeConnection(null, stmt, rSet);
        }
        return funcoes;
    }

    private List<PerguntaQuiz> getPerguntasAlternativasQuizByCodModeloByCodUnidade(Long codModeloQuiz, Long codUnidade,
                                                                                   Connection conn) throws  SQLException{
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        List<PerguntaQuiz> perguntas = new ArrayList<>();
        try{
            stmt = conn.prepareStatement("SELECT * FROM quiz_perguntas QP\n" +
                    "WHERE QP.cod_modelo = ? AND QP. cod_unidade = ?\n" +
                    "ORDER BY QP.ordem");
            stmt.setLong(1, codModeloQuiz);
            stmt.setLong(2, codUnidade);
            rSet = stmt.executeQuery();
            while(rSet.next()){
                PerguntaQuiz pergunta = QuizModeloConverter.createPerguntaQuiz(rSet);
                pergunta.setAlternativas(getAlternativasPerguntaQuiz(codModeloQuiz, codUnidade, pergunta.getCodigo(),
                        pergunta.getTipo(), conn));
                perguntas.add(pergunta);
            }
        }finally {
            closeConnection(null, stmt, rSet);
        }
        return perguntas;
    }

    private List<Alternativa> getAlternativasPerguntaQuiz(Long codModeloQuiz, Long codUnidade, Long codPergunta,
                                                          String tipoPergunta, Connection conn) throws SQLException{
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        List<Alternativa> alternativas = new ArrayList<>();
        try{
            stmt = conn.prepareStatement("SELECT *, NULL AS SELECIONADA, NULL AS ORDEM_SELECIONADA FROM quiz_alternativa_pergunta\n" +
                    "WHERE cod_modelo = ? AND cod_unidade = ? AND cod_pergunta = ?\n" +
                    "ORDER BY ordem");
            stmt.setLong(1, codModeloQuiz);
            stmt.setLong(2, codUnidade);
            stmt.setLong(3, codPergunta);
            rSet = stmt.executeQuery();
            while (rSet.next()){
                alternativas.add(QuizModeloConverter.createAlternativa(rSet, tipoPergunta));
            }
        }finally {
            closeConnection(null, stmt, rSet);
        }
        return alternativas;
    }
}
