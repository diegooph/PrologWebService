package br.com.zalf.prolog.webservice.v3.frota.checklistordemservico;

import br.com.zalf.prolog.webservice.v3.frota.checklistordemservico._model.ChecklistOrdemServicoEntity;
import br.com.zalf.prolog.webservice.v3.frota.checklistordemservico._model.ChecklistOrdemServicoPk;
import br.com.zalf.prolog.webservice.v3.frota.checklistordemservico._model.ChecklistOrdemServicoProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created on 2021-03-26
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public interface ChecklistOrdemServicoDao extends JpaRepository<ChecklistOrdemServicoEntity, ChecklistOrdemServicoPk> {
    @Query(value = "select * from func_checklist_ordem_servico_listagem(" +
            "f_cod_unidade => to_bigint_array(:codUnidade));", nativeQuery = true)
    List<ChecklistOrdemServicoProjection> getOrdensServico(final List<Long> codUnidade);
}
