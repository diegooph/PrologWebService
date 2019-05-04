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
            final LocalDateTime dataHoraAtualUtc = Now.localDateTimeUtc();
            validateDataHoraItensResolvidos(dataHoraAtualUtc, itensResolvidos);
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

    private void validateDataHoraItensResolvidos(
            @NotNull final LocalDateTime dataHoraAtualUtc,
            @NotNull final List<ItemResolvidoIntegracaoTransport> itensResolvidos) throws ProLogException {
        for (final ItemResolvidoIntegracaoTransport itemResolvido : itensResolvidos) {
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

    @NotNull
    private SuccessResponseIntegracao verifyItensDummy(
            @NotNull final List<ItemResolvidoIntegracaoTransport> itensResolvidos) throws ProLogException {
        for (final ItemResolvidoIntegracaoTransport itemResolvido : itensResolvidos) {
            if (itemResolvido.getCodUnidadeOrdemServico() == null
                    || itemResolvido.getCodUnidadeOrdemServico() <= 0) {
                throw new GenericException("O 'codUnidadeOrdemServico' deve ser um número positivo e não nulo.");
            }
            if (itemResolvido.getCodOrdemServico() == null
                    || itemResolvido.getCodOrdemServico() <= 0) {
                throw new GenericException("O 'codOrdemServico' deve ser um número positivo e não nulo.");
            }
            if (itemResolvido.getCodItemResolvido() == null
                    || itemResolvido.getCodItemResolvido() <= 0) {
                throw new GenericException("O 'codItemResolvido' deve ser um número positivo e não nulo.");
            }
            if (itemResolvido.getCpfColaboradorResolucao() == null
                    || itemResolvido.getCpfColaboradorResolucao().isEmpty()) {
                throw new GenericException("O 'cpfColaboradoResolucao' não pode ser vazio ou nulo.");
            }
            if (itemResolvido.getPlacaVeiculo() == null
                    || itemResolvido.getPlacaVeiculo().isEmpty()) {
                throw new GenericException("A 'placaVeiculo' não pode ser vazio ou nulo.");
            }
            if (itemResolvido.getKmColetadoVeiculo() == null
                    || itemResolvido.getKmColetadoVeiculo() < 0) {
                throw new GenericException("O 'kmColetadoVeiculo' deve ser um número positivo e não nulo.");
            }
            if (itemResolvido.getDuracaoResolucaoItemEmMilissegundos() == null
                    || itemResolvido.getDuracaoResolucaoItemEmMilissegundos() < 0) {
                throw new GenericException(
                        "O atributo'duracaoResolucaoItemEmMilissegundos' deve ser um número positivo e não nulo.");
            }
            if (itemResolvido.getFeedbackResolucao() == null ||
                    itemResolvido.getFeedbackResolucao().isEmpty()) {
                throw new GenericException("O 'feedbackResolucao' não pode ser vazio ou nulo.");
            }
            if (itemResolvido.getDataHoraResolvidoProLog() == null) {
                throw new GenericException("A 'dataHoraResolvidoProLog' não pode ser nula.");
            }
            if (itemResolvido.getDataHoraInicioResolucao() == null) {
                throw new GenericException("A 'dataHoraInicioResolucao' não pode ser nula.");
            }
            if (itemResolvido.getDataHoraFimResolucao() == null) {
                throw new GenericException("A 'dataHoraFimResolucao' não pode ser nula.");
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