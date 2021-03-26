package br.com.zalf.prolog.webservice.frota.v3.pneuservico;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
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
    @EmbeddedId
    private PK pk;
    @Column(name = "cod_tipo_servico", nullable = false)
    private Long codTipoServico;
    @Column(name = "cod_unidade", nullable = false)
    private Long codUnidade;
    @Column(name = "cod_pneu", nullable = false)
    private Long codPneu;
    @Column(name = "custo", nullable = false)
    private BigDecimal custo;
    @Column(name = "vida", nullable = false)
    private Integer vida;
    @OneToOne
    @JoinColumns({@JoinColumn(name = "codigo", referencedColumnName = "cod_servico_realizado"),
                         @JoinColumn(name = "fonte_servico_realizado",
                                     referencedColumnName = "fonte_servico_realizado")})
    private PneuServicoRealizadoIncrementaVidaEntity pneuServicoRealizadoIncrementaVida;
    @OneToOne
    @JoinColumns({@JoinColumn(name = "codigo", referencedColumnName = "cod_servico_realizado"),
                         @JoinColumn(name = "fonte_servico_realizado",
                                     referencedColumnName = "fonte_servico_realizado")})
    private PneuServicoCadastroEntity pneuServicoCadastro;

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Embeddable
    public static class PK implements Serializable {
        @Column(name = "codigo", nullable = false, unique = true, updatable = false)
        private Long codigo;
        @Column(name = "fonte_servico_realizado", nullable = false, length = 20)
        private String fonteServicoRealizado;
    }
}
