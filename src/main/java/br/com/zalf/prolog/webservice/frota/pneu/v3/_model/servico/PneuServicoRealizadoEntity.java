package br.com.zalf.prolog.webservice.frota.pneu.v3._model.servico;

import br.com.zalf.prolog.webservice.frota.pneu.v3._model.PneuEntity;
import br.com.zalf.prolog.webservice.frota.pneu.v3._model.servico.converters.FonteServicoPneuConverter;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * Created on 2021-03-16
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Entity
@Table(name = "pneu_servico_realizado", schema = "public")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@EqualsAndHashCode
public class PneuServicoRealizadoEntity {

    @EmbeddedId
    private Key key;

    @ManyToOne(fetch = FetchType.LAZY,
               targetEntity = PneuTipoServicoEntity.class)
    @JoinColumn(name = "cod_tipo_servico",
                referencedColumnName = "codigo",
                foreignKey = @ForeignKey(name = "fk_pneu_servico_realizado_pneu_tipo_servico",
                                         value = ConstraintMode.CONSTRAINT),
                nullable = false)
    private PneuTipoServicoEntity tipoServico;

    @Column(name = "cod_unidade", nullable = false)
    private Long codUnidade;

    @ManyToOne(fetch = FetchType.LAZY,
               targetEntity = PneuEntity.class)
    @JoinColumn(name = "cod_pneu",
                referencedColumnName = "codigo",
                foreignKey = @ForeignKey(name = "fk_pneu_servico_realizado_pneu",
                                         value = ConstraintMode.CONSTRAINT),
                nullable = false)
    private PneuEntity pneu;

    @Column(name = "custo", nullable = false)
    private BigDecimal custo;

    @Column(name = "vida", nullable = false)
    private Integer vida;


    public boolean isCadastro() {
        return Objects.equals(key.fonteServicoRealizado, FonteServico.CADASTRO);
    }

    @Embeddable
    @Builder
    @AllArgsConstructor
    @Getter
    @NoArgsConstructor
    @EqualsAndHashCode
    public static class Key implements Serializable {

        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE,
                        generator = "pneu_servico_realizado_generator")
        @SequenceGenerator(name = "pneu_servico_realizado_generator",
                           sequenceName = "pneu_servico_realizado_data_codigo_seq")
        @Column(name = "codigo", nullable = false, unique = true)
        private Long id;

        @Convert(converter = FonteServicoPneuConverter.class)
        @Column(name = "fonte_servico_realizado", nullable = false, length = 20)
        private FonteServico fonteServicoRealizado;
    }
}
