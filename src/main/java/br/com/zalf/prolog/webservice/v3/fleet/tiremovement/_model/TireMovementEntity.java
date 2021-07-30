package br.com.zalf.prolog.webservice.v3.fleet.tiremovement._model;

import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.OrigemDestinoEnum;
import br.com.zalf.prolog.webservice.v3.fleet.tire._model.TireEntity;
import br.com.zalf.prolog.webservice.v3.fleet.tireservice._model.TireServiceEntity;
import br.com.zalf.prolog.webservice.v3.fleet.vehicle._model.VehicleEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.persistence.*;
import java.util.Optional;
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
@Table(schema = "public", name = "movimentacao")
public final class TireMovementEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigo", nullable = false)
    @NotNull
    private Long id;
    @Column(name = "cod_unidade", nullable = false)
    @NotNull
    private Long branchId;
    @OneToOne(mappedBy = "tireMovementEntity", fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn
    @NotNull
    private TireMovementSourceEntity tireMovementSourceEntity;
    @OneToOne(mappedBy = "tireMovementEntity", fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn
    @NotNull
    private TireMovementDestinationEntity tireMovementDestinationEntity;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_movimentacao_processo", nullable = false)
    @NotNull
    private TireMovementProcessEntity tireMovementProcessEntity;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_pneu", referencedColumnName = "codigo", nullable = false)
    @NotNull
    private TireEntity tireEntity;
    @Column(name = "sulco_interno")
    @Nullable
    private Double internalGroove;
    @Column(name = "sulco_central_interno")
    @Nullable
    private Double middleInternalGroove;
    @Column(name = "sulco_central_externo")
    @Nullable
    private Double middleExternalGroove;
    @Column(name = "sulco_externo")
    @Nullable
    private Double externalGroove;
    @Column(name = "pressao_atual")
    @Nullable
    private Double currentPressure;
    @Column(name = "vida")
    private int tireLifeCycle;
    @Column(name = "observacao")
    @Nullable
    private String notes;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "movimentacao_pneu_servico_realizado",
               joinColumns = @JoinColumn(name = "cod_movimentacao"),
               inverseJoinColumns = @JoinColumn(name = "cod_servico_realizado"))
    @Nullable
    private Set<TireServiceEntity> tireServiceEntities;

    public boolean isTireMovementOnVehicle() {
        return tireMovementSourceEntity.getVehicleEntity() != null
                || tireMovementDestinationEntity.getVehicleEntity() != null;
    }

    @NotNull
    public Optional<VehicleMovement> getVehicleMovement() {
        if (tireMovementSourceEntity.getVehicleEntity() != null) {
            //noinspection ConstantConditions
            return createVehicleMovement(tireMovementSourceEntity.getVehicleEntity(),
                                         tireMovementSourceEntity.getVehicleKm(),
                                         tireMovementSourceEntity.getVehicleLayoutId());
        } else if (tireMovementDestinationEntity.getVehicleEntity() != null) {
            //noinspection ConstantConditions
            return createVehicleMovement(tireMovementDestinationEntity.getVehicleEntity(),
                                         tireMovementDestinationEntity.getVehicleKm(),
                                         tireMovementDestinationEntity.getVehicleLayoutId());
        }

        return Optional.empty();
    }

    @NotNull
    public OrigemDestinoEnum getSourceMovementType() {
        return tireMovementSourceEntity.getMovementSourceType();
    }

    @NotNull
    public OrigemDestinoEnum getDestinationMovementType() {
        return tireMovementDestinationEntity.getMovementDestinationType();
    }

    public boolean isFromTo(@NotNull final OrigemDestinoEnum origem, @NotNull final OrigemDestinoEnum destino) {
        return getSourceMovementType().equals(origem) && getDestinationMovementType().equals(destino);
    }

    public boolean temServicoIncrementaVida() {
        if (tireServiceEntities == null) {
            return false;
        }
        return tireServiceEntities.stream()
                .filter(TireServiceEntity::isIncreaseLifeCycle)
                .findFirst()
                .map(TireServiceEntity::isIncreaseLifeCycle)
                .orElse(false);
    }

    @NotNull
    private Optional<VehicleMovement> createVehicleMovement(@NotNull final VehicleEntity vehicleEntity,
                                                            @NotNull final Long kmColetadoVeiculo,
                                                            @NotNull final Long vehicleLayoutId) {
        return Optional.of(new VehicleMovement(vehicleEntity.getId(),
                                               vehicleEntity.getPlate(),
                                               vehicleEntity.getFleetId(),
                                               kmColetadoVeiculo,
                                               vehicleLayoutId));
    }
}
