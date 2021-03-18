package br.com.zalf.prolog.webservice.frota.pneu.v3._model.servico;

import br.com.zalf.prolog.webservice.database._model.BaseEntity;
import br.com.zalf.prolog.webservice.database._model.DadosDelecao;
import br.com.zalf.prolog.webservice.frota.pneu.v3._model.PneuEntity;
import br.com.zalf.prolog.webservice.frota.pneu.v3._model.servico.converters.FonteServicoPneuConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.stream.Stream;

/**
 * Created on 2021-03-16
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "pneu_servico_realizado_data", schema = "public")
@Data
public class PneuServicoEntity extends BaseEntity {

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
    private Byte vida;

    @Convert(converter = FonteServicoPneuConverter.class)
    @Column(name = "fonte_servico_realizado", nullable = false, length = 20)
    private FonteServico fonteServico;

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

    @AllArgsConstructor
    @Getter
    public enum FonteServico {
        CADASTRO("FONTE_CADASTRO"), MOVIMENTACAO("FONTE_MOVIMENTACAO");

        private final String name;

        @NotNull
        public static FonteServico fromName(@NotNull final String name) {
            return Stream.of(FonteServico.values())
                    .filter(e -> e.name.equals(name))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Nenhuma fonte de serviço encontrada para a string: "
                                                                            + name));

        }

    }

}
