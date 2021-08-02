package br.com.zalf.prolog.webservice.v3.fleet.attach._model;

import br.com.zalf.prolog.webservice.v3.fleet.vehicle._model.VehicleEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;

/**
 * Created on 2021-06-14
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
@Builder(toBuilder = true, setterPrefix = "with")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@IdClass(CurrentAttachPk.class)
@Table(schema = "public", name = "veiculo_acoplamento_atual")
public class CurrentAttachEntity {
    @Id
    @Column(name = "cod_processo")
    private Long attachProcessId;
    @MapsId
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_processo", referencedColumnName = "codigo")
    private AttachProcessEntity attachProcessEntity;
    @Column(name = "cod_unidade", nullable = false)
    private Long branchId;
    @Id
    @Column(name = "cod_posicao", nullable = false)
    private Short positionId;
    @Column(name = "cod_diagrama", nullable = false)
    private Long vehicleLayoutId;
    @Column(name = "motorizado", nullable = false)
    private boolean hasEngine;
    @OneToOne
    @JoinColumn(name = "cod_veiculo", referencedColumnName = "codigo")
    private VehicleEntity vehicleEntity;
    @Column(name = "acoplado", nullable = false)
    private boolean isAttached;

    @NotNull
    public Long getCurrentAttachVehicleId() {
        return vehicleEntity.getId();
    }
}
