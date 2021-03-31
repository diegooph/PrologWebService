package br.com.zalf.prolog.webservice.frota.pneu.afericao.v3.dao;

import br.com.zalf.prolog.webservice.frota.pneu.afericao.v3._model.AfericaoEntity;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.v3._model.projections.AfericaoAvulsaProjection;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.v3._model.projections.AfericaoPlacaProjection;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Created on 2021-02-09
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Repository
public interface AfericaoV3Dao extends JpaRepository<AfericaoEntity, Long> {

    @NotNull
    @Query(value = "select * " +
            "from func_afericao_get_afericoes_placas_paginada(        " +
            "f_cod_unidades => to_bigint_array(:codUnidades),         " +
            "f_cod_tipo_veiculo => cast(:codTipoVeiculo as bigint),   " +
            "f_placa_veiculo => :placaVeiculo,                        " +
            "f_data_inicial => date(:dataInicial),                    " +
            "f_data_final => date(:dataFinal),                        " +
            "f_limit => cast(:limit as bigint),                       " +
            "f_offset => cast(:offset as bigint));                    ",
           nativeQuery = true)
    List<AfericaoPlacaProjection> getAfericoes(@NotNull final List<Long> codUnidades,
                                               @NotNull final Long codTipoVeiculo,
                                               @NotNull final String placaVeiculo,
                                               @NotNull final LocalDate dataInicial,
                                               @NotNull final LocalDate dataFinal,
                                               final int limit,
                                               final int offset);

    @NotNull
    @Query(value = "select * " +
            "from func_afericao_get_afericoes_avulsas_paginada( " +
            "f_cod_unidades => to_bigint_array(:codUnidades),   " +
            "f_data_inicial => date(:dataInicial),              " +
            "f_data_final => date(:dataFinal),                  " +
            "f_limit => cast(:limit as bigint),                 " +
            "f_offset => cast(:offset as bigint));              ",
           nativeQuery = true)
    List<AfericaoAvulsaProjection> getAfericoes(@NotNull final List<Long> codUnidades,
                                                @NotNull final LocalDate dataInicial,
                                                @NotNull final LocalDate dataFinal,
                                                final int limit,
                                                final int offset);
}
