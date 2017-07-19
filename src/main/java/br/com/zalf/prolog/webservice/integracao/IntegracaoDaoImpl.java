package br.com.zalf.prolog.webservice.integracao;

import br.com.zalf.prolog.webservice.integracao.sistema.SistemaKey;
import com.sun.istack.internal.NotNull;

import java.sql.SQLException;

/**
 * Created by luiz on 18/07/17.
 */
public final class IntegracaoDaoImpl implements IntegracaoDao {

    @Override
    public SistemaKey getSistemaKey(@NotNull String userToken, @NotNull RecursoIntegrado recursoIntegrado) throws SQLException {
        return null;
    }
}