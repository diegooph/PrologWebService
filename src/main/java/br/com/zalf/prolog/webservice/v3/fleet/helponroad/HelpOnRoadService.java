package br.com.zalf.prolog.webservice.v3.fleet.helponroad;

import br.com.zalf.prolog.webservice.v3.fleet.helponroad._model.OpeningHelpOnRoadEntity;
import br.com.zalf.prolog.webservice.v3.fleet.kmprocessos._model.EntityKmColetado;
import br.com.zalf.prolog.webservice.v3.fleet.kmprocessos._model.KmProcessoAtualizavel;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created on 2021-03-26
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class HelpOnRoadService implements KmProcessoAtualizavel {
    @NotNull
    private final HelpOnRoadOpeningDao helpOnRoadOpeningDao;

    @NotNull
    @Override
    public EntityKmColetado getEntityKmColetado(@NotNull final Long entityId,
                                                @NotNull final Long vehicleId) {
        return getOpeningHelpOnRoadEntityByHelpOnRoadId(entityId);
    }

    @Override
    public void updateKmColetadoProcesso(@NotNull final Long processId,
                                         @NotNull final Long vehicleId,
                                         final long newKm) {
        updateKmCollectedOpeningHelpOnRoad(processId, newKm);
    }

    @NotNull
    public OpeningHelpOnRoadEntity getOpeningHelpOnRoadEntityByHelpOnRoadId(@NotNull final Long helpOnRoadId) {
        return helpOnRoadOpeningDao.getOpeningHelpOnRoadEntityByHelpOnRoadId(helpOnRoadId);
    }

    public void updateKmCollectedOpeningHelpOnRoad(@NotNull final Long helpOnRoadId,
                                                   final long newKm) {
        final OpeningHelpOnRoadEntity entity = getOpeningHelpOnRoadEntityByHelpOnRoadId(helpOnRoadId)
                .toBuilder()
                .withKmCollectedOpening(newKm)
                .build();
        helpOnRoadOpeningDao.save(entity);
    }
}
