package br.com.zalf.prolog.webservice.gente.quiz.quizModelo;

import br.com.zalf.prolog.commons.colaborador.Funcao;
import br.com.zalf.prolog.commons.questoes.Alternativa;
import br.com.zalf.prolog.commons.util.DateUtils;
import br.com.zalf.prolog.gente.quiz.AlternativaEscolhaQuiz;
import br.com.zalf.prolog.gente.quiz.AlternativaOrdenamentoQuiz;
import br.com.zalf.prolog.gente.quiz.ModeloQuiz;
import br.com.zalf.prolog.gente.quiz.PerguntaQuiz;
import br.com.zalf.prolog.webservice.DatabaseConnection;
import br.com.zalf.prolog.webservice.gente.treinamento.TreinamentoDao;
import br.com.zalf.prolog.webservice.gente.treinamento.TreinamentoDaoImpl;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Zalf on 04/01/17.
 */
public class QuizModeloDaoImpl extends DatabaseConnection implements QuizModeloDao {

    private static final String TAG = QuizModeloDaoImpl.class.getSimpleName();

    public List<ModeloQuiz> getModelosQuizDisponiveis(Long codUnidade, Long codFuncaoColaborador) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        List<ModeloQuiz> modelos = new ArrayList<>();
        TreinamentoDao treinamentoDao = new TreinamentoDaoImpl();
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT QM.*, QMT.cod_treinamento FROM quiz_modelo QM JOIN quiz_modelo_funcao QMF\n" +
                    "  ON QM.cod_unidade = QMF.cod_unidade\n" +
                    "  AND QM.codigo = QMF.cod_modelo\n" +
                    "  LEFT JOIN quiz_modelo_treinamento QMT ON QMT.cod_modelo_quiz = QM.CODIGO AND\n" +
                    "    QMT.cod_unidade = QM.cod_unidade\n" +
                    "WHERE QM.data_hora_abertura <= ?\n" +
                    "  AND data_hora_fechamento >= ?\n" +
                    "  AND QMF.cod_unidade = ?\n" +
                    "  AND QMF.cod_funcao_colaborador = ?;");
            stmt.setTimestamp(1, DateUtils.toTimestamp(new Date(System.currentTimeMillis())));
            stmt.setTimestamp(2, DateUtils.toTimestamp(new Date(System.currentTimeMillis())));
            stmt.setLong(3, codUnidade);
            stmt.setLong(4, codFuncaoColaborador);
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                ModeloQuiz modelo = QuizModeloConverter.createModeloQuiz(rSet);
                modelo.setFuncoesLiberadas(getFuncoesLiberadas(modelo.getCodigo(), codUnidade, conn));
                modelo.setPerguntas(getPerguntasQuiz(modelo.getCodigo(), codUnidade, conn));
                modelo.setMaterialApoio(treinamentoDao.getTreinamentoByCod(rSet.getLong("COD_TREINAMENTO"), codUnidade));
                modelos.add(modelo);
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return modelos;
    }

    private List<Funcao> getFuncoesLiberadas(Long codModeloQuiz, Long codUnidade, Connection conn) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        List<Funcao> funcoes = new ArrayList<>();
        try {
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
            while (rSet.next()) {
                Funcao funcao = new Funcao();
                funcao.setCodigo(rSet.getLong("CODIGO"));
                funcao.setNome(rSet.getString("NOME"));
                funcoes.add(funcao);
            }
        } finally {
            closeConnection(null, stmt, rSet);
        }
        return funcoes;
    }

    private List<PerguntaQuiz> getPerguntasQuiz(Long codModeloQuiz, Long codUnidade, Connection conn) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        List<PerguntaQuiz> perguntas = new ArrayList<>();
        try {
            stmt = conn.prepareStatement("SELECT * FROM quiz_perguntas QP\n" +
                    "WHERE QP.cod_modelo = ? AND QP. cod_unidade = ?\n" +
                    "ORDER BY QP.ordem");
            stmt.setLong(1, codModeloQuiz);
            stmt.setLong(2, codUnidade);
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                PerguntaQuiz pergunta = QuizModeloConverter.createPerguntaQuiz(rSet);
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
        List<Alternativa> alternativas = new ArrayList<>();
        try {
            stmt = conn.prepareStatement("SELECT *, NULL AS SELECIONADA, NULL AS ORDEM_SELECIONADA FROM quiz_alternativa_pergunta\n" +
                    "WHERE cod_modelo = ? AND cod_unidade = ? AND cod_pergunta = ?\n" +
                    "ORDER BY ordem");
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

    @Override
    public List<ModeloQuiz> getModelosQuizByCodUnidade(Long codUnidade) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        List<ModeloQuiz> quizzes = new ArrayList<>();
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT codigo, nome FROM quiz_modelo WHERE cod_unidade = ? order by 1");
            stmt.setLong(1, codUnidade);
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                ModeloQuiz modeloQuiz = new ModeloQuiz();
                modeloQuiz.setCodigo(rSet.getLong("CODIGO"));
                modeloQuiz.setNome(rSet.getString("NOME"));
                quizzes.add(modeloQuiz);
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return quizzes;
    }

    public Long insertModeloQuiz(ModeloQuiz modeloQuiz, Long codUnidade) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            stmt = conn.prepareStatement("INSERT INTO quiz_modelo(cod_unidade, nome, descricao, data_hora_abertura, " +
                    "data_hora_fechamento, porcentagem_aprovacao) VALUES (?,?,?,?,?,?) RETURNING codigo;");
            stmt.setLong(1, codUnidade);
            stmt.setString(2, modeloQuiz.getNome());
            stmt.setString(3, modeloQuiz.getDescricao());
            stmt.setTimestamp(4, DateUtils.toTimestamp(modeloQuiz.getDataHoraAbertura()));
            stmt.setTimestamp(5, DateUtils.toTimestamp(modeloQuiz.getDataHoraFechamento()));
            stmt.setDouble(6, modeloQuiz.getPorcentagemAprovacao());
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                Long codModeloQuiz = rSet.getLong("CODIGO");
                modeloQuiz.setCodigo(codModeloQuiz);
                // insere as perguntas e alternativas do modelo
                insertPerguntasModeloQuiz(modeloQuiz.getPerguntas(), codModeloQuiz, codUnidade, conn);
                // insere os cargos que podem acessar esse modelo de quiz
                insertCargosModeloQuiz(modeloQuiz.getFuncoesLiberadas(), codModeloQuiz, codUnidade, conn);
                TreinamentoDao treinamentoDao = new TreinamentoDaoImpl();
                // insere o treinamento
                Long codTreinamento = treinamentoDao.insert(modeloQuiz.getMaterialApoio());
                // associa o treinamento ao modleo de quiz
                insertQuizModeloTreinamento(codTreinamento, codModeloQuiz, codUnidade, conn);
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

    private void insertPerguntasModeloQuiz(List<PerguntaQuiz> perguntas, Long codModeloQuiz, Long codUnidade,
                                           Connection conn) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("INSERT INTO quiz_perguntas(cod_modelo, cod_unidade, pergunta, ordem, " +
                    "tipo, url_imagem) VALUES (?,?,?,?,?,?) RETURNING codigo;");
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
//                    insert treinamento do quiz
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
            stmt = conn.prepareStatement("INSERT INTO quiz_alternativa_pergunta(cod_modelo, cod_unidade, cod_pergunta, " +
                    "alternativa, ordem, correta) VALUES (?,?,?,?,?,?) RETURNING codigo");
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

    private void insertCargosModeloQuiz(List<Funcao> funcoesLiberadas, Long codModeloQuiz, Long codUnidade,
                                        Connection conn) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("INSERT INTO quiz_modelo_funcao(cod_unidade, cod_modelo, cod_funcao_colaborador) VALUES (?,?,?);");
            stmt.setLong(1, codUnidade);
            stmt.setLong(2, codModeloQuiz);
            for (Funcao funcao : funcoesLiberadas) {
                stmt.setLong(3, funcao.getCodigo());
                int count = stmt.executeUpdate();
                if (count == 0) {
                    throw new SQLException("Erro ao vincular o cargo ao modelo de quiz");
                }
            }
        } finally {
            closeConnection(null, stmt, null);
        }
    }

    private void insertQuizModeloTreinamento(Long codTreinamento, Long codModeloQuiz, Long codUnidade, Connection conn)
            throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("INSERT INTO quiz_modelo_treinamento(cod_modelo_quiz, cod_unidade, cod_treinamento) VALUES (?,?,?)");
            stmt.setLong(1, codModeloQuiz);
            stmt.setLong(2, codUnidade);
            stmt.setLong(3, codTreinamento);
            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Erro ao inserir o treinamento do Quiz");
            }
        } finally {
            closeConnection(null, stmt, null);
        }
    }
}
