package test.br.com.zalf.prolog.webservice.pilares.frota.veiculo.acoplamento;

import br.com.zalf.prolog.webservice.frota.veiculo.acoplamento._model.realizacao.VeiculoAcoplamentoProcessoRealizacao;
import br.com.zalf.prolog.webservice.frota.veiculo.acoplamento.validator.AcomplamentoValidacaoHolder;
import br.com.zalf.prolog.webservice.frota.veiculo.acoplamento.validator.VeiculoAcoplamentoValidator;
import br.com.zalf.prolog.webservice.frota.veiculo.acoplamento.validator.VeiculoAcoplamentoValidatorException;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static br.com.zalf.prolog.webservice.frota.veiculo.acoplamento._model.VeiculoAcoplamentoAcaoEnum.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class VeiculoAcoplamentoValidatorTest {

    private void validateDoesNotThrow(@NotNull final AcomplamentoValidacaoHolder dadosBanco,
                                      @NotNull final VeiculoAcoplamentoProcessoRealizacao processoRealizacao) {
        final VeiculoAcoplamentoValidator validator = new VeiculoAcoplamentoValidator(dadosBanco, processoRealizacao);
        assertDoesNotThrow(validator::validate);
    }

    private void validateThrowsWithErrorMessage(@NotNull final AcomplamentoValidacaoHolder dadosBanco,
                                                @NotNull final VeiculoAcoplamentoProcessoRealizacao processoRealizacao,
                                                @NotNull final String errorMessageToValidate) {
        final VeiculoAcoplamentoValidator validator = new VeiculoAcoplamentoValidator(dadosBanco, processoRealizacao);
        final VeiculoAcoplamentoValidatorException throwable =
                assertThrows(VeiculoAcoplamentoValidatorException.class, validator::validate);
        assertThat(throwable).isNotNull();
        assertThat(throwable.getDetailedMessage()).isEqualToIgnoringCase(errorMessageToValidate);
    }

    @Test
    void givenNovoProcessoAcoplamento_whenInserted_shouldNotFail() {
        final AcomplamentoValidacaoHolder dadosBanco = VeiculosEstadoBancoCreator.builder()
                .codVeiculos(1L, 2L)
                .codProcessosAcoplamentosVinculados(null, null)
                .posicoesAcoplados(1, 2)
                .motorizados(true, false)
                .possuemHubodometro(false, false)
                .build();

        final VeiculoAcoplamentoProcessoRealizacao processoRealizacao = AcoesRealizadasCreator.builder()
                .codVeiculos(1L, 2L)
                .codDiagramasVeiculos(1L, 1L)
                .motorizados(true, false)
                .acoesRealizadas(ACOPLADO, ACOPLADO)
                .posicoesAcoesRealizadas(1, 2)
                .kmsColetados(1000L, null)
                .build(null);
        validateDoesNotThrow(dadosBanco, processoRealizacao);
    }

    @Test
    void givenEdicaoProcessoAcoplamento_whenNadaMudou_shouldNotFail() {
        final AcomplamentoValidacaoHolder dadosBanco = VeiculosEstadoBancoCreator.builder()
                .codVeiculos(1L, 2L)
                .codProcessosAcoplamentosVinculados(1L, 1L)
                .posicoesAcoplados(1, 2)
                .motorizados(true, false)
                .possuemHubodometro(false, false)
                .build();
        final VeiculoAcoplamentoProcessoRealizacao processoRealizacao = AcoesRealizadasCreator.builder()
                .codVeiculos(1L, 2L)
                .codDiagramasVeiculos(1L, 1L)
                .motorizados(true, false)
                .acoesRealizadas(PERMANECEU, PERMANECEU)
                .posicoesAcoesRealizadas(1, 2)
                .kmsColetados(1000L, null)
                .build(1L);
        validateDoesNotThrow(dadosBanco, processoRealizacao);
    }

    @Test
    void givenEdicaoProcessoAcoplamento_whenTodosDesacoplados_shouldNotFail() {
        final AcomplamentoValidacaoHolder dadosBanco = VeiculosEstadoBancoCreator.builder()
                .codVeiculos(1L, 2L)
                .codProcessosAcoplamentosVinculados(1L, 1L)
                .posicoesAcoplados(1, 2)
                .motorizados(true, false)
                .possuemHubodometro(false, false)
                .build();
        final VeiculoAcoplamentoProcessoRealizacao processoRealizacao = AcoesRealizadasCreator.builder()
                .codVeiculos(1L, 2L)
                .codDiagramasVeiculos(1L, 1L)
                .motorizados(true, false)
                .acoesRealizadas(DESACOPLADO, DESACOPLADO)
                .posicoesAcoesRealizadas(1, 2)
                .kmsColetados(1000L, null)
                .build(1L);
        validateDoesNotThrow(dadosBanco, processoRealizacao);
    }

    @Test
    void givenEdicaoProcessoAcoplamento_whenVeiculosMudaramPosicaoMesmoAcoplamento_shouldNotFail() {
        final AcomplamentoValidacaoHolder dadosBanco = VeiculosEstadoBancoCreator.builder()
                .codVeiculos(1L, 2L, 3L)
                .codProcessosAcoplamentosVinculados(1L, 1L, 1L)
                .posicoesAcoplados(1, 2, 3)
                .motorizados(true, false, false)
                .possuemHubodometro(false, false, false)
                .build();
        final VeiculoAcoplamentoProcessoRealizacao processoRealizacao = AcoesRealizadasCreator.builder()
                .codVeiculos(1L, 2L, 3L)
                .codDiagramasVeiculos(1L, 1L, 1L)
                .motorizados(true, false, false)
                .acoesRealizadas(DESACOPLADO, MUDOU_POSICAO, MUDOU_POSICAO)
                .posicoesAcoesRealizadas(1, 3, 2)
                .kmsColetados(1000L, null, null)
                .build(1L);
        validateDoesNotThrow(dadosBanco, processoRealizacao);
    }

    @Test
    void givenEdicaoProcessoAcoplamento_whenTratorAlterado_shouldNotFail() {
        final AcomplamentoValidacaoHolder dadosBanco = VeiculosEstadoBancoCreator.builder()
                .codVeiculos(1L, 2L, 3L)
                .codProcessosAcoplamentosVinculados(1L, 1L, null)
                .posicoesAcoplados(1, 2, null)
                .motorizados(true, false, true)
                .possuemHubodometro(false, false, false)
                .build();
        final VeiculoAcoplamentoProcessoRealizacao processoRealizacao = AcoesRealizadasCreator.builder()
                .codVeiculos(1L, 2L, 3L)
                .codDiagramasVeiculos(1L, 1L, 1L)
                .motorizados(true, false, true)
                .acoesRealizadas(DESACOPLADO, PERMANECEU, ACOPLADO)
                .posicoesAcoesRealizadas(1, 2, 1)
                .kmsColetados(1000L, null, 1000L)
                .build(1L);
        validateDoesNotThrow(dadosBanco, processoRealizacao);
    }

    @Test
    void givenEdicaoProcessoAcoplamento_whenDesacoplaTodosAcoplaNovos_shouldNotFail() {
        final AcomplamentoValidacaoHolder dadosBanco = VeiculosEstadoBancoCreator.builder()
                .codVeiculos(1L, 2L, 3L, 4L)
                .codProcessosAcoplamentosVinculados(1L, 1L, null, null)
                .posicoesAcoplados(1, 2, null, null)
                .motorizados(true, false, true, false)
                .possuemHubodometro(false, false, false, false)
                .build();
        final VeiculoAcoplamentoProcessoRealizacao processoRealizacao = AcoesRealizadasCreator.builder()
                .codVeiculos(1L, 2L, 3L, 4L)
                .codDiagramasVeiculos(1L, 1L, 1L, 1L)
                .motorizados(true, false, true, false)
                .acoesRealizadas(DESACOPLADO, DESACOPLADO, ACOPLADO, ACOPLADO)
                .posicoesAcoesRealizadas(1, 2, 1, 2)
                .kmsColetados(1000L, null, 1000L, null)
                .build(1L);
        validateDoesNotThrow(dadosBanco, processoRealizacao);
    }

    @Test
    void givenNovoProcessoAcoplamento_whenApenasReboquesAcoplados_shouldNotFail() {
        final AcomplamentoValidacaoHolder dadosBanco = VeiculosEstadoBancoCreator.builder()
                .codVeiculos(1L, 2L)
                .motorizados(false, false)
                .possuemHubodometro(false, false)
                .posicoesAcoplados(null, null)
                .codProcessosAcoplamentosVinculados(null, null)
                .build();
        final VeiculoAcoplamentoProcessoRealizacao processoRealizacao = AcoesRealizadasCreator.builder()
                .codVeiculos(1L, 2L)
                .codDiagramasVeiculos(1L, 1L)
                .motorizados(false, false)
                .acoesRealizadas(ACOPLADO, ACOPLADO)
                .posicoesAcoesRealizadas(2, 3)
                .kmsColetados(null, null)
                .build(null);
        validateDoesNotThrow(dadosBanco, processoRealizacao);
    }

    @Test
    void givenEdicaoProcessoAcoplamento_whenQtdVeiculosRecebidosDiferenteQtdVeiculosBanco_shouldFail() {
        final AcomplamentoValidacaoHolder dadosBanco = VeiculosEstadoBancoCreator.builder()
                .codVeiculos(1L, 2L)
                .codProcessosAcoplamentosVinculados(1L, 1L)
                .posicoesAcoplados(1, 2)
                .motorizados(true, false)
                .possuemHubodometro(false, false)
                .build();
        final VeiculoAcoplamentoProcessoRealizacao processoRealizacao =
                new VeiculoAcoplamentoProcessoRealizacao(ValidatorTestConstants.COD_UNIDADE_TESTES,
                                                         "Teste validator",
                                                         Collections.emptyList(),
                                                         1L);
        final String errorMessage =
                "Total de ve??culos no processo (0) est?? diferente do total de ve??culos buscados do banco (2).";
        validateThrowsWithErrorMessage(dadosBanco, processoRealizacao, errorMessage);
    }

    @Test
    void givenEdicaoProcessoAcoplamento_whenVeiculosRecebidosDiferenteVeiculosBanco_shouldFail() {
        final AcomplamentoValidacaoHolder dadosBanco = VeiculosEstadoBancoCreator.builder()
                .codVeiculos(1L, 2L)
                .codProcessosAcoplamentosVinculados(1L, 1L)
                .posicoesAcoplados(1, 2)
                .motorizados(true, false)
                .possuemHubodometro(false, false)
                .build();
        final VeiculoAcoplamentoProcessoRealizacao processoRealizacao = AcoesRealizadasCreator.builder()
                .codVeiculos(1L, 4L)
                .codDiagramasVeiculos(1L, 1L)
                .motorizados(true, false)
                .acoesRealizadas(PERMANECEU, PERMANECEU)
                .posicoesAcoesRealizadas(1, 1)
                .kmsColetados(1000L, null)
                .build(1L);

        final String errorMessage = "Ve??culos no processo ("
                + processoRealizacao.getCodVeiculosProcesso() +
                ") divergem dos ve??culos buscados do banco ("
                + dadosBanco.getCodVeiculos() + ").";
        validateThrowsWithErrorMessage(dadosBanco, processoRealizacao, errorMessage);
    }

    @Test
    void givenEdicaoProcessoAcoplamento_whenVeiculosRecebidosRepetidosEmDiferentesProcessos_shouldFail() {
        final AcomplamentoValidacaoHolder dadosBanco = VeiculosEstadoBancoCreator.builder()
                .codVeiculos(1L, 2L)
                .codProcessosAcoplamentosVinculados(1L, 1L)
                .posicoesAcoplados(1, 2)
                .motorizados(true, false)
                .possuemHubodometro(false, false)
                .build();
        final VeiculoAcoplamentoProcessoRealizacao processoRealizacao = AcoesRealizadasCreator.builder()
                .codVeiculos(1L, 2L, 2L)
                .codDiagramasVeiculos(1L, 1L, 1L)
                .motorizados(true, false, false)
                .acoesRealizadas(PERMANECEU, PERMANECEU, PERMANECEU)
                .posicoesAcoesRealizadas(1, 2, 3)
                .kmsColetados(1000L, null, null)
                .build(1L);

        final String errorMessage = "N??o podem existir ve??culos duplicados no acoplamento.";
        validateThrowsWithErrorMessage(dadosBanco, processoRealizacao, errorMessage);
    }

    @Test
    void givenNovoProcessoAcoplamento_whenPosicoesComGaps_shouldFail() {
        final AcomplamentoValidacaoHolder dadosBanco = VeiculosEstadoBancoCreator.builder()
                .codVeiculos(1L, 2L)
                .motorizados(true, false)
                .possuemHubodometro(false, false)
                .posicoesAcoplados(null, null)
                .codProcessosAcoplamentosVinculados(null, null)
                .build();
        final VeiculoAcoplamentoProcessoRealizacao processoRealizacao = AcoesRealizadasCreator.builder()
                .codVeiculos(1L, 2L)
                .codDiagramasVeiculos(1L, 1L)
                .motorizados(true, false)
                .acoesRealizadas(ACOPLADO, ACOPLADO)
                .posicoesAcoesRealizadas(1, 3)
                .kmsColetados(1000L, null)
                .build(null);

        final String errorMessage = "A ordem do acoplamento n??o est?? correta, devem ser sequenciais: "
                + processoRealizacao.getPosicoesOrdenadas();
        validateThrowsWithErrorMessage(dadosBanco, processoRealizacao, errorMessage);
    }

    @Test
    void givenEdicaoProcessoAcoplamento_whenVeiculosMudaramPosicaoDeixandoGaps_shouldFail() {
        final AcomplamentoValidacaoHolder dadosBanco = VeiculosEstadoBancoCreator.builder()
                .codVeiculos(1L, 2L, 3L)
                .codProcessosAcoplamentosVinculados(1L, 1L, 1L)
                .posicoesAcoplados(1, 2, 3)
                .motorizados(true, false, false)
                .possuemHubodometro(false, false, false)
                .build();
        final VeiculoAcoplamentoProcessoRealizacao processoRealizacao = AcoesRealizadasCreator.builder()
                .codVeiculos(1L, 2L, 3L)
                .codDiagramasVeiculos(1L, 1L, 1L)
                .motorizados(true, false, false)
                .acoesRealizadas(PERMANECEU, MUDOU_POSICAO, DESACOPLADO)
                .posicoesAcoesRealizadas(1, 3, 3)
                .kmsColetados(1000L, null, null)
                .build(1L);

        final String errorMessage = "A ordem do acoplamento n??o est?? correta, devem ser sequenciais: "
                + processoRealizacao.getPosicoesOrdenadas();
        validateThrowsWithErrorMessage(dadosBanco, processoRealizacao, errorMessage);
    }

    @Test
    void givenNovoProcessoAcoplamento_whenVariosVeiculoMotorizadoAcoplados_shouldFail() {
        final AcomplamentoValidacaoHolder dadosBanco = VeiculosEstadoBancoCreator.builder()
                .codVeiculos(1L, 2L)
                .motorizados(true, true)
                .possuemHubodometro(false, false)
                .posicoesAcoplados(null, null)
                .codProcessosAcoplamentosVinculados(null, null)
                .build();
        final VeiculoAcoplamentoProcessoRealizacao processoRealizacao = AcoesRealizadasCreator.builder()
                .codVeiculos(1L, 2L)
                .codDiagramasVeiculos(1L, 1L)
                .motorizados(true, true)
                .acoesRealizadas(ACOPLADO, ACOPLADO)
                .posicoesAcoesRealizadas(1, 2)
                .kmsColetados(1000L, 1000L)
                .build(null);

        final String errorMessage = "?? permitido apenas um ve??culo motorizado no acoplamento, encontrados: "
                + processoRealizacao.getCodVeiculosMotorizadosProcesso();
        validateThrowsWithErrorMessage(dadosBanco, processoRealizacao, errorMessage);
    }

    @Test
    void givenNovoProcessoAcoplamento_whenVeiculoMotorizadoNaPosicaoErrada_shouldFail() {
        final AcomplamentoValidacaoHolder dadosBanco = VeiculosEstadoBancoCreator.builder()
                .codVeiculos(1L, 2L)
                .motorizados(true, true)
                .possuemHubodometro(false, false)
                .posicoesAcoplados(null, null)
                .codProcessosAcoplamentosVinculados(null, null)
                .build();
        final VeiculoAcoplamentoProcessoRealizacao processoRealizacao = AcoesRealizadasCreator.builder()
                .codVeiculos(1L, 2L)
                .codDiagramasVeiculos(1L, 1L)
                .motorizados(true, false)
                .acoesRealizadas(ACOPLADO, ACOPLADO)
                .posicoesAcoesRealizadas(2, 3)
                .kmsColetados(1000L, null)
                .build(null);

        final String errorMessage = "O trator n??o pode ser aplicado numa posi????o que n??o ?? a 1.";
        validateThrowsWithErrorMessage(dadosBanco, processoRealizacao, errorMessage);
    }

    @Test
    void givenNovoProcessoAcoplamento_whenReboquesNasPosicoesErradas_shouldFail() {
        final AcomplamentoValidacaoHolder dadosBanco = VeiculosEstadoBancoCreator.builder()
                .codVeiculos(1L, 2L)
                .motorizados(false, false)
                .possuemHubodometro(false, false)
                .posicoesAcoplados(null, null)
                .codProcessosAcoplamentosVinculados(null, null)
                .build();
        final VeiculoAcoplamentoProcessoRealizacao processoRealizacao = AcoesRealizadasCreator.builder()
                .codVeiculos(1L, 2L)
                .codDiagramasVeiculos(1L, 1L)
                .motorizados(false, false)
                .acoesRealizadas(ACOPLADO, ACOPLADO)
                .posicoesAcoesRealizadas(1, 2)
                .kmsColetados(1000L, null)
                .build(null);

        final String errorMessage = "Os reboques n??o podem ocupar posi????es de tratores (1).";
        validateThrowsWithErrorMessage(dadosBanco, processoRealizacao, errorMessage);
    }

    @Test
    void givenNovoProcessoAcoplamento_whenVeiculoMotorizadoNaoColetouKm_shouldFail() {
        final AcomplamentoValidacaoHolder dadosBanco = VeiculosEstadoBancoCreator.builder()
                .codVeiculos(1L, 2L)
                .motorizados(true, false)
                .possuemHubodometro(false, false)
                .posicoesAcoplados(null, null)
                .codProcessosAcoplamentosVinculados(null, null)
                .build();
        final VeiculoAcoplamentoProcessoRealizacao processoRealizacao = AcoesRealizadasCreator.builder()
                .codVeiculos(1L, 2L)
                .codDiagramasVeiculos(1L, 1L)
                .motorizados(true, false)
                .acoesRealizadas(ACOPLADO, ACOPLADO)
                .posicoesAcoesRealizadas(1, 2)
                .kmsColetados(null, null)
                .build(null);

        final String errorMessage = "N??o foi realizado a coleta do KM dos tratores.";
        validateThrowsWithErrorMessage(dadosBanco, processoRealizacao, errorMessage);
    }

    @Test
    void givenNovoProcessoAcoplamento_whenReboquesComHubodometroNaoColetaramKm_shouldFail() {
        final AcomplamentoValidacaoHolder dadosBanco = VeiculosEstadoBancoCreator.builder()
                .codVeiculos(1L, 2L)
                .motorizados(true, false)
                .possuemHubodometro(false, true)
                .posicoesAcoplados(null, null)
                .codProcessosAcoplamentosVinculados(null, null)
                .build();
        final VeiculoAcoplamentoProcessoRealizacao processoRealizacao = AcoesRealizadasCreator.builder()
                .codVeiculos(1L, 2L)
                .codDiagramasVeiculos(1L, 1L)
                .motorizados(true, false)
                .acoesRealizadas(ACOPLADO, ACOPLADO)
                .posicoesAcoesRealizadas(1, 2)
                .kmsColetados(1000L, null)
                .build(null);

        final String errorMessage = "N??o foi realizado a coleta do KM dos roboques com hubodometro.";
        validateThrowsWithErrorMessage(dadosBanco, processoRealizacao, errorMessage);
    }

    @Test
    void givenEdicaoProcessoAcoplamento_whenVeiculosPertencemProcessoDistintos_shouldFail() {
        final AcomplamentoValidacaoHolder dadosBanco = VeiculosEstadoBancoCreator.builder()
                .codVeiculos(1L, 2L)
                .codProcessosAcoplamentosVinculados(1L, 2L)
                .posicoesAcoplados(1, 2)
                .motorizados(true, false)
                .possuemHubodometro(false, false)
                .build();
        final VeiculoAcoplamentoProcessoRealizacao processoRealizacao = AcoesRealizadasCreator.builder()
                .codVeiculos(1L, 2L)
                .codDiagramasVeiculos(1L, 1L)
                .motorizados(true, false)
                .acoesRealizadas(PERMANECEU, PERMANECEU)
                .posicoesAcoesRealizadas(1, 2)
                .kmsColetados(1000L, null)
                .build(1L);

        final String errorMessage = "Os ve??culos do processo pertencem a diferentes processos de acoplamento: "
                + dadosBanco.getCodProcessosAcoplamentosDistintos();
        validateThrowsWithErrorMessage(dadosBanco, processoRealizacao, errorMessage);
    }

    @Test
    void givenEdicaoProcessoAcoplamento_whenVeiculosNaoPercentemProcessoSendoEditado_shouldFail() {
        final AcomplamentoValidacaoHolder dadosBanco = VeiculosEstadoBancoCreator.builder()
                .codVeiculos(1L, 2L)
                .codProcessosAcoplamentosVinculados(2L, 2L)
                .posicoesAcoplados(1, 2)
                .motorizados(true, false)
                .possuemHubodometro(false, false)
                .build();
        final VeiculoAcoplamentoProcessoRealizacao processoRealizacao = AcoesRealizadasCreator.builder()
                .codVeiculos(1L, 2L)
                .codDiagramasVeiculos(1L, 1L)
                .motorizados(true, false)
                .acoesRealizadas(PERMANECEU, PERMANECEU)
                .posicoesAcoesRealizadas(1, 2)
                .kmsColetados(1000L, null)
                .build(1L);

        final String errorMessage = "Ve??culos no BD est??o nos processos de acoplamento de c??digos "
                + dadosBanco.getCodProcessosAcoplamentosDistintos() +
                " por??m o processo sendo editado ?? o de c??digo 1.";
        validateThrowsWithErrorMessage(dadosBanco, processoRealizacao, errorMessage);
    }

    @Test
    void givenNovoProcessoAcoplamento_whenVeiculosJaAcopladosPresentesEmNovoProcesso_shouldFail() {
        final AcomplamentoValidacaoHolder dadosBanco = VeiculosEstadoBancoCreator.builder()
                .codVeiculos(1L, 2L)
                .codProcessosAcoplamentosVinculados(2L, 2L)
                .posicoesAcoplados(1, 2)
                .motorizados(true, false)
                .possuemHubodometro(false, false)
                .build();

        final VeiculoAcoplamentoProcessoRealizacao processoRealizacao = AcoesRealizadasCreator.builder()
                .codVeiculos(1L, 2L)
                .codDiagramasVeiculos(1L, 1L)
                .motorizados(true, false)
                .acoesRealizadas(ACOPLADO, ACOPLADO)
                .posicoesAcoesRealizadas(1, 2)
                .kmsColetados(1000L, null)
                .build(null);

        final String errorMessage = String.format(
                "Um novo acoplamento est?? sendo inserido, por??m, algum dos ve??culos recebidos" +
                        " j?? est?? acoplado: %s.", dadosBanco.getCodVeiculos());
        validateThrowsWithErrorMessage(dadosBanco, processoRealizacao, errorMessage);
    }

    @Test
    void givenEdicaoProcessoAcoplamento_whenVeiculosAcopladosNaoEstaoLivres_shouldFail() {
        final AcomplamentoValidacaoHolder dadosBanco = VeiculosEstadoBancoCreator.builder()
                .codVeiculos(1L, 2L)
                .codProcessosAcoplamentosVinculados(1L, null)
                .posicoesAcoplados(1, null)
                .motorizados(true, false)
                .possuemHubodometro(false, false)
                .build();

        final VeiculoAcoplamentoProcessoRealizacao processoRealizacao = AcoesRealizadasCreator.builder()
                .codVeiculos(1L, 2L)
                .codDiagramasVeiculos(1L, 1L)
                .motorizados(true, false)
                .acoesRealizadas(ACOPLADO, ACOPLADO)
                .posicoesAcoesRealizadas(1, 2)
                .kmsColetados(1000L, null)
                .build(1L);

        final String errorMessage = "N??o ?? poss??vel acoplar o ve??culo 1 pois ele j?? est?? acoplado.";
        validateThrowsWithErrorMessage(dadosBanco, processoRealizacao, errorMessage);
    }

    @Test
    void givenEdicaoProcessoAcoplamento_whenVeiculosDesacopladosJaEstaoDesacoplados_shouldFail() {
        final AcomplamentoValidacaoHolder dadosBanco = VeiculosEstadoBancoCreator.builder()
                .codVeiculos(1L, 2L)
                .motorizados(true, false)
                .possuemHubodometro(false, false)
                .posicoesAcoplados(null, null)
                .codProcessosAcoplamentosVinculados(null, null)
                .build();

        final VeiculoAcoplamentoProcessoRealizacao processoRealizacao = AcoesRealizadasCreator.builder()
                .codVeiculos(1L, 2L)
                .codDiagramasVeiculos(1L, 1L)
                .motorizados(true, false)
                .acoesRealizadas(DESACOPLADO, DESACOPLADO)
                .posicoesAcoesRealizadas(1, 2)
                .kmsColetados(1000L, null)
                .build(1L);

        final String errorMessage = String.format("N??o ?? poss??vel desacoplar um ve??culo que n??o est?? acoplado." +
                                                          "\nVe??culo: %d." +
                                                          "\nProcesso: %d.", 1, 1);
        validateThrowsWithErrorMessage(dadosBanco, processoRealizacao, errorMessage);
    }

    @Test
    void givenNovoProcessoAcoplamento_whenVeiculosDesacoplados_shouldFail() {
        final AcomplamentoValidacaoHolder dadosBanco = VeiculosEstadoBancoCreator.builder()
                .codVeiculos(1L, 2L)
                .motorizados(true, false)
                .possuemHubodometro(false, false)
                .posicoesAcoplados(null, null)
                .codProcessosAcoplamentosVinculados(null, null)
                .build();

        final VeiculoAcoplamentoProcessoRealizacao processoRealizacao = AcoesRealizadasCreator.builder()
                .codVeiculos(1L, 2L)
                .codDiagramasVeiculos(1L, 1L)
                .motorizados(true, false)
                .acoesRealizadas(DESACOPLADO, DESACOPLADO)
                .posicoesAcoesRealizadas(1, 2)
                .kmsColetados(1000L, null)
                .build(null);

        final String errorMessage = String.format("N??o ?? poss??vel desacoplar o ve??culo %d pois nenhum " +
                                                          "c??digo de processo para edi????o foi recebido.", 1);
        validateThrowsWithErrorMessage(dadosBanco, processoRealizacao, errorMessage);
    }

    @Test
    void givenEdicaoProcessoAcoplamento_whenVeiculosMantidosPosicaoNaoEstaoNaMesmaPosicao_shouldFail() {
        final AcomplamentoValidacaoHolder dadosBanco = VeiculosEstadoBancoCreator.builder()
                .codVeiculos(1L, 2L, 3L)
                .codProcessosAcoplamentosVinculados(1L, 1L, 1L)
                .posicoesAcoplados(1, 3, 2)
                .motorizados(true, false, false)
                .possuemHubodometro(false, false, false)
                .build();

        final VeiculoAcoplamentoProcessoRealizacao processoRealizacao = AcoesRealizadasCreator.builder()
                .codVeiculos(1L, 2L, 3L)
                .codDiagramasVeiculos(1L, 1L, 1L)
                .motorizados(true, false, false)
                .acoesRealizadas(PERMANECEU, PERMANECEU, PERMANECEU)
                .posicoesAcoesRealizadas(1, 2, 3)
                .kmsColetados(1000L, null, null)
                .build(1L);

        final String errorMessage = String.format(
                "O ve??culo %d foi mantido na posi????o %d por??m ele n??o est?? acoplado nesta posi????o no processo" +
                        " de c??digo %d.", 2, 2, 1);
        validateThrowsWithErrorMessage(dadosBanco, processoRealizacao, errorMessage);
    }

    @Test
    void givenNovoProcessoAcoplamento_whenVeiculosMantidosPosicao_shouldFail() {
        final AcomplamentoValidacaoHolder dadosBanco = VeiculosEstadoBancoCreator.builder()
                .codVeiculos(1L, 2L)
                .motorizados(true, false)
                .possuemHubodometro(false, false)
                .posicoesAcoplados(null, null)
                .codProcessosAcoplamentosVinculados(null, null)
                .build();
        final VeiculoAcoplamentoProcessoRealizacao processoRealizacao = AcoesRealizadasCreator.builder()
                .codVeiculos(1L, 2L)
                .codDiagramasVeiculos(1L, 1L)
                .motorizados(true, false)
                .acoesRealizadas(PERMANECEU, PERMANECEU)
                .posicoesAcoesRealizadas(1, 2)
                .kmsColetados(1000L, null)
                .build(null);

        final String errorMessage = String.format(
                "N??o ?? poss??vel manter na posi????o o ve??culo %d pois nenhum c??digo de processo para edi????o " +
                        "foi recebido.", 1);
        validateThrowsWithErrorMessage(dadosBanco, processoRealizacao, errorMessage);
    }

    @Test
    void givenEdicaoProcessoAcoplamento_whenVeiculosMudaramPosicaoEstaoEmPosicoesIguais_shouldFail() {
        final AcomplamentoValidacaoHolder dadosBanco = VeiculosEstadoBancoCreator.builder()
                .codVeiculos(1L, 2L, 3L)
                .codProcessosAcoplamentosVinculados(1L, 1L, 1L)
                .posicoesAcoplados(1, 2, 3)
                .motorizados(true, false, false)
                .possuemHubodometro(false, false, false)
                .build();
        final VeiculoAcoplamentoProcessoRealizacao processoRealizacao = AcoesRealizadasCreator.builder()
                .codVeiculos(1L, 2L, 3L)
                .codDiagramasVeiculos(1L, 1L, 1L)
                .motorizados(true, false, false)
                .acoesRealizadas(PERMANECEU, MUDOU_POSICAO, MUDOU_POSICAO)
                .posicoesAcoesRealizadas(1, 2, 3)
                .kmsColetados(1000L, null, null)
                .build(1L);

        final String errorMessage = String.format("N??o ?? poss??vel realizar uma mudan??a de posi????o pois o ve??culo n??o " +
                                                          "est?? acoplado em outra posi????o deste processo." +
                                                          "\nVe??culo: %d." +
                                                          "\nPosi????o acoplado: %d." +
                                                          "\nProcesso editado: %d.", 2, 2, 1);
        validateThrowsWithErrorMessage(dadosBanco, processoRealizacao, errorMessage);
    }

    @Test
    void givenNovoProcessoAcoplamento_whenVeiculosMudaramPosicao_shouldFail() {
        final AcomplamentoValidacaoHolder dadosBanco = VeiculosEstadoBancoCreator.builder()
                .codVeiculos(1L, 2L, 3L)
                .motorizados(true, false, false)
                .possuemHubodometro(false, false, false)
                .posicoesAcoplados(null, null, null)
                .codProcessosAcoplamentosVinculados(null, null, null)
                .build();
        final VeiculoAcoplamentoProcessoRealizacao processoRealizacao = AcoesRealizadasCreator.builder()
                .codVeiculos(1L, 2L, 3L)
                .codDiagramasVeiculos(1L, 1L, 1L)
                .motorizados(true, false, false)
                .acoesRealizadas(MUDOU_POSICAO, MUDOU_POSICAO, MUDOU_POSICAO)
                .posicoesAcoesRealizadas(1, 2, 3)
                .kmsColetados(1000L, null, null)
                .build(null);

        final String errorMessage = String.format("N??o ?? poss??vel mudar de posi????o o ve??culo %d pois nenhum c??digo " +
                                                          "de processo para edi????o foi recebido.", 1);
        validateThrowsWithErrorMessage(dadosBanco, processoRealizacao, errorMessage);
    }
}