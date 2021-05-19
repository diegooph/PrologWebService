package br.com.zalf.prolog.webservice.v3.frota.veiculo.pneu._model;

import br.com.zalf.prolog.webservice.frota.pneu._model.StatusPneu;
import br.com.zalf.prolog.webservice.v3.frota.pneu._model.PneuEntity;
import br.com.zalf.prolog.webservice.v3.frota.veiculo._model.VeiculoEntity;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Created on 2021-05-18
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Entity
@Table(name = "veiculo_pneu", schema = "public")
public class VeiculoPneuEntity {

    @EmbeddedId
    private PK primaryKey;

    @Column(name = "cod_unidade", nullable = false)
    private Long codUnidade;

    @Column(name = "posicao", nullable = false)
    private Integer posicao;

    @Column(name = "data_hora_cadastro", nullable = false, columnDefinition = "default now()")
    private LocalDateTime dataHoraCadastro;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_pneu", nullable = false, columnDefinition = "default 'EM_USO'")
    private StatusPneu statusPneu;

    @Column(name = "cod_diagrama", nullable = false)
    private Long codDiagrama;

    @Embeddable
    static class PK implements Serializable {

        @OneToOne
        @JoinColumn(name = "cod_pneu", referencedColumnName = "codigo")
        private PneuEntity pneu;

        @ManyToOne
        @JoinColumn(name = "cod_veiculo", referencedColumnName = "codigo")
        private VeiculoEntity veiculo;
    }
}
