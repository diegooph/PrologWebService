package br.com.zalf.prolog.webservice.v3.fleet.tiremaintenance;

import br.com.zalf.prolog.webservice.v3.OffsetBasedPageRequest;
import br.com.zalf.prolog.webservice.v3.fleet.kmprocessos._model.EntityKmColetado;
import br.com.zalf.prolog.webservice.v3.fleet.kmprocessos._model.KmProcessoAtualizavel;
import br.com.zalf.prolog.webservice.v3.fleet.tiremaintenance._model.ServicoPneuEntity;
import br.com.zalf.prolog.webservice.v3.fleet.tiremaintenance._model.ServicoPneuStatus;
import br.com.zalf.prolog.webservice.v3.fleet.tiremaintenance._model.TireMaintenanceFilter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

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
    @Transactional
    public List<ServicoPneuEntity> getAllTireMaintenance(@NotNull final TireMaintenanceFilter filtro) {
        final Optional<ServicoPneuStatus> status = Optional.ofNullable(filtro.getMaintenanceStatus());
        return servicoPneuDao.findServicosPneuByUnidades(filtro.getBranchesId(),
                                                         filtro.getVehicleId(),
                                                         filtro.getTireId(),
                                                         status.map(ServicoPneuStatus::getAsBoolean).orElse(null),
                                                         OffsetBasedPageRequest.of(filtro.getLimit(),
                                                                                   filtro.getOffset(),
                                                                                   Sort.unsorted()));
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
