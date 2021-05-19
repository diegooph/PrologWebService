package br.com.zalf.prolog.webservice.v3.frota.pneu._model;

import javax.persistence.*;

/**
 * Created on 2021-05-18
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Entity
@Table(name = "dimensao_pneu", schema = "public")
public class DimensaoPneuEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigo", nullable = false)
    private Long codigo;

    @Column(name = "altura", nullable = false)
    private Integer altura;

    @Column(name = "largura", nullable = false)
    private Integer largura;

    @Column(name = "aro", nullable = false)
    private Double aro;
}
