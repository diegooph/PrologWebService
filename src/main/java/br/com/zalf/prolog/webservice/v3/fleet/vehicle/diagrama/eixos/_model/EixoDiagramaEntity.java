package br.com.zalf.prolog.webservice.v3.fleet.vehicle.diagrama.eixos._model;

import br.com.zalf.prolog.webservice.v3.fleet.vehicle.diagrama._model.DiagramaEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * Created on 2021-06-11
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
@Builder(toBuilder = true, setterPrefix = "with")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@IdClass(EixoDiagramaPk.class)
@Table(schema = "public", name = "veiculo_diagrama_eixos")
public class EixoDiagramaEntity {
    public static final char EIXO_DIANTEIRO = 'D';
    public static final char EIXO_TRASEIRO = 'T';
    @Id
    @Column(name = "cod_diagrama", nullable = false)
    private short codDiagrama;
    @MapsId
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_diagrama", referencedColumnName = "codigo")
    private DiagramaEntity diagramaEntity;
    @Column(name = "tipo_eixo", nullable = false)
    private char tipoEixo;
    @Id
    @Column(name = "posicao", nullable = false)
    private short posicao;
    @Column(name = "qt_pneus", nullable = false)
    private short qtPneus;
    @Column(name = "eixo_direcional", nullable = false)
    private boolean eixoDirecional;
}
