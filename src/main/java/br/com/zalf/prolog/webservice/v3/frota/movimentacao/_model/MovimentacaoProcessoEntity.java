package br.com.zalf.prolog.webservice.v3.frota.movimentacao._model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
public final class MovimentacaoProcessoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigo", nullable = false)
    private Long codigo;
    @Column(name = "cod_unidade", nullable = false)
    private Long codUnidade;
    @OneToMany(mappedBy = "movimentacaoProcesso", fetch = FetchType.LAZY)
    private Set<MovimentacaoEntity> movimentacoes;

    @NotNull
    public List<MovimentacaoEntity> getMovimentacoesNoVeiculo(@NotNull final Long codVeiculo) {
        return movimentacoes
                .stream()
                .filter(m -> m.isMovimentacaoNoVeiculo(codVeiculo))
                .collect(Collectors.toList());
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
}
