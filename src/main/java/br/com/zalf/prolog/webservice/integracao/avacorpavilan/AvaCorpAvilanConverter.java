package br.com.zalf.prolog.webservice.integracao.avacorpavilan;

import br.com.zalf.prolog.webservice.integracao.avacorpavilan._model.*;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.stream.Collectors;

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
    public static OsAvilan convert(@NotNull final OsIntegracao osIntegracao) {
        return new OsAvilan(
                osIntegracao.getCodFilial(),
                osIntegracao.getCodUnidade(),
                osIntegracao.getCodOsProlog(),
                osIntegracao.getDataHoraAbertura(),
                osIntegracao.getDataHoraAbertura(),
                osIntegracao.getPlacaVeiculo(),
                osIntegracao.getKmVeiculoNaAbertura(),
                osIntegracao.getCpfColaboradorChecklist(),
                osIntegracao.getItensNok()
                        .stream()
                        .map(itemOsIntegracao -> createItemOsAvilan(osIntegracao, itemOsIntegracao))
                        .collect(Collectors.toList()));
    }

    @NotNull
    private static ItemOsAvilan createItemOsAvilan(@NotNull final OsIntegracao osIntegracao,
                                                   @NotNull final ItemOsIntegracao itemOsIntegracao) {
        return new ItemOsAvilan(
                osIntegracao.getCodFilial(),
                osIntegracao.getCodUnidade(),
                osIntegracao.getDataHoraAbertura(),
                itemOsIntegracao.getCodDefeito(),
                itemOsIntegracao.getDescricaoAlternativa(),
                // Se o item está fechado inserimos o serviço de fechamento.
                itemOsIntegracao.getDataHoraFechamento() != null
                        ? Collections.singletonList(createFechamentoOsAvilan(osIntegracao, itemOsIntegracao))
                        : Collections.emptyList());
    }

    @NotNull
    private static FechamentoOsAvilan createFechamentoOsAvilan(@NotNull final OsIntegracao osIntegracao,
                                                               @NotNull final ItemOsIntegracao itemOsIntegracao) {
        return new FechamentoOsAvilan(
                osIntegracao.getCodFilial(),
                osIntegracao.getCodUnidade(),
                itemOsIntegracao.getDataHoraFechamento(),
                itemOsIntegracao.getCodServico(),
                itemOsIntegracao.getDescricaoFechamentoItem());
    }
}
