package br.com.zalf.prolog.webservice.integracao.transport;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.date.Now;
import br.com.zalf.prolog.webservice.errorhandling.exception.GenericException;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.integracao.BaseIntegracaoService;
import br.com.zalf.prolog.webservice.integracao.response.SuccessResponseIntegracao;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
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
            final LocalDateTime dataHoraAtualUtc = Now.getLocalDateTimeUtc();
            validateDadosItensResolvidos(dataHoraAtualUtc, itensResolvidos);
            dao.resolverMultiplosItens(tokenIntegracao, dataHoraAtualUtc, itensResolvidos);
            return new SuccessResponseIntegracao("Itens resolvidos com sucesso");
        } catch (final Throwable t) {
            Log.e(TAG, "[INTEGRACAO - TRANSLECCHI] Erro ao salvar os itens resolvidos na Integração", t);
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
            Log.e(TAG, String.format("[INTEGRACAO - TRANSLECCHI] Erro ao buscar os itens pendentes na Integração\n" +
                    "Código do último item pendente sincronizado: %d", codUltimoItemPendenteSincronizado), t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao buscar itens pendentes para sincronizar");
        }
    }

    private void validateDadosItensResolvidos(
            @NotNull final LocalDateTime dataHoraAtualUtc,
            @NotNull final List<ItemResolvidoIntegracaoTransport> itensResolvidos) throws ProLogException {
        //noinspection ForLoopReplaceableByForEach
        for (int i = 0; i < itensResolvidos.size(); i++) {
            final ItemResolvidoIntegracaoTransport itemResolvido = itensResolvidos.get(i);
            if (itemResolvido.getCodUnidadeOrdemServico() <= 0) {
                throw new GenericException(String.format(
                        "O 'codUnidadeOrdemServico = %d' deve ser um número positivo e não nulo.",
                        itemResolvido.getCodUnidadeOrdemServico()));
            }
            if (itemResolvido.getCodOrdemServico() <= 0) {
                throw new GenericException(String.format(
                        "O 'codOrdemServico = %d' deve ser um número positivo e não nulo.",
                        itemResolvido.getCodOrdemServico()));
            }
            if (itemResolvido.getCodItemResolvido() <= 0) {
                throw new GenericException(String.format(
                        "O 'codItemResolvido = %d' deve ser um número positivo e não nulo.",
                        itemResolvido.getCodItemResolvido()));
            }
            if (itemResolvido.getCpfColaboradorResolucao().isEmpty()) {
                throw new GenericException(String.format(
                        "O 'cpfColaboradoResolucao = %s' não pode ser vazio ou nulo.",
                        itemResolvido.getCpfColaboradorResolucao()));
            }
            if (itemResolvido.getPlacaVeiculo().isEmpty()) {
                throw new GenericException(String.format(
                        "A 'placaVeiculo = %s' não pode ser vazio ou nulo.",
                        itemResolvido.getPlacaVeiculo()));
            }
            if (itemResolvido.getKmColetadoVeiculo() < 0) {
                throw new GenericException(String.format(
                        "O 'kmColetadoVeiculo = %d' deve ser um número positivo e não nulo.",
                        itemResolvido.getKmColetadoVeiculo()));
            }
            if (itemResolvido.getDuracaoResolucaoItemEmMilissegundos() < 0) {
                throw new GenericException(
                        "A 'duracaoResolucaoItemEmMilissegundos' deve ser um número positivo e não nulo.");
            }
            if (itemResolvido.getFeedbackResolucao().isEmpty()) {
                throw new GenericException(String.format(
                        "O 'feedbackResolucao = %s' não pode ser vazio ou nulo.",
                        itemResolvido.getFeedbackResolucao()));
            }
            if (itemResolvido.getDataHoraInicioResolucao().isAfter(itemResolvido.getDataHoraFimResolucao())) {
                final String msg = String.format(
                        "A data/hora de ínicio da resolução é posterior à data/hora de fim do conserto para o item %d",
                        itemResolvido.getCodItemResolvido());
                throw new GenericException(msg);
            }
            if (itemResolvido.getDataHoraInicioResolucao().isAfter(dataHoraAtualUtc)) {
                final String msg = String.format(
                        "A data/hora de ínicio da resolução é posterior à data/hora atual para o item %d",
                        itemResolvido.getCodItemResolvido());
                throw new GenericException(msg);
            }
            if (itemResolvido.getDataHoraFimResolucao().isAfter(dataHoraAtualUtc)) {
                final String msg = String.format(
                        "A data/hora de fim da resolução é posterior à data/hora atual para o item %d",
                        itemResolvido.getCodItemResolvido());
                throw new GenericException(msg);
            }
        }
    }

    @NotNull
    private SuccessResponseIntegracao verifyItensDummy(
            @NotNull final List<ItemResolvidoIntegracaoTransport> itensResolvidos) throws ProLogException {
        final LocalDateTime dataHoraAtualUtc = Now.getLocalDateTimeUtc();
        validateDadosItensResolvidos(dataHoraAtualUtc, itensResolvidos);
        return new SuccessResponseIntegracao("Itens resolvidos");
    }

    @NotNull
    private List<ItemPendenteIntegracaoTransport> getDummy() {
        final List<ItemPendenteIntegracaoTransport> itensPendentes = new ArrayList<>();
        itensPendentes.add(ItemPendenteIntegracaoTransport.getDummy());
        itensPendentes.add(ItemPendenteIntegracaoTransport.getDummyTipoOutros());
        return itensPendentes;
    }

    @NotNull
    SuccessResponseIntegracao resolverMultiplosItensDummy(
            final String tokenIntegracao,
            final List<ItemResolvidoIntegracaoTransport> itensResolvidos) throws ProLogException {
        try {
            if (tokenIntegracao == null) {
                throw new GenericException("Um Token deve ser fornecido");
            }
            if (itensResolvidos == null || itensResolvidos.size() <= 0) {
                throw new GenericException("Nenhum item foi recebido para a resolução");
            }
            return verifyItensDummy(itensResolvidos);
        } catch (final Throwable t) {
            Log.e(TAG, "[INTEGRACAO - TRANSLECCHI] Erro ao resolver itens pendentes", t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao resolver itens pendentes");
        }
    }

    @NotNull
    List<ItemPendenteIntegracaoTransport> getItensPendentesDummy(
            final String tokenIntegracao,
            final Long codUltimoItemPendenteSincronizado) throws ProLogException {
        try {
            if (tokenIntegracao == null) {
                throw new GenericException("Um Token deve ser fornecido");
            }
            if (codUltimoItemPendenteSincronizado == null || codUltimoItemPendenteSincronizado < 0) {
                throw new GenericException("Um código positivo para a busca deve ser fornecido");
            }
            return getDummy();
        } catch (final Throwable t) {
            Log.e(TAG, String.format("[INTEGRACAO - TRANSLECCHI] Erro ao buscar os itens pendentes na Integração\n" +
                    "Código do último item pendente sincronizado: %d", codUltimoItemPendenteSincronizado), t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao buscar itens pendentes para sincronizar");
        }
    }
}