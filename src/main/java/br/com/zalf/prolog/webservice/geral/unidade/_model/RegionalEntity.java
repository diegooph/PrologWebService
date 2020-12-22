package br.com.zalf.prolog.webservice.geral.unidade._model;

import lombok.Data;

import javax.persistence.*;

/**
 * Created on 2020-11-24
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Entity
@Table(schema = "public", name = "regional")
@Data
public class RegionalEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigo", nullable = false)
    private Long codigo;

    @Column(name = "regiao")
    private String regiao;
}
