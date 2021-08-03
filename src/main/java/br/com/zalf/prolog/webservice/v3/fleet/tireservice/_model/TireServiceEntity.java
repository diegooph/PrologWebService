package br.com.zalf.prolog.webservice.v3.fleet.tireservice._model;

import br.com.zalf.prolog.webservice.v3.fleet.tire._model.TireEntity;
import br.com.zalf.prolog.webservice.v3.fleet.tireservice.servicetype.TireServiceTypeEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Created on 2021-03-16
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Builder(toBuilder = true, setterPrefix = "with")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Table(name = "pneu_servico_realizado", schema = "public")
public class TireServiceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigo", nullable = false)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_tipo_servico", nullable = false)
    private TireServiceTypeEntity tireServiceTypeEntity;
    @Column(name = "cod_unidade", nullable = false)
    private Long branchId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_pneu", referencedColumnName = "codigo")
    private TireEntity tireEntity;
    @Column(name = "custo", nullable = false)
    private BigDecimal serviceCost;
    @Column(name = "vida", nullable = false)
    private Integer tireLifeCycle;
    @Column(name = "fonte_servico_realizado", nullable = false, length = 20)
    private String tireServiceOrigin;

    public boolean isIncreaseLifeCycle() {
        return tireServiceTypeEntity.isIncreaseLifeCycle();
    }
}
