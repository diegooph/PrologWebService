package br.com.zalf.prolog.webservice.frota.pneu.recapadoras.tipo_servico;

import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

/**
 * Created on 05/06/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class TipoServicoRealizadoDaoImpl extends DatabaseConnection implements TipoServicoRealizadoDao {

    public TipoServicoRealizadoDaoImpl() {
    }

    @Override
    public Long insert(@NotNull final Long codUnidade,
                       @NotNull final Long codPneu,
                       @NotNull final TipoServicoRecapadora tipoServico) throws SQLException {
        return null;
    }
}
