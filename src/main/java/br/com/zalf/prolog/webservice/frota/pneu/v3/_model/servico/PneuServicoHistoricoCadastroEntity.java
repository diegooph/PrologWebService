package br.com.zalf.prolog.webservice.frota.pneu.v3._model.servico;

import br.com.zalf.prolog.webservice.database._model.DadosDelecao;
import br.com.zalf.prolog.webservice.frota.pneu.v3._model.PneuEntity;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created on 2021-03-18
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Entity
@Table(name = "pneu_servico_cadastro_data", schema = "public")
@Data
public class PneuServicoHistoricoCadastroEntity {

    @EmbeddedId
    private Id id;

    @Embedded
    @AttributeOverrides({
          @AttributeOverride(name = "deletado", column = @Column(name = "deletado",
                                                                 columnDefinition = "boolean default false",
                                                                 nullable = false)),
          @AttributeOverride(name = "data", column = @Column(name = "data_hora_deletado")),
          @AttributeOverride(name = "username", column = @Column(name = "pg_username_delecao")),
          @AttributeOverride(name = "motivo", column = @Column(name = "motivo_delecao"))
    })
    private DadosDelecao dadosDelecao;


    @Embeddable
    @Builder
    @NoArgsConstructor
    @Getter
    @EqualsAndHashCode
    public static class Id implements Serializable {

        @OneToOne(fetch = FetchType.LAZY,
                  targetEntity = PneuEntity.class)
        @JoinColumn(name = "cod_pneu", referencedColumnName = "codigo")
        private PneuEntity pneu;

        @OneToOne(fetch = FetchType.LAZY,
                  targetEntity = PneuServicoEntity.class)
        @JoinColumns(value = {
                @JoinColumn(name = "cod_servico_realizado", referencedColumnName = "codigo"),
                @JoinColumn(name = "fonte_servico_realizado", referencedColumnName = "fonte_servico_realizado")
        }, foreignKey = @ForeignKey(name = "fk_pneu_servico_cadastro_pneu_servico_realizado",
                                    value = ConstraintMode.CONSTRAINT))
        private PneuServicoEntity servico;

    }
}
