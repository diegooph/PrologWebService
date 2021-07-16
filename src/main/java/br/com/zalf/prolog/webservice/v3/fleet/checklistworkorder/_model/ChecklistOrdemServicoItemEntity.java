package br.com.zalf.prolog.webservice.v3.fleet.checklistworkorder._model;

import br.com.zalf.prolog.webservice.v3.fleet.kmprocessos._model.EntityKmColetado;
import br.com.zalf.prolog.webservice.v3.fleet.kmprocessos._model.VeiculoKmColetado;
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
@Table(schema = "public", name = "checklist_ordem_servico_itens")
public final class ChecklistOrdemServicoItemEntity implements EntityKmColetado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigo", nullable = false)
    private Long codigo;
    @Column(name = "km")
    private Long kmColetadoVeiculoFechamentoItem;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
                         @JoinColumn(name = "cod_unidade", referencedColumnName = "cod_unidade"),
                         @JoinColumn(name = "cod_os", referencedColumnName = "codigo")
                 })
    private ChecklistOrdemServicoEntity ordemServico;

    @NotNull
    @Override
    public VeiculoKmColetado getVeiculoKmColetado() {
        return VeiculoKmColetado.of(ordemServico.getChecklist().getCodVeiculo(), kmColetadoVeiculoFechamentoItem);
    }
}
