package br.com.zalf.prolog.webservice.integracao.praxio;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.errorhandling.exception.GenericException;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.integracao.BaseIntegracaoService;
import br.com.zalf.prolog.webservice.integracao.praxio.afericao.MedicaoIntegracaoPraxio;
import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.ItemResolvidoGlobus;
import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.OrdemServicoAbertaGlobus;
import br.com.zalf.prolog.webservice.integracao.response.SuccessResponseIntegracao;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 12/12/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class IntegracaoPraxioService extends BaseIntegracaoService {
    @NotNull
    private static final String TAG = IntegracaoPraxioService.class.getSimpleName();
    @NotNull
    private final IntegracaoPraxioDao dao = new IntegracaoPraxioDaoImpl();

    @NotNull
    public List<MedicaoIntegracaoPraxio> getAfericoesRealizadas(final String tokenIntegracao,
                                                                final Long codUltimaAfericao) throws ProLogException {
        try {
            if (tokenIntegracao == null) {
                throw new GenericException("Um Token deve ser fornecido");
            }
            if (codUltimaAfericao == null) {
                throw new GenericException("Um código para a busca deve ser fornecido");
            }
            ensureValidToken(tokenIntegracao, TAG);
            return dao.getAfericoesRealizadas(tokenIntegracao, codUltimaAfericao);
        } catch (final Throwable t) {
            Log.e(TAG, String.format("Erro ao buscar as novas aferições da Integração\n" +
                    "Código da última aferição sincronizada: %d", codUltimaAfericao), t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao buscar aferições para sincronizar");
        }
    }

    @NotNull
    public SuccessResponseIntegracao inserirOrdensServicoGlobus(
            final String tokenIntegracao,
            final List<OrdemServicoAbertaGlobus> ordensServicoAbertas) throws ProLogException {
        try {
            if (tokenIntegracao == null) {
                throw new GenericException("Um Token deve ser fornecido");
            }
            if (ordensServicoAbertas == null) {
                throw new GenericException("Nenhuma informação de O.S aberta foi recebida");
            }
            ensureValidToken(tokenIntegracao, TAG);
            dao.inserirOrdensServicoGlobus(tokenIntegracao, ordensServicoAbertas);
            return new SuccessResponseIntegracao("Ordens de Serviços Abertas foram inseridas no ProLog");
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao inserir as Ordens de Serviços Abertas no banco de dados do ProLog", t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao inserir as Ordens de Serviços Abertas no ProLog");
        }
    }

    @NotNull
    public SuccessResponseIntegracao resolverMultiplosItens(
            final String tokenIntegracao,
            final List<ItemResolvidoGlobus> itensResolvidos) throws ProLogException {
        try {
            if (tokenIntegracao == null) {
                throw new GenericException("Um Token deve ser fornecido");
            }
            if (itensResolvidos == null) {
                throw new GenericException("Nenhum item fechado foi recebido");
            }
            ensureValidToken(tokenIntegracao, TAG);
            dao.resolverMultiplosItens(tokenIntegracao, itensResolvidos);
            return new SuccessResponseIntegracao("Todos os itens foram resolvidos com sucesso no ProLog");
        } catch (final Throwable t) {
            Log.e(TAG, "", t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao inserir as Ordens de Serviços Abertas no ProLog");
        }
    }

    @NotNull
    List<MedicaoIntegracaoPraxio> getDummy() {
        final List<MedicaoIntegracaoPraxio> afericoes = new ArrayList<>();
        afericoes.add(MedicaoIntegracaoPraxio.createDummyAfericaoPlacaSulcoPressao());
        afericoes.add(MedicaoIntegracaoPraxio.createDummyAfericaoPlacaSulco());
        afericoes.add(MedicaoIntegracaoPraxio.createDummyAfericaoPlacaPressao());
        afericoes.add(MedicaoIntegracaoPraxio.createDummyAfericaoPneuAvulsoSulco());
        return afericoes;
    }
}