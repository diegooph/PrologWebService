package br.com.zalf.prolog.webservice.v3.fleet.vehicle.vehiclelayout._model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.persistence.*;
import java.util.Set;

@Builder(toBuilder = true, setterPrefix = "with")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@Table(schema = "public", name = "veiculo_diagrama")
public class VehicleLayoutEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigo", nullable = false)
    @NotNull
    private Short id;
    @Column(name = "nome", nullable = false)
    @NotNull
    private String name;
    @Column(name = "url_imagem")
    @Nullable
    private String imageUrl;
    @Column(name = "motorizado", nullable = false)
    private boolean hasEngine;
    @OneToMany(mappedBy = "vehicleLayoutEntity", fetch = FetchType.LAZY, targetEntity = AxleLayoutEntity.class)
    @NotNull
    private Set<AxleLayoutEntity> axleLayoutEntities;

    public long getAxleQuantity(final char axleType) {
        return axleLayoutEntities
                .stream()
                .filter(axleEntity -> axleEntity.getAxleType() == axleType)
                .count();
    }
}
