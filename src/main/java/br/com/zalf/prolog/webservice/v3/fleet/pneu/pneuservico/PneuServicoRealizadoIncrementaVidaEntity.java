package br.com.zalf.prolog.webservice.v3.fleet.pneu.pneuservico;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * Created on 2021-03-18
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Entity
@Table(name = "pneu_servico_realizado_incrementa_vida", schema = "public")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PneuServicoRealizadoIncrementaVidaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigo", nullable = false)
    private Long codigo;
    @Column(name = "cod_servico_realizado", nullable = false)
    private Long codServicoRealizado;
    @Column(name = "cod_modelo_banda", nullable = false)
    private Long codModeloBanda;
    @Column(name = "vida_nova_pneu", nullable = false)
    private Integer vidaNovaPneu;
    @Column(name = "fonte_servico_realizado", nullable = false, length = 20)
    private String fonteServicoRealizado;
}