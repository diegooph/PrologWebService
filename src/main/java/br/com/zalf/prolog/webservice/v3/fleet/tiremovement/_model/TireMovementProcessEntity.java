package br.com.zalf.prolog.webservice.v3.fleet.tiremovement._model;

import br.com.zalf.prolog.webservice.commons.util.datetime.TimezoneUtils;
import br.com.zalf.prolog.webservice.v3.LocalDateTimeUtcAttributeConverter;
import br.com.zalf.prolog.webservice.v3.fleet.processeskm._model.KmCollectedEntity;
import br.com.zalf.prolog.webservice.v3.fleet.processeskm._model.KmCollectedVehicle;
import br.com.zalf.prolog.webservice.v3.user.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;
import java.time.LocalDateTime;
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
@Table(schema = "public", name = "movimentacao_processo")
public final class TireMovementProcessEntity implements KmCollectedEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigo", nullable = false)
    private Long id;
    @Column(name = "cod_unidade", nullable = false)
    private Long branchId;
    @Convert(converter = LocalDateTimeUtcAttributeConverter.class)
    @Column(name = "data_hora")
    private LocalDateTime movementProcessAt;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cpf_responsavel", referencedColumnName = "cpf")
    private UserEntity movementProcessBy;
    @Column(name = "observacao")
    private String notes;
    @OneToMany(mappedBy = "tireMovementProcessEntity", fetch = FetchType.LAZY)
    private Set<TireMovementEntity> tireMovementEntities;

    @NotNull
    @Override
    public KmCollectedVehicle getKmCollectedVehicle() {
        return getVehicleMovement()
                .orElseThrow(() -> {
                    throw new IllegalStateException(
                            String.format("O processo de movimentação %d não possui veículo associado.", id));
                })
                .toKmCollectedVehicle();
    }

    @NotNull
    public Optional<VehicleMovement> getVehicleMovement() {
        for (final TireMovementEntity tireMovement : tireMovementEntities) {
            // Movimentações no Prolog só podem envolver um veículo. Dessa forma, ao encontrar um veículo podemos
            // retornar imediatamente.
            final Optional<VehicleMovement> vehicleMovement = tireMovement.getVehicleMovement();
            if (vehicleMovement.isPresent()) {
                return vehicleMovement;
            }
        }

        return Optional.empty();
    }

    @NotNull
    public LocalDateTime getMovementProcessAtWithTimeZone() {
        return TimezoneUtils.applyTimezone(movementProcessAt, movementProcessBy.getColaboradorZoneId());
    }
}
