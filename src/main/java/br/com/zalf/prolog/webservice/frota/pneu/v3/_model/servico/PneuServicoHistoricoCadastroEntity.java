package br.com.zalf.prolog.webservice.frota.pneu.v3._model.servico;

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
@Table(name = "pneu_servico_cadastro", schema = "public")
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PneuServicoHistoricoCadastroEntity {

    @EmbeddedId
    private Id id;

    @Embeddable
    @Builder
    @AllArgsConstructor
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
