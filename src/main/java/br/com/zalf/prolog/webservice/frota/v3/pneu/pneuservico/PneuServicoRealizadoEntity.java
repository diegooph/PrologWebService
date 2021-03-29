package br.com.zalf.prolog.webservice.frota.v3.pneu.pneuservico;

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
    @Column(name = "codigo", nullable = false, unique = true, updatable = false)
    private Long codigo;
    @Column(name = "fonte_servico_realizado", nullable = false, length = 20)
    private String fonteServicoRealizado;
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
    @OneToOne(fetch = FetchType.LAZY)
    private PneuServicoRealizadoIncrementaVidaEntity pneuServicoRealizadoIncrementaVida;
    @OneToOne(fetch = FetchType.LAZY)
    private PneuServicoCadastroEntity pneuServicoCadastro;
}
