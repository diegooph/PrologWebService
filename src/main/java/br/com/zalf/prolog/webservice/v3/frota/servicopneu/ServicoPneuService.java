package br.com.zalf.prolog.webservice.v3.frota.servicopneu;

import br.com.zalf.prolog.webservice.v3.frota.kmprocessos._model.EntityKmColetado;
import br.com.zalf.prolog.webservice.v3.frota.kmprocessos._model.KmProcessoAtualizavel;
import br.com.zalf.prolog.webservice.v3.frota.servicopneu._model.FiltroServicoListagemDto;
import br.com.zalf.prolog.webservice.v3.frota.servicopneu._model.ServicoPneuEntity;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
public class ServicoPneuService implements KmProcessoAtualizavel {
    @NotNull
    private final ServicoPneuDao servicoPneuDao;

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
        updateKmColetadoFechamento(codProcesso, novoKm);
    }

    @NotNull
    public List<ServicoPneuEntity> findServicosPneuByFilter(@NotNull final FiltroServicoListagemDto dto) {
        return servicoPneuDao.findServicosPneuByUnidades(dto.getCodUnidades(),
                                                         dto.getCodVeiculo(),
                                                         dto.getCodPneu(),
                                                         dto.getStatus().getFiltroFechado(),
                                                         PageRequest.of(dto.getOffset(),
                                                                        dto.getLimit(),
                                                                        Sort.by("codigo")));
    }

    @NotNull
    public ServicoPneuEntity getByCodigo(@NotNull final Long codigo) {
        return servicoPneuDao.getOne(codigo);
    }

    @Transactional
    public void updateKmColetadoFechamento(@NotNull final Long codServicoPneu,
                                           final long novoKm) {
        final ServicoPneuEntity entity = getByCodigo(codServicoPneu)
                .toBuilder()
                .withKmColetadoVeiculoFechamentoServico(novoKm)
                .build();
        servicoPneuDao.save(entity);
    }
}
