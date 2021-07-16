package br.com.zalf.prolog.webservice.v3.fleet.checklistworkorder._model;

import br.com.zalf.prolog.webservice.v3.fleet.kmprocessos._model.EntityKmColetado;
import br.com.zalf.prolog.webservice.v3.fleet.kmprocessos._model.VeiculoKmColetado;
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
@Table(schema = "public", name = "checklist_ordem_servico_itens")
public final class ChecklistWorkOrderItemEntity implements EntityKmColetado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigo", nullable = false)
    @NotNull
    private Long id;
    @Column(name = "km")
    @Nullable
    private Long vehicleKmAtResolution;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({@JoinColumn(name = "cod_unidade", referencedColumnName = "cod_unidade"),
                         @JoinColumn(name = "cod_os", referencedColumnName = "codigo")})
    @NotNull
    private ChecklistWorkOrderEntity workOrderEntity;

    @NotNull
    @Override
    public VeiculoKmColetado getVeiculoKmColetado() {
        if (vehicleKmAtResolution == null) {
            throw new IllegalStateException("O KM não pode ser null!");
        }
        return VeiculoKmColetado.of(workOrderEntity.getChecklist().getCodVeiculo(), vehicleKmAtResolution);
    }
}
