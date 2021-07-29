package br.com.zalf.prolog.webservice.v3.fleet.vehicle.makemodel._model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;

/**
 * Created on 2021-06-11
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
@Builder(toBuilder = true, setterPrefix = "with")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@Table(schema = "public", name = "modelo_veiculo")
public class VehicleModelEntity {
    @Id
    @Column(name = "codigo", nullable = false)
    @NotNull
    private Long id;
    @Column(name = "nome", nullable = false)
    @NotNull
    private String name;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_marca", referencedColumnName = "codigo")
    @NotNull
    private VehicleMakeEntity vehicleMakeEntity;
    @Column(name = "cod_empresa", nullable = false)
    @NotNull
    private Long companyId;
}
