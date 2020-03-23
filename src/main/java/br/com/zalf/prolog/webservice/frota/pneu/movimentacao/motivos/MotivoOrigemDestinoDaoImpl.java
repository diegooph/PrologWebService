package br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos;

import br.com.zalf.prolog.webservice.commons.util.TokenCleaner;
import br.com.zalf.prolog.webservice.commons.util.date.Now;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos._model.MotivoOrigemDestinoInsercao;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Created on 2020-03-20
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
public class MotivoOrigemDestinoDaoImpl extends DatabaseConnection implements MotivoOrigemDestinoDao {

    @Override
    @NotNull
    public Long insert(@NotNull final MotivoOrigemDestinoInsercao motivoOrigemDestinoInsercao,
                       @NotNull final String tokenAutenticacao) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();

            stmt = conn.prepareStatement("SELECT * FROM FUNC_MOTIVO_ORIGEM_DESTINO_INSERE(" +
                    "F_COD_MOTIVO := ?," +
                    "F_COD_EMPRESA := ?," +
                    "F_ORIGEM := ?," +
                    "F_DESTINO := ?," +
                    "F_OBRIGATORIO := ?," +
                    "F_DATA_HORA_INSERCAO := ?," +
                    "F_TOKEN_AUTENTICACAO := ?)" +
                    "AS V_COD_MOTIVO_ORIGEM_DESTINO;");

            stmt.setLong(1, motivoOrigemDestinoInsercao.getCodMotivo());
            stmt.setLong(2, motivoOrigemDestinoInsercao.getCodEmpresa());
            stmt.setString(3, motivoOrigemDestinoInsercao.getOrigemMovimentacao().toString());
            stmt.setString(4, motivoOrigemDestinoInsercao.getDestinoMovimentacao().toString());
            stmt.setBoolean(5, true);
            stmt.setObject(6, Now.offsetDateTimeUtc());
            stmt.setString(7, TokenCleaner.getOnlyToken(tokenAutenticacao));

            rSet = stmt.executeQuery();

            while (rSet.next()) {
                return rSet.getLong("V_COD_MOTIVO_ORIGEM_DESTINO");
            }

        } finally {
            close(conn, stmt);

        }

        return null;
    }

}
