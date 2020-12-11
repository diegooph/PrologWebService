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
            throw new IllegalStateException();
        }

        if (!dadosBanco.getCodVeiculosProcesso().containsAll(processoRealizacao.getCodVeiculosProcesso())) {
            throw new IllegalStateException();
        }
    }

    private void validateVeiculosRepetidosAcoplamento() {
        final List<Long> codVeiculos = processoRealizacao.getAcoesRealizadas().stream()
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
        final List<Short> posicoesAcoplamento = processoRealizacao.getAcoesRealizadas().stream()
                .map(VeiculoAcoplamentoAcaoRealizada::getPosicaoAcaoRealizada)
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
            throw new IllegalStateException();
        }

        if (processoRealizacao.getCodProcessoAcoplamentoEditado().isPresent()) {
            final Long codigo = processoRealizacao.getCodProcessoAcoplamentoEditado().get();
            if (!dadosBanco.mesmoProcessoAcoplamento(codigo)) {
                throw new IllegalStateException();
            }
        } else {
            // Se estou criando, as placas não devem conter acoplamentos
            if (!dadosBanco.isTodosVeiculosNaoAcoplados()) {
                throw new IllegalStateException();
            }
        }
    }

    private void validateAcoesAcoplamentos() {
        processoRealizacao
                .getVeiculosAcoplados()
                .forEach(veiculo -> {
                    if (dadosBanco.isVeiculoAcoplado(veiculo.getCodVeiculo())) {
                        throw new IllegalStateException();
                    }
                });

        processoRealizacao
                .getVeiculosDesacoplados()
                .forEach(veiculo -> {
                    if (processoRealizacao.getCodProcessoAcoplamentoEditado().isPresent()) {
                        final Long codProcessoAcoplamento = processoRealizacao.getCodProcessoAcoplamentoEditado().get();
                        if (!dadosBanco.isVeiculoAcopladoProcesso(veiculo.getCodVeiculo(),
                                                                  codProcessoAcoplamento)) {
                            throw new IllegalStateException();
                        }
                    } else {
                        throw new IllegalStateException();
                    }
                });

        processoRealizacao
                .getVeiculosMantidosPosicao()
                .forEach(veiculo -> {
                    if (processoRealizacao.getCodProcessoAcoplamentoEditado().isPresent()) {
                        final Long codProcessoAcoplamento = processoRealizacao.getCodProcessoAcoplamentoEditado().get();
                        if (!dadosBanco.isVeiculoAcopladoProcessoEPosicao(veiculo.getCodVeiculo(),
                                                                          codProcessoAcoplamento,
                                                                          veiculo.getPosicaoAcaoRealizada())) {
                            throw new IllegalStateException();
                        }
                    } else {
                        throw new IllegalStateException();
                    }
                });

        processoRealizacao
                .getVeiculosMudaramPosicao()
                .forEach(veiculo -> {
                    if (processoRealizacao.getCodProcessoAcoplamentoEditado().isPresent()) {
                        final Long codProcessoAcoplamento = processoRealizacao.getCodProcessoAcoplamentoEditado().get();
                        if (!dadosBanco.isVeiculoAcopladoProcessoComPosicaoDiferente(veiculo.getCodVeiculo(),
                                                                                     codProcessoAcoplamento,
                                                                                     veiculo.getPosicaoAcaoRealizada())) {
                            throw new IllegalStateException();
                        }
                    } else {
                        throw new IllegalStateException();
                    }
                });
    }
}
