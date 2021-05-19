package br.com.zalf.prolog.webservice.v3.frota.pneu._model;

import br.com.zalf.prolog.webservice.v3.gente.empresa._model.EmpresaEntity;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Created on 2021-05-18
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Entity
@Table(name = "pneu_posicao_nomenclatura_empresa", schema = "public")
public class PneuPosicaoNomenclaturaEmpresaEntity {

    @EmbeddedId
    private PK primaryKey;

    @Column(name = "nomenclatura", nullable = false)
    private String nomenclatura;

    @Column(name = "cod_colaborador_cadastro")
    private Long codColaboradorCadastro;

    @Column(name = "data_hora_cadastro", nullable = false)
    private LocalDateTime dataHoraCadastro;

    @Column(name = "cod_auxiliar")
    private String codAuxiliar;

    @Embeddable
    static class PK implements Serializable {

        @ManyToOne
        @JoinColumn(name = "cod_empresa", referencedColumnName = "codigo")
        private EmpresaEntity empresa;

        @Column(name = "cod_diagrama", nullable = false)
        private Long codDiagrama;

        @Column(name = "posicao_prolog", nullable = false)
        private Integer posicaoProlog;
    }
}
