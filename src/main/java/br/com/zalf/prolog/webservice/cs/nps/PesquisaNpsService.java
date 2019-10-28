package br.com.zalf.prolog.webservice.cs.nps;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.network.ResponseWithCod;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.cs.nps.model.PesquisaNpsBloqueio;
import br.com.zalf.prolog.webservice.cs.nps.model.PesquisaNpsDisponivel;
import br.com.zalf.prolog.webservice.cs.nps.model.PesquisaNpsRealizada;
import com.google.common.annotations.VisibleForTesting;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Optional;

/**
 * Created on 2019-10-10
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class PesquisaNpsService {
    @NotNull
    private final String TAG = PesquisaNpsService.class.getSimpleName();
    @NotNull
    private final PesquisaNpsDao dao = Injection.providePesquisaNpsDao();

    @VisibleForTesting
    @NotNull
    public Response getPesquisaNpsColaborador(@NotNull final Long codColaborador) {
        try {
            final Optional<PesquisaNpsDisponivel> optional = dao.getPesquisaNpsColaborador(codColaborador);
            if (optional.isPresent()) {
                return Response
                        .ok(optional.get())
                        .type(MediaType.APPLICATION_JSON)
                        .build();
            }

            return Response
                    .noContent()
                    .build();
        } catch (final Throwable throwable) {
            Log.e(TAG, "Erro ao buscar pesquisa de NPS para colaborador: " + codColaborador, throwable);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(throwable, "Erro ao buscar pesquisa de NPS");
        }
    }

    @VisibleForTesting
    @NotNull
    public ResponseWithCod insertRespostasPesquisaNps(@NotNull final PesquisaNpsRealizada pesquisaRealizada) {
        try {
            return ResponseWithCod.ok(
                    "Recebemos sua resposta, obrigado!",
                    dao.insereRespostasPesquisaNps(pesquisaRealizada));
        } catch (final Throwable throwable) {
            final String errorMessage = String.format("Erro ao inserir pesquisa de NPS.\n" +
                            "codPesquisaNps: %d\n" +
                            "codColaborador: %d",
                    pesquisaRealizada.getCodPesquisaNps(),
                    pesquisaRealizada.getCodColaboradorRealizacao());
            Log.e(TAG, errorMessage, throwable);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(throwable, "Erro ao salvar pesquisa de NPS");
        }
    }

    @VisibleForTesting
    public void bloqueiaPesquisaNpsColaborador(@NotNull final PesquisaNpsBloqueio pesquisaBloqueio) {
        try {
            dao.bloqueiaPesquisaNpsColaborador(pesquisaBloqueio);
        } catch (final Throwable throwable) {
            final String errorMessage = String.format("Erro ao bloquear pesquisa de NPS.\n" +
                            "codPesquisaNps: %d\n" +
                            "codColaborador: %d",
                    pesquisaBloqueio.getCodPesquisaNps(),
                    pesquisaBloqueio.getCodColaboradorBloqueio());
            Log.e(TAG, errorMessage, throwable);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(throwable, "Erro ao bloquear pesquisa de NPS");
        }
    }
}
