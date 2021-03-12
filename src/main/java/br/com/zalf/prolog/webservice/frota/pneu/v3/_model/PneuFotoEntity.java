package br.com.zalf.prolog.webservice.frota.pneu.v3._model;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Created on 2021-03-11
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Entity
@Table(name = "pneu_foto_cadastro", schema = "public")
@Data
public class PneuFotoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigo", nullable = false)
    private Long codigo;

    @ManyToOne(cascade = CascadeType.ALL,
               fetch = FetchType.LAZY,
               targetEntity = PneuEntity.class)
    @JoinColumn(name = "cod_pneu",
                referencedColumnName = "codigo",
                foreignKey = @ForeignKey(name = "fk_pneu_foto_cadastro_pneu", value = ConstraintMode.CONSTRAINT),
                nullable = false)
    private PneuEntity pneu;

    @Column(name = "url_foto", nullable = false, unique = true)
    private String url;

    @Column(name = "foto_sincronizada", columnDefinition = "boolean default false", nullable = false)
    private boolean sincronizada;

    @Column(name = "data_hora_sincronizacao_foto")
    private LocalDateTime dataSincronizacao;
}
