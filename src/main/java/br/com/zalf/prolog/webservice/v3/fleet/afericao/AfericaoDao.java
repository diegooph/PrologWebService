package br.com.zalf.prolog.webservice.v3.fleet.afericao;

import br.com.zalf.prolog.webservice.v3.fleet.afericao._model.AfericaoAvulsaProjection;
import br.com.zalf.prolog.webservice.v3.fleet.afericao._model.AfericaoEntity;
import br.com.zalf.prolog.webservice.v3.fleet.afericao._model.AfericaoPlacaProjection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

/**
 * Created on 2021-03-26
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public interface AfericaoDao extends JpaRepository<AfericaoEntity, Long> {
    @NotNull
    @Query(value = "select * from func_afericao_get_afericoes_placas_paginada(" +
            "f_cod_unidades => to_bigint_array(:codUnidades),         " +
            "f_cod_tipo_veiculo => to_bigint(:codTipoVeiculo),        " +
            "f_cod_veiculo => to_bigint(:codVeiculo),                 " +
            "f_data_inicial => date(:dataInicial),                    " +
            "f_data_final => date(:dataFinal),                        " +
            "f_limit => :limit,                                       " +
            "f_offset => :offset,                                     " +
            "f_incluir_medidas => :incluirMedidas);                   ", nativeQuery = true)
    List<AfericaoPlacaProjection> getAfericoesPlacas(@NotNull final List<Long> codUnidades,
                                                     @Nullable final Long codTipoVeiculo,
                                                     @Nullable final Long codVeiculo,
                                                     @NotNull final LocalDate dataInicial,
                                                     @NotNull final LocalDate dataFinal,
                                                     final int limit,
                                                     final int offset,
                                                     final boolean incluirMedidas);

    @NotNull
    @Query(value = "select * from func_afericao_get_afericoes_avulsas_paginada( " +
            "f_cod_unidades => to_bigint_array(:codUnidades),   " +
            "f_data_inicial => date(:dataInicial),              " +
            "f_data_final => date(:dataFinal),                  " +
            "f_limit => :limit,                                 " +
            "f_offset => :offset,                               " +
            "f_incluir_medidas => :incluirMedidas);             ", nativeQuery = true)
    List<AfericaoAvulsaProjection> getAfericoesAvulsas(@NotNull final List<Long> codUnidades,
                                                       @NotNull final LocalDate dataInicial,
                                                       @NotNull final LocalDate dataFinal,
                                                       final int limit,
                                                       final int offset,
                                                       final boolean incluirMedidas);
}
