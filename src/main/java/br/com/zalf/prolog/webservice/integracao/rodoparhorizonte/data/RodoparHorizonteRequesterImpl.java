package br.com.zalf.prolog.webservice.integracao.rodoparhorizonte.data;

import br.com.zalf.prolog.webservice.errorhandling.error.ProLogError;
import br.com.zalf.prolog.webservice.integracao.network.RestClient;
import br.com.zalf.prolog.webservice.integracao.praxio.data.ApiAutenticacaoHolder;
import br.com.zalf.prolog.webservice.integracao.rodoparhorizonte.model.AfericaoAvulsaRodoparHorizonte;
import br.com.zalf.prolog.webservice.integracao.rodoparhorizonte.model.AfericaoPlacaRodoparHorizonte;
import br.com.zalf.prolog.webservice.integracao.rodoparhorizonte.model.ResponseAfericaoRodoparHorizonte;
import br.com.zalf.prolog.webservice.integracao.rodoparhorizonte.model.error.ErrorBodyHandler;
import br.com.zalf.prolog.webservice.integracao.rodoparhorizonte.model.error.RodoparHorizonteException;
import br.com.zalf.prolog.webservice.integracao.rodoparhorizonte.model.token.RodoparCredentials;
import br.com.zalf.prolog.webservice.integracao.rodoparhorizonte.model.token.RodoparHorizonteTokenCreator;
import br.com.zalf.prolog.webservice.integracao.rodoparhorizonte.model.token.RodoparHorizonteTokenError;
import br.com.zalf.prolog.webservice.integracao.rodoparhorizonte.model.token.RodoparHorizonteTokenIntegracao;
import okhttp3.ResponseBody;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created on 25/05/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class RodoparHorizonteRequesterImpl implements RodoparHorizonteRequester {

    @NotNull
    @Override
    public RodoparHorizonteTokenIntegracao getTokenUsuarioIntegracao(
            @NotNull final ApiAutenticacaoHolder autenticacaoHolder,
            @NotNull final RodoparCredentials credentials) throws Throwable {
        final RodoparHorizonteRest service = RestClient.getService(RodoparHorizonteRest.class);
        final Call<RodoparHorizonteTokenIntegracao> call =
                service.getTokenUsuarioIntegracao(
                        autenticacaoHolder.getPrologTokenIntegracao(),
                        autenticacaoHolder.getUrl(),
                        credentials.getUsername(),
                        credentials.getPassword(),
                        credentials.getGrantType());
        return handleResponse(call.execute(), true);
    }

    @NotNull
    @Override
    public ResponseAfericaoRodoparHorizonte insertAfericaoPlaca(
            @NotNull final ApiAutenticacaoHolder autenticacaoHolder,
            @NotNull final String tokenIntegracao,
            @NotNull final AfericaoPlacaRodoparHorizonte afericao) throws Throwable {
        final RodoparHorizonteRest service = RestClient.getService(RodoparHorizonteRest.class);
        final Call<ResponseAfericaoRodoparHorizonte> call =
                service.insertAfericaoPlaca(
                        autenticacaoHolder.getPrologTokenIntegracao(),
                        RodoparHorizonteTokenCreator.createToken(tokenIntegracao),
                        autenticacaoHolder.getUrl(),
                        afericao);
        return handleResponse(call.execute());
    }

    @NotNull
    @Override
    public ResponseAfericaoRodoparHorizonte insertAfericaoAvulsa(
            @NotNull final ApiAutenticacaoHolder autenticacaoHolder,
            @NotNull final String tokenIntegracao,
            @NotNull final AfericaoAvulsaRodoparHorizonte afericao) throws Throwable {
        final RodoparHorizonteRest service = RestClient.getService(RodoparHorizonteRest.class);
        final Call<ResponseAfericaoRodoparHorizonte> call =
                service.insertAfericaoAvulsa(
                        autenticacaoHolder.getPrologTokenIntegracao(),
                        RodoparHorizonteTokenCreator.createToken(tokenIntegracao),
                        autenticacaoHolder.getUrl(),
                        afericao);
        return handleResponse(call.execute());
    }

    @NotNull
    private <T> T handleResponse(@Nullable final Response<T> response) throws Throwable {
        return handleResponse(response, false);
    }

    @NotNull
    private <T> T handleResponse(@Nullable final Response<T> response, final boolean tokenResponse) throws Throwable {
        if (response != null) {
            if (response.isSuccessful() && response.body() != null) {
                return response.body();
            } else {
                if (response.errorBody() == null) {
                    throw new RodoparHorizonteException(
                            "[INTEGRACAO] Rodopar n??o retornou todas as informa????es",
                            "A comunica????o retornou erro por??m sem nenhuma informa????o no corpo do erro");
                }
                if (tokenResponse) {
                    // A busca do token retorna erro em um padr??o diferente das demais requisi????es. Por isso tratamos
                    // esse caso especificamente.
                    final RodoparHorizonteTokenError tokenError =
                            ErrorBodyHandler.getTokenExceptionFromBody(response.errorBody());
                    throw new RodoparHorizonteException(
                            tokenError.getErrorDescription(),
                            "A requisi????o do token retornou erro, provavelmente falta mapeamento no Rodopar");
                }
                final ProLogError proLogError = toProLogError(response.errorBody());
                throw new RodoparHorizonteException(
                        ErrorBodyHandler.getErrorMessage(response.code(), proLogError),
                        "Integra????o retornou um erro e mapeamos para a estrutura do ProLogError");
            }
        } else {
            throw new RodoparHorizonteException(
                    "[INTEGRACAO] Nunhuma resposta obtida da integra????o com o sistema Rodopar",
                    "A comunica????o com o Rodopar retornou um response vazio");
        }
    }

    @NotNull
    private ProLogError toProLogError(@NotNull final ResponseBody errorBody) {
        try {
            return ErrorBodyHandler.getProLogErrorFromBody(errorBody);
        } catch (final Throwable t) {
            throw new RodoparHorizonteException(
                    "[INTEGRACAO] Mensagem do sistema Rodopar fora do padr??o esperado",
                    "N??o foi poss??vel obter o JSON de resposta da requisi????o",
                    t);
        }
    }
}
