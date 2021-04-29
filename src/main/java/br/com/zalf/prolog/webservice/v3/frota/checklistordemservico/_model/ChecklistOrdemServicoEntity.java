package br.com.zalf.prolog.webservice.v3.frota.checklistordemservico._model;

import br.com.zalf.prolog.webservice.v3.frota.checklist._model.ChecklistEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
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
@IdClass(ChecklistOrdemServicoPk.class)
@Table(schema = "public", name = "checklist_ordem_servico")
public final class ChecklistOrdemServicoEntity {
    @Id
    @Column(name = "codigo", nullable = false)
    private Long codOrdemServico;
    @Id
    @Column(name = "cod_unidade", nullable = false)
    private Long codUnidade;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_checklist", nullable = false)
    private ChecklistEntity checklist;
    @OneToMany(mappedBy = "ordemServico", fetch = FetchType.LAZY)
    private Set<ChecklistOrdemServicoItemEntity> itensOrdemServico;
}
