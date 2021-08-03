package br.com.zalf.prolog.webservice.v3.fleet.processeskm;

import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.v3.fleet.processeskm._model.*;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * Created on 2021-03-25
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UpdateProcessKmService {
    private static final String TAG = UpdateProcessKmService.class.getSimpleName();
    @NotNull
    private final UpdateProcessKmDao updateProcessKmDao;
    @NotNull
    private final UpdateProcessKmServiceFactory serviceFactory;
    @NotNull
    private final UpdateProcessKmWorker updateProcessKmWorker;
    @NotNull
    private final UpdateProcessKmMapper mapper;

    @Transactional
    public void updateProcessKm(@NotNull final UpdateProcessKm updateProcessKm) {
        final ProcessKmUpdatable service = serviceFactory.createService(updateProcessKm);
        final UpdateKmResponse response = updateProcessKmWorker.updateProcessKm(service, updateProcessKm);
        if (response.isWasKmUpdated()) {
            Log.d(TAG, "Km was updated, saving history");
            final UpdateProcessKmEntity entity = mapper.toEntity(updateProcessKm, response.getOldKm());
            updateProcessKmDao.save(entity);
        } else {
            Log.d(TAG, "Km not updated, no history will be saved");
        }
    }
}
