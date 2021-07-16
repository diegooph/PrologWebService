package br.com.zalf.prolog.webservice.v3.fleet.checklistworkorder;

import br.com.zalf.prolog.webservice.v3.fleet.checklistworkorder._model.ChecklistWorkOrderItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created on 2021-03-26
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public interface ChecklistWorkOrderItemDao extends JpaRepository<ChecklistWorkOrderItemEntity, Long> {

}
