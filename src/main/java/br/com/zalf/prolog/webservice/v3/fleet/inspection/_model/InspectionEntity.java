package br.com.zalf.prolog.webservice.v3.fleet.inspection._model;

import br.com.zalf.prolog.webservice.v3.LocalDateTimeUtcAttributeConverter;
import br.com.zalf.prolog.webservice.v3.fleet.processeskm._model.KmCollectedEntity;
import br.com.zalf.prolog.webservice.v3.fleet.processeskm._model.KmCollectedVehicle;
import br.com.zalf.prolog.webservice.v3.fleet.tiremaintenance._model.TireMaintenanceEntity;
import br.com.zalf.prolog.webservice.v3.fleet.vehicle._model.VehicleEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * Created on 2021-03-25
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Builder(toBuilder = true, setterPrefix = "with")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@Table(schema = "public", name = "afericao")
public final class InspectionEntity implements KmCollectedEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigo", nullable = false)
    private Long id;
    @Column(name = "cod_unidade", nullable = false)
    private Long branchId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_veiculo", referencedColumnName = "codigo", nullable = false)
    private VehicleEntity vehicleEntity;
    @Column(name = "km_veiculo", nullable = false)
    private Long vehicleKm;
    @OneToMany(mappedBy = "inspectionEntity", fetch = FetchType.LAZY)
    private Set<TireMaintenanceEntity> tireMaintenanceEntities;
    @Convert(converter = LocalDateTimeUtcAttributeConverter.class)
    @Column(name = "data_hora")
    private LocalDateTime inspectedAt;
    @OneToMany(mappedBy = "inspectionEntity", fetch = FetchType.LAZY, targetEntity = InspectionMeasureEntity.class)
    private Set<InspectionMeasureEntity> inspectionMeasureEntities;

    @NotNull
    @Override
    public KmCollectedVehicle getKmCollectedVehicle() {
        if (vehicleEntity == null || vehicleKm == null) {
            throw new IllegalStateException("O KM não pode ser null!");
        }
        return KmCollectedVehicle.of(vehicleEntity.getId(), vehicleKm);
    }
}
