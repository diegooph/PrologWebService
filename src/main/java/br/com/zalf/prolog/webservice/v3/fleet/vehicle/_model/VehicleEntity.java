package br.com.zalf.prolog.webservice.v3.fleet.vehicle._model;

import br.com.zalf.prolog.webservice.frota.veiculo.historico._model.OrigemAcaoEnum;
import br.com.zalf.prolog.webservice.v3.fleet.attach._model.AttachProcessEntity;
import br.com.zalf.prolog.webservice.v3.fleet.attach._model.CurrentAttachEntity;
import br.com.zalf.prolog.webservice.v3.fleet.vehicle.makemodel._model.VehicleModelEntity;
import br.com.zalf.prolog.webservice.v3.fleet.vehicle.vehiclelayout._model.VehicleLayoutEntity;
import br.com.zalf.prolog.webservice.v3.fleet.vehicle.vehicletype._model.VehicleTypeEntity;
import br.com.zalf.prolog.webservice.v3.general.branch._model.BranchEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Formula;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.Optional;

@Builder(toBuilder = true, setterPrefix = "with")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@Table(schema = "public", name = "veiculo")
public class VehicleEntity {
    @Column(name = "cod_eixos", nullable = false, columnDefinition = "bigint default 1")
    @NotNull
    private final Long axleId = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigo", nullable = false)
    @NotNull
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_unidade_cadastro", referencedColumnName = "codigo")
    @NotNull
    private BranchEntity branchRegisterEntity;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_unidade", referencedColumnName = "codigo")
    @NotNull
    private BranchEntity branchEntity;
    @Column(name = "cod_empresa", nullable = false)
    @NotNull
    private Long companyId;
    @Column(name = "placa", length = 7, nullable = false)
    @NotNull
    private String plate;
    @Column(name = "identificador_frota", length = 15)
    @Nullable
    private String fleetId;
    @Column(name = "km", nullable = false)
    @NotNull
    private Long vehicleKm;
    @Column(name = "status_ativo", nullable = false)
    private boolean isActive;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_diagrama", referencedColumnName = "codigo")
    @NotNull
    private VehicleLayoutEntity vehicleLayoutEntity;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_tipo", referencedColumnName = "codigo")
    @NotNull
    private VehicleTypeEntity vehicleTypeEntity;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_modelo", referencedColumnName = "codigo")
    @NotNull
    private VehicleModelEntity vehicleModelEntity;
    @Column(name = "data_hora_cadastro", nullable = false, columnDefinition = "timestamp with time zone default now()")
    @NotNull
    private OffsetDateTime createdAt;
    @Column(name = "foi_editado", nullable = false, columnDefinition = "boolean default false")
    private boolean wasEdited;
    @Column(name = "motorizado", nullable = false)
    private boolean hasEngine;
    @Column(name = "possui_hubodometro", nullable = false, columnDefinition = "boolean default false")
    private boolean hasHubodometer;
    @Enumerated(EnumType.STRING)
    @Column(name = "origem_cadastro", nullable = false)
    @NotNull
    private OrigemAcaoEnum registerOrigin;
    @Column(name = "acoplado", nullable = false)
    private boolean isAttached;
    @Formula(value = "(select count(*) from veiculo_pneu vp where vp.cod_veiculo = codigo)")
    @NotNull
    private Integer appliedTiresQuantity;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinTable(name = "veiculo_acoplamento_atual",
               joinColumns = @JoinColumn(name = "cod_veiculo", referencedColumnName = "codigo"),
               inverseJoinColumns = @JoinColumn(name = "cod_processo", referencedColumnName = "codigo"))
    @Nullable
    private AttachProcessEntity attachProcessEntity;

    @NotNull
    public Optional<AttachProcessEntity> getAttachProcessEntity() {
        return Optional.ofNullable(attachProcessEntity);
    }

    @Nullable
    public Short getCurrentAttachPosition() {
        if (attachProcessEntity == null) {
            return null;
        }
        return attachProcessEntity.getCurrentAttachEntities()
                .stream()
                .filter(currentAttachEntity -> currentAttachEntity.getCurrentAttachVehicleId().equals(id))
                .map(CurrentAttachEntity::getPositionId)
                .findFirst()
                .orElse(null);
    }
}
