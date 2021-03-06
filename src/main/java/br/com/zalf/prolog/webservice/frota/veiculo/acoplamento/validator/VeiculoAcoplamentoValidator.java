package br.com.zalf.prolog.webservice.frota.veiculo.acoplamento.validator;

import br.com.zalf.prolog.webservice.commons.util.ListUtils;
import br.com.zalf.prolog.webservice.frota.veiculo.acoplamento._model.realizacao.VeiculoAcoplamentoAcaoRealizada;
import br.com.zalf.prolog.webservice.frota.veiculo.acoplamento._model.realizacao.VeiculoAcoplamentoProcessoRealizacao;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
        garantePosicoesEmOrdemSemGaps();
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
                .ifPresent(veiculo -> fail("N??o foi realizado a coleta do KM dos roboques com hubodometro."));
    }

    private void garanteVeiculoMotorizadoColetouKm() {
        processoRealizacao.getVeiculosMotorizadosProcesso()
                .stream()
                .filter(VeiculoAcoplamentoAcaoRealizada::naoColetouKm)
                .findAny()
                .ifPresent(veiculo -> fail("N??o foi realizado a coleta do KM dos tratores."));
    }

    private void garanteReboquesNasPosicoesCorretas() {
        processoRealizacao.getVeiculosRebocadosProcesso()
                .stream()
                .map(VeiculoAcoplamentoAcaoRealizada::getPosicaoAcaoRealizada)
                .filter(posicao -> posicao == POSICAO_ACOPLAMENTO_TRATOR)
                .findAny()
                .ifPresent(posicao -> fail("Os reboques n??o podem ocupar posi????es de tratores (1)."));
    }

    private void garanteVeiculoMotorizadoNaPosicaoCorreta() {
        processoRealizacao.getVeiculosMotorizadosProcesso()
                .stream()
                .map(VeiculoAcoplamentoAcaoRealizada::getPosicaoAcaoRealizada)
                .filter(posicao -> posicao != POSICAO_ACOPLAMENTO_TRATOR)
                .findAny()
                .ifPresent(posicao -> fail("O trator n??o pode ser aplicado numa posi????o que n??o ?? a 1."));
    }

    private void garanteUnicoVeiculoMotorizadoAcoplado() {
        final List<Long> codVeiculosMotorizadosProcesso = processoRealizacao.getCodVeiculosMotorizadosProcesso();
        if (codVeiculosMotorizadosProcesso.size() > 1) {
            fail("?? permitido apenas um ve??culo motorizado no acoplamento, " +
                         "encontrados: " + codVeiculosMotorizadosProcesso);
        }
    }

    private void garanteVeiculosProcessoComVeiculosBanco() {
        garanteQtdVeiculosRecebidosIgualQtdVeiculosBanco();
        garanteVeiculosRecebidosIguaisVeiculosBanco();
    }

    private void garanteQtdVeiculosRecebidosIgualQtdVeiculosBanco() {
        if (processoRealizacao.getTotalVeiculosProcesso() != dadosBanco.getTotalVeiculos()) {
            fail("Total de ve??culos no processo (%d) est?? diferente do total de ve??culos buscados do banco (%d).",
                 processoRealizacao.getTotalVeiculosProcesso(),
                 dadosBanco.getTotalVeiculos());
        }
    }

    private void garanteVeiculosRecebidosIguaisVeiculosBanco() {
        if (dadosBanco.faltaAlgumVeiculo(processoRealizacao.getCodVeiculosProcesso())) {
            fail("Ve??culos no processo (%s) divergem dos ve??culos buscados do banco (%s).",
                 processoRealizacao.getCodVeiculosProcesso(),
                 dadosBanco.getCodVeiculos());
        }
    }

    private void garanteVeiculosRecebidosSejamDiferentes() {
        final List<Long> codVeiculos = processoRealizacao.getCodVeiculosProcesso();
        codVeiculos.stream()
                .filter(codVeiculo -> Collections.frequency(codVeiculos, codVeiculo) > 1)
                .findAny()
                .ifPresent(codVeiculo -> fail("N??o podem existir ve??culos duplicados no acoplamento."));
    }

    private void garantePosicoesEmOrdemSemGaps() {
        final List<Short> posicoesAcoplamento = processoRealizacao.getPosicoesOrdenadas();
        if (!ListUtils.constainsSomeInOrder(posicoesAcoplamento, POSICOES_VALIDAS_ORDENADAS_COM_TRATOR)
                && !ListUtils.constainsSomeInOrder(posicoesAcoplamento, POSICOES_VALIDAS_ORDENADAS_SEM_TRATOR)) {
            fail("A ordem do acoplamento n??o est?? correta, devem ser sequenciais: " + posicoesAcoplamento);
        }
    }

    private void garanteVeiculosPertencemUnicoProcesso() {
        if (dadosBanco.existemVeiculosComProcessosDiferentes()) {
            fail("Os ve??culos do processo pertencem a diferentes processos de acoplamento: %s",
                 dadosBanco.getCodProcessosAcoplamentosDistintos());
        }
    }

    private void garanteVeiculosPercentemProcessoSendoEditado() {
        processoRealizacao
                .getCodProcessoAcoplamentoEditado()
                .ifPresent(codProcessoAcoplamento -> {
                    if (!dadosBanco.isTodosProcessosAcoplamentosDoCodigo(codProcessoAcoplamento)) {
                        fail("Ve??culos no BD est??o nos processos de acoplamento de c??digos %s " +
                                     "por??m o processo sendo editado ?? o de c??digo %d.",
                             dadosBanco.getCodProcessosAcoplamentosDistintos(),
                             codProcessoAcoplamento);
                    }
                });
    }

    private void garanteVeiculosDesacopladosEmNovoProcesso() {
        if (processoRealizacao.isInserindoNovoProcesso() && dadosBanco.isAlgumVeiculoAcoplado()) {
            fail("Um novo acoplamento est?? sendo inserido, por??m, algum dos ve??culos recebidos" +
                         " j?? est?? acoplado: %s.", dadosBanco.getCodVeiculos());
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
                        fail("N??o ?? poss??vel acoplar o ve??culo %d pois ele j?? est?? acoplado.", codVeiculo);
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
                            fail("N??o ?? poss??vel desacoplar um ve??culo que n??o est?? acoplado." +
                                         "\nVe??culo: %d." +
                                         "\nProcesso: %d.",
                                 codVeiculo,
                                 codProcessoAcoplamento);
                        }
                    } else {
                        fail("N??o ?? poss??vel desacoplar o ve??culo %d pois nenhum " +
                                     "c??digo de processo para edi????o foi recebido.",
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
                            fail("O ve??culo %d foi mantido na posi????o %d por??m ele " +
                                         "n??o est?? acoplado nesta posi????o no processo de c??digo %d.",
                                 codVeiculo,
                                 posicaoAcaoRealizada,
                                 codProcessoAcoplamento);
                        }
                    } else {
                        fail("N??o ?? poss??vel manter na posi????o o ve??culo %d pois nenhum " +
                                     "c??digo de processo para edi????o foi recebido.",
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
                            fail("N??o ?? poss??vel realizar uma mudan??a de posi????o pois o ve??culo n??o " +
                                         "est?? acoplado em outra posi????o deste processo." +
                                         "\nVe??culo: %d." +
                                         "\nPosi????o acoplado: %d." +
                                         "\nProcesso editado: %d.",
                                 codVeiculo,
                                 posicaoAcaoRealizada,
                                 codProcessoAcoplamento);
                        }
                    } else {
                        fail("N??o ?? poss??vel mudar de posi????o o ve??culo %d pois nenhum " +
                                     "c??digo de processo para edi????o foi recebido.",
                             codVeiculo);
                    }
                });
    }

    private void fail(@NotNull final String detailedMessage, @Nullable final Object... args) {
        throw new VeiculoAcoplamentoValidatorException(String.format(detailedMessage, args));
    }
}
