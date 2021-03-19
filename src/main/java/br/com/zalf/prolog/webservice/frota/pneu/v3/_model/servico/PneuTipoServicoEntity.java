package br.com.zalf.prolog.webservice.frota.pneu.v3._model.servico;

import br.com.zalf.prolog.webservice.database._model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * Created on 2021-03-16
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */

@Entity
@Table(name = "pneu_tipo_servico", schema = "public")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Getter
public class PneuTipoServicoEntity extends BaseEntity {

    @Column(name = "cod_empresa", nullable = false)
    private Long codEmpresa;

    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "incrementa_vida", nullable = false)
    private boolean incremental;

    @Column(name = "status_ativo", columnDefinition = "boolean default true",nullable = false)
    private boolean ativo;

    @Column(name = "editavel", columnDefinition = "boolean default true", nullable = false)
    private boolean editavel;

    @Column(name = "utilizado_cadastro_pneu", columnDefinition = "boolean default false", nullable = false)
    private boolean utilizadoCadastroPneu;

    @Column(name = "cod_colaborador_criacao", nullable = false)
    private Long codColaboradorCriacao;

    @Column(name = "data_hora_criacao", nullable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "cod_colaborador_edicao")
    private Long codColaboradorEdicao;

    @Column(name = "data_hora_edicao")
    private LocalDateTime dataEdicao;

}
