package br.com.zalf.prolog.webservice.v3.fleet.inspection._model;

import br.com.zalf.prolog.webservice.v3.fleet.tire._model.TireEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * Created on 2021-05-25
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@IdClass(InspectionMeasurePk.class)
@Entity
@Table(name = "afericao_valores", schema = "public")
public final class InspectionMeasureEntity {
    @Id
    @Column(name = "cod_afericao")
    private Long inspectionId;
    @Id
    @Column(name = "cod_pneu")
    private Long tireId;
    @MapsId
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_afericao", referencedColumnName = "codigo")
    private InspectionEntity inspectionEntity;
    @MapsId
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_pneu", referencedColumnName = "codigo")
    private TireEntity tireEntity;
    @Column(name = "psi")
    private Double measuredPressure;
    @Column(name = "posicao")
    private Integer tirePositionApplied;
    @Column(name = "vida_momento_afericao")
    private Integer timesRetreaded;
}
