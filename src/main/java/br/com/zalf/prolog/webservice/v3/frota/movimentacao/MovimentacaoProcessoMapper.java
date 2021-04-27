package br.com.zalf.prolog.webservice.v3.frota.movimentacao;

import br.com.zalf.prolog.webservice.commons.util.datetime.LocalDateTimeUtils;
import br.com.zalf.prolog.webservice.v3.frota.movimentacao._model.*;
import br.com.zalf.prolog.webservice.v3.frota.pneu._model.PneuEntity;
import br.com.zalf.prolog.webservice.v3.frota.veiculo._model.VeiculoEntity;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created on 2021-04-26
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
@Component
public final class MovimentacaoProcessoMapper {
    public List<MovimentacaoProcessoListagemDto> toDto(
            @NotNull final List<MovimentacaoProcessoEntity> processosMovimentacao) {
        return processosMovimentacao.stream()
                .map(this::createMovimentacaoProcessoListagemDto)
                .collect(Collectors.toList());
    }

    @NotNull
    private MovimentacaoProcessoListagemDto createMovimentacaoProcessoListagemDto(
            @NotNull final MovimentacaoProcessoEntity processoEntity) {
        return new MovimentacaoProcessoListagemDto(
                processoEntity.getCodigo(),
                processoEntity.getCodUnidade(),
                processoEntity.getDataHoraRealizacao(),
                LocalDateTimeUtils.applyTimezone(
                        processoEntity.getDataHoraRealizacao(),
                        ZoneId.of(processoEntity.getColaboradorRealizacaoProcesso().getUnidade().getTimezone())),
                processoEntity.getColaboradorRealizacaoProcesso().getCodigo(),
                StringUtils.leftPad(
                        processoEntity.getColaboradorRealizacaoProcesso().getCpf().toString(), 11, "0"),
                processoEntity.getColaboradorRealizacaoProcesso().getNome(),
                processoEntity.getObservacao(),
                processoEntity.getMovimentacoes().stream()
                        .map(this::createMovimentacaoListagemDto)
                        .collect(Collectors.toList()));
    }

    private MovimentacaoListagemDto createMovimentacaoListagemDto(@NotNull final MovimentacaoEntity movimentacaoEntity) {
        final VeiculoEntity veiculoOrigem = movimentacaoEntity.getMovimentacaoOrigem().getVeiculo();
        final VeiculoEntity veiculoDestino = movimentacaoEntity.getMovimentacaoDestino().getVeiculo();
        final RecapadoraEntity recapadoraDestino = movimentacaoEntity.getMovimentacaoDestino().getRecapadora();
        return new MovimentacaoListagemDto(
                movimentacaoEntity.getCodigo(),
                movimentacaoEntity.getCodUnidade(),
                veiculoOrigem != null ? veiculoOrigem.getCodigo() : null,
                veiculoOrigem != null ? veiculoOrigem.getPlaca() : null,
                veiculoOrigem != null ? veiculoOrigem.getIdentificadorFrota() : null,
                movimentacaoEntity.getMovimentacaoOrigem().getCodDiagrama(),
                movimentacaoEntity.getMovimentacaoOrigem().getKmColetadoVeiculo(),
                movimentacaoEntity.getMovimentacaoOrigem().getTipoOrigem().asString(),
                veiculoDestino != null ? veiculoDestino.getCodigo() : null,
                veiculoDestino != null ? veiculoDestino.getPlaca() : null,
                veiculoDestino != null ? veiculoDestino.getIdentificadorFrota() : null,
                movimentacaoEntity.getMovimentacaoDestino().getCodDiagrama(),
                movimentacaoEntity.getMovimentacaoDestino().getKmColetadoVeiculo(),
                movimentacaoEntity.getMovimentacaoDestino().getTipoDestino().asString(),
                movimentacaoEntity.getMovimentacaoDestino().getPosicaoPneuDestino(),
                movimentacaoEntity.getMovimentacaoDestino().getCodMotivoDescarte(),
                movimentacaoEntity.getMovimentacaoDestino().getCodColeta(),
                movimentacaoEntity.getMovimentacaoDestino().getUrlImagemDescarte1(),
                movimentacaoEntity.getMovimentacaoDestino().getUrlImagemDescarte2(),
                movimentacaoEntity.getMovimentacaoDestino().getUrlImagemDescarte3(),
                recapadoraDestino != null ? recapadoraDestino.getCodigo() : null,
                recapadoraDestino != null ? recapadoraDestino.getNome() : null,
                createPneuMovimentacaoListagemDto(movimentacaoEntity.getPneu()));
    }

    private PneuMovimentacaoListagemDto createPneuMovimentacaoListagemDto(@NotNull final PneuEntity pneuEntity) {
        return new PneuMovimentacaoListagemDto(pneuEntity.getCodigo(),
                                               pneuEntity.getCodigoCliente(),
                                               pneuEntity.getCodModelo(),
                                               pneuEntity.getCodDimensao(),
                                               pneuEntity.getVidaAtual(),
                                               pneuEntity.getPressaoAtual(),
                                               pneuEntity.getAlturaSulcoInterno(),
                                               pneuEntity.getAlturaSulcoCentralInterno(),
                                               pneuEntity.getAlturaSulcoCentralExterno(),
                                               pneuEntity.getAlturaSulcoExterno());
    }
}
