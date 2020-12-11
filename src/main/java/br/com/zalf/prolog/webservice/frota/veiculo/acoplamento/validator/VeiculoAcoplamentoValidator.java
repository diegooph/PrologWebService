package br.com.zalf.prolog.webservice.frota.veiculo.acoplamento.validator;

import br.com.zalf.prolog.webservice.errorhandling.exception.GenericException;
import br.com.zalf.prolog.webservice.frota.veiculo.acoplamento._model.realizacao.VeiculoAcoplamentoAcaoRealizada;
import br.com.zalf.prolog.webservice.frota.veiculo.acoplamento._model.realizacao.VeiculoAcoplamentoProcessoRealizacao;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class VeiculoAcoplamentoValidator {
    @NotNull
    private final HolderAcomplamentoValidacao acomplamentoValidacao;
    @NotNull
    private final VeiculoAcoplamentoProcessoRealizacao processoRealizacao;

    public void validate() {
        //TODO acomplamentoValidacao == processoRealizacao
        validateVeiculosRepetidosAcoplamento();
        validateOrdenacao();
        validateVeiculosPertencemUnicoProcesso();
        validateAcoesAcoplamentos();
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
        if (acomplamentoValidacao.existemVeiculosComProcessosDiferentes()) {
            throw new IllegalStateException();
        }

        // Se estou criando, as placas não devem conter acoplamentos
        if (processoRealizacao.getCodProcessoAcoplamentoEditado().isPresent()) {
            final Long codigo = processoRealizacao.getCodProcessoAcoplamentoEditado().get();
            if (!acomplamentoValidacao.mesmoProcessoAcoplamento(codigo)) {
                throw new IllegalStateException();
            }
        } else {
            if (!acomplamentoValidacao.nenhumProcesso()) {
                throw new IllegalStateException();
            }
        }
    }

    private void validateAcoesAcoplamentos() {
        processoRealizacao
                .getVeiculosAcoplados()
                .forEach(veiculo -> {
                    if (acomplamentoValidacao.isVeiculoAcoplado(veiculo.getCodVeiculo())) {
                        throw new IllegalStateException();
                    }
                });

        processoRealizacao
                .getVeiculosDesacoplados()
                .forEach(veiculo -> {
                    if (processoRealizacao.getCodProcessoAcoplamentoEditado().isPresent()) {
                        final Long codProcessoAcoplamento = processoRealizacao.getCodProcessoAcoplamentoEditado().get();
                        if (!acomplamentoValidacao.isVeiculoAcopladoProcesso(veiculo.getCodVeiculo(),
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
                        if (!acomplamentoValidacao.isVeiculoAcopladoProcessoEPosicao(veiculo.getCodVeiculo(),
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
                        if (!acomplamentoValidacao.isVeiculoAcopladoProcessoComPosicaoDiferente(veiculo.getCodVeiculo(),
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
