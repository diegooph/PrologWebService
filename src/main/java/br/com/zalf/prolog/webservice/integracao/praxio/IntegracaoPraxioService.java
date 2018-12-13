package br.com.zalf.prolog.webservice.integracao.praxio;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.NotAuthorizedException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 12/12/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class IntegracaoPraxioService {
    @NotNull
    private static final String TAG = IntegracaoPraxioService.class.getSimpleName();
    @NotNull
    private final IntegracaoPraxioDao dao = new IntegracaoPraxioDaoImpl();

    @NotNull
    List<MedicaoIntegracaoPraxio> getDummy() {
        final List<MedicaoIntegracaoPraxio> afericoes = new ArrayList<>();
        afericoes.add(MedicaoIntegracaoPraxio.createDummyAfericaoPlacaSulcoPressao());
        afericoes.add(MedicaoIntegracaoPraxio.createDummyAfericaoPlacaSulco());
        afericoes.add(MedicaoIntegracaoPraxio.createDummyAfericaoPlacaPressao());
        afericoes.add(MedicaoIntegracaoPraxio.createDummyAfericaoPneuAvulsoSulco());
        return afericoes;
    }

    @NotNull
    public List<MedicaoIntegracaoPraxio> getAfericoesRealizadas(
            @NotNull final String tokenIntegracao,
            @NotNull final Long codUltimaAfericao) throws ProLogException {
        ensureValidToken(tokenIntegracao);

        try {
            return dao.getAfericoesRealizadas(tokenIntegracao, codUltimaAfericao);
        } catch (final Throwable t) {
            Log.e(TAG, String.format("Erro ao buscar as novas aferições da Integração\n" +
                    "Código da última aferição sincronizada: %d", codUltimaAfericao), t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao buscar marcações para sincronizar");
        }
    }

    private void ensureValidToken(@NotNull final String tokenIntegracao) throws ProLogException {
        try {
            if (!Injection.provideAutenticacaoIntegracaoDao().verifyIfTokenIntegracaoExists(tokenIntegracao)) {
                throw new NotAuthorizedException("Token Integração não existe no banco de dados: " + tokenIntegracao);
            }
        } catch (final Throwable t) {
            Log.e(TAG, String.format("Erro ao verificar se o tokenIntegracao existe: %s", tokenIntegracao), t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao verificar Token da Integração");
        }
    }

    @NotNull
    List<MedicaoIntegracaoPraxio> getAfericoesRealizadasDummy(final String tokenIntegracao,
                                                              final Long codUltimaAfericao) throws ProLogException {
        try {
            if (tokenIntegracao == null) {
                throw new GenericException("Um Token deve ser fornecido");
            }
            if (codUltimaAfericao == null) {
                throw new GenericException("Um código para a busca deve ser fornecido");
            }
            return getDummy();
        } catch (Throwable t) {
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro na busca das aferições de teste");
        }
    }
}