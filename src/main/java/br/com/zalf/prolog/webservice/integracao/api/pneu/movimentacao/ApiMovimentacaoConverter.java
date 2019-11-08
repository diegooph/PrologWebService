package br.com.zalf.prolog.webservice.integracao.api.pneu.movimentacao;

import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.Movimentacao;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.OrigemDestinoEnum;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.ProcessoMovimentacao;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.destino.DestinoVeiculo;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.origem.OrigemVeiculo;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Created on 10/29/19
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class ApiMovimentacaoConverter {
    private ApiMovimentacaoConverter() {
        throw new IllegalStateException(ApiMovimentacaoConverter.class.getSimpleName() + " cannot be instantiated!");
    }

    @NotNull
    public static List<ProcessoMovimentacaoGlobus> convert(@NotNull final ProcessoMovimentacao processoMovimentacao,
                                                           @NotNull final LocalDateTime dataHoraMovimentacao) {
        final List<ProcessoMovimentacaoGlobus> processoMovimentacaoGlobus = new ArrayList<>();
        for (int i = 0; i < processoMovimentacao.getMovimentacoes().size(); i++) {
            final Movimentacao movimentacao = processoMovimentacao.getMovimentacoes().get(i);
            if (movimentacao.isFromOrigemToDestino(OrigemDestinoEnum.ESTOQUE, OrigemDestinoEnum.VEICULO)) {
                final DestinoVeiculo destinoVeiculo = (DestinoVeiculo) movimentacao.getDestino();
                processoMovimentacaoGlobus.add(new ProcessoMovimentacaoGlobus(
                        (long) i,
                        destinoVeiculo.getVeiculo().getPlaca(),
                        dataHoraMovimentacao,
                        movimentacao.getPneu().getCodigoCliente(),
                        ProcessoMovimentacaoGlobus.PNEU_INSERIDO,
                        movimentacao.getObservacao(),
                        destinoVeiculo.getPosicaoDestinoPneu()));
            } else if (movimentacao.isFromOrigemToDestino(OrigemDestinoEnum.VEICULO, OrigemDestinoEnum.ESTOQUE)) {
                final OrigemVeiculo origemVeiculo = ((OrigemVeiculo) movimentacao.getOrigem());
                processoMovimentacaoGlobus.add(new ProcessoMovimentacaoGlobus(
                        (long) i,
                        origemVeiculo.getVeiculo().getPlaca(),
                        dataHoraMovimentacao,
                        movimentacao.getPneu().getCodigoCliente(),
                        ProcessoMovimentacaoGlobus.PNEU_RETIRADO,
                        movimentacao.getObservacao(),
                        origemVeiculo.getPosicaoOrigemPneu()));
            } else if (movimentacao.isFromOrigemToDestino(OrigemDestinoEnum.VEICULO, OrigemDestinoEnum.VEICULO)) {
                final OrigemVeiculo origemVeiculo = ((OrigemVeiculo) movimentacao.getOrigem());
                final DestinoVeiculo destinoVeiculo = (DestinoVeiculo) movimentacao.getDestino();
                // Retira pneu
                processoMovimentacaoGlobus.add(new ProcessoMovimentacaoGlobus(
                        (long) i,
                        origemVeiculo.getVeiculo().getPlaca(),
                        dataHoraMovimentacao,
                        movimentacao.getPneu().getCodigoCliente(),
                        ProcessoMovimentacaoGlobus.PNEU_RETIRADO,
                        movimentacao.getObservacao(),
                        origemVeiculo.getPosicaoOrigemPneu()));

                // Aplica pneu
                processoMovimentacaoGlobus.add(new ProcessoMovimentacaoGlobus(
                        (long) i,
                        destinoVeiculo.getVeiculo().getPlaca(),
                        dataHoraMovimentacao,
                        movimentacao.getPneu().getCodigoCliente(),
                        ProcessoMovimentacaoGlobus.PNEU_INSERIDO,
                        movimentacao.getObservacao(),
                        destinoVeiculo.getPosicaoDestinoPneu()));
            } else {
                throw new IllegalStateException("Esse processo de movimentação não é válido para essa integração");
            }
        }
        final AtomicInteger counter = new AtomicInteger(0);
        final List<ProcessoMovimentacaoGlobus> collect = processoMovimentacaoGlobus
                .stream()
                .sorted(Comparator.comparing(ProcessoMovimentacaoGlobus::getTipoOperacao).reversed())
                .peek(pmg -> pmg.setSequencia((long) counter.getAndIncrement()))
                .collect(Collectors.toList());
        return collect;
    }
}