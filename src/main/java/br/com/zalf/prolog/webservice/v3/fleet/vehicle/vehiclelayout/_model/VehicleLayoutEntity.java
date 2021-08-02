package br.com.zalf.prolog.webservice.v3.fleet.vehicle.vehiclelayout._model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    private Short id;
    @Column(name = "nome", nullable = false)
    private String name;
    @Column(name = "url_imagem")
    private String imageUrl;
    @Column(name = "motorizado", nullable = false)
    private boolean hasEngine;
    @OneToMany(mappedBy = "vehicleLayoutEntity", fetch = FetchType.LAZY, targetEntity = AxleLayoutEntity.class)
    private Set<AxleLayoutEntity> axleLayoutEntities;

    public long getAxleQuantity(final char axleType) {
        return axleLayoutEntities.stream()
                .filter(axleEntity -> axleEntity.getAxleType() == axleType)
                .count();
    }
}
