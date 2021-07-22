package br.com.zalf.prolog.webservice.v3.fleet.tire.pneuservico;

import br.com.zalf.prolog.webservice.v3.fleet.tire._model.TireEntity;
import br.com.zalf.prolog.webservice.v3.fleet.tire.pneuservico.tiposervico.PneuTipoServicoEntity;
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
@Entity
@Table(name = "pneu_servico_realizado", schema = "public")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PneuServicoRealizadoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigo", nullable = false)
    private Long codigo;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_tipo_servico", nullable = false)
    private PneuTipoServicoEntity tipoServico;
    @Column(name = "cod_unidade", nullable = false)
    private Long codUnidade;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_pneu", referencedColumnName = "codigo")
    private TireEntity pneuServicoRealizado;
    @Column(name = "custo", nullable = false)
    private BigDecimal custo;
    @Column(name = "vida", nullable = false)
    private Integer vida;
    @Column(name = "fonte_servico_realizado", nullable = false, length = 20)
    private String fonteServicoRealizado;

    public boolean isIncrementaVida() {
        return tipoServico.isIncrementaVida();
    }
}
