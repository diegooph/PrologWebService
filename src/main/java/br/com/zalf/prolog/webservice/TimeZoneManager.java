package br.com.zalf.prolog.webservice;

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
public final class TimeZoneManager {

    private TimeZoneManager() {
        throw new IllegalStateException(TimeZoneManager.class.getSimpleName() + " cannot be instantiated!");
    }

    public static LocalDateTime getZonedLocalDateTimeForCpf(@NotNull final Long cpf,
                                                            @NotNull final Connection connection) throws SQLException {
        final PreparedStatement statement = connection.prepareStatement("SELECT TIMEZONE FROM UNIDADE U " +
                "JOIN COLABORADOR C ON U.CODIGO = C.COD_UNIDADE WHERE C.CPF = ?;");
        statement.setLong(1, cpf);
        final ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            return LocalDateTime.now(ZoneId.of(resultSet.getString("TIMEZONE")));
        } else {
            throw new SQLException("Erro ao buscar o timezone para o cpf: " + cpf);
        }
    }

    public static LocalDateTime getZonedLocalDateTimeForToken(@NotNull final String token,
                                                              @NotNull final Connection connection) throws SQLException {
        final PreparedStatement statement = connection.prepareStatement("SELECT TIMEZONE FROM UNIDADE U " +
                "  JOIN COLABORADOR C ON U.CODIGO = C.COD_UNIDADE " +
                "  JOIN TOKEN_AUTENTICACAO TA ON C.CPF = TA.CPF_COLABORADOR WHERE TA.TOKEN = ?;");
        statement.setString(1, token);
        final ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            return LocalDateTime.now(ZoneId.of(resultSet.getString("TIMEZONE")));
        } else {
            throw new SQLException("Erro ao buscar o timezone para o token: " + token);
        }
    }

    public static LocalDateTime getZonedLocalDateTimeForCodUnidade(@NotNull final Long codUnidade,
                                                                   @NotNull final Connection connection) throws SQLException {
        final PreparedStatement statement = connection.prepareStatement("SELECT TIMEZONE FROM UNIDADE U WHERE U.CODIGO = ?;");
        statement.setLong(1, codUnidade);
        final ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            return LocalDateTime.now(ZoneId.of(resultSet.getString("TIMEZONE")));
        } else {
            throw new SQLException("Erro ao buscar o timezone para a unidade: " + codUnidade);
        }
    }
}