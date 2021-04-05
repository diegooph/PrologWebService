package br.com.zalf.prolog.webservice.v3.frota.afericao;

import br.com.zalf.prolog.webservice.v3.frota.afericao._model.AfericaoEntity;
import br.com.zalf.prolog.webservice.v3.frota.afericao._model.dto.busca.FiltroAfericaoAvulsa;
import br.com.zalf.prolog.webservice.v3.frota.afericao._model.dto.busca.FiltroAfericaoPlaca;
import br.com.zalf.prolog.webservice.v3.frota.afericao._model.projections.AfericaoAvulsaProjection;
import br.com.zalf.prolog.webservice.v3.frota.afericao._model.projections.AfericaoPlacaProjection;
import br.com.zalf.prolog.webservice.v3.frota.kmprocessos._model.EntityKmColetado;
import br.com.zalf.prolog.webservice.v3.frota.kmprocessos._model.KmProcessoAtualizavel;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

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

    @NotNull
    public List<AfericaoPlacaProjection> getAfericoesPlacas(@NotNull final FiltroAfericaoPlaca filtro) {
        return afericaoDao.getAfericoes(filtro.getDadosGerais().getCodUnidades(),
                                        filtro.getCodTipoVeiculo(),
                                        filtro.getPlacaVeiculo(),
                                        filtro.getDadosGerais().getDataInicial(),
                                        filtro.getDadosGerais().getDataFinal(),
                                        filtro.getDadosGerais().getLimit(),
                                        filtro.getDadosGerais().getOffset());
    }

    @NotNull
    public List<AfericaoAvulsaProjection> getAfericoesAvulsas(@NotNull final FiltroAfericaoAvulsa filtro) {
        return afericaoDao.getAfericoes(filtro.getDadosGerais().getCodUnidades(),
                                        filtro.getDadosGerais().getDataInicial(),
                                        filtro.getDadosGerais().getDataFinal(),
                                        filtro.getDadosGerais().getLimit(),
                                        filtro.getDadosGerais().getOffset());
    }
}
