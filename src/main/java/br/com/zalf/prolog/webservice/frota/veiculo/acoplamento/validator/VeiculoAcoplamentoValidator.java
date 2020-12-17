package br.com.zalf.prolog.webservice.frota.veiculo.acoplamento.validator;

import br.com.zalf.prolog.webservice.commons.util.ListUtils;
import br.com.zalf.prolog.webservice.frota.veiculo.acoplamento._model.realizacao.VeiculoAcoplamentoAcaoRealizada;
import br.com.zalf.prolog.webservice.frota.veiculo.acoplamento._model.realizacao.VeiculoAcoplamentoProcessoRealizacao;
import io.sentry.util.Nullable;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

import static br.com.zalf.prolog.webservice.frota.veiculo.acoplamento._model.realizacao.VeiculoAcoplamentoAcaoRealizada.POSICAO_ACOPLAMENTO_TRATOR;
import static br.com.zalf.prolog.webservice.frota.veiculo.acoplamento._model.realizacao.VeiculoAcoplamentoProcessoRealizacao.POSICOES_VALIDAS_ORDENADAS_COM_TRATOR;
import static br.com.zalf.prolog.webservice.frota.veiculo.acoplamento._model.realizacao.VeiculoAcoplamentoProcessoRealizacao.POSICOES_VALIDAS_ORDENADAS_SEM_TRATOR;

@AllArgsConstructor
public final class VeiculoAcoplamentoValidator {
    @NotNull
    private final AcomplamentoValidacaoHolder dadosBanco;
    @NotNull
    private final VeiculoAcoplamentoProcessoRealizacao processoRealizacao;

    public void validate() {
        garanteVeiculosProcessoComVeiculosBanco();
        garanteVeiculosRecebidosSejamDiferentes();
        garantePosicoesEmOrdem();
        garanteUnicoVeiculoMotorizadoAcoplado();
        garanteVeiculoMotorizadoNaPosicaoCorreta();
        garanteReboquesNasPosicoesCorretas();
        garanteVeiculoMotorizadoColetouKm();
        garanteReboquesComHubodometroColetaramKm();
        garanteVeiculosPertencemUnicoProcesso();
        garanteVeiculosPercentemProcessoSendoEditado();
        garanteVeiculosDesacopladosEmNovoProcesso();
        garanteAcoesAcoplamentosCorretas();
    }

    private void garanteReboquesComHubodometroColetaramKm() {
        final List<Long> codVeiculosComHubodometro = dadosBanco.getCodVeiculosComHubodometro();
        processoRealizacao.getVeiculosByCodigos(codVeiculosComHubodometro)
                .stream()
                .filter(VeiculoAcoplamentoAcaoRealizada::naoColetouKm)
                .findAny()
                .ifPresent(veiculo -> fail("Não foi realizado a coleta do KM dos roboques com hubodometro."));
    }

    private void garanteVeiculoMotorizadoColetouKm() {
        processoRealizacao.getVeiculosMotorizadosProcesso()
                .stream()
                .filter(VeiculoAcoplamentoAcaoRealizada::naoColetouKm)
                .findAny()
                .ifPresent(veiculo -> fail("Não foi realizado a coleta do KM dos tratores."));
    }

    private void garanteReboquesNasPosicoesCorretas() {
        processoRealizacao.getVeiculosRebocadosProcesso()
                .stream()
                .map(VeiculoAcoplamentoAcaoRealizada::getPosicaoAcaoRealizada)
                .filter(posicao -> posicao == POSICAO_ACOPLAMENTO_TRATOR)
                .findAny()
                .ifPresent(posicao -> fail("Os reboques não podem ocupar posições de tratores (1)."));
    }

    private void garanteVeiculoMotorizadoNaPosicaoCorreta() {
        processoRealizacao.getVeiculosMotorizadosProcesso()
                .stream()
                .map(VeiculoAcoplamentoAcaoRealizada::getPosicaoAcaoRealizada)
                .filter(posicao -> posicao != POSICAO_ACOPLAMENTO_TRATOR)
                .findAny()
                .ifPresent(posicao -> fail("O trator não pode ser aplicado numa posição que não é a 1."));
    }

    private void garanteUnicoVeiculoMotorizadoAcoplado() {
        final List<Long> codVeiculosMotorizadosProcesso = processoRealizacao.getCodVeiculosMotorizadosProcesso();
        if (codVeiculosMotorizadosProcesso.size() > 1) {
            fail("É permitido apenas um veículo motorizado no acoplamento, " +
                         "encontrados: " + codVeiculosMotorizadosProcesso);
        }
    }

    private void garanteVeiculosProcessoComVeiculosBanco() {
        garanteQtdVeiculosRecebidosIgualQtdVeiculosBanco();
        garanteVeiculosRecebidosIguaisVeiculosBanco();
    }

    private void garanteQtdVeiculosRecebidosIgualQtdVeiculosBanco() {
        if (processoRealizacao.getTotalVeiculosProcesso() != dadosBanco.getTotalVeiculos()) {
            fail("Total de veículos no processo (%d) está diferente do total de veículos buscados do banco (%d).",
                 processoRealizacao.getTotalVeiculosProcesso(),
                 dadosBanco.getTotalVeiculos());
        }
    }

    private void garanteVeiculosRecebidosIguaisVeiculosBanco() {
        if (dadosBanco.faltaAlgumVeiculo(processoRealizacao.getCodVeiculosProcesso())) {
            fail("Veículos no processo (%s) divergem dos veículos buscados do banco (%s).",
                 processoRealizacao.getCodVeiculosProcesso(),
                 dadosBanco.getCodVeiculos());
        }
    }

