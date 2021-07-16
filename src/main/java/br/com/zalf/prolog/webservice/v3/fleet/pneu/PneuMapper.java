package br.com.zalf.prolog.webservice.v3.fleet.pneu;

import br.com.zalf.prolog.webservice.commons.util.datetime.Now;
import br.com.zalf.prolog.webservice.frota.pneu._model.StatusPneu;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.OrigemDestinoEnum;
import br.com.zalf.prolog.webservice.v3.fleet.movimentacao._model.MovimentacaoDestinoEntity;
import br.com.zalf.prolog.webservice.v3.fleet.pneu._model.*;
import br.com.zalf.prolog.webservice.v3.fleet.vehicle._model.VeiculoEntity;
import br.com.zalf.prolog.webservice.v3.general.branch._model.BranchEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created on 2021-03-15
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Component
public class PneuMapper {
    @NotNull
    public PneuEntity toEntity(@NotNull final PneuCadastroDto dto) {
        return PneuEntity.builder()
                .codEmpresa(dto.getCodEmpresaAlocado())
                .unidade(createUnidade(dto.getCodUnidadeAlocado()))
                .codigoCliente(dto.getCodigoCliente())
                .modeloPneu(createModeloPneu(dto.getCodModeloPneu()))
                .dimensaoPneu(createDimensaoPneuEntity(dto.getCodDimensaoPneu()))
                .pressaoRecomendada(dto.getPressaoRecomendadaPneu())
                .status(StatusPneu.ESTOQUE)
                .vidaAtual(dto.getVidaAtualPneu())
                .vidaTotal(dto.getVidaTotalPneu())
                .modeloBanda(dto.getCodModeloBanda() == null ? null : createModeloBanda(dto.getCodModeloBanda()))
                .dot(dto.getDotPneu())
                .valor(dto.getValorPneu())
                .dataHoraCadastro(Now.getOffsetDateTimeUtc())
                .pneuNovoNuncaRodado(dto.getPneuNovoNuncaRodado())
                .codUnidadeCadastro(dto.getCodUnidadeAlocado())
                .build();
    }

    @NotNull
    public List<PneuListagemDto> toPneuListagemDto(@NotNull final List<PneuEntity> pneus) {
        return pneus.stream().map(this::toPneuListagemDto).collect(Collectors.toList());
    }

    @NotNull
    private PneuListagemDto toPneuListagemDto(@NotNull final PneuEntity pneu) {
        final ModeloBandaEntity modeloBanda = pneu.getModeloBanda();
        final VeiculoEntity veiculo = pneu.getVeiculoPneuAplicado();
        final MovimentacaoDestinoEntity movimentacaoAnalise =
                pneu.getStatus().equals(StatusPneu.ANALISE)
                        ? pneu.getUltimaMovimentacaoByStatus(OrigemDestinoEnum.ANALISE)
                        : null;
        final MovimentacaoDestinoEntity movimentacaoDescarte =
                pneu.getStatus().equals(StatusPneu.DESCARTE)
                        ? pneu.getUltimaMovimentacaoByStatus(OrigemDestinoEnum.DESCARTE)
                        : null;
        return PneuListagemDto.of(pneu.getCodigo(),
                                  pneu.getCodigoCliente(),
                                  pneu.getUnidade().getGroup().getId(),
                                  pneu.getUnidade().getGroup().getName(),
                                  pneu.getUnidade().getId(),
                                  pneu.getUnidade().getName(),
                                  pneu.getVidaAtual(),
                                  pneu.getVidaTotal(),
                                  pneu.getPressaoRecomendada(),
                                  pneu.getPressaoAtual(),
                                  pneu.getAlturaSulcoExterno(),
                                  pneu.getAlturaSulcoCentralExterno(),
                                  pneu.getAlturaSulcoCentralInterno(),
                                  pneu.getAlturaSulcoInterno(),
                                  pneu.getDot(),
                                  pneu.getDimensaoPneu().getCodigo(),
                                  pneu.getDimensaoPneu().getAltura().doubleValue(),
                                  pneu.getDimensaoPneu().getLargura().doubleValue(),
                                  pneu.getDimensaoPneu().getAro(),
                                  pneu.getModeloPneu().getMarca().getCodigo(),
                                  pneu.getModeloPneu().getMarca().getNome(),
                                  pneu.getModeloPneu().getCodigo(),
                                  pneu.getModeloPneu().getNome(),
                                  pneu.getModeloPneu().getQuantidadeSulcos().intValue(),
                                  pneu.getModeloPneu().getAlturaSulcos(),
                                  pneu.getValor(),
                                  modeloBanda == null ? null : modeloBanda.getMarcaBanda().getCodigo(),
                                  modeloBanda == null ? null : modeloBanda.getMarcaBanda().getNome(),
                                  modeloBanda == null ? null : modeloBanda.getCodigo(),
                                  modeloBanda == null ? null : modeloBanda.getNome(),
                                  modeloBanda == null ? null : modeloBanda.getQuantidadeSulcos().intValue(),
                                  modeloBanda == null ? null : modeloBanda.getAlturaSulcos(),
                                  modeloBanda == null ? null : pneu.getValorUltimaBandaAplicada(),
                                  pneu.isPneuNovoNuncaRodado(),
                                  pneu.getStatus(),
                                  veiculo == null ? null : veiculo.getCodigo(),
                                  veiculo == null ? null : veiculo.getPlaca(),
                                  veiculo == null ? null : veiculo.getIdentificadorFrota(),
                                  pneu.getPosicaoAplicado(),
                                  movimentacaoAnalise == null ? null : movimentacaoAnalise.getRecapadora().getCodigo(),
                                  movimentacaoAnalise == null ? null : movimentacaoAnalise.getRecapadora().getNome(),
                                  movimentacaoAnalise == null ? null : movimentacaoAnalise.getCodColeta(),
                                  movimentacaoDescarte == null ? null : movimentacaoDescarte.getCodMotivoDescarte(),
                                  movimentacaoDescarte == null ? null : movimentacaoDescarte.getUrlImagemDescarte1(),
                                  movimentacaoDescarte == null ? null : movimentacaoDescarte.getUrlImagemDescarte2(),
                                  movimentacaoDescarte == null ? null : movimentacaoDescarte.getUrlImagemDescarte3());
    }

    @NotNull
    private BranchEntity createUnidade(@NotNull final Long codUnidadeAlocado) {
        return BranchEntity.builder().withId(codUnidadeAlocado).build();
    }

    @NotNull
    private DimensaoPneuEntity createDimensaoPneuEntity(@NotNull final Long codDimensaoPneu) {
        return DimensaoPneuEntity.builder().withCodigo(codDimensaoPneu).build();
    }

    @NotNull
    private ModeloPneuEntity createModeloPneu(@NotNull final Long codModeloPneu) {
        return ModeloPneuEntity.builder().withCodigo(codModeloPneu).build();
    }

    @NotNull
    private ModeloBandaEntity createModeloBanda(@NotNull final Long codModeloBanda) {
        return ModeloBandaEntity.builder().withCodigo(codModeloBanda).build();
    }
}
