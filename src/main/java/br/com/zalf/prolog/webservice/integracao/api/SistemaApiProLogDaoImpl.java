package br.com.zalf.prolog.webservice.integracao.api;

import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created on 14/09/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class SistemaApiProLogDaoImpl extends DatabaseConnection implements SistemaApiProLogDao {
    @Override
    public boolean isServicoMovimentacao(@NotNull final Long codServico) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * " +
                    "FROM FUNC_PNEU_AFERICAO_SERVICO_TIPO_SERVICO(F_COD_SERVICO := ?) AS IS_MOVIMENTACAO");
            stmt.setLong(1, codServico);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return rSet.getBoolean("IS_MOVIMENTACAO");
            } else {
                throw new SQLException("Erro ao validar se o serviço é de movimentação:\n" +
                        "codServico: " + codServico);
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }
}
