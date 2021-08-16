package br.com.zalf.prolog.webservice.frota.pneu.recapadoras;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.network.ResponseWithCod;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.autenticacao._model.token.TokenCleaner;
import br.com.zalf.prolog.webservice.errorhandling.exception.RecapadoraException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created on 13/04/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class RecapadoraService {
    private static final String TAG = RecapadoraService.class.getSimpleName();
    private final RecapadoraDao dao = Injection.provideRecapadoraDao();

    public AbstractResponse insertRecapadora(@NotNull final String token,
                                             @NotNull final Recapadora recapadora) throws RecapadoraException {
        try {
            return ResponseWithCod.ok(
                    "Recapadora inserida com sucesso!",
                    dao.insertRecapadora(TokenCleaner.getOnlyToken(token), recapadora));
        } catch (Exception e) {
            Log.e(TAG, "Erro ao inserir recapadora", e);
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
        } catch (Exception e) {
            Log.e(TAG, "Erro ao atualizar recapadora", e);
            throw new RecapadoraException(
                    "Não foi possível atualizar a recapadora, tente novamente!",
                    "Erro ao atualizar a recapadora!");
        }
    }

    public List<Recapadora> getRecapadoras(@NotNull final Long codEmpresa,
                                           @Nullable final Boolean ativas) throws RecapadoraException {
        try {
            return dao.getRecapadoras(codEmpresa, ativas);
        } catch (Exception e) {
            Log.e(TAG, "Erro ao buscar recapadoras para a empresa: " + codEmpresa, e);
            throw new RecapadoraException(
                    "Não foi possível buscar as recapadoras, tente novamente!",
                    "Erro ao buscar as recapadoras!");
        }
    }

    public Recapadora getRecapadora(@NotNull final Long codEmpresa,
                                    @NotNull final Long codRecapadora) throws RecapadoraException {
        try {
            return dao.getRecapadora(codEmpresa, codRecapadora);
        } catch (Exception e) {
            Log.e(TAG, "Erro ao buscar recapadora de código: " + codRecapadora, e);
            throw new RecapadoraException(
                    "Não foi possível buscar esta recapadora, tente novamente!",
                    "Erro ao buscar esta recapadora!");
        }
    }

    public Response alterarStatusRecapadoras(@NotNull final String token,
                                             @NotNull final Long codEmpresa,
                                             @NotNull final List<Recapadora> recapadoras) throws RecapadoraException {
        try {
            dao.alterarStatusRecapadoras(TokenCleaner.getOnlyToken(token), codEmpresa, recapadoras);
            return Response.ok("Status");
        } catch (Exception e) {
            Log.e(TAG, "Erro ao alterar status das recapadoras", e);
            throw new RecapadoraException(
                    "Não foi possível atualizar o status das recapadoras, tente novamente!",
                    "Erro ao atualizar o status das recapadoras!");
        }
    }
}