package br.com.zalf.prolog.webservice.gente.quizRelatorios;

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

    private PreparedStatement getEstratificacaoRealizacaoQuiz(Connection conn, String cpf, String codModeloQuiz,
                                                              Long codUnidade, long dataInicial, long dataFinal) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("-- estratificação de realização do quiz, podendo ser " +
                "filtrada por modelo de quiz e nome do colaborador\n" +
                "SELECT c.matricula_ambev as \"MATRICULA PROMAX\",\n" +
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
                "WHERE C.cpf::text like ? and c.status_ativo = true AND C.cod_unidade = ? AND C.status_ativo = TRUE\n" +
                "ORDER BY \"REALIZADOS\" DESC;");
        stmt.setString(1, codModeloQuiz);
        stmt.setDate(2, DateUtils.toSqlDate(new Date(dataInicial)));
        stmt.setDate(3, DateUtils.toSqlDate(new Date(dataFinal)));
        stmt.setString(4, cpf);
        stmt.setLong(5, codUnidade);
        return stmt;
    }

    public void getEstratificacaoRealizacaoQuizCsv(OutputStream out, String cpf, String codModeloQuiz, Long codUnidade,
                                                   long dataInicial, long dataFinal) throws IOException, SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getEstratificacaoRealizacaoQuiz(conn, cpf, codModeloQuiz, codUnidade, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            new CsvWriter().write(rSet, out);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    public Report getEstratificacaoRealizacaoQuizReport(String cpf, String codModeloQuiz, Long codUnidade,
                                                        long dataInicial, long dataFinal) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getEstratificacaoRealizacaoQuiz(conn, cpf, codModeloQuiz, codUnidade, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            return ReportConverter.createReport(rSet);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    private PreparedStatement getRealizacaoQuizByCargo(Connection conn, Long codUnidade, String codCargo, String codModeloQuiz)
            throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("select realizados.nome AS \"CARGO\", count(realizados.nome) as \"TOTAL\"," +
                " sum(realizados.total) as \"REALIZARAM\",\n" +
                "  round((sum(realizados.total) / count(realizados.nome)::FLOAT)::NUMERIC*100) || '%' as \"PROPORÇÃO\"\n" +
                "  from\n" +
                "(SELECT DISTINCT c.cpf, f.nome,\n" +
                "  case when q.cpf_colaborador > 0 then 1 else 0 end as total\n" +
                "FROM colaborador c\n" +
                "  JOIN funcao f on f.codigo = c.cod_funcao and f.cod_empresa = c.cod_empresa\n" +
                "  left join quiz q on q.cpf_colaborador = c.cpf\n" +
                "  left join quiz_modelo qm on qm.codigo = q.cod_modelo and qm.cod_unidade = q.cod_unidade\n" +
                "WHERE c.cod_unidade = ? and f.codigo::text like ? and (qm.codigo is null or qm.codigo::text like ? ) and c.status_ativo = true) as realizados\n" +
                "GROUP BY 1;");
        stmt.setLong(1, codUnidade);
        stmt.setString(2, codCargo);
        stmt.setString(3, codModeloQuiz);
        return stmt;
    }

    public void getRealizacaoQuizByCargoCsv(OutputStream out, Long codUnidade, String codCargo, String codModeloQuiz)
            throws SQLException, IOException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getRealizacaoQuizByCargo(conn, codUnidade, codCargo, codModeloQuiz);
            rSet = stmt.executeQuery();
            new CsvWriter().write(rSet, out);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    public Report getRealizacaoQuizByCargoReport(Long codUnidade, String codCargo, String codModeloQuiz) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getRealizacaoQuizByCargo(conn, codUnidade, codCargo, codModeloQuiz);
            rSet = stmt.executeQuery();
            return ReportConverter.createReport(rSet);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }


}
