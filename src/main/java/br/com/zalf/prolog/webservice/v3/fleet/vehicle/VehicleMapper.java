package br.com.zalf.prolog.webservice.v3.fleet.vehicle;

import br.com.zalf.prolog.webservice.commons.util.datetime.Now;
import br.com.zalf.prolog.webservice.frota.veiculo.historico._model.OrigemAcaoEnum;
import br.com.zalf.prolog.webservice.v3.fleet.acoplamento._model.AcoplamentoAtualEntity;
import br.com.zalf.prolog.webservice.v3.fleet.acoplamento._model.AcoplamentoProcessoEntity;
import br.com.zalf.prolog.webservice.v3.fleet.vehicle._model.*;
import br.com.zalf.prolog.webservice.v3.fleet.vehicle.makemodel._model.VehicleModelEntity;
import br.com.zalf.prolog.webservice.v3.fleet.vehicle.vehiclelayout._model.AxleLayoutEntity;
import br.com.zalf.prolog.webservice.v3.fleet.vehicle.vehiclelayout._model.VehicleLayoutEntity;
import br.com.zalf.prolog.webservice.v3.fleet.vehicle.vehicletype._model.VehicleTypeEntity;
import br.com.zalf.prolog.webservice.v3.general.branch._model.BranchEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class VehicleMapper {

    @NotNull
    public List<VeiculoListagemDto> toDto(@NotNull final List<VeiculoEntity> veiculoEntities) {
        return veiculoEntities.stream()
                .map(this::createVeiculoListagemDto)
                .collect(Collectors.toList());
    }

    @NotNull
    public VeiculoEntity toEntity(@NotNull final VehicleCreateDto dto,
                                  @NotNull final BranchEntity branchEntity,
                                  @NotNull final VehicleLayoutEntity vehicleLayoutEntity,
                                  @NotNull final VehicleTypeEntity vehicleTypeEntity,
                                  @NotNull final VehicleModelEntity vehicleModelEntity,
                                  @NotNull final OrigemAcaoEnum registerOrigin) {
        return VeiculoEntity.builder()
                .withCodEmpresa(dto.getCompanyId())
                .withBranchEntity(branchEntity)
                .withBranchEntityCadastro(branchEntity)
                .withVehicleLayoutEntity(vehicleLayoutEntity)
                .withMotorizado(vehicleLayoutEntity.isHasEngine())
                .withVehicleTypeEntity(vehicleTypeEntity)
                .withVehicleModelEntity(vehicleModelEntity)
                .withPlaca(dto.getVehiclePlate())
                .withIdentificadorFrota(dto.getFleetId())
                .withKm(dto.getVehicleKm())
                .withPossuiHobodometro(dto.getHasHubodometer())
                .withDataHoraCadatro(Now.getOffsetDateTimeUtc())
                .withStatusAtivo(true)
                .withOrigemCadastro(registerOrigin)
                .build();
    }

    @NotNull
    private VeiculoListagemDto createVeiculoListagemDto(@NotNull final VeiculoEntity veiculoEntity) {
        final VehicleModelEntity vehicleModelEntity = veiculoEntity.getVehicleModelEntity();
        final VehicleTypeEntity vehicleTypeEntity = veiculoEntity.getVehicleTypeEntity();
        final VehicleLayoutEntity vehicleLayoutEntity = veiculoEntity.getVehicleLayoutEntity();
        final BranchEntity branchEntity = veiculoEntity.getBranchEntity();
        final Optional<AcoplamentoProcessoEntity> acoplamentoProcessoEntity =
                veiculoEntity.getAcoplamentoProcessoEntity();

        return new VeiculoListagemDto(
                veiculoEntity.getCodigo(),
                veiculoEntity.getPlaca(),
                veiculoEntity.getIdentificadorFrota(),
                veiculoEntity.isMotorizado(),
                veiculoEntity.isPossuiHobodometro(),
                vehicleModelEntity.getVehicleMakeEntity().getId(),
                vehicleModelEntity.getVehicleMakeEntity().getName(),
                vehicleModelEntity.getId(),
                vehicleModelEntity.getName(),
                vehicleLayoutEntity.getId(),
                vehicleLayoutEntity.getAxleQuantity(AxleLayoutEntity.FRONT_AXLE),
                vehicleLayoutEntity.getAxleQuantity(AxleLayoutEntity.REAR_AXLE),
                vehicleTypeEntity.getId(),
                vehicleTypeEntity.getName(),
                branchEntity.getId(),
                branchEntity.getName(),
                branchEntity.getGroup().getId(),
                branchEntity.getGroup().getName(),
                veiculoEntity.getKm(),
                veiculoEntity.isStatusAtivo(),
                veiculoEntity.getQtdPneusAplicados(),
                veiculoEntity.isAcoplado(),
                veiculoEntity.getPosicaoAcopladoAtual(),
                acoplamentoProcessoEntity.map(acoplamentoProcesso -> createVeiculosAcoplamentos(veiculoEntity.getCodigo(),
                                                                                                acoplamentoProcesso.getCodigo(),
                                                                                                acoplamentoProcesso.getAcoplamentoAtualEntities()))
                        .orElse(null));
    }

    @NotNull
    private VeiculosAcopladosListagemDto createVeiculosAcoplamentos(
            @NotNull final Long codVeiculo,
            @NotNull final Long codProcessoAcoplamento,
            @NotNull final Set<AcoplamentoAtualEntity> acoplamentosAtuais) {
        return new VeiculosAcopladosListagemDto(
                codProcessoAcoplamento,
                acoplamentosAtuais.stream()
                        .filter(acoplamento -> !acoplamento.getCodVeiculoAcoplamentoAtual().equals(codVeiculo))
                        .map(this::createVeiculoAcoplado)
                        .collect(Collectors.toList()));
    }

    @NotNull
    private VeiculoAcopladoListagemDto createVeiculoAcoplado(
            @NotNull final AcoplamentoAtualEntity acoplamentoAtualEntity) {
        return new VeiculoAcopladoListagemDto(acoplamentoAtualEntity.getVeiculoEntity().getCodigo(),
                                              acoplamentoAtualEntity.getVeiculoEntity().getPlaca(),
                                              acoplamentoAtualEntity.getVeiculoEntity().getIdentificadorFrota(),
                                              acoplamentoAtualEntity.getVeiculoEntity().isMotorizado(),
                                              acoplamentoAtualEntity.getCodPosicao());
    }
}
