package br.com.zalf.prolog.webservice.gente.quiz.quizRelatorios;

import br.com.zalf.prolog.commons.Report;
import br.com.zalf.prolog.commons.util.DateUtils;
import br.com.zalf.prolog.webservice.CsvWriter;
import br.com.zalf.prolog.webservice.DatabaseConnection;
import br.com.zalf.prolog.webservice.report.ReportConverter;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

/**
 * Created by Zart on 20/03/17.
 */
public class QuizRelatorioDaoImpl extends DatabaseConnection {

    private static final String TAG = QuizRelatorioDaoImpl.class.getSimpleName();

    private PreparedStatement getEstratificacaoRealizacaoQuiz(Connection conn, String codModeloQuiz,
                                                              Long codUnidade, long dataInicial, long dataFinal) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT c.matricula_ambev as \"MATRICULA PROMAX\",\n" +
                "  c.matricula_trans as \"MATRICULA RH\",\n" +
                "  C.NOME AS \"NOME\",\n" +
                "  f.nome AS \"FUNÇÃO\",\n" +
                "  COALESCE(REALIZADOS.REALIZADOS,REALIZADOS.REALIZADOS,0) AS \"REALIZADOS\",\n" +
                "  COALESCE(REALIZADOS.QT_APROVADOS,REALIZADOS.QT_APROVADOS,0) AS \"APROVADOS\",\n" +
                "  case when COALESCE(REALIZADOS.REALIZADOS,REALIZADOS.REALIZADOS,0) > 0 then\n" +
                "    round(\n" +
                "        (COALESCE(REALIZADOS.QT_APROVADOS,REALIZADOS.QT_APROVADOS,0) /\n" +
                "         COALESCE(REALIZADOS.REALIZADOS,REALIZADOS.REALIZADOS,0)::NUMERIC)*100) || '%'\n" +
                "    else 0 || '%' end AS \"PORCENTAGEM DE APROVAÇÃO\",\n" +
                "  COALESCE(REALIZADOS.MAX_ACERTOS,REALIZADOS.MAX_ACERTOS,0) AS \"MÁXIMO DE ACERTOS\",\n" +
                "  COALESCE(REALIZADOS.MIN_ACERTOS,REALIZADOS.MIN_ACERTOS,0) AS \"MÍNIMO DE ACERTOS\"\n" +
                "FROM COLABORADOR C\n" +
                "  JOIN funcao f on f.codigo = c.cod_funcao and f.cod_empresa = c.cod_empresa\n" +
                "  LEFT JOIN\n" +
                "  (SELECT C.CPF AS CPF_REALIZADOS, COUNT(C.CPF) AS REALIZADOS,\n" +
                "  SUM(\n" +
                "    CASE WHEN (Q.qt_corretas::REAL/(Q.qt_corretas+Q.qt_erradas)::REAL) >= QM.porcentagem_aprovacao THEN 1\n" +
                "    ELSE 0\n" +
                "    END\n" +
                "  ) AS QT_APROVADOS, MAX(Q.qt_corretas) AS MAX_ACERTOS, MIN(q.qt_corretas) as MIN_ACERTOS FROM\n" +
                "  COLABORADOR C LEFT JOIN QUIZ Q ON Q.cpf_colaborador = C.CPF\n" +
                "  JOIN QUIZ_MODELO QM ON QM.CODIGO = Q.COD_MODELO AND QM.COD_UNIDADE = Q.COD_UNIDADE\n" +
                "  WHERE Q.cod_modelo::TEXT LIKE ? and q.data_hora BETWEEN ? and ?\n" +
                "GROUP BY 1) AS REALIZADOS ON CPF_REALIZADOS = C.CPF\n" +
                "WHERE c.status_ativo = true AND C.cod_unidade = ? AND C.status_ativo = TRUE\n" +
                "ORDER BY \"REALIZADOS\" DESC;");
        stmt.setString(1, codModeloQuiz);
        stmt.setDate(2, DateUtils.toSqlDate(new Date(dataInicial)));
        stmt.setDate(3, DateUtils.toSqlDate(new Date(dataFinal)));
        stmt.setLong(4, codUnidade);
        return stmt;
    }

    public void getEstratificacaoRealizacaoQuizCsv(OutputStream out, String codModeloQuiz, Long codUnidade,
                                                   long dataInicial, long dataFinal) throws IOException, SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getEstratificacaoRealizacaoQuiz(conn, codModeloQuiz, codUnidade, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            new CsvWriter().write(rSet, out);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    public Report getEstratificacaoRealizacaoQuizReport(String codModeloQuiz, Long codUnidade,
                                                        long dataInicial, long dataFinal) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getEstratificacaoRealizacaoQuiz(conn, codModeloQuiz, codUnidade, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            return ReportConverter.createReport(rSet);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    private PreparedStatement getRealizacaoQuizByCargo(Connection conn, Long codUnidade, String codModeloQuiz)
            throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT qm.nome as \"MODELO QUIZ\", F.nome AS \"FUNÇÃO\",\n" +
                "  realizar.total_deveriam_ter_realizado AS \"CADASTRADOS\",\n" +
                "  coalesce(realizados.total_realizaram, 0) AS \"REALIZARAM\",\n" +
                "  trunc((coalesce(realizados.total_realizaram, 0) / realizar.total_deveriam_ter_realizado::float)*100) || '%' as \"PROPORÇÃO\"\n" +
                "FROM quiz_modelo_funcao qmf\n" +
                "  JOIN quiz_modelo qm on qm.codigo = qmf.cod_modelo and qm.cod_unidade = qmf.cod_unidade\n" +
                "  JOIN unidade U ON U.codigo = QMF.cod_unidade\n" +
                "  JOIN FUNCAO F ON F.codigo = QMF.cod_funcao_colaborador AND U.cod_empresa = F.cod_empresa\n" +
                "  JOIN (SELECT qmf.cod_modelo ,qmf.cod_funcao_colaborador as cod_funcao_deveriam, count(c.cpf) as total_deveriam_ter_realizado\n" +
                "        FROM quiz_modelo_funcao qmf\n" +
                "        JOIN colaborador c on c.cod_funcao = qmf.cod_funcao_colaborador and c.cod_unidade = qmf.cod_unidade\n" +
                "        WHERE qmf.cod_unidade = ? and qmf.cod_modelo::text like ?\n" +
                "        GROUP BY 1, 2) as realizar on qmf.cod_funcao_colaborador = realizar.cod_funcao_deveriam and qmf.cod_modelo = realizar.cod_modelo\n" +
                "  LEFT JOIN (SELECT calculo.cod_modelo, calculo.cod_funcao as cod_funcao_realizaram, count(calculo.cpf) as total_realizaram\n" +
                "              FROM\n" +
                "                        (SELECT q.cod_modelo ,c.cpf, c.cod_funcao, count(c.cod_funcao)\n" +
                "                        FROM quiz q\n" +
                "                        JOIN colaborador c ON c.cpf = q.cpf_colaborador\n" +
                "                         WHERE q.cod_unidade = ? and q.cod_modelo::text like ?\n" +
                "                        GROUP BY 1, 2, 3) AS calculo\n" +
                "  GROUP BY 1, 2) as realizados on qmf.cod_funcao_colaborador = realizados.cod_funcao_realizaram and realizados.cod_modelo = qmf.cod_modelo\n" +
                "WHERE qmf.cod_unidade = ? and qmf.cod_modelo::text like ?\n" +
                "ORDER BY qm.nome, f.nome");
        stmt.setLong(1, codUnidade);
        stmt.setString(2, codModeloQuiz);
        stmt.setLong(3, codUnidade);
        stmt.setString(4, codModeloQuiz);
        stmt.setLong(5, codUnidade);
        stmt.setString(6, codModeloQuiz);
        return stmt;
    }

    public void getRealizacaoQuizByCargoCsv(OutputStream out, Long codUnidade, String codModeloQuiz)
            throws SQLException, IOException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getRealizacaoQuizByCargo(conn, codUnidade, codModeloQuiz);
            rSet = stmt.executeQuery();
            new CsvWriter().write(rSet, out);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    public Report getRealizacaoQuizByCargoReport(Long codUnidade, String codModeloQuiz) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getRealizacaoQuizByCargo(conn, codUnidade, codModeloQuiz);
            rSet = stmt.executeQuery();
            return ReportConverter.createReport(rSet);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    private PreparedStatement getEstratificacaoRespostas(Connection conn, Long codUnidade, String codModeloQuiz) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT q.cod_modelo, qm.nome, qp.pergunta, qap.alternativa,\n" +
                "  count(qap.alternativa) as total,\n" +
                "  sum(CASE WHEN qap.correta= true and qr.selecionada = true then 1 else 0 end) as total_acertos,\n" +
                "  round((sum(CASE WHEN qap.correta= true and qr.selecionada = true then 1 else 0 end)::float /\n" +
                "  count(qap.alternativa)*100)::numeric) || '%' as porcentagem_acertos\n" +
                "FROM quiz Q\n" +
                "JOIN quiz_modelo qm on qm.codigo = q.cod_modelo and qm.cod_unidade = q.cod_unidade\n" +
                "JOIN quiz_perguntas qp on qp.cod_unidade = q.cod_unidade and qp.cod_modelo = q.cod_modelo\n" +
                "JOIN quiz_alternativa_pergunta qap on qap.cod_pergunta = qp.codigo and qap.cod_unidade = qp.cod_unidade " +
                "and qap.cod_modelo = qp.cod_modelo\n" +
                "JOIN quiz_respostas qr on qr.cod_quiz = q.codigo and qr.cod_unidade = q.cod_unidade " +
                "and qr.cod_modelo = q.cod_modelo and qp.codigo = qr.cod_pergunta\n " +
                "and qr.cod_alternativa = qap.codigo\n " +
                "where qap.correta = true and q.cod_unidade = ? and qm.codigo::text like ?\n" +
                "GROUP BY 1,2,3,4\n" +
                "ORDER BY 1, 7 DESC;");
        stmt.setLong(1, codUnidade);
        stmt.setString(2, codModeloQuiz);
        return stmt;
    }

    public void getEstratificacaoQuizRespostasCsv(OutputStream out, Long codUnidade, String codModeloQuiz) throws SQLException,
            IOException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getEstratificacaoRespostas(conn, codUnidade, codModeloQuiz);
            rSet = stmt.executeQuery();
            new CsvWriter().write(rSet, out);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    public Report getEstratificacaoQuizRespostasReport(Long codUnidade, String codModeloQuiz) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getEstratificacaoRespostas(conn, codUnidade, codModeloQuiz);
            rSet = stmt.executeQuery();
            return ReportConverter.createReport(rSet);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }
}
