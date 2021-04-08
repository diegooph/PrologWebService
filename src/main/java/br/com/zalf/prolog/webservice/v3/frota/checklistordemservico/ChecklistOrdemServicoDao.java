package br.com.zalf.prolog.webservice.v3.frota.checklistordemservico;

import br.com.zalf.prolog.webservice.v3.frota.checklistordemservico._model.ChecklistOrdemServicoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created on 2021-03-26
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public interface ChecklistOrdemServicoDao extends JpaRepository<ChecklistOrdemServicoEntity, Long> {

}
