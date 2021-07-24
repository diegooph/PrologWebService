package br.com.zalf.prolog.webservice.v3.fleet.helponroad;

import br.com.zalf.prolog.webservice.v3.fleet.helponroad._model.OpeningHelpOnRoadEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created on 2021-03-26
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public interface HelpOnRoadOpeningDao extends JpaRepository<OpeningHelpOnRoadEntity, Long> {

    @NotNull
    OpeningHelpOnRoadEntity getOpeningHelpOnRoadEntityByHelpOnRoadId(@NotNull final Long idHelpOnRoad);
}
