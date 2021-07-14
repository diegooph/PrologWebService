package br.com.zalf.prolog.webservice.v3.fleet.checklistordemservico;

import br.com.zalf.prolog.webservice.v3.fleet.checklistordemservico._model.ChecklistOrdemServicoItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created on 2021-03-26
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public interface ChecklistOrdemServicoItemDao extends JpaRepository<ChecklistOrdemServicoItemEntity, Long> {

}
