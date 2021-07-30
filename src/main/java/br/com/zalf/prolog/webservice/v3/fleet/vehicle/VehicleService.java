package br.com.zalf.prolog.webservice.v3.fleet.vehicle;

import br.com.zalf.prolog.webservice.commons.network.SuccessResponse;
import br.com.zalf.prolog.webservice.frota.veiculo.historico._model.OrigemAcaoEnum;
import br.com.zalf.prolog.webservice.frota.veiculo.model.VeiculoTipoProcesso;
import br.com.zalf.prolog.webservice.frota.veiculo.validator.VehicleValidator;
import br.com.zalf.prolog.webservice.integracao.BlockedOperationYaml;
import br.com.zalf.prolog.webservice.v3.OffsetBasedPageRequest;
import br.com.zalf.prolog.webservice.v3.fleet.vehicle._model.VehicleCreateDto;
import br.com.zalf.prolog.webservice.v3.fleet.vehicle._model.VehicleEntity;
import br.com.zalf.prolog.webservice.v3.fleet.vehicle.makemodel.VehicleModelService;
import br.com.zalf.prolog.webservice.v3.fleet.vehicle.makemodel._model.VehicleModelEntity;
import br.com.zalf.prolog.webservice.v3.fleet.vehicle.vehiclelayout.VehicleLayoutService;
import br.com.zalf.prolog.webservice.v3.fleet.vehicle.vehiclelayout._model.VehicleLayoutEntity;
import br.com.zalf.prolog.webservice.v3.fleet.vehicle.vehicletype.VehicleTypeService;
import br.com.zalf.prolog.webservice.v3.fleet.vehicle.vehicletype._model.VehicleTypeEntity;
import br.com.zalf.prolog.webservice.v3.general.branch.BranchService;
import br.com.zalf.prolog.webservice.v3.general.branch._model.BranchEntity;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class VehicleService {
    @NotNull
    private final VehicleDao vehicleDao;
    @NotNull
    private final BranchService branchService;
    @NotNull
    private final VehicleTypeService vehicleTypeService;
    @NotNull
    private final VehicleModelService vehicleModelService;
    @NotNull
    private final VehicleLayoutService vehicleLayoutService;
    @NotNull
    private final VehicleMapper mapper;
    @NotNull
    private final BlockedOperationYaml blockedOperation;

    @NotNull
    @Transactional
    public SuccessResponse insert(@Nullable final String integrationToken,
                                  @NotNull final VehicleCreateDto vehicleCreateDto) throws Throwable {
        blockedOperation.validateBlockedCompanyBranch(vehicleCreateDto.getCodEmpresaAlocado(),
                                                      vehicleCreateDto.getCodUnidadeAlocado());
        VehicleValidator.validacaoMotorizadoSemHubodometro(vehicleCreateDto.getPossuiHubodometro(),
                                                           vehicleCreateDto.getCodTipoVeiculo());
        final BranchEntity branchEntity = branchService.getBranchById(vehicleCreateDto.getCodUnidadeAlocado());
        final VehicleModelEntity vehicleModelEntity =
                vehicleModelService.getById(vehicleCreateDto.getCodModeloVeiculo());
        final VehicleTypeEntity vehicleTypeEntity = vehicleTypeService.getById(vehicleCreateDto.getCodTipoVeiculo());
        final VehicleLayoutEntity vehicleLayoutEntity =
                vehicleLayoutService.getById(vehicleTypeEntity.getVehicleLayoutId());
        final VehicleEntity saved = vehicleDao.save(mapper.toEntity(vehicleCreateDto,
                                                                    branchEntity,
                                                                    vehicleLayoutEntity,
                                                                    vehicleTypeEntity,
                                                                    vehicleModelEntity,
                                                                    getRegisterOrigin(integrationToken)));
        return new SuccessResponse(saved.getId(), "Veículo inserido com sucesso.");
    }

    @NotNull
    public VehicleEntity getById(@NotNull final Long vehicleId) {
        return vehicleDao.getOne(vehicleId);
    }

    @NotNull
    @Transactional
    public List<VehicleEntity> getAllVehicles(@NotNull final List<Long> branchesId,
                                              final boolean includeInactive,
                                              final int limit,
                                              final int offset) {
        return vehicleDao.getAllVehicles(branchesId,
                                         includeInactive,
                                         OffsetBasedPageRequest.of(limit, offset, Sort.unsorted()));
    }

    @NotNull
    public Long updateKmVeiculo(@NotNull final Long branchId,
                                @NotNull final Long vehicleId,
                                @NotNull final Long vehicleProcessId,
                                @NotNull final VeiculoTipoProcesso vehicleProcessType,
                                @NotNull final OffsetDateTime processDateTime,
                                final long vehicleKm,
                                final boolean shouldPropagateKmToTrailers) {
        return vehicleDao.updateKmByCodVeiculo(branchId,
                                               vehicleId,
                                               vehicleProcessId,
                                               VeiculoTipoProcesso.valueOf(vehicleProcessType.toString()),
                                               processDateTime,
                                               vehicleKm,
                                               shouldPropagateKmToTrailers);
    }

    @NotNull
    private OrigemAcaoEnum getRegisterOrigin(@Nullable final String integrationToken) {
        return integrationToken != null ? OrigemAcaoEnum.API : OrigemAcaoEnum.PROLOG_WEB;
    }
}
