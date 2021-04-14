package br.com.zalf.prolog.webservice.v3.frota.afericao;

import br.com.zalf.prolog.webservice.integracao.RecursoIntegrado;
import br.com.zalf.prolog.webservice.integracao.newrouter.Integrado;
import br.com.zalf.prolog.webservice.v3.frota.afericao._model.AfericaoEntity;
import br.com.zalf.prolog.webservice.v3.frota.kmprocessos._model.EntityKmColetado;
import br.com.zalf.prolog.webservice.v3.frota.kmprocessos._model.KmProcessoAtualizavel;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * Created on 2021-03-26
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AfericaoService implements KmProcessoAtualizavel {
    @NotNull
    private final AfericaoDao afericaoDao;

    @NotNull
    @Override
    public EntityKmColetado getEntityKmColetado(@NotNull final Long entityId,
                                                @NotNull final Long codVeiculo) {
        return getByCodigo(entityId);
    }

    @Override
    public void updateKmColetadoProcesso(@NotNull final Long codProcesso,
                                         @NotNull final Long codVeiculo,
                                         final long novoKm) {
        updateKmColetado(codProcesso, novoKm);
    }

    @NotNull
    @Integrado(recursoIntegrado = RecursoIntegrado.AFERICAO)
    public AfericaoEntity getByCodigo(@NotNull final Long codigo) {
        return afericaoDao.getOne(codigo);
    }

    @Transactional
    public void updateKmColetado(@NotNull final Long codAfericao,
                                 final long novoKm) {
        final AfericaoEntity entity = getByCodigo(codAfericao)
                .toBuilder()
                .withKmColetadoVeiculo(novoKm)
                .build();
        afericaoDao.save(entity);
    }
}
