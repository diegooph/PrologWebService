package br.com.zalf.prolog.webservice.frota.veiculo.acoplamento.validator;

import br.com.zalf.prolog.webservice.frota.veiculo.acoplamento._model.realizacao.VeiculoAcoplamentoAcaoRealizada;
import br.com.zalf.prolog.webservice.frota.veiculo.acoplamento._model.realizacao.VeiculoAcoplamentoProcessoRealizacao;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
public final class VeiculoAcoplamentoValidator {
    @NotNull
    private final AcomplamentoValidacaoHolder dadosBanco;
    @NotNull
    private final VeiculoAcoplamentoProcessoRealizacao processoRealizacao;

    public void validate() {
        validateVeiculosProcessoIguaisVeiculosBanco();
        validateVeiculosRepetidosAcoplamento();
        validateOrdenacao();
        validateVeiculosPertencemUnicoProcesso();
        validateAcoesAcoplamentos();
    }

    private void validateVeiculosProcessoIguaisVeiculosBanco() {
        if (processoRealizacao.getTotalVeiculosProcesso() != dadosBanco.getTotalVeiculos()) {
            throw new VeiculoAcoplamentoValidatorException(String.format(
                    "Total de veículos no processo (%d) está diferente do total de veículos buscados do banco (%d).",
                    processoRealizacao.getTotalVeiculosProcesso(),
                    dadosBanco.getTotalVeiculos()));
        }

        if (!dadosBanco.getCodVeiculos().containsAll(processoRealizacao.getCodVeiculosProcesso())) {
            throw new VeiculoAcoplamentoValidatorException(String.format(
                    "Veículos no processo (%s) divergem dos veículos buscados do banco (%s).",
                    processoRealizacao.getCodVeiculosProcesso(),
                    dadosBanco.getCodVeiculos()));
        }
    }

    private void validateVeiculosRepetidosAcoplamento() {
        final List<Long> codVeiculos = processoRealizacao
                .getAcoesRealizadas()
                .stream()
                .map(VeiculoAcoplamentoAcaoRealizada::getCodVeiculo)
                .collect(Collectors.toList());
        codVeiculos.stream()
                .filter(codVeiculo -> Collections.frequency(codVeiculos, codVeiculo) > 1)
                .findAny()
                .ifPresent(codVeiculo -> {
                    throw new VeiculoAcoplamentoValidatorException(
                            "Não podem existir veículos duplicados no acoplamento.");
                });
    }

    private void validateOrdenacao() {
        final List<Short> posicoesAcoplamento = processoRealizacao
                .getAcoesRealizadas()
                .stream()
                .filter(VeiculoAcoplamentoAcaoRealizada::foiAcopladoOuMantidoNaComposicao)
                .map(VeiculoAcoplamentoAcaoRealizada::getPosicaoAcaoRealizada)
                .sorted()
                .collect(Collectors.toList());
        for (int i = 0; i < posicoesAcoplamento.size(); i++) {
            if (posicoesAcoplamento.get(i) != i + 1) {
                throw new VeiculoAcoplamentoValidatorException(
                        "A ordem do acoplamento não está correta, devem ser sequenciais: " + posicoesAcoplamento);
            }
        }
    }

    private void validateVeiculosPertencemUnicoProcesso() {
        if (dadosBanco.existemVeiculosComProcessosDiferentes()) {
            throw new VeiculoAcoplamentoValidatorException(String.format(
                    "Os veículos do processo pertencem a diferentes processos de acoplamento: %s",
                    dadosBanco.getCodProcessosAcoplamentosDistintos()));
        }

        if (processoRealizacao.getCodProcessoAcoplamentoEditado().isPresent()) {
            final Long codProcessoAcoplamento = processoRealizacao.getCodProcessoAcoplamentoEditado().get();
            if (!dadosBanco.isTodosProcessosAcoplamentosDoCodigo(codProcessoAcoplamento)) {
                throw new VeiculoAcoplamentoValidatorException(String.format(
                        "Veículos no BD estão nos processos de acoplamento de códigos %s porém o processo sendo " +
                                "editado é o de código %d.",
                        dadosBanco.getCodProcessosAcoplamentosDistintos(),
                        codProcessoAcoplamento));
            }
        } else {
            if (!dadosBanco.isTodosVeiculosNaoAcoplados()) {
                throw new VeiculoAcoplamentoValidatorException(
                        String.format("Um novo acoplamento está sendo inserido, porém alguns dos veículos recebidos" +
                                              " já estão acoplados: %s.", dadosBanco.getCodVeiculos()));
            }
        }
    }

