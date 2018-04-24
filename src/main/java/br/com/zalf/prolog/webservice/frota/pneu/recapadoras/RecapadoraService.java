package br.com.zalf.prolog.webservice.frota.pneu.recapadoras;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.util.TokenCleaner;
import br.com.zalf.prolog.webservice.errorhandling.exception.RecapadoraException;
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

    public Response insertRecapadora(@NotNull final String token,
                                     @NotNull final Recapadora recapadora) throws RecapadoraException {
        try {
            dao.insertRecapadora(TokenCleaner.getOnlyToken(token), recapadora);
            return Response.ok("Recapadora inserida com sucesso!");
        } catch (SQLException e) {
            throw new RecapadoraException(
                    "Não foi possível inserir a recapadora, tente novamente!",
                    "Erro ao inserir a recapadora!");
        }
    }

    public Response atualizaRecapadoras(@NotNull final Long codEmpresa,
                                        @NotNull final Recapadora recapadora) throws RecapadoraException {
        try {
            dao.atualizaRecapadoras(codEmpresa, recapadora);
            return Response.ok("Recapadora atualizada com sucesso!");
        } catch (SQLException e) {
            throw new RecapadoraException(
                    "Não foi possível atualizar a recapadora, tente novamente!",
                    "Erro ao atualizar a recapadora!");
        }
    }

    public List<Recapadora> getRecapadoras(@NotNull final Long codEmpresa,
                                           @Nullable final Boolean ativas) throws RecapadoraException {
        try {
            return dao.getRecapadoras(codEmpresa, ativas);
        } catch (SQLException e) {
            throw new RecapadoraException(
                    "Não foi possível buscar as recapadoras, tente novamente!",
                    "Erro ao buscar as recapadoras!");
        }
    }

    public Response alterarStatusRecapadoras(@NotNull final String token,
                                             @NotNull final Long codEmpresa,
                                             @NotNull final List<Recapadora> recapadoras) throws RecapadoraException {
        try {
            dao.alterarStatusRecapadoras(TokenCleaner.getOnlyToken(token), codEmpresa, recapadoras);
            return Response.ok("Status");
        } catch (SQLException e) {
            throw new RecapadoraException(
                    "Não foi possível atualizar o status das recapadoras, tente novamente!",
                    "Erro ao atualizar o status das recapadoras!");
        }
    }
}
