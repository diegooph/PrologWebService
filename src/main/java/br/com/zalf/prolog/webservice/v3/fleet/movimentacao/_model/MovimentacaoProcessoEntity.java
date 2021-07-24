package br.com.zalf.prolog.webservice.v3.fleet.movimentacao._model;

import br.com.zalf.prolog.webservice.commons.util.datetime.TimezoneUtils;
import br.com.zalf.prolog.webservice.v3.LocalDateTimeUtcAttributeConverter;
import br.com.zalf.prolog.webservice.v3.fleet.processeskm._model.KmCollectedEntity;
import br.com.zalf.prolog.webservice.v3.fleet.processeskm._model.KmCollectedVehicle;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

/**
 * Created on 2021-03-25
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Builder(toBuilder = true, setterPrefix = "with")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@Table(schema = "public", name = "movimentacao_processo")
public final class MovimentacaoProcessoEntity implements KmCollectedEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigo", nullable = false)
    private Long codigo;
    @Column(name = "cod_unidade", nullable = false)
    private Long codUnidade;
    @Convert(converter = LocalDateTimeUtcAttributeConverter.class)
    @Column(name = "data_hora")
    private LocalDateTime dataHoraRealizacao;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cpf_responsavel", referencedColumnName = "cpf")
    private ColaboradorEntity colaboradorRealizacaoProcesso;
    @Column(name = "observacao")
    private String observacao;
    @OneToMany(mappedBy = "movimentacaoProcesso", fetch = FetchType.LAZY)
    private Set<MovimentacaoEntity> movimentacoes;

    @NotNull
    @Override
    public KmCollectedVehicle getKmCollectedVehicle() {
        return getVeiculo()
                .orElseThrow(() -> {
                    throw new IllegalStateException(String.format(
                            "O processo de movimentação %d não possui veículo associado.",
                            codigo));
                })
                .toVeiculoKmColetado();
    }

    @NotNull
    public Optional<VeiculoMovimentacao> getVeiculo() {
        for (final MovimentacaoEntity movimentacao : movimentacoes) {
            // Movimentações no Prolog só podem envolver um veículo. Dessa forma, ao encontrar um veículo podemos
            // retornar imediatamente.
            final Optional<VeiculoMovimentacao> veiculo = movimentacao.getVeiculo();
            if (veiculo.isPresent()) {
                return veiculo;
            }
        }

        return Optional.empty();
    }

    @NotNull
    public LocalDateTime getDataHoraRealizacaoTzAplicado() {
        return TimezoneUtils.applyTimezone(this.dataHoraRealizacao,
                                           this.colaboradorRealizacaoProcesso.getColaboradorZoneId());
    }
}