    private void validateAcoesAcoplamentos() {
        processoRealizacao
                .getVeiculosAcoplados()
                .forEach(veiculo -> {
                    final Long codVeiculo = veiculo.getCodVeiculo();
                    if (dadosBanco.isVeiculoAcoplado(codVeiculo)) {
                        throw new VeiculoAcoplamentoValidatorException(
                                String.format("Não é possível acoplar o veículo %d pois ele já está acoplado.",
                                              codVeiculo));
                    }
                });

        processoRealizacao
                .getVeiculosDesacoplados()
                .forEach(veiculo -> {
                    final Long codVeiculo = veiculo.getCodVeiculo();
                    if (processoRealizacao.getCodProcessoAcoplamentoEditado().isPresent()) {
                        final Long codProcessoAcoplamento = processoRealizacao.getCodProcessoAcoplamentoEditado().get();
                        if (!dadosBanco.isVeiculoAcopladoProcesso(codVeiculo, codProcessoAcoplamento)) {
                            throw new VeiculoAcoplamentoValidatorException(
                                    String.format("Não é possível desacoplar um veículo que não está acoplado." +
                                                          "\nVeículo: %d." +
                                                          "\nProcesso: %d.",
                                                  codVeiculo,
                                                  codProcessoAcoplamento));
                        }
                    } else {
                        throw new VeiculoAcoplamentoValidatorException(
                                String.format("Não é possível desacoplar o veículo %d pois nenhum " +
                                                      "código de processo para edição foi recebido.",
                                              codVeiculo));
                    }
                });

        processoRealizacao
                .getVeiculosMantidosPosicao()
                .forEach(veiculo -> {
                    final Long codVeiculo = veiculo.getCodVeiculo();
                    final short posicaoAcaoRealizada = veiculo.getPosicaoAcaoRealizada();
                    if (processoRealizacao.getCodProcessoAcoplamentoEditado().isPresent()) {
                        final Long codProcessoAcoplamento = processoRealizacao.getCodProcessoAcoplamentoEditado().get();
                        if (!dadosBanco.isVeiculoAcopladoProcessoEPosicao(codVeiculo,
                                                                          codProcessoAcoplamento,
                                                                          posicaoAcaoRealizada)) {
                            throw new VeiculoAcoplamentoValidatorException(
                                    String.format("O veículo %d foi mantido na posição %d porém ele " +
                                                          "não está acoplado nesta posição no processo de código %d.",
                                                  codVeiculo,
                                                  posicaoAcaoRealizada,
                                                  codProcessoAcoplamento));
                        }
                    } else {
                        throw new VeiculoAcoplamentoValidatorException(
                                String.format("Não é possível manter na posição o veículo %d pois nenhum " +
                                                      "código de processo para edição foi recebido.",
                                              codVeiculo));
                    }
                });

        processoRealizacao
                .getVeiculosMudaramPosicao()
                .forEach(veiculo -> {
                    final Long codVeiculo = veiculo.getCodVeiculo();
                    if (processoRealizacao.getCodProcessoAcoplamentoEditado().isPresent()) {
                        final Long codProcessoAcoplamento = processoRealizacao.getCodProcessoAcoplamentoEditado().get();
                        final short posicaoAcaoRealizada = veiculo.getPosicaoAcaoRealizada();
                        if (!dadosBanco.isVeiculoAcopladoProcessoComPosicaoDiferente(codVeiculo,
                                                                                     codProcessoAcoplamento,
                                                                                     posicaoAcaoRealizada)) {
                            throw new VeiculoAcoplamentoValidatorException(
                                    String.format("Não é possível realizar uma mudança de posição pois o veículo não " +
                                                          "está acoplado em outra posição deste processo." +
                                                          "\nVeículo: %d." +
                                                          "\nPosição acoplado: %d." +
                                                          "\nProcesso editado: %d.",
                                                  codVeiculo,
                                                  posicaoAcaoRealizada,
                                                  codProcessoAcoplamento));
                        }
                    } else {
                        throw new VeiculoAcoplamentoValidatorException(
                                String.format("Não é possível mudar de posição o veículo %d pois nenhum " +
                                                      "código de processo para edição foi recebido.",
                                              codVeiculo));
                    }
                });
    }
}
