package br.com.zalf.prolog.webservice.v3.frota.pneu._model;

import br.com.zalf.prolog.webservice.v3.gente.empresa._model.EmpresaEntity;

import javax.persistence.*;

/**
 * Created on 2021-05-18
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Entity
@Table(name = "marca_banda", schema = "public")
public class MarcaBandaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigo", nullable = false)
    private Long codigo;

    @Column(name = "nome", nullable = false)
    private String nome;

    @ManyToOne
    @JoinColumn(name = "cod_empresa", referencedColumnName = "codigo")
    private EmpresaEntity empresa;
}
