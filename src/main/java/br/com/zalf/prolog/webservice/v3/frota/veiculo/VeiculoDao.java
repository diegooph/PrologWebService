package br.com.zalf.prolog.webservice.v3.frota.veiculo;

import br.com.zalf.prolog.webservice.frota.veiculo.model.VeiculoTipoProcesso;
import br.com.zalf.prolog.webservice.v3.frota.veiculo._model.VeiculoEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;

@Repository
public interface VeiculoDao extends JpaRepository<VeiculoEntity, Long> {

    @NotNull
    @Query(value = "select * from func_veiculo_update_km_atual(" +
                   "f_cod_unidade => to_bigint(:codUnidade)," +
                   "f_cod_veiculo => to_bigint(:codVeiculo)," +
                   "f_km_coletado => to_bigint(:kmVeiculo)," +
                   "f_cod_processo => to_bigint(:veiculoCodProcesso)," +
                   "f_tipo_processo => to_text(:veiculoTipoProcesso)," +
                   "f_deve_propagar_km => :devePropagarKmParaReboques," +
                   "f_data_hora => date(:dataHoraProcesso)) as km_processo", nativeQuery = true)
    Long updateKmByCodVeiculo(@NotNull final Long codUnidade,
                              @NotNull final Long codVeiculo,
                              @NotNull final Long veiculoCodProcesso,
                              @NotNull final VeiculoTipoProcesso veiculoTipoProcesso,
                              @NotNull final OffsetDateTime dataHoraProcesso,
                              final long kmVeiculo,
                              final boolean devePropagarKmParaReboques);

}
