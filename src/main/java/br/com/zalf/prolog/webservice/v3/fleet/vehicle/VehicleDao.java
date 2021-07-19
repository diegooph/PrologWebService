package br.com.zalf.prolog.webservice.v3.fleet.vehicle;

import br.com.zalf.prolog.webservice.frota.veiculo.model.VeiculoTipoProcesso;
import br.com.zalf.prolog.webservice.v3.fleet.vehicle._model.VehicleEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface VehicleDao extends JpaRepository<VehicleEntity, Long> {

    @NotNull
    @Query(value = "select * from func_veiculo_update_km_atual(" +
            "f_cod_unidade => to_bigint(:branchId)," +
            "f_cod_veiculo => to_bigint(:vehicleId)," +
            "f_km_coletado => to_bigint(:vehicleKm)," +
            "f_cod_processo => to_bigint(:vehicleProcessId)," +
            "f_tipo_processo => to_text(:vehicleProcessType)," +
            "f_deve_propagar_km => :shouldPropagateKmToTrailers," +
            "f_data_hora => date(:processDateTime))", nativeQuery = true)
    Long updateKmByCodVeiculo(@NotNull final Long branchId,
                              @NotNull final Long vehicleId,
                              @NotNull final Long vehicleProcessId,
                              @NotNull final VeiculoTipoProcesso vehicleProcessType,
                              @NotNull final OffsetDateTime processDateTime,
                              final long vehicleKm,
                              final boolean shouldPropagateKmToTrailers);

    @NotNull
    @Query("select v from VehicleEntity v " +
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
                   "and (:incluirInativos = true or v.isActive = true) " +
                   "order by v.id asc")
    List<VehicleEntity> getAllVehicles(@NotNull final List<Long> codUnidades,
                                       final boolean incluirInativos,
                                       @NotNull final Pageable pageable);
}