    private void garanteVeiculosRecebidosSejamDiferentes() {
        final List<Long> codVeiculos = processoRealizacao.getCodVeiculosProcesso();
        codVeiculos.stream()
                .filter(codVeiculo -> Collections.frequency(codVeiculos, codVeiculo) > 1)
                .findAny()
                .ifPresent(codVeiculo -> fail("Não podem existir veículos duplicados no acoplamento."));
    }

    private void garantePosicoesEmOrdem() {
        final List<Short> posicoesAcoplamento = processoRealizacao.getPosicoesOrdenadas();
        if (!ListUtils.constainsSomeInOrder(posicoesAcoplamento, POSICOES_VALIDAS_ORDENADAS_COM_TRATOR)
                && !ListUtils.constainsSomeInOrder(posicoesAcoplamento, POSICOES_VALIDAS_ORDENADAS_SEM_TRATOR)) {
            fail("A ordem do acoplamento não está correta, devem ser sequenciais: " + posicoesAcoplamento);
        }
    }

    private void garanteVeiculosPertencemUnicoProcesso() {
        if (dadosBanco.existemVeiculosComProcessosDiferentes()) {
            fail("Os veículos do processo pertencem a diferentes processos de acoplamento: %s",
                 dadosBanco.getCodProcessosAcoplamentosDistintos());
        }
    }

    private void garanteVeiculosPercentemProcessoSendoEditado() {
        processoRealizacao
                .getCodProcessoAcoplamentoEditado()
                .ifPresent(codProcessoAcoplamento -> {
                    if (!dadosBanco.isTodosProcessosAcoplamentosDoCodigo(codProcessoAcoplamento)) {
                        fail("Veículos no BD estão nos processos de acoplamento de códigos %s " +
                                     "porém o processo sendo editado é o de código %d.",
                             dadosBanco.getCodProcessosAcoplamentosDistintos(),
                             codProcessoAcoplamento);
                    }
                });
    }

    private void garanteVeiculosDesacopladosEmNovoProcesso() {
        if (processoRealizacao.isInserindoNovoProcesso() && dadosBanco.isAlgumVeiculoAcoplado()) {
            fail("Um novo acoplamento está sendo inserido, porém, algum dos veículos recebidos" +
                         " já está acoplado: %s.", dadosBanco.getCodVeiculos());
        }
    }

    private void garanteAcoesAcoplamentosCorretas() {
        garanteVeiculosAcopladosEstaoLivres();
        garanteVeiculosDesacopladosEstaoAcoplados();
        garanteVeiculosMantidosPosicaoEstaoNaMesmaPosicao();
        garanteVeiculosMudaramPosicaoEstaoEmPosicaoDiferente();
    }

    private void garanteVeiculosAcopladosEstaoLivres() {
        processoRealizacao
                .getVeiculosAcoplados()
                .forEach(veiculo -> {
                    final Long codVeiculo = veiculo.getCodVeiculo();
                    if (dadosBanco.isVeiculoAcoplado(codVeiculo)) {
                        fail("Não é possível acoplar o veículo %d pois ele já está acoplado.", codVeiculo);
                    }
                });
    }

    private void garanteVeiculosDesacopladosEstaoAcoplados() {
        processoRealizacao
                .getVeiculosDesacoplados()
                .forEach(veiculo -> {
                    final Long codVeiculo = veiculo.getCodVeiculo();
                    if (processoRealizacao.getCodProcessoAcoplamentoEditado().isPresent()) {
                        final Long codProcessoAcoplamento = processoRealizacao.getCodProcessoAcoplamentoEditado().get();
                        if (!dadosBanco.isVeiculoAcopladoProcesso(codVeiculo, codProcessoAcoplamento)) {
                            fail("Não é possível desacoplar um veículo que não está acoplado." +
                                         "\nVeículo: %d." +
                                         "\nProcesso: %d.",
                                 codVeiculo,
                                 codProcessoAcoplamento);
                        }
                    } else {
                        fail("Não é possível desacoplar o veículo %d pois nenhum " +
                                     "código de processo para edição foi recebido.",
                             codVeiculo);
                    }
                });
    }

    private void garanteVeiculosMantidosPosicaoEstaoNaMesmaPosicao() {
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
                            fail("O veículo %d foi mantido na posição %d porém ele " +
                                         "não está acoplado nesta posição no processo de código %d.",
                                 codVeiculo,
                                 posicaoAcaoRealizada,
                                 codProcessoAcoplamento);
                        }
                    } else {
                        fail("Não é possível manter na posição o veículo %d pois nenhum " +
                                     "código de processo para edição foi recebido.",
                             codVeiculo);
                    }
                });
    }

    private void garanteVeiculosMudaramPosicaoEstaoEmPosicaoDiferente() {
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
                            fail("Não é possível realizar uma mudança de posição pois o veículo não " +
                                         "está acoplado em outra posição deste processo." +
                                         "\nVeículo: %d." +
                                         "\nPosição acoplado: %d." +
                                         "\nProcesso editado: %d.",
                                 codVeiculo,
                                 posicaoAcaoRealizada,
                                 codProcessoAcoplamento);
                        }
                    } else {
                        fail("Não é possível mudar de posição o veículo %d pois nenhum " +
                                     "código de processo para edição foi recebido.",
                             codVeiculo);
                    }
                });
    }

    private void fail(@NotNull final String detailedMessage, @Nullable final Object... args) {
        throw new VeiculoAcoplamentoValidatorException(String.format(detailedMessage, args));
    }
}
