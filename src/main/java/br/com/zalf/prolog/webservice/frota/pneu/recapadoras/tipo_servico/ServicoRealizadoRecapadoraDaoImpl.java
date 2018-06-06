package br.com.zalf.prolog.webservice.frota.pneu.recapadoras.tipo_servico;

import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.frota.pneu.recapadoras.tipo_servico.model.ServicoRealizadoRecapadora;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created on 05/06/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class ServicoRealizadoRecapadoraDaoImpl extends DatabaseConnection implements ServicoRealizadoRecapadoraDao {

    public ServicoRealizadoRecapadoraDaoImpl() {
    }

    @Override
    public Long insert(@NotNull final Long codUnidade,
                       @NotNull final Long codPneu,
                       @NotNull final ServicoRealizadoRecapadora servicoRealizado) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("");
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return null;
            } else {
                throw new SQLException("Não foi possível inserir o servico realizado no pneu: " + codPneu);
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }
}
