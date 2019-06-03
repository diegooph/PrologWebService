package br.com.zalf.prolog.webservice.integracao.praxio;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.date.Now;
import br.com.zalf.prolog.webservice.errorhandling.exception.GenericException;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.integracao.BaseIntegracaoService;
import br.com.zalf.prolog.webservice.integracao.praxio.afericao.MedicaoIntegracaoPraxio;
import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.ItemOSAbertaGlobus;
import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.ItemResolvidoGlobus;
import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.OrdemServicoAbertaGlobus;
import br.com.zalf.prolog.webservice.integracao.response.SuccessResponseIntegracao;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
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
    SuccessResponseIntegracao inserirOrdensServicoGlobus(
            final String tokenIntegracao,
            final List<OrdemServicoAbertaGlobus> ordensServicoAbertas) throws ProLogException {
        try {
            if (tokenIntegracao == null) {
                throw new GenericException("Um Token deve ser fornecido");
            }
            if (ordensServicoAbertas == null || ordensServicoAbertas.isEmpty()) {
                throw new GenericException("Nenhuma informação de O.S. aberta foi recebida");
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
    SuccessResponseIntegracao resolverMultiplosItens(
            final String tokenIntegracao,
            final List<ItemResolvidoGlobus> itensResolvidos) throws ProLogException {
        try {
            if (tokenIntegracao == null) {
                throw new GenericException("Um Token deve ser fornecido");
            }
            if (itensResolvidos == null) {
                throw new GenericException("Nenhum item resolvido foi recebido");
            }
            ensureValidToken(tokenIntegracao, TAG);
            dao.resolverMultiplosItens(tokenIntegracao, itensResolvidos);
            return new SuccessResponseIntegracao("Todos os itens foram resolvidos com sucesso no ProLog");
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao resolver os itens no ProLog", t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao resolver os itens no ProLog");
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

    @NotNull
    SuccessResponseIntegracao inserirOrdensServicoGlobusDummy(
            final String tokenIntegracao,
            final List<OrdemServicoAbertaGlobus> ordensServicoAbertas) throws ProLogException {
        try {
            if (tokenIntegracao == null) {
                throw new GenericException("Um Token deve ser fornecido");
            }
            if (!tokenIntegracao.equals("kffdm2ba5ai3lsk79kqur9rb3mq7hv59qa8pr0sho4mcr56clck")) {
                throw new GenericException("O Token fornecido é inválido");
            }
            if (ordensServicoAbertas == null || ordensServicoAbertas.isEmpty()) {
                throw new GenericException("Nenhuma informação de O.S. aberta foi recebida");
            }
            validateOrdemsServico(ordensServicoAbertas);
            return new SuccessResponseIntegracao("Ordens de Serviços Abertas foram inseridas no ProLog");
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao inserir as Ordens de Serviços Abertas no banco de dados do ProLog", t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao inserir as Ordens de Serviços Abertas no ProLog");
        }
    }

    @NotNull
    SuccessResponseIntegracao resolverMultiplosItensDummy(
            final String tokenIntegracao,
            final List<ItemResolvidoGlobus> itensResolvidos) throws ProLogException {
        try {
            if (tokenIntegracao == null) {
                throw new GenericException("Um Token deve ser fornecido");
            }
            if (!tokenIntegracao.equals("kffdm2ba5ai3lsk79kqur9rb3mq7hv59qa8pr0sho4mcr56clck")) {
                throw new GenericException("O Token fornecido é inválido");
            }
            if (itensResolvidos == null || itensResolvidos.isEmpty()) {
                throw new GenericException("Nenhuma informação de O.S. aberta foi recebida");
            }
            final LocalDateTime dataHoraAtualUtc = Now.localDateTimeUtc();
            validateDadosItensResolvidos(dataHoraAtualUtc, itensResolvidos);
            return new SuccessResponseIntegracao("Todos os itens foram resolvidos com sucesso no ProLog");
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao resolver os itens no ProLog", t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao resolver os itens no ProLog");
        }
    }

    private void validateOrdemsServico(
            @NotNull final List<OrdemServicoAbertaGlobus> ordensServicoAbertas) throws ProLogException {
        for (final OrdemServicoAbertaGlobus ordemServico : ordensServicoAbertas) {
            if (ordemServico.getCodOsGlobus() <= 0) {
                throw new GenericException("A propriedade 'codOsGlobus' deve ser um número positivo");
            }
            if (ordemServico.getCodUnidadeItemOs() <= 0) {
                throw new GenericException("A propriedade 'codUnidadeItemOs' deve ser um número positivo");
            }
            if (ordemServico.getCodChecklistProLog() <= 0) {
                throw new GenericException("A propriedade 'codChecklistProLog' deve ser um número positivo");
            }
            if (ordemServico.getItensOSAbertaGlobus().size() <= 0) {
                throw new GenericException("A lista de itens não pode estar vazia");
            }

            final List<ItemOSAbertaGlobus> itensOSAbertaGlobus = ordemServico.getItensOSAbertaGlobus();
            for (final ItemOSAbertaGlobus itemOS : itensOSAbertaGlobus) {
                if (itemOS.getCodItemGlobus() <= 0) {
                    throw new GenericException("A propriedade 'codItemGlobus' deve ser um número positivo");
                }
                if (itemOS.getCodPerguntaItemOs() <= 0) {
                    throw new GenericException("A propriedade 'codPerguntaItemOs' deve ser um número positivo");
                }
                if (itemOS.getCodAlternativaItemOs() <= 0) {
                    throw new GenericException("A propriedade 'codAlternativaItemOs' deve ser um número positivo");
                }
            }
        }
    }

    private void validateDadosItensResolvidos(
            @NotNull final LocalDateTime dataHoraAtualUtc,
            @NotNull final List<ItemResolvidoGlobus> itensResolvidos) throws ProLogException {
        for (final ItemResolvidoGlobus itemResolvido : itensResolvidos) {
            if (itemResolvido.getCodUnidadeItemOs() <= 0) {
                throw new GenericException(String.format(
                        "O 'codUnidadeItemOs = %d' deve ser um número positivo e não nulo.",
                        itemResolvido.getCodUnidadeItemOs()));
            }
            if (itemResolvido.getCodOsGlobus() <= 0) {
                throw new GenericException(String.format(
                        "O 'codOsGlobus = %d' deve ser um número positivo e não nulo.",
                        itemResolvido.getCodOsGlobus()));
            }
            if (itemResolvido.getCodItemResolvidoGlobus() <= 0) {
                throw new GenericException(String.format(
                        "O 'codItemResolvidoGlobus = %d' deve ser um número positivo e não nulo.",
                        itemResolvido.getCodItemResolvidoGlobus()));
            }
            if (itemResolvido.getCpfColaboradorResolucao().isEmpty()) {
                throw new GenericException(String.format(
                        "O 'cpfColaboradorResolucao = %s' não pode ser vazio ou nulo.",
                        itemResolvido.getCpfColaboradorResolucao()));
            }
            if (itemResolvido.getPlacaVeiculoItemOs().isEmpty()) {
                throw new GenericException(String.format(
                        "A 'placaVeiculoItemOs = %s' não pode ser vazio ou nulo.",
                        itemResolvido.getPlacaVeiculoItemOs()));
            }
            if (itemResolvido.getKmColetadoResolucao() < 0) {
                throw new GenericException(String.format(
                        "O 'kmColetadoResolucao = %d' deve ser um número positivo e não nulo.",
                        itemResolvido.getKmColetadoResolucao()));
            }
            if (itemResolvido.getDuracaoResolucaoItemOsMillis() < 0) {
                throw new GenericException(
                        "A 'duracaoResolucaoItemOsMillis' deve ser um número positivo e não nulo.");
            }
            if (itemResolvido.getFeedbackResolucaoItemOs().isEmpty()) {
                throw new GenericException(String.format(
                        "O 'feedbackResolucaoItemOs = %s' não pode ser vazio ou nulo.",
                        itemResolvido.getFeedbackResolucaoItemOs()));
            }
            if (itemResolvido.getDataHoraInicioResolucaoItemOsUtc()
                    .isAfter(itemResolvido.getDataHoraFimResolucaoItemOsUtc())) {
                final String msg = String.format(
                        "A data/hora de ínicio da resolução é posterior à data/hora de fim do conserto para o item %d",
                        itemResolvido.getCodItemResolvidoGlobus());
                throw new GenericException(msg);
            }
            if (itemResolvido.getDataHoraInicioResolucaoItemOsUtc().isAfter(dataHoraAtualUtc)) {
                final String msg = String.format(
                        "A data/hora de ínicio da resolução é posterior à data/hora atual para o item %d",
                        itemResolvido.getCodItemResolvidoGlobus());
                throw new GenericException(msg);
            }
            if (itemResolvido.getDataHoraFimResolucaoItemOsUtc().isAfter(dataHoraAtualUtc)) {
                final String msg = String.format(
                        "A data/hora de fim da resolução é posterior à data/hora atual para o item %d",
                        itemResolvido.getCodItemResolvidoGlobus());
                throw new GenericException(msg);
            }
        }
    }
}