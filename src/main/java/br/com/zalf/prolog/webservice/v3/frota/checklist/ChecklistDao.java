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
            "f_cod_unidades => to_bigint_array(:codUnidades),        " +
            "f_cod_colaborador => :codColaborador,       " +
            "f_cod_tipoVeiculo => :codTipoVeiculo,       " +
            "f_cod_eiculo => :codVeiculo,       " +
            "f_incluir_espostas => :incluirRespostas,       " +
            "f_data_inicial => :dataInicial,       " +
            "f_data_final => :dataFinal,       " +
            "f_limit => :limit       " +
            "       );", nativeQuery = true)
    List<ChecklistProjection> getChecklists(@NotNull final List<Long> codUnidades,
                                            @Nullable Long codColaborador,
                                            @Nullable Long codTipoVeiculo,
                                            @Nullable Long codVeiculo,
                                            boolean incluirRespostas,
                                            @NotNull final LocalDate dataInicial,
                                            @NotNull final LocalDate dataFinal,
                                            int limit,
                                            long offset);
}