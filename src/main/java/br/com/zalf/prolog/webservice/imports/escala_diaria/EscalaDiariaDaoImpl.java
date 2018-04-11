package br.com.zalf.prolog.webservice.imports.escala_diaria;

import br.com.zalf.prolog.webservice.commons.util.PostgresUtil;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Created on 10/04/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class EscalaDiariaDaoImpl extends DatabaseConnection implements EscalaDiariaDao {

    public EscalaDiariaDaoImpl() {
    }

    @Override
    public void insertOrUpdateEscalaDiaria(@NotNull final Long codUnidade,
                                           @NotNull final List<EscalaDiariaItem> escalaDiariaItens) throws SQLException {

    }

    @Override
    public void insertOrUpdateEscalaDiariaItem(@NotNull final Long codUnidade,
                                               @NotNull final EscalaDiariaItem escalaDiariaItem,
                                               final boolean isInsert) throws SQLException {

    }

    @Override
    public List<EscalaDiaria> getEscalasDiarias(@NotNull final Long codUnidade,
                                                @NotNull final LocalDate dataInicial,
                                                @NotNull final LocalDate dataFinal) throws SQLException {

        return null;
    }

    @Override
    public void deleteEscalaDiariaItens(@NotNull final Long codUnidade,
                                        @NotNull final List<Long> codEscalas) throws SQLException {
        if (codEscalas.isEmpty())
            return;

        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("DELETE FROM ESCALA_DIARIA " +
                    "WHERE COD_UNIDADE = ? AND COD_ESCALA::TEXT LIKE ANY (ARRAY[?])");
            stmt.setLong(1, codUnidade);
            stmt.setArray(2, PostgresUtil.ListLongToArray(conn, codEscalas));
            final int count = stmt.executeUpdate();
            if (count == 0) {
                throw new SQLException("Erro ao deletar Escala");
            }
        } finally {
            closeConnection(conn, stmt, null);
        }
    }
}
