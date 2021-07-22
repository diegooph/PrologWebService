package br.com.zalf.prolog.webservice.v3.fleet.tire;

import br.com.zalf.prolog.webservice.commons.util.datetime.Now;
import br.com.zalf.prolog.webservice.frota.pneu._model.StatusPneu;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.OrigemDestinoEnum;
import br.com.zalf.prolog.webservice.frota.veiculo.historico._model.OrigemAcaoEnum;
import br.com.zalf.prolog.webservice.v3.fleet.movimentacao._model.MovimentacaoDestinoEntity;
import br.com.zalf.prolog.webservice.v3.fleet.tire._model.*;
import br.com.zalf.prolog.webservice.v3.fleet.vehicle._model.VehicleEntity;
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
public class TireMapper {
    @NotNull
    public TireEntity toEntity(@NotNull final TireCreateDto dto, @NotNull final OrigemAcaoEnum registerOrigin) {
        return TireEntity.builder()
                .withCompanyId(dto.getCompanyId())
                .withBranchEntity(createBranchEntity(dto.getBranchId()))
                .withClientNumber(dto.getTireClientNumber())
                .withTireModelEntity(createTireModelEntity(dto.getTireModelId()))
                .withTireSizeEntity(createTireSizeEntity(dto.getTireSizeId()))
                .withRecommendedPressure(dto.getTirePressureRecommended())
                .withTireStatus(StatusPneu.ESTOQUE)
                .withTimesRetreaded(dto.getTimesRetreaded())
                .withMaxRetreads(dto.getMaxRetreads())
                .withTreadModelEntity(
                        dto.getTireTreadId() == null ? null : createTreadModelEntity(dto.getTireTreadId()))
                .withDot(dto.getTireDot())
                .withPrice(dto.getTirePrice())
                .withCreatedAt(Now.getOffsetDateTimeUtc())
                .withIsTireNew(dto.getIsTireNew())
                .withBranchIdRegister(dto.getBranchId())
                .withRegisterOrigin(registerOrigin)
                .build();
    }

    @NotNull
    public List<TireDto> toDto(@NotNull final List<TireEntity> pneus) {
        return pneus.stream().map(this::toDto).collect(Collectors.toList());
    }

    @NotNull
    private TireDto toDto(@NotNull final TireEntity pneu) {
        final TreadModelEntity modeloBanda = pneu.getTreadModelEntity();
        final VehicleEntity veiculo = pneu.getVehicleApplied();
        final MovimentacaoDestinoEntity movimentacaoAnalise =
                pneu.getTireStatus().equals(StatusPneu.ANALISE)
                        ? pneu.getUltimaMovimentacaoByStatus(OrigemDestinoEnum.ANALISE)
                        : null;
        final MovimentacaoDestinoEntity movimentacaoDescarte =
                pneu.getTireStatus().equals(StatusPneu.DESCARTE)
                        ? pneu.getUltimaMovimentacaoByStatus(OrigemDestinoEnum.DESCARTE)
                        : null;
        return TireDto.of(pneu.getId(),
                          pneu.getClientNumber(),
                          pneu.getBranchEntity().getGroup().getId(),
                          pneu.getBranchEntity().getGroup().getName(),
                          pneu.getBranchEntity().getId(),
                          pneu.getBranchEntity().getName(),
                          pneu.getTimesRetreaded(),
                          pneu.getMaxRetreads(),
                          pneu.getRecommendedPressure(),
                          pneu.getCurrentPressure(),
                          pneu.getExternalGroove(),
                          pneu.getMiddleExternalGroove(),
                          pneu.getMiddleInternalGroove(),
                          pneu.getInternalGroove(),
                          pneu.getDot(),
                          pneu.getTireSizeEntity().getId(),
                          pneu.getTireSizeEntity().getWidth().doubleValue(),
                          pneu.getTireSizeEntity().getAspectRation().doubleValue(),
                          pneu.getTireSizeEntity().getDiameter(),
                          pneu.getTireModelEntity().getTireBrandEntity().getId(),
                          pneu.getTireModelEntity().getTireBrandEntity().getName(),
                          pneu.getTireModelEntity().getId(),
                          pneu.getTireModelEntity().getName(),
                          pneu.getTireModelEntity().getGroovesQuantity().intValue(),
                          pneu.getTireModelEntity().getGroovesWidth(),
                          pneu.getPrice(),
                          modeloBanda == null ? null : modeloBanda.getTreadBrandEntity().getId(),
                          modeloBanda == null ? null : modeloBanda.getTreadBrandEntity().getName(),
                          modeloBanda == null ? null : modeloBanda.getId(),
                          modeloBanda == null ? null : modeloBanda.getName(),
                          modeloBanda == null ? null : modeloBanda.getGroovesQuantity().intValue(),
                          modeloBanda == null ? null : modeloBanda.getGroovesWidth(),
                          modeloBanda == null ? null : pneu.getValorUltimaBandaAplicada(),
                          pneu.isTireNew(),
                          pneu.getTireStatus(),
                          veiculo == null ? null : veiculo.getId(),
                          veiculo == null ? null : veiculo.getPlate(),
                          veiculo == null ? null : veiculo.getFleetId(),
                          pneu.getPositionApplied(),
                          movimentacaoAnalise == null ? null : movimentacaoAnalise.getRecapadora().getCodigo(),
                          movimentacaoAnalise == null ? null : movimentacaoAnalise.getRecapadora().getNome(),
                          movimentacaoAnalise == null ? null : movimentacaoAnalise.getCodColeta(),
                          movimentacaoDescarte == null ? null : movimentacaoDescarte.getCodMotivoDescarte(),
                          movimentacaoDescarte == null ? null : movimentacaoDescarte.getUrlImagemDescarte1(),
                          movimentacaoDescarte == null ? null : movimentacaoDescarte.getUrlImagemDescarte2(),
                          movimentacaoDescarte == null ? null : movimentacaoDescarte.getUrlImagemDescarte3());
    }

    @NotNull
    private BranchEntity createBranchEntity(@NotNull final Long codUnidadeAlocado) {
        return BranchEntity.builder().withId(codUnidadeAlocado).build();
    }

    @NotNull
    private TireSizeEntity createTireSizeEntity(@NotNull final Long codDimensaoPneu) {
        return TireSizeEntity.builder().withId(codDimensaoPneu).build();
    }

    @NotNull
    private TireModelEntity createTireModelEntity(@NotNull final Long codModeloPneu) {
        return TireModelEntity.builder().withId(codModeloPneu).build();
    }

    @NotNull
    private TreadModelEntity createTreadModelEntity(@NotNull final Long codModeloBanda) {
        return TreadModelEntity.builder().withId(codModeloBanda).build();
    }
}
