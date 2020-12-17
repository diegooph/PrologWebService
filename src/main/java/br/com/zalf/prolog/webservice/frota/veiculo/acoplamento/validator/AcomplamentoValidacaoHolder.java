package br.com.zalf.prolog.webservice.frota.veiculo.acoplamento.validator;

import lombok.AllArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@AllArgsConstructor
public final class AcomplamentoValidacaoHolder {
    @NotNull
    private final Map<Long, VeiculoEstadoAcoplamento> veiculosEstadoAcoplamento;

    public int getTotalVeiculos() {
        return veiculosEstadoAcoplamento.size();
    }

    @NotNull
    public Collection<Long> getCodVeiculos() {
        return veiculosEstadoAcoplamento.keySet();
    }

    public boolean isVeiculoAcopladoProcesso(@NotNull final Long codVeiculo,
                                             @NotNull final Long codProcessoAcoplamento) {
        return isVeiculoAcoplado(codVeiculo) &&
                codProcessoAcoplamento.equals(getVeiculo(codVeiculo).getCodProcessoAcoplamentoVinculado());
    }

    public boolean isVeiculoAcopladoProcessoEPosicao(@NotNull final Long codVeiculo,
                                                     @NotNull final Long codProcessoAcoplamento,
                                                     @NotNull final Short posicaoAcoplado) {
        return isVeiculoAcopladoProcesso(codVeiculo, codProcessoAcoplamento) &&
                posicaoAcoplado.equals(getVeiculo(codVeiculo).getPosicaoAcoplado());
    }

    public boolean isVeiculoAcopladoProcessoComPosicaoDiferente(@NotNull final Long codVeiculo,
                                                                @NotNull final Long codProcessoAcoplamento,
                                                                @NotNull final Short posicaoAcoplado) {
        return isVeiculoAcopladoProcesso(codVeiculo, codProcessoAcoplamento) &&
                !posicaoAcoplado.equals(getVeiculo(codVeiculo).getPosicaoAcoplado());
    }

    public boolean isVeiculoAcoplado(@NotNull final Long codVeiculo) {
        return getVeiculo(codVeiculo).isAcoplado();
    }

    public boolean existemVeiculosComProcessosDiferentes() {
        return getCodProcessosAcoplamentosDistintos().size() > 1;
    }

    public boolean isTodosProcessosAcoplamentosDoCodigo(@NotNull final Long codPrecessoAcoplamento) {
        return getCodProcessosAcoplamentosDistintos()
                .stream()
                .allMatch(codigo -> codigo.equals(codPrecessoAcoplamento));
    }

    public boolean faltaAlgumVeiculo(@NotNull final List<Long> codVeiculos) {
        return !getCodVeiculos().containsAll(codVeiculos);
    }

    public boolean isAlgumVeiculoAcoplado() {
        return veiculosEstadoAcoplamento
                .values()
                .stream()
                .map(VeiculoEstadoAcoplamento::getCodProcessoAcoplamentoVinculado)
                .anyMatch(Objects::nonNull);
    }

    @NotNull
    public List<Long> getCodProcessosAcoplamentosDistintos() {
        return veiculosEstadoAcoplamento
                .values()
                .stream()
                .map(VeiculoEstadoAcoplamento::getCodProcessoAcoplamentoVinculado)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
    }

    @NotNull
    private VeiculoEstadoAcoplamento getVeiculo(@NotNull final Long codVeiculo) {
        if (!veiculosEstadoAcoplamento.containsKey(codVeiculo)) {
            throw new VeiculoAcoplamentoValidatorException("Veículo de código " + codVeiculo + " não encontrado!");
        }
        return veiculosEstadoAcoplamento.get(codVeiculo);
    }
}
