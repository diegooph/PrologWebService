package br.com.zalf.prolog.webservice.v3.fleet.vehicle;

import br.com.zalf.prolog.webservice.frota.veiculo.model.VeiculoTipoProcesso;
import br.com.zalf.prolog.webservice.v3.fleet.vehicle._model.VeiculoEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface VehicleDao extends JpaRepository<VeiculoEntity, Long> {

    @NotNull
    @Query(value = "select * from func_veiculo_update_km_atual(" +
            "f_cod_unidade => to_bigint(:codUnidade)," +
            "f_cod_veiculo => to_bigint(:codVeiculo)," +
            "f_km_coletado => to_bigint(:kmVeiculo)," +
            "f_cod_processo => to_bigint(:veiculoCodProcesso)," +
            "f_tipo_processo => to_text(:veiculoTipoProcesso)," +
            "f_deve_propagar_km => :devePropagarKmParaReboques," +
            "f_data_hora => date(:dataHoraProcesso))", nativeQuery = true)
    Long updateKmByCodVeiculo(@NotNull final Long codUnidade,
                              @NotNull final Long codVeiculo,
                              @NotNull final Long veiculoCodProcesso,
                              @NotNull final VeiculoTipoProcesso veiculoTipoProcesso,
                              @NotNull final OffsetDateTime dataHoraProcesso,
                              final long kmVeiculo,
                              final boolean devePropagarKmParaReboques);

    @NotNull
    @Query("select v from VeiculoEntity v " +
                   "join fetch v.modeloVeiculoEntity mv " +
                   "join fetch mv.marcaVeiculoEntity mav " +
                   "join fetch v.tipoVeiculoEntity tv " +
                   "join fetch v.diagramaEntity d " +
                   "join fetch d.eixosDiagramaEntities e " +
                   "join fetch v.branchEntity u " +
                   "join fetch u.group g " +
                   "left join fetch v.acoplamentoProcessoEntity ap " +
                   "left join fetch ap.acoplamentoAtualEntities ate " +
                   "where u.id in :codUnidades " +
                   "and (:incluirInativos = true or v.statusAtivo = true) " +
                   "order by v.codigo asc")
    List<VeiculoEntity> getAllVehicles(@NotNull final List<Long> codUnidades,
                                       final boolean incluirInativos,
                                       @NotNull final Pageable pageable);
}
