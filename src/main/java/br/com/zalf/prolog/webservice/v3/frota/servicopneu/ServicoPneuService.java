package br.com.zalf.prolog.webservice.v3.frota.servicopneu;

import br.com.zalf.prolog.webservice.v3.frota.servicopneu._model.ServicoPneuEntity;
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
public class ServicoPneuService {

    @NotNull
    private final ServicoPneuDao servicoPneuDao;

    @Autowired
    public ServicoPneuService(@NotNull final ServicoPneuDao servicoPneuDao) {
        this.servicoPneuDao = servicoPneuDao;
    }

    @NotNull
    public ServicoPneuEntity getByCodigo(@NotNull final Long codigo) {
        return servicoPneuDao.getOne(codigo);
    }

    public void update(@NotNull final ServicoPneuEntity servicoPneuEntity) {
        servicoPneuDao.save(servicoPneuEntity);
    }

    @Transactional
    public void updateKmColetadoFechamento(@NotNull final Long codServicoPneu,
                                           final long novoKm) {
        final ServicoPneuEntity entity = getByCodigo(codServicoPneu)
                .toBuilder()
                .withKmColetadoVeiculoFechamentoServico(novoKm)
                .build();
        update(entity);
    }
}
