package br.com.zalf.prolog.webservice.v3.fleet.vehicle.vehiclelayout._model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
@IdClass(AxleLayoutPk.class)
@Table(schema = "public", name = "veiculo_diagrama_eixos")
public class AxleLayoutEntity {
    public static final char FRONT_AXLE = 'D';
    public static final char REAR_AXLE = 'T';
    @Id
    @Column(name = "cod_diagrama", nullable = false)
    private short id;
    @MapsId
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_diagrama", referencedColumnName = "codigo")
    private VehicleLayoutEntity vehicleLayoutEntity;
    @Column(name = "tipo_eixo", nullable = false)
    private char axleType;
    @Id
    @Column(name = "posicao", nullable = false)
    private short position;
    @Column(name = "qt_pneus", nullable = false)
    private short tireQuantity;
    @Column(name = "eixo_direcional", nullable = false)
    private boolean isDirectionalAxle;
}
