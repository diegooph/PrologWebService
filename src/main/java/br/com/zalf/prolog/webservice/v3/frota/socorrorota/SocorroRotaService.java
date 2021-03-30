package br.com.zalf.prolog.webservice.v3.frota.socorrorota;

import br.com.zalf.prolog.webservice.v3.frota.socorrorota._model.AberturaSocorroRotaEntity;
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
    private final SocorroRotaAberturaDao socorroRotaAberturaDao;

    @Autowired
    public SocorroRotaService(@NotNull final SocorroRotaAberturaDao socorroRotaAberturaDao) {
        this.socorroRotaAberturaDao = socorroRotaAberturaDao;
    }

    @NotNull
    public AberturaSocorroRotaEntity getAberturaSocorroRotaByCodSocorro(@NotNull final Long codSocorroRta) {
        return socorroRotaAberturaDao.getAberturaSocorroRotaEntityByCodSocorroRota(codSocorroRta);
    }

    public void updateKmColetadoAberturaSocorro(@NotNull final Long codSocorroRota,
                                                final long novoKm) {
        final AberturaSocorroRotaEntity entity = getAberturaSocorroRotaByCodSocorro(codSocorroRota)
                .toBuilder()
                .withKmColetadoVeiculoAberturaSocorro(novoKm)
                .build();
        socorroRotaAberturaDao.save(entity);
    }
}
