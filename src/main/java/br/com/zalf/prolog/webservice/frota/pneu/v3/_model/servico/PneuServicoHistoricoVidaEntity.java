package br.com.zalf.prolog.webservice.frota.pneu.v3._model.servico;

import br.com.zalf.prolog.webservice.database._model.DadosDelecao;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created on 2021-03-18
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Entity
@Table(name = "pneu_servico_realizado_incrementa_vida", schema = "public")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PneuServicoHistoricoVidaEntity {

    @EmbeddedId
    private Id id;

    @Column(name = "vida_nova_pneu", nullable = false)
    private Integer vidaNova;

    @Column(name = "cod_modelo_banda", nullable = false)
    private Long codModeloBanda;

    @Embedded
    @AttributeOverrides(value = {
            @AttributeOverride(name = "deletado", column = @Column(name = "deletado",
                                                                   columnDefinition = "boolean " +
                                                                           "default false",
                                                                   nullable = false)),
            @AttributeOverride(name = "data", column = @Column(name = "data_hora_deletado")),
            @AttributeOverride(name = "username", column = @Column(name = "pg_username_delecao")),
            @AttributeOverride(name = "motivo", column = @Column(name = "motivo_delecao"))
    })
    private DadosDelecao dadosDelecao;

    @Embeddable
    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @EqualsAndHashCode
    public static class Id implements Serializable {

        @OneToOne(fetch = FetchType.LAZY,
                  targetEntity = PneuServicoEntity.class)
        @JoinColumns(value = {
                @JoinColumn(name = "cod_servico_realizado", referencedColumnName = "codigo"),
                @JoinColumn(name = "fonte_servico_realizado", referencedColumnName = "fonte_servico_realizado")
        }, foreignKey = @ForeignKey(name = "fk_servico_realizado_incrementa_vida_pneu",
                                    value = ConstraintMode.CONSTRAINT))
        private PneuServicoEntity servico;
    }
}
