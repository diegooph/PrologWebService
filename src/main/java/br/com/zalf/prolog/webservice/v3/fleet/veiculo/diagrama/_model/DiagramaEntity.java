package br.com.zalf.prolog.webservice.v3.fleet.veiculo.diagrama._model;

import br.com.zalf.prolog.webservice.v3.fleet.veiculo.diagrama.eixos._model.EixoDiagramaEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Set;

@Builder(toBuilder = true, setterPrefix = "with")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@Table(schema = "public", name = "veiculo_diagrama")
public class DiagramaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigo", nullable = false)
    private Short codigo;
    @Column(name = "nome", nullable = false)
    private String nome;
    @Column(name = "url_imagem")
    private String urlImagem;
    @Column(name = "motorizado", nullable = false)
    private boolean motorizado;
    @OneToMany(mappedBy = "diagramaEntity", fetch = FetchType.LAZY, targetEntity = EixoDiagramaEntity.class)
    private Set<EixoDiagramaEntity> eixosDiagramaEntities;

    public long getQtdEixos(final char eixo) {
        return eixosDiagramaEntities
                .stream()
                .filter(eixosEntity -> eixosEntity.getTipoEixo() == eixo)
                .count();
    }
}
