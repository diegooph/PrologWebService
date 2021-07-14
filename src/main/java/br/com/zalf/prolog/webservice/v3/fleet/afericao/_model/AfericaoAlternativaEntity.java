package br.com.zalf.prolog.webservice.v3.fleet.afericao._model;

import lombok.Data;

import javax.persistence.*;

/**
 * Created on 2021-05-25
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */

@Entity
@Table(name = "afericao_alternativa_manutencao_inspecao", schema = "public")
@Data
public class AfericaoAlternativaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigo", nullable = false)
    private Long codigo;
    @Column(name = "alternativa", nullable = false)
    private String alternativa;
    @Column(name = "status_ativo", nullable = false)
    private Boolean ativo;
}
