package br.com.zalf.prolog.webservice.v3.frota.veiculo.tipo._model;

import br.com.zalf.prolog.webservice.v3.frota.veiculo.diagrama._model.DiagramaEntity;
import br.com.zalf.prolog.webservice.v3.gente.empresa._model.EmpresaEntity;

import javax.persistence.*;

/**
 * Created on 2021-05-18
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Entity
@Table(name = "veiculo_tipo", schema = "public")
public class VeiculoTipoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigo", nullable = false)
    private Long codigo;

    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "status_ativo", nullable = false)
    private boolean ativo;

    @ManyToOne
    @JoinColumn(name = "cod_diagrama", referencedColumnName = "codigo")
    private DiagramaEntity diagrama;

    @ManyToOne
    @JoinColumn(name = "cod_empresa", referencedColumnName = "codigo")
    private EmpresaEntity empresa;

    @Column(name = "cod_auxiliar")
    private String codAuxiliar;
}
