package br.com.zalf.prolog.webservice.frota.pneu.v3.dao.servico;

import br.com.zalf.prolog.webservice.frota.pneu.v3._model.servico.PneuServicoRealizadoIncrementaVidaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created on 2021-03-18
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Repository
public interface PneuServicoRealizadoIncrementaVidaV3Dao extends JpaRepository<PneuServicoRealizadoIncrementaVidaEntity,
                                                                 PneuServicoRealizadoIncrementaVidaEntity.Id> {
}
