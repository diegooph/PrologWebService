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
    public static OrdemServicoAvaCorpAvilan convert(@NotNull final OsIntegracao osIntegracao) {
        return new OrdemServicoAvaCorpAvilan(
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
                        .map(itemOsIntegracao -> createDefeitoAvaCorpAvilan(osIntegracao, itemOsIntegracao))
                        .collect(Collectors.toList()));
    }

    @NotNull
    private static DefeitoAvaCorpAvilan createDefeitoAvaCorpAvilan(@NotNull final OsIntegracao osIntegracao,
                                                                   @NotNull final ItemOsIntegracao itemOsIntegracao) {
        return new DefeitoAvaCorpAvilan(
                osIntegracao.getCodFilial(),
                osIntegracao.getCodUnidade(),
                osIntegracao.getDataHoraAbertura(),
                itemOsIntegracao.getCodDefeito(),
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
                osIntegracao.getCodFilial(),
                osIntegracao.getCodUnidade(),
                itemOsIntegracao.getDataHoraFechamento(),
                itemOsIntegracao.getCodServico(),
                itemOsIntegracao.getDescricaoFechamentoItem());
    }
}
