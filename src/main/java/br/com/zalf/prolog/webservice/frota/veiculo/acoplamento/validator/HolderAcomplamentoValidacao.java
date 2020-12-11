package br.com.zalf.prolog.webservice.frota.veiculo.acoplamento.validator;

import lombok.AllArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@AllArgsConstructor
public final class HolderAcomplamentoValidacao {
    @NotNull
    private final Map<Long, VeiculoEstadoAcoplamento> veiculosEstadoAcoplamento;

    public int getTotalVeiculos() {
        return veiculosEstadoAcoplamento.size();
    }

    @NotNull
    public Collection<Long> getCodVeiculosProcesso() {
        return veiculosEstadoAcoplamento.keySet();
    }

    public boolean isVeiculoAcopladoProcesso(@NotNull final Long codVeiculo,
                                             @NotNull final Long codProcessoAcoplamento) {
        return isVeiculoAcoplado(codVeiculo) &&
                codProcessoAcoplamento.equals(veiculosEstadoAcoplamento.get(codVeiculo)
                                                      .getCodProcessoAcoplamentoVinculado());
    }

    public boolean isVeiculoAcopladoProcessoEPosicao(@NotNull final Long codVeiculo,
                                                     @NotNull final Long codProcessoAcoplamento,
                                                     @NotNull final Short posicaoAcoplado) {
        return isVeiculoAcopladoProcesso(codVeiculo, codProcessoAcoplamento) &&
                posicaoAcoplado.equals(veiculosEstadoAcoplamento.get(codVeiculo).getPosicaoAcoplado());
    }

    public boolean isVeiculoAcopladoProcessoComPosicaoDiferente(@NotNull final Long codVeiculo,
                                                                @NotNull final Long codProcessoAcoplamento,
                                                                @NotNull final Short posicaoAcoplado) {
        return isVeiculoAcopladoProcesso(codVeiculo, codProcessoAcoplamento) &&
                !posicaoAcoplado.equals(veiculosEstadoAcoplamento.get(codVeiculo).getPosicaoAcoplado());
    }

    public boolean isVeiculoAcoplado(@NotNull final Long codVeiculo) {
        if (!veiculosEstadoAcoplamento.containsKey(codVeiculo)) {
            throw new IllegalArgumentException("Veículo de código " + codVeiculo + " não encontrado!");
        }

        return veiculosEstadoAcoplamento.get(codVeiculo).isAcoplado();
    }

    public boolean existemVeiculosComProcessosDiferentes() {
        return getCodProcessosAcoplamentosDistintos().size() > 1;
    }

    public boolean mesmoProcessoAcoplamento(@NotNull final Long codPrecessoAcoplamento) {
        return getCodProcessosAcoplamentosDistintos().stream()
                .allMatch(codigo -> codigo.equals(codPrecessoAcoplamento));
    }

    public boolean nenhumProcesso() {
        return veiculosEstadoAcoplamento.values().stream().noneMatch(Objects::nonNull);
    }

    @NotNull
    private List<Long> getCodProcessosAcoplamentosDistintos() {
        return veiculosEstadoAcoplamento
                .values()
                .stream()
                .map(VeiculoEstadoAcoplamento::getCodVeiculo)
                .distinct()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
