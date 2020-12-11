package br.com.zalf.prolog.webservice.frota.veiculo.acoplamento.validator;

import br.com.zalf.prolog.webservice.errorhandling.exception.GenericException;
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
    private final HolderAcomplamentoValidacao dadosBanco;
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
            throw new IllegalStateException(String.format(
                    "Total de veículos no processo (%d) está diferente do total de veículos " +
                            "buscados do banco (%d).",
                    processoRealizacao.getTotalVeiculosProcesso(),
                    dadosBanco.getTotalVeiculos()));
        }

        if (!dadosBanco.getCodVeiculos().containsAll(processoRealizacao.getCodVeiculosProcesso())) {
            throw new IllegalStateException(String.format(
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
                    throw new GenericException("Não podem existir veículos duplicados no acoplamento.");
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
                throw new GenericException("Erro ao realizar operação.",
                                           "A ordem do acoplamento não está correta, devem ser sequenciais: "
                                                   + posicoesAcoplamento);
            }
        }
    }

    private void validateVeiculosPertencemUnicoProcesso() {
        // Estou editando veículos de vários acoplamentos.
        if (dadosBanco.existemVeiculosComProcessosDiferentes()) {
            throw new IllegalStateException(String.format("Os veículos do processo pertencem a " +
                                                                  "diferentes processos de acoplamento: %s",
                                                          dadosBanco.getCodProcessosAcoplamentosDistintos()));
        }

        if (processoRealizacao.getCodProcessoAcoplamentoEditado().isPresent()) {
            final Long codProcessoAcoplamento = processoRealizacao.getCodProcessoAcoplamentoEditado().get();
            if (!dadosBanco.isTodosProcessosAcoplamentosDoCodigo(codProcessoAcoplamento)) {
                throw new IllegalStateException(String.format(
                        "Veículos no BD estão nos processos de acoplamento de códigos %s porém o processo sendo " +
                                "editado é o de código %d.",
                        dadosBanco.getCodProcessosAcoplamentosDistintos(),
                        codProcessoAcoplamento));
            }
        } else {
            // Se estou criando, as placas não devem conter acoplamentos
            if (!dadosBanco.isTodosVeiculosNaoAcoplados()) {
                throw new IllegalStateException(
                        String.format("Nenhum processo está sendo editado, porém algum dos veículos recebidos" +
                                              " já está acoplado: %s.", dadosBanco.getCodVeiculos()));
            }
        }
    }

    private void validateAcoesAcoplamentos() {
        processoRealizacao
                .getVeiculosAcoplados()
                .forEach(veiculo -> {
                    final Long codVeiculo = veiculo.getCodVeiculo();
                    if (dadosBanco.isVeiculoAcoplado(codVeiculo)) {
                        throw new IllegalStateException(
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
                            throw new IllegalStateException(
                                    String.format("O veículo %d foi desacoplado porém ele não está acoplado " +
                                                          "no processo de código %d.",
                                                  codVeiculo,
                                                  codProcessoAcoplamento));
                        }
                    } else {
                        throw new IllegalStateException(
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
                            throw new IllegalStateException(
                                    String.format("O veículo %d foi mantido na posição %d porém ele " +
                                                          "não está acoplado nesta posição no processo de código %d.",
                                                  codVeiculo,
                                                  posicaoAcaoRealizada,
                                                  codProcessoAcoplamento));
                        }
                    } else {
                        throw new IllegalStateException(
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
                            throw new IllegalStateException(
                                    String.format("O veículo %d mudou para a posição %d porém ele " +
                                                          "não está acoplado em outra posição no processo de " +
                                                          "código %d (para justificar essa mudança de posição).",
                                                  codVeiculo,
                                                  posicaoAcaoRealizada,
                                                  codProcessoAcoplamento));
                        }
                    } else {
                        throw new IllegalStateException(
                                String.format("Não é possível mudar de posição o veículo %d pois nenhum " +
                                                      "código de processo para edição foi recebido.",
                                              codVeiculo));
                    }
                });
    }
}
