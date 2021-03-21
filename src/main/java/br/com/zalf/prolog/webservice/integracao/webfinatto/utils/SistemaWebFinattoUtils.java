package br.com.zalf.prolog.webservice.integracao.webfinatto.utils;

import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.OrigemDestinoEnum;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.ProcessoMovimentacao;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.destino.DestinoVeiculo;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.origem.OrigemVeiculo;
import br.com.zalf.prolog.webservice.gente.colaborador.model.Empresa;
import br.com.zalf.prolog.webservice.gente.colaborador.model.Regional;
import br.com.zalf.prolog.webservice.geral.unidade._model.Unidade;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public final class SistemaWebFinattoUtils {

    public static boolean isUnidadeMasterProlog(@NotNull final Long codUnidadeProlog) {
        return codUnidadeProlog.equals(SistemaWebFinattoConstants.UNIDADE_MASTER_PROLOG);
    }

    @NotNull
    public static List<Long> getCodUnidadesFiltroProlog(@NotNull final List<Empresa> filtrosProlog) {
        return filtrosProlog.stream()
                .map(Empresa::getListRegional)
                .flatMap(Collection::stream)
                .map(Regional::getListUnidade)
                .flatMap(Collection::stream)
                .map(Unidade::getCodigo)
                .collect(Collectors.toList());
    }

    @NotNull
    public static String formatCpfAsString(@NotNull final Long cpf) {
        return String.format("%011d", cpf);
    }

    @NotNull
    public static String getPlacaFromProcessoMovimentacao(
            @NotNull final ProcessoMovimentacao processoMovimentacao) throws Throwable {
        return processoMovimentacao
                .getMovimentacoes()
                .stream()
                .filter(movimentacao -> movimentacao.isTo(OrigemDestinoEnum.VEICULO)
                        || movimentacao.isFrom(OrigemDestinoEnum.VEICULO))
                .map(movimentacao -> {
                    if (movimentacao.getOrigem() instanceof OrigemVeiculo) {
                        return ((OrigemVeiculo) movimentacao.getOrigem()).getVeiculo().getPlaca();
                    }
                    return ((DestinoVeiculo) movimentacao.getDestino()).getVeiculo().getPlaca();
                })
                .findAny()
                .orElseThrow(() -> {
                    throw new IllegalStateException("Não encontramos a placa no processo de movimentação");
                });
    }
}
