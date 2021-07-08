package br.com.zalf.prolog.webservice.errorhandling.sql;

import br.com.zalf.prolog.webservice.errorhandling.exception.GenericException;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.postgresql.util.PSQLException;

import java.sql.BatchUpdateException;
import java.sql.SQLException;

/**
 * Created on 18/06/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public class ProLogSqlExceptionTranslator implements SqlExceptionTranslator {
    @NotNull
    @Override
    public final ProLogException doTranslate(@NotNull final SQLException sqlException) {
        return doTranslate(sqlException, sqlException.getMessage());
    }

    @NotNull
    @Override
    public final ProLogException doTranslate(@NotNull final SQLException sqlException,
                                             @NotNull final String fallBackErrorMessage) {
        try {
            // Primeiro tentamos a tradução personalizada.
            final ProLogException proLogException = customTranslate(sqlException, fallBackErrorMessage);
            if (proLogException != null) {
                return proLogException;
            }

            if (sqlException instanceof PSQLException) {
                return handlePSQLException((PSQLException) sqlException, fallBackErrorMessage);
            } else if (sqlException instanceof BatchUpdateException) {
                if (sqlException.getNextException() instanceof PSQLException) {
                    return handlePSQLException((PSQLException) sqlException.getNextException(), fallBackErrorMessage);
                }
            }
        } catch (final Throwable t) {
            // Se acontecer algum outro erro ao tentarmos mapear o erro principal, realizamos o fallBack para a
            // mensagem recebida lançando uma exception genérica.
            return new GenericException(fallBackErrorMessage, "Erro interno ao realizar mapeamento de exception.", t);
        }

        return new DataAccessException(fallBackErrorMessage,
                                       "Não foi possível realizar o mapeamento do erro.",
                                       sqlException);
    }

    @Nullable
    protected ProLogException customTranslate(@NotNull final SQLException sqlException,
                                              @NotNull final String fallBackErrorMessage) {
        return null;
    }

    @NotNull
    private ProLogException handlePSQLException(@NotNull final PSQLException sqlException,
                                                @NotNull final String fallBackErrorMessage) {
        if (String.valueOf(sqlException.getSQLState()).equals(SqlErrorCodes.UNIQUE_VIOLATION.getErrorCode())) {
            return new DuplicateKeyException(
                    "Este registro já existe no banco de dados.",
                    getDetailMessage(sqlException),
                    sqlException.getMessage(),
                    false);
        }

        if (String.valueOf(sqlException.getSQLState()).equals(SqlErrorCodes.FOREIGN_KEY_VIOLATION.getErrorCode())) {
            return new ForeignKeyException(
                    "Ocorreu um erro ao inserir/atualizar os dados.",
                    getDetailMessage(sqlException),
                    sqlException.getMessage(),
                    true);
        }

        if (String.valueOf(sqlException.getSQLState()).equals(SqlErrorCodes.CHECK_VIOLATION.getErrorCode())) {
            return new ConstraintCheckException(
                    "Ocorreu um erro ao inserir/atualizar os dados.",
                    getDetailMessage(sqlException),
                    sqlException.getMessage(),
                    false);
        }

        if (String.valueOf(sqlException.getSQLState()).equals(SqlErrorCodes.NOT_NULL_VIOLATION.getErrorCode())) {
            return new NotNullViolationException(
                    "Ocorreu um erro ao inserir/atualizar os dados.",
                    getDetailMessage(sqlException),
                    sqlException.getMessage(),
                    false);
        }

        if (String.valueOf(sqlException.getSQLState()).equals(SqlErrorCodes.SERVER_SIDE_ERROR.getErrorCode())) {
            return new ServerSideErrorException(
                    "Um erro ocorreu no servidor.",
                    getDetailMessage(sqlException),
                    sqlException.getMessage(),
                    true);
        }

        if (String.valueOf(sqlException.getSQLState()).equals(SqlErrorCodes.CLIENT_SIDE_ERROR.getErrorCode())) {
            return new ClientSideErrorException(
                    "Um erro ocorreu nas validações dos dados enviados.",
                    getDetailMessage(sqlException),
                    sqlException.getMessage(),
                    false);
        }

        if (String.valueOf(sqlException.getSQLState()).equals(SqlErrorCodes.BD_GENERIC_ERROR_CODE.getErrorCode())) {
            return new GenericException(getPSQLErrorMessage(sqlException));
        }

        return new DataAccessException(fallBackErrorMessage);
    }

    @NotNull
    private String getPSQLErrorMessage(@NotNull final SQLException sqlException) {
        return ((PSQLException) sqlException).getServerErrorMessage().getMessage();
    }

    @NotNull
    private String getDetailMessage(@NotNull final SQLException sqlException) {
        final ConstraintsCheckEnum constraint = getPSQLErrorConstraint(sqlException);
        final ValidEntityTableName tableName =
                ValidEntityTableName.getTableNameFromMessage(getPSQLErrorMessage(sqlException));
        return constraint.getDetailMessage(tableName);
    }

    @NotNull
    private ConstraintsCheckEnum getPSQLErrorConstraint(@NotNull final SQLException sqlException) {
        if (sqlException instanceof PSQLException) {
            return ConstraintsCheckEnum.fromString(((PSQLException) sqlException).getServerErrorMessage()
                                                           .getConstraint());
        }
        return ConstraintsCheckEnum.DEFAULT;
    }
}