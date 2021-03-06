package br.com.zalf.prolog.webservice.integracao.avacorpavilan;

import br.com.zalf.prolog.webservice.integracao.avacorpavilan._model.*;
import br.com.zalf.prolog.webservice.integracao.praxio.data.ApiAutenticacaoHolder;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.stream.Collectors;

import static br.com.zalf.prolog.webservice.commons.util.StringUtils.getIntegerValueFromString;

/**
 * Created on 2020-08-31
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class AvaCorpAvilanConverter {
    private AvaCorpAvilanConverter() {
        throw new IllegalStateException(AvaCorpAvilanConverter.class.getSimpleName() + " cannot be instantiated!");
    }

    @NotNull
    public static OrdemServicoAvaCorpAvilan convert(@NotNull final OsIntegracao osIntegracao,
                                                    @NotNull final ApiAutenticacaoHolder apiAutenticacaoHolder) {
        if (apiAutenticacaoHolder.getApiShortCode() == null) {
            throw new IllegalArgumentException("apiShortCode não pode ser nulo.");
        }
        return new OrdemServicoAvaCorpAvilan(
                getIntegerValueFromString(osIntegracao.getCodFilial()),
                getIntegerValueFromString(osIntegracao.getCodUnidade()),
                osIntegracao.getCodOsProlog(),
                osIntegracao.getDataHoraAbertura(),
                osIntegracao.getDataHoraFechamento(),
                osIntegracao.getDataHoraFechamento(),
                osIntegracao.getDataHoraAbertura(),
                apiAutenticacaoHolder.getApiShortCode(),
                osIntegracao.getPlacaVeiculo(),
                osIntegracao.getKmVeiculoNaAbertura(),
                osIntegracao.getCpfColaboradorChecklist(),
                osIntegracao.getCpfColaboradorChecklist(),
                osIntegracao.getItensNok()
                        .stream()
                        .map(itemOsIntegracao -> createDefeitoAvaCorpAvilan(osIntegracao, itemOsIntegracao))
                        .collect(Collectors.toList()));
    }

    @NotNull
    private static DefeitoAvaCorpAvilan createDefeitoAvaCorpAvilan(@NotNull final OsIntegracao osIntegracao,
                                                                   @NotNull final ItemOsIntegracao itemOsIntegracao) {
        return new DefeitoAvaCorpAvilan(
                getIntegerValueFromString(osIntegracao.getCodFilial()),
                getIntegerValueFromString(osIntegracao.getCodUnidade()),
                osIntegracao.getDataHoraAbertura(),
                getIntegerValueFromString(itemOsIntegracao.getCodDefeito()),
                itemOsIntegracao.getDescricaoAlternativa(),
                // Se o item está fechado inserimos o serviço de fechamento.
                itemOsIntegracao.getDataHoraFechamento() != null
                        ? Collections.singletonList(createServicoAvaCorpAvilan(osIntegracao, itemOsIntegracao))
                        : Collections.emptyList());
    }

    @NotNull
    private static ServicoAvaCorpAvilan createServicoAvaCorpAvilan(@NotNull final OsIntegracao osIntegracao,
                                                                   @NotNull final ItemOsIntegracao itemOsIntegracao) {
        return new ServicoAvaCorpAvilan(
                getIntegerValueFromString(osIntegracao.getCodFilial()),
                getIntegerValueFromString(osIntegracao.getCodUnidade()),
                itemOsIntegracao.getDataHoraFechamento(),
                getIntegerValueFromString(itemOsIntegracao.getCodServico()),
                itemOsIntegracao.getDescricaoFechamentoItem());
    }
}
