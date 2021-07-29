package br.com.zalf.prolog.webservice.v3.fleet.checklist._model;

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
@Table(schema = "public", name = "checklist")
public final class ChecklistEntity implements KmCollectedEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigo", nullable = false)
    @NotNull
    private Long id;
    @Column(name = "cod_unidade", nullable = false)
    @NotNull
    private Long branchId;
    @Column(name = "cod_veiculo", nullable = false)
    @NotNull
    private Long vehicleId;
    @Column(name = "km_veiculo", nullable = false)
    private long vehicleKm;

    @NotNull
    @Override
    public KmCollectedVehicle getKmCollectedVehicle() {
        return KmCollectedVehicle.of(vehicleId, vehicleKm);
    }
}
