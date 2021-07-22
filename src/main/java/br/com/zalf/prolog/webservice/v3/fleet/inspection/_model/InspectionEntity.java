package br.com.zalf.prolog.webservice.v3.fleet.inspection._model;

import br.com.zalf.prolog.webservice.v3.LocalDateTimeUtcAttributeConverter;
import br.com.zalf.prolog.webservice.v3.fleet.kmprocessos._model.EntityKmColetado;
import br.com.zalf.prolog.webservice.v3.fleet.kmprocessos._model.VeiculoKmColetado;
import br.com.zalf.prolog.webservice.v3.fleet.tiremaintenance._model.TireMaintenanceEntity;
import br.com.zalf.prolog.webservice.v3.fleet.vehicle._model.VehicleEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
public final class InspectionEntity implements EntityKmColetado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigo", nullable = false)
    @NotNull
    private Long id;
    @Column(name = "cod_unidade", nullable = false)
    @NotNull
    private Long branchId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_veiculo", referencedColumnName = "codigo", nullable = false)
    @Nullable
    private VehicleEntity vehicleEntity;
    @Column(name = "km_veiculo", nullable = false)
    @Nullable
    private Long vehicleKm;
    @OneToMany(mappedBy = "inspectionEntity", fetch = FetchType.LAZY)
    @Nullable
    private Set<TireMaintenanceEntity> tireMaintenanceEntities;
    @Convert(converter = LocalDateTimeUtcAttributeConverter.class)
    @Column(name = "data_hora")
    @NotNull
    private LocalDateTime inspectedAt;
    @OneToMany(mappedBy = "inspectionEntity", fetch = FetchType.LAZY, targetEntity = InspectionMeasureEntity.class)
    @NotNull
    private Set<InspectionMeasureEntity> inspectionMeasureEntities;

    @NotNull
    @Override
    public VeiculoKmColetado getVeiculoKmColetado() {
        if (vehicleEntity == null || vehicleKm == null) {
            throw new IllegalStateException("O KM não pode ser null!");
        }
        return VeiculoKmColetado.of(vehicleEntity.getId(), vehicleKm);
    }
}
