package br.com.zalf.prolog.webservice.v3.frota.checklistordemservico._model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
public final class ChecklistOrdemServicoItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigo", nullable = false)
    private Long codigo;
    @Column(name = "km")
    private long kmColetadoVeiculoFechamentoItem;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
                         @JoinColumn(name = "cod_unidade", referencedColumnName = "cod_unidade"),
                         @JoinColumn(name = "cod_os", referencedColumnName = "codigo")
                 })
    private ChecklistOrdemServicoEntity ordemServico;
}
