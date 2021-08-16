package br.com.zalf.prolog.webservice;

import br.com.zalf.prolog.webservice.autenticacao._model.token.TokenCleaner;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneId;

/**
 * Created on 3/2/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class TimeZoneManager extends DatabaseConnection {

    private TimeZoneManager() {
        throw new IllegalStateException(TimeZoneManager.class.getSimpleName() + " cannot be instantiated!");
    }

    @NotNull
    public static ZoneId getZoneIdForCodUnidade(@NotNull final Long codUnidade) throws SQLException {
        Connection connection = null;
        try {
            connection = getConnection();
            return getZoneIdForCodUnidade(codUnidade, connection);
        } finally {
            close(connection);
        }
    }

    @NotNull
    public static ZoneId getZoneIdForCodUnidade(@NotNull final Long codUnidade,
                                                @NotNull final Connection connection) throws SQLException {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement("SELECT TIMEZONE FROM UNIDADE U WHERE U.CODIGO = ?;");
            statement.setLong(1, codUnidade);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return ZoneId.of(resultSet.getString("TIMEZONE"));
            } else {
                throw new SQLException("Erro ao buscar o timezone para a unidade: " + codUnidade);
            }
        } finally {
            close(statement, resultSet);
        }
    }

    @NotNull
    public static ZoneId getZoneIdForCpf(@NotNull final Long cpf) throws SQLException {
        Connection connection = null;
        try {
            connection = getConnection();
            return getZoneIdForCpf(cpf, connection);
        } finally {
            close(connection);
        }
    }

    @NotNull
    public static ZoneId getZoneIdForCpf(@NotNull final Long cpf,
                                         @NotNull final Connection connection) throws SQLException {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement("SELECT TIMEZONE FROM UNIDADE U " +
                    "JOIN COLABORADOR C ON U.CODIGO = C.COD_UNIDADE WHERE C.CPF = ?;");
            statement.setLong(1, cpf);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return ZoneId.of(resultSet.getString("TIMEZONE"));
            } else {
                throw new SQLException("Erro ao buscar o timezone para o cpf: " + cpf);
            }
        } finally {
            close(statement, resultSet);
        }
    }

    @NotNull
    public static ZoneId getZoneIdForCodColaborador(@NotNull final Long codColaborador,
                                                    @NotNull final Connection connection) throws SQLException {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement("SELECT TIMEZONE FROM UNIDADE U " +
                    "JOIN COLABORADOR C ON U.CODIGO = C.COD_UNIDADE WHERE C.CODIGO = ?;");
            statement.setLong(1, codColaborador);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return ZoneId.of(resultSet.getString("TIMEZONE"));
            } else {
                throw new SQLException("Erro ao buscar o timezone para o colaborador de código: " + codColaborador);
            }
        } finally {
            close(statement, resultSet);
        }
    }

    @NotNull
    public static ZoneId getZoneIdForToken(@NotNull final String token) throws SQLException {
        Connection connection = null;
        try {
            connection = getConnection();
            return getZoneIdForToken(token, connection);
        } finally {
            close(connection);
        }
    }

    @NotNull
    public static ZoneId getZoneIdForToken(@NotNull final String token,
                                           @NotNull final Connection connection) throws SQLException {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement("SELECT TIMEZONE FROM UNIDADE U " +
                    "  JOIN COLABORADOR C ON U.CODIGO = C.COD_UNIDADE " +
                    "  JOIN TOKEN_AUTENTICACAO TA ON C.CPF = TA.CPF_COLABORADOR WHERE TA.TOKEN = ?;");
            statement.setString(1, TokenCleaner.getOnlyToken(token));
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return ZoneId.of(resultSet.getString("TIMEZONE"));
            } else {
                throw new SQLException("Erro ao buscar o timezone para o token: " + token);
            }
        } finally {
            close(statement, resultSet);
        }
    }
}