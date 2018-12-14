package br.com.zalf.prolog.webservice.log;

import br.com.zalf.prolog.webservice.commons.util.SqlType;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.integracao.logger.LogRequisicao;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static br.com.zalf.prolog.webservice.commons.util.StatementUtils.bindValueOrNull;

public class LogDaoImpl extends DatabaseConnection implements LogDao {

	public LogDaoImpl() {

	}

	@Override
	public boolean insert(@NotNull final String log, @NotNull final String identificador) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("INSERT INTO LOG_JSON(JSON, IDENTIFICADOR) VALUES (?,?)");
			stmt.setString(1, log);
			stmt.setString(2, identificador);
			return stmt.executeUpdate() > 0;
		} finally {
			close(conn, stmt);
		}
	}

	@Override
	public void insertRequestLog(@NotNull final String tokenRequisicao,
								 @NotNull final LogRequisicao logRequisicao) throws Throwable {
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("INSERT INTO INTEGRACAO.LOG_REQUISICAO " +
					"(COD_EMPRESA, " +
					" CLASS_RESOURCE, " +
					" METHOD_RESOURCE, " +
					" HTTP_METHOD, " +
					" URL_ACESSO, " +
					" HEADERS, " +
					" PARAMETERS, " +
					" BODY_REQUEST, " +
					" DATA_HORA_REQUISICAO) " +
					"VALUES ((SELECT TI.COD_EMPRESA " +
					"         FROM INTEGRACAO.TOKEN_INTEGRACAO TI " +
					"         WHERE TI.TOKEN_INTEGRACAO = ?), ?, ?, ?, ?, ?, ?, ?, ?);");
			stmt.setString(1, tokenRequisicao);
			bindValueOrNull(stmt,2, logRequisicao.getClassResource(), SqlType.TEXT);
			bindValueOrNull(stmt,3, logRequisicao.getMethodResource(), SqlType.TEXT);
			bindValueOrNull(stmt,4, logRequisicao.getHttpMethod(), SqlType.TEXT);
			bindValueOrNull(stmt,5, logRequisicao.getUrlAcesso(), SqlType.TEXT);
			bindValueOrNull(stmt,6, logRequisicao.getHeaders(), SqlType.TEXT);
			bindValueOrNull(stmt,7, logRequisicao.getParameters(), SqlType.TEXT);
			bindValueOrNull(stmt,8, logRequisicao.getBodyRequest(), SqlType.TEXT);
			stmt.setObject(9, logRequisicao.getDataHoraRequisicao());
			if (stmt.executeUpdate() <= 0) {
				throw new SQLException("Não foi possível salvar o Log");
			}
		} finally {
			close(conn, stmt);
		}
	}

}