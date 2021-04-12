package br.com.zalf.prolog.webservice.v3.frota.checklistordemservico;

import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.StatusOrdemServico;
import br.com.zalf.prolog.webservice.v3.frota.checklistordemservico._model.ChecklistOrdemServicoEntity;
import br.com.zalf.prolog.webservice.v3.frota.checklistordemservico._model.ChecklistOrdemServicoPk;
import br.com.zalf.prolog.webservice.v3.frota.checklistordemservico._model.ChecklistOrdemServicoProjection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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
            "f_cod_unidades => to_bigint_array(:codUnidade)," +
            "f_cod_tipo_veiculo => :codTipoVeiculo," +
            "f_cod_veiculo => :codVeiculo," +
            "f_status_ordem_servico => :statusOrdemServico," +
            "f_incluir_itens_ordem_servico => :incluirItensOrdemServico," +
            "f_limit => :limit," +
            "f_offset => :offset);", nativeQuery = true)
    List<ChecklistOrdemServicoProjection> getOrdensServico(@NotNull final List<Long> codUnidade,
                                                           @Nullable final Long codTipoVeiculo,
                                                           @Nullable final String codVeiculo,
                                                           @Nullable final StatusOrdemServico statusOrdemServico,
                                                           final boolean incluirItensOrdemServico,
                                                           final int limit,
                                                           final int offset);
}
