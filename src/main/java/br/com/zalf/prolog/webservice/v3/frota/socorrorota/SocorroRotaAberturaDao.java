package br.com.zalf.prolog.webservice.v3.frota.socorrorota;

import br.com.zalf.prolog.webservice.v3.frota.socorrorota._model.AberturaSocorroRotaEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created on 2021-03-26
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public interface SocorroRotaAberturaDao extends JpaRepository<AberturaSocorroRotaEntity, Long> {

    @NotNull
    AberturaSocorroRotaEntity getAberturaSocorroRotaEntityByCodSocorroRota(@NotNull final Long codSocorroRota);
}
