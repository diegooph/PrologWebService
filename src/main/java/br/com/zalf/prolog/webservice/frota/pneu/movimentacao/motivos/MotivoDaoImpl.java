package br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos;

import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos._model.MotivoInsercao;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Created on 2020-03-17
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
public class MotivoDaoImpl extends DatabaseConnection implements MotivoDao {

    @Override
    @NotNull
    public Long insert(@NotNull final MotivoInsercao motivoInsercao) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();

            stmt = conn.prepareStatement("SELECT * FROM FUNC_MOTIVO_TROCA_INSERE(" +
                    "F_COD_EMPRESA_MOTIVO_TROCA := ?," +
                    "F_DESRICAO_MOTIVO_TROCA := ?," +
                    "F_ATIVO_MOTIVO_TROCA := ?," +
                    "F_DATA_HORA_INSERCAO_MOTIVO_TROCA := ?)");

            rSet = stmt.executeQuery();

            while(rSet.next()) {
                return rSet.getLong("F_COD_MOTIVO_TROCA");
            }

        } finally {
            close(conn, stmt);

        }

        return null;
    }

}
