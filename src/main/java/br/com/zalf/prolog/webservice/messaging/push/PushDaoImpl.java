package br.com.zalf.prolog.webservice.messaging.push;

import br.com.zalf.prolog.webservice.commons.util.date.Now;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.messaging.push._model.PushColaboradorCadastro;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * Created on 2020-01-24
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class PushDaoImpl extends DatabaseConnection implements PushDao {

    @Override
    public void salvarTokenPushColaborador(@NotNull final String userToken,
                                           @NotNull final PushColaboradorCadastro pushColaborador) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.prepareCall("{CALL MESSAGING.FUNC_PUSH_SALVA_TOKEN_COLABORADOR(" +
                    "F_COD_COLABORADOR            => ?," +
                    "F_TOKEN_COLABORADOR_LOGADO   => ?," +
                    "F_APLICACAO_REFERENCIA_TOKEN => ? :: MESSAGING.APLICACAO_REFERENCIA_TOKEN_TYPE," +
                    "F_TOKEN_PUSH_FIREBASE        => ?," +
                    "F_DATA_HORA_ATUAL            => ?)}");
            stmt.setLong(1, pushColaborador.getCodColaborador());
            stmt.setString(2, userToken);
            stmt.setString(3, pushColaborador.getAplicacaoReferenciaToken().asString());
            stmt.setString(4, pushColaborador.getTokenPushFirebase());
            stmt.setObject(5, Now.offsetDateTimeUtc());
            stmt.execute();
        } finally {
            close(conn, stmt);
        }
    }
}
