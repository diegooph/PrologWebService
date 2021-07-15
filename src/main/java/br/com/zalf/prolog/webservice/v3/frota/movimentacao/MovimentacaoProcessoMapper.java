package br.com.zalf.prolog.webservice.v3.frota.movimentacao;

import br.com.zalf.prolog.webservice.v3.frota.movimentacao._model.*;
import br.com.zalf.prolog.webservice.v3.frota.pneu._model.PneuEntity;
import br.com.zalf.prolog.webservice.v3.frota.pneu.pneuservico.PneuServicoRealizadoEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created on 2021-04-26
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
@Component
public final class MovimentacaoProcessoMapper {
    @NotNull
    public List<MovimentacaoProcessoListagemDto> toDto(
            @NotNull final List<MovimentacaoProcessoEntity> processosMovimentacao) {
        return processosMovimentacao.stream()
                .map(this::createMovimentacaoProcessoListagemDto)
                .collect(Collectors.toList());
    }

    @NotNull
    private MovimentacaoProcessoListagemDto createMovimentacaoProcessoListagemDto(
            @NotNull final MovimentacaoProcessoEntity processoEntity) {
        final Optional<VeiculoMovimentacao> veiculo = processoEntity.getVeiculo();
        return new MovimentacaoProcessoListagemDto(processoEntity.getCodigo(),
                                                   processoEntity.getCodUnidade(),
                                                   processoEntity.getDataHoraRealizacao(),
                                                   processoEntity.getDataHoraRealizacaoTzAplicado(),
                                                   processoEntity.getColaboradorRealizacaoProcesso().getCodigo(),
                                                   processoEntity.getColaboradorRealizacaoProcesso().getCpfFormatado(),
                                                   processoEntity.getColaboradorRealizacaoProcesso().getNome(),
                                                   veiculo.map(VeiculoMovimentacao::getCodVeiculo).orElse(null),
                                                   veiculo.map(VeiculoMovimentacao::getPlacaVeiculo).orElse(null),
                                                   veiculo.map(VeiculoMovimentacao::getIdentificadorFrota).orElse(null),
                                                   veiculo.map(VeiculoMovimentacao::getKmColetado).orElse(null),
                                                   veiculo.map(VeiculoMovimentacao::getCodDiagramaVeiculo).orElse(null),
                                                   processoEntity.getObservacao(),
                                                   processoEntity.getMovimentacoes().stream()
                                                           .map(this::createMovimentacaoListagemDto)
                                                           .collect(Collectors.toList()));
    }

    @NotNull
    private MovimentacaoListagemDto createMovimentacaoListagemDto(
            @NotNull final MovimentacaoEntity movimentacaoEntity) {
        final MovimentacaoOrigemEntity movimentacaoOrigem = movimentacaoEntity.getMovimentacaoOrigem();
        final MovimentacaoDestinoEntity movimentacaoDestino = movimentacaoEntity.getMovimentacaoDestino();
        final PneuEntity pneu = movimentacaoEntity.getPneu();
        final RecapadoraEntity recapadoraDestino = movimentacaoDestino.getRecapadora();
        final Optional<Set<PneuServicoRealizadoEntity>> servicosRealizadosOptional =
                Optional.ofNullable(movimentacaoEntity.getServicosRealizados());
        return new MovimentacaoListagemDto(movimentacaoEntity.getCodigo(),
                                           pneu.getCodigo(),
                                           pneu.getCodigoCliente(),
                                           pneu.getDimensaoPneu().getCodigo(),
                                           movimentacaoEntity.getVida(),
                                           movimentacaoEntity.getSulcoInterno(),
                                           movimentacaoEntity.getSulcoCentralInterno(),
                                           movimentacaoEntity.getSulcoCentralExterno(),
                                           movimentacaoEntity.getSulcoExterno(),
                                           movimentacaoEntity.getPressaoAtual(),
                                           movimentacaoOrigem.getTipoOrigem().asString(),
                                           movimentacaoOrigem.getPosicaoPneuOrigem(),
                                           movimentacaoDestino.getTipoDestino().asString(),
                                           movimentacaoDestino.getPosicaoPneuDestino(),
                                           movimentacaoEntity.getObservacao(),
                                           movimentacaoDestino.getCodMotivoDescarte(),
                                           movimentacaoDestino.getUrlImagemDescarte1(),
                                           movimentacaoDestino.getUrlImagemDescarte2(),
                                           movimentacaoDestino.getUrlImagemDescarte3(),
                                           recapadoraDestino != null ? recapadoraDestino.getCodigo() : null,
                                           recapadoraDestino != null ? recapadoraDestino.getNome() : null,
                                           movimentacaoDestino.getCodColeta(),
                                           servicosRealizadosOptional
                                                   .map(this::createMovimentacaoPneuServicoRealizadoDtos)
                                                   .orElse(null));
    }

    @Nullable
    private List<MovimentacaoPneuServicoRealizadoDto> createMovimentacaoPneuServicoRealizadoDtos(
            @NotNull final Set<PneuServicoRealizadoEntity> pneuServicoRealizadoEntities) {
        return pneuServicoRealizadoEntities.size() == 0
                ? null
                : pneuServicoRealizadoEntities.stream()
                        .map(this::createMovimentacaoPneuServicoRealizadoDto)
                        .collect(Collectors.toList());
    }

    @NotNull
    private MovimentacaoPneuServicoRealizadoDto createMovimentacaoPneuServicoRealizadoDto(
            @NotNull final PneuServicoRealizadoEntity pneuServicoRealizadoEntity) {
        return new MovimentacaoPneuServicoRealizadoDto(pneuServicoRealizadoEntity.getCodigo(),
                                                       pneuServicoRealizadoEntity.getTipoServico().getNome(),
                                                       pneuServicoRealizadoEntity.getTipoServico().isIncrementaVida(),
                                                       pneuServicoRealizadoEntity.getCusto(),
                                                       pneuServicoRealizadoEntity.getVida());
    }
}
