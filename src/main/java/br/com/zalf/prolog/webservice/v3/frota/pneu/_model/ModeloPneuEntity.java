package br.com.zalf.prolog.webservice.v3.frota.pneu._model;

import br.com.zalf.prolog.webservice.v3.gente.empresa._model.EmpresaEntity;

import javax.persistence.*;

/**
 * Created on 2021-05-18
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Entity
@Table(name = "modelo_pneu", schema = "public")
public class ModeloPneuEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigo", nullable = false)
    private Long codigo;

    @ManyToOne
    @JoinColumn(name = "cod_marca", referencedColumnName = "codigo")
    private MarcaPneuEntity marca;

    @ManyToOne
    @JoinColumn(name = "cod_empresa", referencedColumnName = "codigo")
    private EmpresaEntity empresa;

    @Column(name = "qt_sulcos", nullable = false, columnDefinition = "default 4")
    private Short quantidadeSulcos;

    @Column(name = "altura_sulcos", nullable = false)
    private Double alturaSulcos;
}
