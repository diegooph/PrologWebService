package br.com.zalf.prolog.webservice.v3.frota.pneu.pneuservico.tiposervico._modal;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Created on 2021-03-16
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */

@Entity
@Table(name = "pneu_tipo_servico", schema = "public")
@Data
@NoArgsConstructor
@Getter
public class PneuTipoServicoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigo", nullable = false)
    private Long codigo;
    @Column(name = "cod_empresa", nullable = false)
    private Long codEmpresa;
    @Column(name = "nome", nullable = false)
    private String nome;
    @Column(name = "incrementa_vida", nullable = false)
    private boolean incrementaVida;
    @Column(name = "status_ativo", columnDefinition = "boolean default true", nullable = false)
    private boolean statusAtivo;
    @Column(name = "editavel", columnDefinition = "boolean default true", nullable = false)
    private boolean editavel;
    @Column(name = "utilizado_cadastro_pneu", columnDefinition = "boolean default false", nullable = false)
    private boolean utilizadoCadastroPneu;
    @Column(name = "cod_colaborador_criacao", nullable = false)
    private Long codColaboradorCriacao;
    @Column(name = "data_hora_criacao", nullable = false)
    private LocalDateTime dataHoraCriacao;
    @Column(name = "cod_colaborador_edicao")
    private Long codColaboradorEdicao;
    @Column(name = "data_hora_edicao")
    private LocalDateTime dataEdicao;
}
