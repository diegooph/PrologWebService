package br.com.zalf.prolog.webservice.v3.fleet.tiremovement._model;

import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.OrigemDestinoEnum;
import br.com.zalf.prolog.webservice.v3.fleet.vehicle._model.VehicleEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.persistence.*;

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
@Table(schema = "public", name = "movimentacao_origem")
public final class TireMovementSourceEntity {
    @Id
    @Column(name = "cod_movimentacao", nullable = false)
    @NotNull
    private Long tireMovementId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_veiculo", referencedColumnName = "codigo")
    @Nullable
    private VehicleEntity vehicleEntity;
    @Column(name = "cod_diagrama")
    @Nullable
    private Long vehicleLayoutId;
    @Column(name = "km_veiculo")
    @Nullable
    private Long vehicleKm;
    @Column(name = "posicao_pneu_origem")
    @Nullable
    private Long tirePosition;
    @Column(name = "tipo_origem")
    @NotNull
    private OrigemDestinoEnum movementSourceType;
    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_movimentacao")
    @NotNull
    private TireMovementEntity tireMovementEntity;
}
