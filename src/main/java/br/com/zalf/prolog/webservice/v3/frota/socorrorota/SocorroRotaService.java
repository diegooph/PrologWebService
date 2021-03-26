package br.com.zalf.prolog.webservice.v3.frota.socorrorota;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created on 2021-03-26
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Service
public class SocorroRotaService {

    @NotNull
    private final SocorroRotaDao socorroRotaDao;

    @Autowired
    public SocorroRotaService(@NotNull final SocorroRotaDao socorroRotaDao) {
        this.socorroRotaDao = socorroRotaDao;
    }

    @NotNull
    public AberturaSocorroRotaEntity getAberturaSocorroRotaByCodSocorro(@NotNull final Long codSocorroRta) {
        return socorroRotaDao.getAberturaSocorroRotaEntityByCodSocorroRota(codSocorroRta);
    }
}
