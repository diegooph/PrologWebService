package br.com.zalf.prolog.webservice.v3.fleet.socorrorota;

import br.com.zalf.prolog.webservice.v3.fleet.kmprocessos._model.EntityKmColetado;
import br.com.zalf.prolog.webservice.v3.fleet.kmprocessos._model.KmProcessoAtualizavel;
import br.com.zalf.prolog.webservice.v3.fleet.socorrorota._model.AberturaSocorroRotaEntity;
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
public class SocorroRotaService implements KmProcessoAtualizavel {
    @NotNull
    private final SocorroRotaAberturaDao socorroRotaAberturaDao;

    @NotNull
    @Override
    public EntityKmColetado getEntityKmColetado(@NotNull final Long entityId,
                                                @NotNull final Long codVeiculo) {
        return getAberturaSocorroRotaByCodSocorro(entityId);
    }

    @Override
    public void updateKmColetadoProcesso(@NotNull final Long codProcesso,
                                         @NotNull final Long codVeiculo,
                                         final long novoKm) {
        updateKmColetadoAberturaSocorro(codProcesso, novoKm);
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
