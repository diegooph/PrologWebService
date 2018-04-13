package br.com.zalf.prolog.webservice.frota.pneu.recapadoras;

import br.com.zalf.prolog.webservice.Injection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;
import java.util.List;

/**
 * Created on 13/04/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class RecapadoraService {

    private final RecapadoraDao dao = Injection.provideRecapadoraDao();

    public void insertRecapadora(@NotNull final String token, @NotNull final Recapadora recapadora) throws SQLException {
        dao.insertRecapadora(token, recapadora);
    }

    public void atualizaRecapadoras(@NotNull final Long codEmpresa, @NotNull final Recapadora recapadora) throws SQLException {
        dao.atualizaRecapadoras(codEmpresa, recapadora);
    }

    public List<Recapadora> getRecapadoras(@NotNull final Long codEmpresa, @Nullable final Boolean ativas) throws SQLException {
        return dao.getRecapadoras(codEmpresa, ativas);
    }

    public void alterarStatusRecapadoras(@NotNull final String token,
                                         @NotNull final Long codEmpresa,
                                         @NotNull final List<Recapadora> recapadoras) throws SQLException {
        dao.alterarStatusRecapadoras(token, codEmpresa, recapadoras);
    }
}
