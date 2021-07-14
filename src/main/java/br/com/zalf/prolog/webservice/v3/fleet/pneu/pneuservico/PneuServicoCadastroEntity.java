package br.com.zalf.prolog.webservice.v3.fleet.pneu.pneuservico;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * Created on 2021-03-18
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Entity
@Table(name = "pneu_servico_cadastro", schema = "public")
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PneuServicoCadastroEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigo", nullable = false)
    private Long codigo;
    @Column(name = "cod_pneu", nullable = false)
    private Long codPneu;
    @Column(name = "cod_servico_realizado", nullable = false)
    private Long codServicoRealizado;
    @Column(name = "fonte_servico_realizado", nullable = false, length = 20)
    private String fonteServicoRealizado;
}