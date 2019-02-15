package br.com.zalf.prolog.webservice.integracao.transport;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.errorhandling.exception.GenericException;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.integracao.BaseIntegracaoService;
import br.com.zalf.prolog.webservice.integracao.response.SuccessResponseIntegracao;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 03/01/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class IntegracaoTransportService extends BaseIntegracaoService {
    @NotNull
    private static final String TAG = IntegracaoTransportService.class.getSimpleName();
    @NotNull
    private final IntegracaoTransportDao dao = new IntegracaoTransportDaoImpl();

    @NotNull
    public SuccessResponseIntegracao resolverMultiplosItens(
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
            return new SuccessResponseIntegracao("Itens resolvidos com sucesso");
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao salvar os itens resolvidos na Integração", t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao salvar itens resolvidos na integração");
        }
    }

    @NotNull
    public List<ItemPendenteIntegracaoTransport> getItensPendentes(
            final String tokenIntegracao,
            final Long codUltimoItemPendenteSincronizado) throws ProLogException {
        try {
            if (tokenIntegracao == null) {
                throw new GenericException("Um Token deve ser fornecido");
            }
            if (codUltimoItemPendenteSincronizado == null) {
                throw new GenericException("Um código para a busca deve ser fornecido");
            }
            ensureValidToken(tokenIntegracao, TAG);
            return dao.getItensPendentes(tokenIntegracao, codUltimoItemPendenteSincronizado);
        } catch (final Throwable t) {
            Log.e(TAG, String.format("Erro ao buscar os itens pendentes na Integração\n" +
                    "Código do último item pendente sincronizado: %d", codUltimoItemPendenteSincronizado), t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao buscar itens pendentes para sincronizar");
        }
    }

    @NotNull
    public SuccessResponseIntegracao resolverMultiplosItensDummy(
            final String tokenIntegracao,
            final List<ItemResolvidoIntegracaoTransport> itensResolvidos) throws ProLogException {
        try {
            if (tokenIntegracao == null) {
                throw new GenericException("Um Token deve ser fornecido");
            }
            if (itensResolvidos == null || itensResolvidos.size() <= 0) {
                throw new GenericException("Nenhum item foi recebido para a resolução");
            }
            ensureValidToken(tokenIntegracao, TAG);
            return verifyItensDummy(itensResolvidos);
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao resolver itens pendentes", t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao resolver itens pendentes");
        }
    }

    @NotNull
    public List<ItemPendenteIntegracaoTransport> getItensPendentesDummy(
            final String tokenIntegracao,
            final Long codUltimoItemPendenteSincronizado) throws ProLogException {
        try {
            if (tokenIntegracao == null) {
                throw new GenericException("Um Token deve ser fornecido");
            }
            if (codUltimoItemPendenteSincronizado == null || codUltimoItemPendenteSincronizado < 0) {
                throw new GenericException("Um código positivo para a busca deve ser fornecido");
            }
            ensureValidToken(tokenIntegracao, TAG);
            return getDummy();
        } catch (final Throwable t) {
            Log.e(TAG, String.format("Erro ao buscar os itens pendentes na Integração\n" +
                    "Código do último item pendente sincronizado: %d", codUltimoItemPendenteSincronizado), t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao buscar itens pendentes para sincronizar");
        }
    }

    @NotNull
    private SuccessResponseIntegracao verifyItensDummy(
            @NotNull final List<ItemResolvidoIntegracaoTransport> itensResolvidos) throws ProLogException {
        for (final ItemResolvidoIntegracaoTransport itensResolvido : itensResolvidos) {
            if (itensResolvido.getCodUnidadeOrdemServico() == null ||
                    itensResolvido.getCodUnidadeOrdemServico() <= 0) {
                throw new GenericException("O 'codUnidadeOrdemServico' deve ser um número positivo e não nulo.");
            }
            if (itensResolvido.getCodOrdemServico() == null ||
                    itensResolvido.getCodOrdemServico() <= 0) {
                throw new GenericException("O 'codOrdemServico' deve ser um número positivo e não nulo.");
            }
            if (itensResolvido.getCodItemResolvido() == null ||
                    itensResolvido.getCodItemResolvido() <= 0) {
                throw new GenericException("O 'codItemResolvido' deve ser um número positivo e não nulo.");
            }
            if (itensResolvido.getCpfColaboradoResolucao() == null ||
                    itensResolvido.getCpfColaboradoResolucao().isEmpty()) {
                throw new GenericException("O 'cpfColaboradoResolucao' não pode ser vazio ou nulo.");
            }
            if (itensResolvido.getPlacaVeiculo() == null ||
                    itensResolvido.getPlacaVeiculo().isEmpty()) {
                throw new GenericException("A 'placaVeiculo' não pode ser vazio ou nulo.");
            }
            if (itensResolvido.getKmColetadoVeiculo() == null ||
                    itensResolvido.getKmColetadoVeiculo() < 0) {
                throw new GenericException("O 'kmColetadoVeiculo' deve ser um número positivo e não nulo.");
            }
            if (itensResolvido.getDuracaoResolucaoItemEmMilissegundos() == null ||
                    itensResolvido.getDuracaoResolucaoItemEmMilissegundos() < 0) {
                throw new GenericException(
                        "O atributo'duracaoResolucaoItemEmMilissegundos' deve ser um número positivo e não nulo.");
            }
            if (itensResolvido.getFeedbackResolucao() == null ||
                    itensResolvido.getFeedbackResolucao().isEmpty()) {
                throw new GenericException("O 'feedbackResolucao' não pode ser vazio ou nulo.");
            }
            if (itensResolvido.getDataHoraResolucao() == null) {
                throw new GenericException("A 'dataHoraResolucao' não pode ser nula.");
            }
        }
        return new SuccessResponseIntegracao("Itens resolvidos");
    }

    @NotNull
    private List<ItemPendenteIntegracaoTransport> getDummy() {
        final List<ItemPendenteIntegracaoTransport> itensPendentes = new ArrayList<>();
        itensPendentes.add(ItemPendenteIntegracaoTransport.getDummy());
        itensPendentes.add(ItemPendenteIntegracaoTransport.getDummyTipoOutros());
        return itensPendentes;
    }
}