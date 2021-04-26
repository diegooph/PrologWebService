package br.com.zalf.prolog.webservice.v3.frota.movimentacao;

import br.com.zalf.prolog.webservice.commons.util.datetime.LocalDateTimeUtils;
import br.com.zalf.prolog.webservice.v3.frota.movimentacao._model.MovimentacaoEntity;
import br.com.zalf.prolog.webservice.v3.frota.movimentacao._model.MovimentacaoListagemDto;
import br.com.zalf.prolog.webservice.v3.frota.movimentacao._model.MovimentacaoProcessoEntity;
import br.com.zalf.prolog.webservice.v3.frota.movimentacao._model.MovimentacaoProcessoListagemDto;
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
        return processosMovimentacao.stream().map(this::toDto).collect(Collectors.toList());
    }

    @NotNull
    private MovimentacaoProcessoListagemDto toDto(@NotNull final MovimentacaoProcessoEntity processoEntity) {
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
                        .map(this::toDto)
                        .collect(Collectors.toList()));
    }

    private MovimentacaoListagemDto toDto(@NotNull final MovimentacaoEntity movimentacaoEntity) {
        final VeiculoEntity veiculoOrigem = movimentacaoEntity.getMovimentacaoOrigem().getVeiculo();
        final VeiculoEntity veiculoDestino = movimentacaoEntity.getMovimentacaoDestino().getVeiculo();
        return new MovimentacaoListagemDto(
                movimentacaoEntity.getCodigo(),
                movimentacaoEntity.getCodUnidade(),
                veiculoOrigem != null ? veiculoOrigem.getCodigo() : null,
                veiculoOrigem != null ? veiculoOrigem.getPlaca() : null,
                veiculoOrigem != null ? veiculoOrigem.getIdentificadorFrota() : null,
                movimentacaoEntity.getMovimentacaoOrigem().getCodDiagrama(),
                movimentacaoEntity.getMovimentacaoOrigem().getKmColetadoVeiculo(),
                movimentacaoEntity.getMovimentacaoOrigem().getTipoOrigem().asString());
    }
}
