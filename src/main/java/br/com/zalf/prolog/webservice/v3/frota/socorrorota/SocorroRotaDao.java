package br.com.zalf.prolog.webservice.v3.frota.socorrorota;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created on 2021-03-26
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public interface SocorroRotaDao extends JpaRepository<AberturaSocorroRotaEntity, Long> {

    @NotNull
    AberturaSocorroRotaEntity getAberturaSocorroRotaEntityByCodSocorroRota(@NotNull final Long codSocorroRota);
}
