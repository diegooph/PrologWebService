package br.com.zalf.prolog.webservice.v3.fleet.tiremovement;

import br.com.zalf.prolog.webservice.commons.network.SuccessResponse;
import br.com.zalf.prolog.webservice.commons.util.datetime.DateUtils;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.OrigemDestinoEnum;
import br.com.zalf.prolog.webservice.v3.OffsetBasedPageRequest;
import br.com.zalf.prolog.webservice.v3.fleet.processeskm._model.KmCollectedEntity;
import br.com.zalf.prolog.webservice.v3.fleet.processeskm._model.ProcessKmUpdatable;
import br.com.zalf.prolog.webservice.v3.fleet.tiremovement._model.TireMovementDestinationEntity;
import br.com.zalf.prolog.webservice.v3.fleet.tiremovement._model.TireMovementEntity;
import br.com.zalf.prolog.webservice.v3.fleet.tiremovement._model.TireMovementProcessEntity;
import br.com.zalf.prolog.webservice.v3.fleet.tiremovement._model.TireMovementSourceEntity;
import br.com.zalf.prolog.webservice.v3.fleet.tireservice.movement.TireServiceMovementService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Created on 2021-03-26
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TireMovementProcessService implements ProcessKmUpdatable {
    @NotNull
    private final TireMovementProcessDao tireMovementProcessDao;
    @NotNull
    private final TireMovementDao tireMovementDao;
    @NotNull
    private final TireMovementSourceDao tireMovementSourceDao;
    @NotNull
    private final TireMovementDestinationDao tireMovementDestinationDao;
    @NotNull
    private final TireServiceMovementService tireServiceMovementService;

    @NotNull
    @Override
    public KmCollectedEntity getEntityKmCollected(@NotNull final Long entityId, @NotNull final Long vehicleId) {
        return getById(entityId);
    }

    @Override
    public void updateProcessKmCollected(@NotNull final Long processId,
                                         @NotNull final Long vehicleId,
                                         final long newKm) {
        updateVehicleKmTireMovement(processId, newKm);
    }

    @NotNull
    public TireMovementProcessEntity getById(@NotNull final Long tireMovementProcessId) {
        return tireMovementProcessDao.getOne(tireMovementProcessId);
    }

    public void update(@NotNull final TireMovementProcessEntity tireMovementProcessEntity) {
        tireMovementProcessDao.save(tireMovementProcessEntity);
    }

    @Transactional
    public void updateVehicleKmTireMovement(@NotNull final Long tireMovementProcessId, final long newKm) {
        getById(tireMovementProcessId)
                .getTireMovementEntities()
                .stream()
                .filter(TireMovementEntity::isTireMovementOnVehicle)
                .forEach(tireMovement -> {
                    final TireMovementSourceEntity source = tireMovement.getTireMovementSourceEntity();
                    final TireMovementDestinationEntity destination = tireMovement.getTireMovementDestinationEntity();
                    if (source.getVehicleEntity() != null) {
                        final TireMovementSourceEntity newSource = source
                                .toBuilder()
                                .withVehicleKm(newKm)
                                .build();
                        tireMovementSourceDao.save(newSource);
                    }
                    if (destination.getVehicleEntity() != null) {
                        final TireMovementDestinationEntity newDestination = destination
                                .toBuilder()
                                .withVehicleKm(newKm)
                                .build();
                        tireMovementDestinationDao.save(newDestination);
                    }
                });
    }

    @NotNull
    @Transactional
    public List<TireMovementProcessEntity> getAllTireMovements(@NotNull final List<Long> branchesId,
                                                               @NotNull final String startDate,
                                                               @NotNull final String endDate,
                                                               @Nullable final Long userId,
                                                               @Nullable final Long vehicleId,
                                                               @Nullable final Long tireId,
                                                               final int limit,
                                                               final int offset) {
        return tireMovementProcessDao.getAllTireMovements(branchesId,
                                                          DateUtils.parseDate(startDate),
                                                          DateUtils.parseDate(endDate),
                                                          userId,
                                                          vehicleId,
                                                          tireId,
                                                          OffsetBasedPageRequest.of(limit, offset, Sort.unsorted()));
    }

    @NotNull
    @Transactional
    public SuccessResponse insertTireMovementProcess(
            @NotNull final TireMovementProcessEntity tireMovementProcessEntity) {
        final TireMovementProcessEntity processoEntitySaved = tireMovementProcessDao.save(tireMovementProcessEntity);
        processoEntitySaved.getTireMovementEntities().forEach(this::insertTireMovement);

        return new SuccessResponse(1L, "Vai dar boa raça!");
    }

    private void insertTireMovement(@NotNull final TireMovementEntity tireMovementEntity) {
        final TireMovementEntity tireMovementEntitySaved = tireMovementDao.save(tireMovementEntity);
        if (tireMovementEntitySaved.isFromTo(OrigemDestinoEnum.ANALISE, OrigemDestinoEnum.ESTOQUE)) {
            insertTireMovementAnalysisToStock(tireMovementEntitySaved);
        }
    }

    private void insertTireMovementAnalysisToStock(@NotNull final TireMovementEntity tireMovementEntity) {
        tireServiceMovementService.insertTireServiceMovement(tireMovementEntity);
    }
}