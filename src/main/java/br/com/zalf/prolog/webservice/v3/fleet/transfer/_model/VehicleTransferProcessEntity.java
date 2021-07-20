package br.com.zalf.prolog.webservice.v3.fleet.transfer._model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

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
@Table(schema = "public", name = "veiculo_transferencia_processo")
public final class VehicleTransferProcessEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigo", nullable = false)
    @NotNull
    private Long id;
    @Column(name = "cod_unidade_origem", nullable = false)
    @NotNull
    private Long fromBranchId;
    @Column(name = "cod_unidade_destino", nullable = false)
    @NotNull
    private Long toBranchId;
    @Column(name = "cod_unidade_colaborador", nullable = false)
    @NotNull
    private Long userBranchId;
    @OneToMany(mappedBy = "transferenciaVeiculoProcesso", fetch = FetchType.LAZY)
    @NotNull
    private Set<VehicleTransferInfosEntity> vehicleTransferInfosEntities;

    @NotNull
    public Optional<VehicleTransferInfosEntity> getVehicleTransferInfos(@NotNull final Long vehicleId) {
        return vehicleTransferInfosEntities
                .stream()
                .filter(info -> info.getVehicleId().equals(vehicleId))
                .findFirst();
    }
}
