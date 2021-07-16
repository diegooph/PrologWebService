package br.com.zalf.prolog.webservice.v3.fleet.vehicle.vehicletype._model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.persistence.*;

@Builder(toBuilder = true, setterPrefix = "with")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@Table(schema = "public", name = "veiculo_tipo")
public class VehicleTypeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigo", nullable = false)
    @NotNull
    private Long id;
    @Column(name = "cod_empresa", nullable = false)
    @NotNull
    private Long companyId;
    @Column(name = "nome", nullable = false)
    @NotNull
    private String name;
    @Column(name = "status_ativo", nullable = false)
    private boolean isActive;
    @Column(name = "cod_diagrama", nullable = false)
    @NotNull
    private Short vehicleLayoutId;
    @Column(name = "cod_auxiliar")
    @Nullable
    private String additionalId;
}
