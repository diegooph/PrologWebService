package br.com.zalf.prolog.webservice.integracao.transport;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.errorhandling.exception.GenericException;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.integracao.BaseIntegracaoService;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 03/01/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
final class IntegracaoTransportService extends BaseIntegracaoService {
    @NotNull
    private static final String TAG = IntegracaoTransportService.class.getSimpleName();
    @NotNull
    private final IntegracaoTransportDao dao = new IntegracaoTransportDaoImpl();

    @NotNull
    AbstractResponse resolverMultiplosItens(
            final String tokenIntegracao,
            final List<ItemResolvidoIntegracaoTransport> itensResolvidos) throws ProLogException {
        try {
            if (tokenIntegracao == null) {
                throw new GenericException("Um Token deve ser fornecido");
            }
            if (itensResolvidos == null) {
                throw new GenericException("Uma lista de itens resolvidos deve ser fornecida");
            }
            ensureValidToken(tokenIntegracao, TAG);
            dao.resolverMultiplosItens(tokenIntegracao, itensResolvidos);
            return Response.ok("Itens resolvidos com sucesso");
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao salvar os itens resolvidos na Integração", t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao salvar itens resolvidos na integração");
        }
    }

    @NotNull
    List<ItemPendenteIntegracaoTransport> getItensPendentes(final String tokenIntegracao,
                                                            final Long codUltimoItemPendente) throws ProLogException {
        try {
            if (tokenIntegracao == null) {
                throw new GenericException("Um Token deve ser fornecido");
            }
            if (codUltimoItemPendente == null) {
                throw new GenericException("Um código para a busca deve ser fornecido");
            }
            ensureValidToken(tokenIntegracao, TAG);
            return dao.getItensPendentes(tokenIntegracao, codUltimoItemPendente);
        } catch (final Throwable t) {
            Log.e(TAG, String.format("Erro ao buscar os itens pendentes na Integração\n" +
                    "Código do último item pendente sincronizado: %d", codUltimoItemPendente), t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao buscar itens pendentes para sincronizar");
        }
    }

    @NotNull
    List<ItemPendenteIntegracaoTransport> getDummy() {
        final List<ItemPendenteIntegracaoTransport> itensPendentes = new ArrayList<>();
        itensPendentes.add(ItemPendenteIntegracaoTransport.getDummy());
        itensPendentes.add(ItemPendenteIntegracaoTransport.getDummyTipoOutros());
        return itensPendentes;
    }
}