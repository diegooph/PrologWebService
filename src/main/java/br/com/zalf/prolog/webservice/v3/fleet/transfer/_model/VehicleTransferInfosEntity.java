package br.com.zalf.prolog.webservice.v3.fleet.transfer._model;

import br.com.zalf.prolog.webservice.v3.fleet.processeskm._model.KmCollectedEntity;
import br.com.zalf.prolog.webservice.v3.fleet.processeskm._model.KmCollectedVehicle;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

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
@Table(schema = "public", name = "veiculo_transferencia_informacoes")
public final class VehicleTransferInfosEntity implements KmCollectedEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigo", nullable = false)
    @NotNull
    private Long id;
    @Column(name = "cod_veiculo", nullable = false)
    @NotNull
    private Long vehicleId;
    @Column(name = "km_veiculo_momento_transferencia", nullable = false)
    private long vehicleKm;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_processo_transferencia", nullable = false)
    @NotNull
    private VehicleTransferProcessEntity transferProcessEntity;

    @NotNull
    @Override
    public KmCollectedVehicle getKmCollectedVehicle() {
        return KmCollectedVehicle.of(vehicleId, vehicleKm);
    }
}
