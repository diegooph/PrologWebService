package br.com.zalf.prolog.webservice;

import br.com.zalf.prolog.webservice.commons.util.TokenCleaner;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
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
            closeConnection(null, statement, resultSet);
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
            closeConnection(null, statement, resultSet);
        }
    }

    @NotNull
    public static LocalDateTime getZonedLocalDateTimeForCpf(@NotNull final Long cpf) throws SQLException {
        Connection connection = null;
        try {
            connection = getConnection();
            return getZonedLocalDateTimeForCpf(cpf, connection);
        } finally {
            closeConnection(connection, null, null);
        }
    }

    @NotNull
    public static LocalDateTime getZonedLocalDateTimeForCpf(@NotNull final Long cpf,
                                                            @NotNull final Connection connection) throws SQLException {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement("SELECT TIMEZONE FROM UNIDADE U " +
                    "JOIN COLABORADOR C ON U.CODIGO = C.COD_UNIDADE WHERE C.CPF = ?;");
            statement.setLong(1, cpf);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return LocalDateTime.now(ZoneId.of(resultSet.getString("TIMEZONE")));
            } else {
                throw new SQLException("Erro ao buscar o timezone para o cpf: " + cpf);
            }
        } finally {
            closeConnection(null, statement, resultSet);
        }
    }

    @NotNull
    public static LocalDateTime getZonedLocalDateTimeForToken(@NotNull final String token) throws SQLException {
        Connection connection = null;
        try {
            connection = getConnection();
            return getZonedLocalDateTimeForToken(token, connection);
        } finally {
            closeConnection(connection, null, null);
        }
    }

    @NotNull
    public static LocalDateTime getZonedLocalDateTimeForToken(@NotNull final String token,
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
                return LocalDateTime.now(ZoneId.of(resultSet.getString("TIMEZONE")));
            } else {
                throw new SQLException("Erro ao buscar o timezone para o token: " + token);
            }
        } finally {
            closeConnection(null, statement, resultSet);
        }
    }

    @NotNull
    public static LocalDateTime getZonedLocalDateTimeForCodUnidade(@NotNull final Long codUnidade) throws SQLException {
        Connection connection = null;
        try {
            connection = getConnection();
            return getZonedLocalDateTimeForCodUnidade(codUnidade, connection);
        } finally {
            closeConnection(connection, null, null);
        }
    }

    @NotNull
    public static LocalDateTime getZonedLocalDateTimeForCodUnidade(@NotNull final Long codUnidade,
                                                                   @NotNull final Connection connection) throws SQLException {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement("SELECT TIMEZONE FROM UNIDADE U WHERE U.CODIGO = ?;");
            statement.setLong(1, codUnidade);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return LocalDateTime.now(ZoneId.of(resultSet.getString("TIMEZONE")));
            } else {
                throw new SQLException("Erro ao buscar o timezone para a unidade: " + codUnidade);
            }
        } finally {
            closeConnection(null, statement, resultSet);
        }
    }
}