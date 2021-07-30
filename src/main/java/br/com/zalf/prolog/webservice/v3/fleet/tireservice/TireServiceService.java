package br.com.zalf.prolog.webservice.v3.fleet.tireservice;

import br.com.zalf.prolog.webservice.frota.pneu.pneutiposervico._model.PneuServicoRealizado;
import br.com.zalf.prolog.webservice.v3.fleet.tire._model.TireEntity;
import br.com.zalf.prolog.webservice.v3.fleet.tireservice._model.TireServiceEntity;
import br.com.zalf.prolog.webservice.v3.fleet.tireservice.register.TireServiceRegisterDao;
import br.com.zalf.prolog.webservice.v3.fleet.tireservice.servicetype.TireServiceTypeEntity;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;

/**
 * Created on 2021-03-18
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TireServiceService {
    @NotNull
    private final TireServiceDao tireServiceDao;
    @NotNull
    private final TireServiceIncreaseLifeCycleDao tireServiceIncreaseLifeCycleDao;
    @NotNull
    private final TireServiceRegisterDao tireServiceRegisterDao;

    @NotNull
    @Transactional
    public TireServiceEntity insertTireService(@NotNull final TireEntity tireEntity,
                                               @NotNull final BigDecimal tireTreadPrice,
                                               @NotNull final TireServiceTypeEntity tireServiceTypeEntity,
                                               @NotNull final String tireServiceOrigin) {
        final TireServiceEntity savedTireService =
                tireServiceDao.save(TireServiceCreator.createTireService(tireServiceTypeEntity,
                                                                         tireEntity,
                                                                         tireServiceOrigin,
                                                                         tireTreadPrice));
        if (tireServiceTypeEntity.isIncreaseLifeCycle()) {
            insertTireServiceIncreaseLifeCycle(savedTireService, tireEntity, tireServiceOrigin);
        }
        if (tireServiceOrigin.equals(PneuServicoRealizado.FONTE_CADASTRO)) {
            insertTireServiceRegister(savedTireService, tireServiceOrigin);
        }
        return savedTireService;
    }

    private void insertTireServiceIncreaseLifeCycle(@NotNull final TireServiceEntity tireServiceEntity,
                                                    @NotNull final TireEntity tireEntity,
                                                    @NotNull final String tireServiceOrigin) {
        tireServiceIncreaseLifeCycleDao.save(
                TireServiceCreator.createTireServiceIncreaseLifeCycle(tireEntity,
                                                                      tireServiceEntity,
                                                                      tireServiceOrigin));
    }

    private void insertTireServiceRegister(@NotNull final TireServiceEntity tireServiceEntity,
                                           @NotNull final String tireServiceOrigin) {
        tireServiceRegisterDao.save(TireServiceCreator.createTireServiceRegister(tireServiceEntity, tireServiceOrigin));
    }
}