package br.com.zalf.prolog.webservice.v3.frota.checklist;

import br.com.zalf.prolog.webservice.v3.frota.checklist._model.ChecklistEntity;
import br.com.zalf.prolog.webservice.v3.frota.checklist._model.ChecklistProjection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

/**
 * Created on 2021-03-25
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public interface ChecklistDao extends JpaRepository<ChecklistEntity, Long> {
    @NotNull
    @Query(value = "select * from func_checklist_get_listagem(" +
            "f_cod_unidades => to_bigint_array(:codUnidades), " +
            "f_data_inicial => :dataInicial, " +
            "f_data_final => :dataFinal, " +
            "f_cod_colaborador => :codColaborador, " +
            "f_cod_tipo_veiculo => :codTipoVeiculo, " +
            "f_cod_veiculo => :codVeiculo, " +
            "f_incluir_respostas => :incluirRespostas, " +
            "f_limit => :limit, " +
            "f_offset => :offset);", nativeQuery = true)
    List<ChecklistProjection> getChecklistsListagem(@NotNull final List<Long> codUnidades,
                                                    @NotNull final LocalDate dataInicial,
                                                    @NotNull final LocalDate dataFinal,
                                                    @Nullable final Long codColaborador,
                                                    @Nullable final Long codTipoVeiculo,
                                                    @Nullable final Long codVeiculo,
                                                    boolean incluirRespostas,
                                                    int limit,
                                                    long offset);
}