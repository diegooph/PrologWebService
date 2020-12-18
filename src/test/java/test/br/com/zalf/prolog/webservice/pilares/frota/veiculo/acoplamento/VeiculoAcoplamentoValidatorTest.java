package test.br.com.zalf.prolog.webservice.pilares.frota.veiculo.acoplamento;

import br.com.zalf.prolog.webservice.frota.veiculo.acoplamento._model.VeiculoAcoplamentoAcaoEnum;
import br.com.zalf.prolog.webservice.frota.veiculo.acoplamento._model.realizacao.VeiculoAcoplamentoAcaoRealizada;
import br.com.zalf.prolog.webservice.frota.veiculo.acoplamento._model.realizacao.VeiculoAcoplamentoProcessoRealizacao;
import br.com.zalf.prolog.webservice.frota.veiculo.acoplamento.validator.AcomplamentoValidacaoHolder;
import br.com.zalf.prolog.webservice.frota.veiculo.acoplamento.validator.VeiculoAcoplamentoValidator;
import br.com.zalf.prolog.webservice.frota.veiculo.acoplamento.validator.VeiculoAcoplamentoValidatorException;
import br.com.zalf.prolog.webservice.frota.veiculo.acoplamento.validator.VeiculoEstadoAcoplamento;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.*;

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
        final VeiculoAcoplamentoValidator validator =
                new VeiculoAcoplamentoValidator(dadosBanco, processoRealizacao);

        final VeiculoAcoplamentoValidatorException throwable =
                assertThrows(VeiculoAcoplamentoValidatorException.class, validator::validate);
        assertThat(throwable).isNotNull();
        assertThat(throwable.getDetailedMessage()).isEqualToIgnoringCase(errorMessageToValidate);
    }

    @Test
    void givenNovoProcessoAcoplamento_whenInserted_shouldNotFail() {
        final AcomplamentoValidacaoHolder dadosBanco = AcoplamentoCreator.createAcomplamentoValidacaoHolder(
                VeiculoEstadoAcoplamento.builder()
                        .withCodVeiculo(1L)
                        .withMotorizado(true)
                        .withPossuiHubodometro(false)
                        .build(),
                VeiculoEstadoAcoplamento.builder()
                        .withCodVeiculo(2L)
                        .withMotorizado(false)
                        .withPossuiHubodometro(false)
                        .build());
        final VeiculoAcoplamentoProcessoRealizacao processoRealizacao = AcoplamentoCreator.createAcoesRealizadas(
                null,
                VeiculoAcoplamentoAcaoRealizada.builder()
                        .withCodVeiculo(1L)
                        .withCodDiagramaVeiculo(1L)
                        .withMotorizado(true)
                        .withAcaoRealizada(VeiculoAcoplamentoAcaoEnum.ACOPLADO)
                        .withPosicaoAcaoRealizada((short) 1)
                        .withKmColetado(1000L)
                        .build(),
                VeiculoAcoplamentoAcaoRealizada.builder()
                        .withCodVeiculo(2L)
                        .withCodDiagramaVeiculo(1L)
                        .withMotorizado(false)
                        .withAcaoRealizada(VeiculoAcoplamentoAcaoEnum.ACOPLADO)
                        .withPosicaoAcaoRealizada((short) 2)
                        .withKmColetado(null)
                        .build());
        validateDoesNotThrow(dadosBanco, processoRealizacao);
    }

    @Test
    void givenEdicaoProcessoAcoplamento_whenNadaMudou_shouldNotFail() {
        final AcomplamentoValidacaoHolder dadosBanco = AcoplamentoCreator.createAcomplamentoValidacaoHolder(
                VeiculoEstadoAcoplamento.builder()
                        .withCodVeiculo(1L)
                        .withCodProcessoAcoplamentoVinculado(1L)
                        .withPosicaoAcoplado((short) 1)
                        .withMotorizado(true)
                        .withPossuiHubodometro(false)
                        .build(),
                VeiculoEstadoAcoplamento.builder()
                        .withCodVeiculo(2L)
                        .withCodProcessoAcoplamentoVinculado(1L)
                        .withPosicaoAcoplado((short) 2)
                        .withMotorizado(false)
                        .withPossuiHubodometro(false)
                        .build());

        final VeiculoAcoplamentoProcessoRealizacao processoRealizacao = AcoplamentoCreator.createAcoesRealizadas(
                1L,
                VeiculoAcoplamentoAcaoRealizada.builder()
                        .withCodVeiculo(1L)
                        .withCodDiagramaVeiculo(1L)
                        .withMotorizado(true)
                        .withAcaoRealizada(VeiculoAcoplamentoAcaoEnum.PERMANECEU)
                        .withPosicaoAcaoRealizada((short) 1)
                        .withKmColetado(1000L)
                        .build(),
                VeiculoAcoplamentoAcaoRealizada.builder()
                        .withCodVeiculo(2L)
                        .withCodDiagramaVeiculo(1L)
                        .withMotorizado(false)
                        .withAcaoRealizada(VeiculoAcoplamentoAcaoEnum.PERMANECEU)
                        .withPosicaoAcaoRealizada((short) 2)
                        .withKmColetado(null)
                        .build());
        validateDoesNotThrow(dadosBanco, processoRealizacao);
    }

    @Test
    void validate_DesacoplamentoCompleto_WhenTodasVeiculosDesacoplados() {
        final AcomplamentoValidacaoHolder dadosBanco = AcoplamentoCreator.createAcomplamentoValidacaoHolder(
                VeiculoEstadoAcoplamento.builder()
                        .withCodVeiculo(1L)
                        .withCodProcessoAcoplamentoVinculado(1L)
                        .withPosicaoAcoplado((short) 1)
                        .withMotorizado(true)
                        .withPossuiHubodometro(false)
                        .build(),
                VeiculoEstadoAcoplamento.builder()
                        .withCodVeiculo(2L)
                        .withCodProcessoAcoplamentoVinculado(1L)
                        .withPosicaoAcoplado((short) 2)
                        .withMotorizado(false)
                        .withPossuiHubodometro(false)
                        .build());

        final VeiculoAcoplamentoProcessoRealizacao processoRealizacao = AcoplamentoCreator.createAcoesRealizadas(
                1L,
                VeiculoAcoplamentoAcaoRealizada.builder()
                        .withCodVeiculo(1L)
                        .withCodDiagramaVeiculo(1L)
                        .withMotorizado(true)
                        .withAcaoRealizada(VeiculoAcoplamentoAcaoEnum.DESACOPLADO)
                        .withPosicaoAcaoRealizada((short) 1)
                        .withKmColetado(1000L)
                        .build(),
                VeiculoAcoplamentoAcaoRealizada.builder()
                        .withCodVeiculo(2L)
                        .withCodDiagramaVeiculo(1L)
                        .withMotorizado(false)
                        .withAcaoRealizada(VeiculoAcoplamentoAcaoEnum.DESACOPLADO)
                        .withPosicaoAcaoRealizada((short) 2)
                        .build());

        validateDoesNotThrow(dadosBanco, processoRealizacao);
    }

    @Test
    void validate_MudouPosicaoAcoplamento_WhenVeiculosMudaramPosicaoMesmoAcoplamento() {
        final List<VeiculoEstadoAcoplamento> veiculosBanco = VeiculosEstadoBancoCreator.builder()
                .codVeiculos(1L, 2L, 3L)
                .codProcessosAcoplamentosVinculados(1L, 1L, 1L)
                .posicoesAcoplados(1, 2, 3)
                .motorizados(true, false, false)
                .build();

        final AcomplamentoValidacaoHolder dadosBanco = AcoplamentoCreator.createAcomplamentoValidacaoHolder(
                VeiculoEstadoAcoplamento.builder()
                        .withCodVeiculo(1L)
                        .withCodProcessoAcoplamentoVinculado(1L)
                        .withPosicaoAcoplado((short) 1)
                        .withMotorizado(true)
                        .withPossuiHubodometro(false)
                        .build(),
                VeiculoEstadoAcoplamento.builder()
                        .withCodVeiculo(2L)
                        .withCodProcessoAcoplamentoVinculado(1L)
                        .withPosicaoAcoplado((short) 2)
                        .withMotorizado(false)
                        .withPossuiHubodometro(false)
                        .build(),
                VeiculoEstadoAcoplamento.builder()
                        .withCodVeiculo(3L)
                        .withCodProcessoAcoplamentoVinculado(1L)
                        .withPosicaoAcoplado((short) 3)
                        .withMotorizado(false)
                        .withPossuiHubodometro(false)
                        .build());

        final VeiculoAcoplamentoProcessoRealizacao processoRealizacao = AcoplamentoCreator.createAcoesRealizadas(
                1L,
                VeiculoAcoplamentoAcaoRealizada.builder()
                        .withCodVeiculo(1L)
                        .withCodDiagramaVeiculo(1L)
                        .withMotorizado(true)
                        .withAcaoRealizada(VeiculoAcoplamentoAcaoEnum.DESACOPLADO)
                        .withPosicaoAcaoRealizada((short) 1)
                        .withKmColetado(1000L)
                        .build(),
                VeiculoAcoplamentoAcaoRealizada.builder()
                        .withCodVeiculo(2L)
                        .withCodDiagramaVeiculo(1L)
                        .withMotorizado(false)
                        .withAcaoRealizada(VeiculoAcoplamentoAcaoEnum.MUDOU_POSICAO)
                        .withPosicaoAcaoRealizada((short) 3)
                        .build(),
                VeiculoAcoplamentoAcaoRealizada.builder()
                        .withCodVeiculo(3L)
                        .withCodDiagramaVeiculo(1L)
                        .withMotorizado(false)
                        .withAcaoRealizada(VeiculoAcoplamentoAcaoEnum.MUDOU_POSICAO)
                        .withPosicaoAcaoRealizada((short) 2)
                        .build());
        validateDoesNotThrow(dadosBanco, processoRealizacao);
    }

    @Test
    void validate_MudouPosicaoAcoplamento_WhenApenasTratorMudouPosicao() {
        final AcomplamentoValidacaoHolder dadosBanco = AcoplamentoCreator.createAcomplamentoValidacaoHolder(
                VeiculoEstadoAcoplamento.builder()
                        .withCodVeiculo(1L)
                        .withCodProcessoAcoplamentoVinculado(1L)
                        .withPosicaoAcoplado((short) 1)
                        .withMotorizado(true)
                        .withPossuiHubodometro(false)
                        .build(),
                VeiculoEstadoAcoplamento.builder()
                        .withCodVeiculo(2L)
                        .withCodProcessoAcoplamentoVinculado(1L)
                        .withPosicaoAcoplado((short) 2)
                        .withMotorizado(false)
                        .withPossuiHubodometro(false)
                        .build(),
                VeiculoEstadoAcoplamento.builder()
                        .withCodVeiculo(3L)
                        .withCodProcessoAcoplamentoVinculado(null)
                        .withPosicaoAcoplado((short) 3)
                        .withMotorizado(true)
                        .withPossuiHubodometro(false)
                        .build());

        final VeiculoAcoplamentoProcessoRealizacao processoRealizacao = AcoplamentoCreator.createAcoesRealizadas(
                1L,
                VeiculoAcoplamentoAcaoRealizada.builder()
                        .withCodVeiculo(1L)
                        .withCodDiagramaVeiculo(1L)
                        .withMotorizado(true)
                        .withAcaoRealizada(VeiculoAcoplamentoAcaoEnum.DESACOPLADO)
                        .withPosicaoAcaoRealizada((short) 1)
                        .withKmColetado(1000L)
                        .build(),
                VeiculoAcoplamentoAcaoRealizada.builder()
                        .withCodVeiculo(2L)
                        .withCodDiagramaVeiculo(1L)
                        .withMotorizado(false)
                        .withAcaoRealizada(VeiculoAcoplamentoAcaoEnum.PERMANECEU)
                        .withPosicaoAcaoRealizada((short) 3)
                        .build(),
                VeiculoAcoplamentoAcaoRealizada.builder()
                        .withCodVeiculo(3L)
                        .withCodDiagramaVeiculo(1L)
                        .withMotorizado(true)
                        .withAcaoRealizada(VeiculoAcoplamentoAcaoEnum.ACOPLADO)
                        .withPosicaoAcaoRealizada((short) 1)
                        .withKmColetado(1500L)
                        .build());
        validateDoesNotThrow(dadosBanco, processoRealizacao);
    }

    @Test
    void validate_DesacoplamentoCompletoAndNovoAcoplamento_WhenTodasVeiculosDesacopladosAndNovosVeiculosAcoplados() {
        final Map<Long, VeiculoEstadoAcoplamento> veiculosEstadoAcoplamento = new HashMap<>();
        final VeiculoEstadoAcoplamento veiculo1 = new VeiculoEstadoAcoplamento(1L,
                                                                               1L,
                                                                               (short) 1,
                                                                               true,
                                                                               false);
        final VeiculoEstadoAcoplamento veiculo2 = new VeiculoEstadoAcoplamento(2L,
                                                                               1L,
                                                                               (short) 2,
                                                                               false,
                                                                               false);
        final VeiculoEstadoAcoplamento veiculo3 = new VeiculoEstadoAcoplamento(3L,
                                                                               null,
                                                                               (short) 0,
                                                                               true,
                                                                               false);
        final VeiculoEstadoAcoplamento veiculo4 = new VeiculoEstadoAcoplamento(4L,
                                                                               null,
                                                                               (short) 0,
                                                                               false,
                                                                               false);
        veiculosEstadoAcoplamento.put(veiculo1.getCodVeiculo(), veiculo1);
        veiculosEstadoAcoplamento.put(veiculo2.getCodVeiculo(), veiculo2);
        veiculosEstadoAcoplamento.put(veiculo3.getCodVeiculo(), veiculo3);
        veiculosEstadoAcoplamento.put(veiculo4.getCodVeiculo(), veiculo4);
        final AcomplamentoValidacaoHolder dadosBanco = new AcomplamentoValidacaoHolder(veiculosEstadoAcoplamento);

        final List<VeiculoAcoplamentoAcaoRealizada> acoesRealizadas = new ArrayList<>();
        acoesRealizadas.add(new VeiculoAcoplamentoAcaoRealizada(1L,
                                                                1L,
                                                                true,
                                                                VeiculoAcoplamentoAcaoEnum.DESACOPLADO,
                                                                (short) 1,
                                                                1000L));
        acoesRealizadas.add(new VeiculoAcoplamentoAcaoRealizada(2L,
                                                                1L,
                                                                false,
                                                                VeiculoAcoplamentoAcaoEnum.DESACOPLADO,
                                                                (short) 2,
                                                                null));
        acoesRealizadas.add(new VeiculoAcoplamentoAcaoRealizada(3L,
                                                                1L,
                                                                true,
                                                                VeiculoAcoplamentoAcaoEnum.ACOPLADO,
                                                                (short) 1,
                                                                1000L));
        acoesRealizadas.add(new VeiculoAcoplamentoAcaoRealizada(4L,
                                                                1L,
                                                                false,
                                                                VeiculoAcoplamentoAcaoEnum.ACOPLADO,
                                                                (short) 2,
                                                                null));
        final VeiculoAcoplamentoProcessoRealizacao processoRealizacao =
                new VeiculoAcoplamentoProcessoRealizacao(5L,
                                                         "observação qualquer",
                                                         acoesRealizadas,
                                                         1L);
        validateDoesNotThrow(dadosBanco, processoRealizacao);
    }

    @Test
    void validate_NovoAcoplamentoReboque_WhenAcoplamentoSemTrator() {
        final Map<Long, VeiculoEstadoAcoplamento> veiculosEstadoAcoplamento = new HashMap<>();
        final VeiculoEstadoAcoplamento veiculo1 = new VeiculoEstadoAcoplamento(1L,
                                                                               null,
                                                                               (short) 0,
                                                                               false,
                                                                               false);
        final VeiculoEstadoAcoplamento veiculo2 = new VeiculoEstadoAcoplamento(2L,
                                                                               null,
                                                                               (short) 0,
                                                                               false,
                                                                               false);
        veiculosEstadoAcoplamento.put(veiculo1.getCodVeiculo(), veiculo1);
        veiculosEstadoAcoplamento.put(veiculo2.getCodVeiculo(), veiculo2);
        final AcomplamentoValidacaoHolder dadosBanco = new AcomplamentoValidacaoHolder(veiculosEstadoAcoplamento);

        final List<VeiculoAcoplamentoAcaoRealizada> acoesRealizadas = new ArrayList<>();
        acoesRealizadas.add(new VeiculoAcoplamentoAcaoRealizada(1L,
                                                                1L,
                                                                false,
                                                                VeiculoAcoplamentoAcaoEnum.ACOPLADO,
                                                                (short) 2,
                                                                null));
        acoesRealizadas.add(new VeiculoAcoplamentoAcaoRealizada(2L,
                                                                1L,
                                                                false,
                                                                VeiculoAcoplamentoAcaoEnum.ACOPLADO,
                                                                (short) 3,
                                                                null));
        final VeiculoAcoplamentoProcessoRealizacao processoRealizacao =
                new VeiculoAcoplamentoProcessoRealizacao(5L,
                                                         "observação qualquer",
                                                         acoesRealizadas,
                                                         null);
        validateDoesNotThrow(dadosBanco, processoRealizacao);
    }

    @Test
    void validate_ThrowsException_WhenQtdVeiculosRecebidosDiferenteQtdVeiculosBanco() {
        final Map<Long, VeiculoEstadoAcoplamento> veiculosEstadoAcoplamento = new HashMap<>();
        final VeiculoEstadoAcoplamento veiculo1 = new VeiculoEstadoAcoplamento(1L,
                                                                               1L,
                                                                               (short) 1,
                                                                               true,
                                                                               false);
        final VeiculoEstadoAcoplamento veiculo2 = new VeiculoEstadoAcoplamento(2L,
                                                                               1L,
                                                                               (short) 2,
                                                                               false,
                                                                               false);
        veiculosEstadoAcoplamento.put(veiculo1.getCodVeiculo(), veiculo1);
        veiculosEstadoAcoplamento.put(veiculo2.getCodVeiculo(), veiculo2);
        final AcomplamentoValidacaoHolder dadosBanco = new AcomplamentoValidacaoHolder(veiculosEstadoAcoplamento);

        final VeiculoAcoplamentoProcessoRealizacao processoRealizacao =
                new VeiculoAcoplamentoProcessoRealizacao(5L,
                                                         "observação qualquer",
                                                         Collections.emptyList(),
                                                         1L);
        final VeiculoAcoplamentoValidator validator =
                new VeiculoAcoplamentoValidator(dadosBanco, processoRealizacao);

        final VeiculoAcoplamentoValidatorException throwable =
                assertThrows(VeiculoAcoplamentoValidatorException.class, validator::validate);
        assertThat(throwable).isNotNull();
        assertThat(throwable.getDetailedMessage())
                .isEqualToIgnoringCase("Total de veículos no processo (0) está diferente do total de veículos " +
                                               "buscados do banco (2).");
    }

    @Test
    void validate_ThrowsException_WhenVeiculosRecebidosDiferentesVeiculosBanco() {
        final Map<Long, VeiculoEstadoAcoplamento> veiculosEstadoAcoplamento = new HashMap<>();
        final VeiculoEstadoAcoplamento veiculo1 = new VeiculoEstadoAcoplamento(1L,
                                                                               1L,
                                                                               (short) 1,
                                                                               true,
                                                                               false);
        final VeiculoEstadoAcoplamento veiculo2 = new VeiculoEstadoAcoplamento(2L,
                                                                               1L,
                                                                               (short) 2,
                                                                               false,
                                                                               false);
        veiculosEstadoAcoplamento.put(veiculo1.getCodVeiculo(), veiculo1);
        veiculosEstadoAcoplamento.put(veiculo2.getCodVeiculo(), veiculo2);
        final AcomplamentoValidacaoHolder dadosBanco =
                new AcomplamentoValidacaoHolder(veiculosEstadoAcoplamento);

        final List<VeiculoAcoplamentoAcaoRealizada> acoesRealizadas = new ArrayList<>();
        acoesRealizadas.add(new VeiculoAcoplamentoAcaoRealizada(1L,
                                                                1L,
                                                                true,
                                                                VeiculoAcoplamentoAcaoEnum.PERMANECEU,
                                                                (short) 1,
                                                                1000L));
        acoesRealizadas.add(new VeiculoAcoplamentoAcaoRealizada(4L,
                                                                1L,
                                                                false,
                                                                VeiculoAcoplamentoAcaoEnum.PERMANECEU,
                                                                (short) 1,
                                                                null));

        final VeiculoAcoplamentoProcessoRealizacao processoRealizacao =
                new VeiculoAcoplamentoProcessoRealizacao(5L,
                                                         "observação qualquer",
                                                         acoesRealizadas,
                                                         1L);
        final VeiculoAcoplamentoValidator validator =
                new VeiculoAcoplamentoValidator(dadosBanco, processoRealizacao);

        final VeiculoAcoplamentoValidatorException throwable =
                assertThrows(VeiculoAcoplamentoValidatorException.class, validator::validate);
        assertThat(throwable).isNotNull();
        assertThat(throwable.getDetailedMessage())
                .isEqualToIgnoringCase("Veículos no processo (" + processoRealizacao.getCodVeiculosProcesso() +
                                               ") divergem dos veículos buscados do banco ("
                                               + dadosBanco.getCodVeiculos() + ").");
    }

    @Test
    void validate_ThrowsException_WhenVeiculosRecebidosRepetidosEmDiferentesProcessos() {
        final Map<Long, VeiculoEstadoAcoplamento> veiculosEstadoAcoplamento = new HashMap<>();
        final VeiculoEstadoAcoplamento veiculo1 = new VeiculoEstadoAcoplamento(1L,
                                                                               1L,
                                                                               (short) 1,
                                                                               true,
                                                                               false);
        final VeiculoEstadoAcoplamento veiculo2 = new VeiculoEstadoAcoplamento(2L,
                                                                               1L,
                                                                               (short) 2,
                                                                               false,
                                                                               false);
        veiculosEstadoAcoplamento.put(veiculo1.getCodVeiculo(), veiculo1);
        veiculosEstadoAcoplamento.put(veiculo2.getCodVeiculo(), veiculo2);
        final AcomplamentoValidacaoHolder dadosBanco =
                new AcomplamentoValidacaoHolder(veiculosEstadoAcoplamento);

        final List<VeiculoAcoplamentoAcaoRealizada> acoesRealizadas = new ArrayList<>();
        acoesRealizadas.add(new VeiculoAcoplamentoAcaoRealizada(1L,
                                                                1L,
                                                                true,
                                                                VeiculoAcoplamentoAcaoEnum.PERMANECEU,
                                                                (short) 1,
                                                                1000L));
        acoesRealizadas.add(new VeiculoAcoplamentoAcaoRealizada(2L,
                                                                1L,
                                                                false,
                                                                VeiculoAcoplamentoAcaoEnum.PERMANECEU,
                                                                (short) 2,
                                                                null));
        acoesRealizadas.add(new VeiculoAcoplamentoAcaoRealizada(2L,
                                                                1L,
                                                                false,
                                                                VeiculoAcoplamentoAcaoEnum.PERMANECEU,
                                                                (short) 3,
                                                                null));

        final VeiculoAcoplamentoProcessoRealizacao processoRealizacao =
                new VeiculoAcoplamentoProcessoRealizacao(5L,
                                                         "observação qualquer",
                                                         acoesRealizadas,
                                                         1L);
        final VeiculoAcoplamentoValidator validator =
                new VeiculoAcoplamentoValidator(dadosBanco, processoRealizacao);

        final VeiculoAcoplamentoValidatorException throwable =
                assertThrows(VeiculoAcoplamentoValidatorException.class, validator::validate);
        assertThat(throwable).isNotNull();
        assertThat(throwable.getDetailedMessage())
                .isEqualToIgnoringCase("Não podem existir veículos duplicados no acoplamento.");
    }

    @Test
    void validate_ThrowsException_WhenPosicoesComGaps() {
        final Map<Long, VeiculoEstadoAcoplamento> veiculosEstadoAcoplamento = new HashMap<>();
        final VeiculoEstadoAcoplamento veiculo1 = new VeiculoEstadoAcoplamento(1L,
                                                                               null,
                                                                               (short) 0,
                                                                               true,
                                                                               false);
        final VeiculoEstadoAcoplamento veiculo2 = new VeiculoEstadoAcoplamento(2L,
                                                                               null,
                                                                               (short) 0,
                                                                               false,
                                                                               false);
        veiculosEstadoAcoplamento.put(veiculo1.getCodVeiculo(), veiculo1);
        veiculosEstadoAcoplamento.put(veiculo2.getCodVeiculo(), veiculo2);
        final AcomplamentoValidacaoHolder dadosBanco =
                new AcomplamentoValidacaoHolder(veiculosEstadoAcoplamento);

        final List<VeiculoAcoplamentoAcaoRealizada> acoesRealizadas = new ArrayList<>();
        acoesRealizadas.add(new VeiculoAcoplamentoAcaoRealizada(1L,
                                                                1L,
                                                                true,
                                                                VeiculoAcoplamentoAcaoEnum.PERMANECEU,
                                                                (short) 1,
                                                                1000L));
        acoesRealizadas.add(new VeiculoAcoplamentoAcaoRealizada(2L,
                                                                1L,
                                                                false,
                                                                VeiculoAcoplamentoAcaoEnum.PERMANECEU,
                                                                (short) 3,
                                                                null));

        final VeiculoAcoplamentoProcessoRealizacao processoRealizacao =
                new VeiculoAcoplamentoProcessoRealizacao(5L,
                                                         "observação qualquer",
                                                         acoesRealizadas,
                                                         null);
        final VeiculoAcoplamentoValidator validator =
                new VeiculoAcoplamentoValidator(dadosBanco, processoRealizacao);

        final VeiculoAcoplamentoValidatorException throwable =
                assertThrows(VeiculoAcoplamentoValidatorException.class, validator::validate);
        assertThat(throwable).isNotNull();
        assertThat(throwable.getDetailedMessage())
                .isEqualToIgnoringCase("A ordem do acoplamento não está correta, devem ser sequenciais: "
                                               + processoRealizacao.getPosicoesOrdenadas());
    }

    @Test
    void validate_ThrowsException_WhenVeiculosMudaramPosicaoDeixandoGaps() {
        final Map<Long, VeiculoEstadoAcoplamento> veiculosEstadoAcoplamento = new HashMap<>();
        final VeiculoEstadoAcoplamento veiculo1 = new VeiculoEstadoAcoplamento(1L,
                                                                               1L,
                                                                               (short) 1,
                                                                               true,
                                                                               false);
        final VeiculoEstadoAcoplamento veiculo2 = new VeiculoEstadoAcoplamento(2L,
                                                                               1L,
                                                                               (short) 2,
                                                                               false,
                                                                               false);
        final VeiculoEstadoAcoplamento veiculo3 = new VeiculoEstadoAcoplamento(3L,
                                                                               1L,
                                                                               (short) 3,
                                                                               false,
                                                                               false);
        veiculosEstadoAcoplamento.put(veiculo1.getCodVeiculo(), veiculo1);
        veiculosEstadoAcoplamento.put(veiculo2.getCodVeiculo(), veiculo2);
        veiculosEstadoAcoplamento.put(veiculo3.getCodVeiculo(), veiculo3);
        final AcomplamentoValidacaoHolder dadosBanco = new AcomplamentoValidacaoHolder(veiculosEstadoAcoplamento);

        final List<VeiculoAcoplamentoAcaoRealizada> acoesRealizadas = new ArrayList<>();
        acoesRealizadas.add(new VeiculoAcoplamentoAcaoRealizada(1L,
                                                                1L,
                                                                true,
                                                                VeiculoAcoplamentoAcaoEnum.PERMANECEU,
                                                                (short) 1,
                                                                1000L));
        acoesRealizadas.add(new VeiculoAcoplamentoAcaoRealizada(2L,
                                                                1L,
                                                                false,
                                                                VeiculoAcoplamentoAcaoEnum.MUDOU_POSICAO,
                                                                (short) 3,
                                                                null));
        acoesRealizadas.add(new VeiculoAcoplamentoAcaoRealizada(3L,
                                                                1L,
                                                                false,
                                                                VeiculoAcoplamentoAcaoEnum.DESACOPLADO,
                                                                (short) 2,
                                                                null));
        final VeiculoAcoplamentoProcessoRealizacao processoRealizacao =
                new VeiculoAcoplamentoProcessoRealizacao(5L,
                                                         "observação qualquer",
                                                         acoesRealizadas,
                                                         1L);
        final VeiculoAcoplamentoValidator validator = new VeiculoAcoplamentoValidator(dadosBanco, processoRealizacao);
        final VeiculoAcoplamentoValidatorException throwable =
                assertThrows(VeiculoAcoplamentoValidatorException.class, validator::validate);
        assertThat(throwable).isNotNull();
        assertThat(throwable.getDetailedMessage())
                .isEqualToIgnoringCase("A ordem do acoplamento não está correta, devem ser sequenciais: "
                                               + processoRealizacao.getPosicoesOrdenadas());
    }

    @Test
    void validate_ThrowsException_WhenVariosVeiculoMotorizadoAcoplados() {
        final Map<Long, VeiculoEstadoAcoplamento> veiculosEstadoAcoplamento = new HashMap<>();
        final VeiculoEstadoAcoplamento veiculo1 = new VeiculoEstadoAcoplamento(1L,
                                                                               null,
                                                                               (short) 0,
                                                                               true,
                                                                               false);
        final VeiculoEstadoAcoplamento veiculo2 = new VeiculoEstadoAcoplamento(2L,
                                                                               null,
                                                                               (short) 0,
                                                                               true,
                                                                               false);
        veiculosEstadoAcoplamento.put(veiculo1.getCodVeiculo(), veiculo1);
        veiculosEstadoAcoplamento.put(veiculo2.getCodVeiculo(), veiculo2);
        final AcomplamentoValidacaoHolder dadosBanco =
                new AcomplamentoValidacaoHolder(veiculosEstadoAcoplamento);

        final List<VeiculoAcoplamentoAcaoRealizada> acoesRealizadas = new ArrayList<>();
        acoesRealizadas.add(new VeiculoAcoplamentoAcaoRealizada(1L,
                                                                1L,
                                                                true,
                                                                VeiculoAcoplamentoAcaoEnum.PERMANECEU,
                                                                (short) 1,
                                                                1000L));
        acoesRealizadas.add(new VeiculoAcoplamentoAcaoRealizada(2L,
                                                                1L,
                                                                true,
                                                                VeiculoAcoplamentoAcaoEnum.PERMANECEU,
                                                                (short) 2,
                                                                1000L));

        final VeiculoAcoplamentoProcessoRealizacao processoRealizacao =
                new VeiculoAcoplamentoProcessoRealizacao(5L,
                                                         "observação qualquer",
                                                         acoesRealizadas,
                                                         null);
        final VeiculoAcoplamentoValidator validator =
                new VeiculoAcoplamentoValidator(dadosBanco, processoRealizacao);

        final VeiculoAcoplamentoValidatorException throwable =
                assertThrows(VeiculoAcoplamentoValidatorException.class, validator::validate);
        assertThat(throwable).isNotNull();
        assertThat(throwable.getDetailedMessage())
                .isEqualToIgnoringCase("É permitido apenas um veículo motorizado no acoplamento, encontrados: "
                                               + processoRealizacao.getCodVeiculosMotorizadosProcesso());
    }

    @Test
    void validate_ThrowsException_WhenVeiculoMotorizadoNaPosicaoErrada() {
        final Map<Long, VeiculoEstadoAcoplamento> veiculosEstadoAcoplamento = new HashMap<>();
        final VeiculoEstadoAcoplamento veiculo1 = new VeiculoEstadoAcoplamento(1L,
                                                                               null,
                                                                               (short) 0,
                                                                               true,
                                                                               false);
        final VeiculoEstadoAcoplamento veiculo2 = new VeiculoEstadoAcoplamento(2L,
                                                                               null,
                                                                               (short) 0,
                                                                               true,
                                                                               false);
        veiculosEstadoAcoplamento.put(veiculo1.getCodVeiculo(), veiculo1);
        veiculosEstadoAcoplamento.put(veiculo2.getCodVeiculo(), veiculo2);
        final AcomplamentoValidacaoHolder dadosBanco =
                new AcomplamentoValidacaoHolder(veiculosEstadoAcoplamento);

        final List<VeiculoAcoplamentoAcaoRealizada> acoesRealizadas = new ArrayList<>();
        acoesRealizadas.add(new VeiculoAcoplamentoAcaoRealizada(1L,
                                                                1L,
                                                                true,
                                                                VeiculoAcoplamentoAcaoEnum.ACOPLADO,
                                                                (short) 2,
                                                                1000L));
        acoesRealizadas.add(new VeiculoAcoplamentoAcaoRealizada(2L,
                                                                1L,
                                                                false,
                                                                VeiculoAcoplamentoAcaoEnum.ACOPLADO,
                                                                (short) 3,
                                                                null));

        final VeiculoAcoplamentoProcessoRealizacao processoRealizacao =
                new VeiculoAcoplamentoProcessoRealizacao(5L,
                                                         "observação qualquer",
                                                         acoesRealizadas,
                                                         null);
        final VeiculoAcoplamentoValidator validator =
                new VeiculoAcoplamentoValidator(dadosBanco, processoRealizacao);

        final VeiculoAcoplamentoValidatorException throwable =
                assertThrows(VeiculoAcoplamentoValidatorException.class, validator::validate);
        assertThat(throwable).isNotNull();
        assertThat(throwable.getDetailedMessage())
                .isEqualToIgnoringCase("O trator não pode ser aplicado numa posição que não é a 1.");
    }

    @Test
    void validate_ThrowsException_WhenReboquesNasPosicoesErradas() {
        final Map<Long, VeiculoEstadoAcoplamento> veiculosEstadoAcoplamento = new HashMap<>();
        final VeiculoEstadoAcoplamento veiculo1 = new VeiculoEstadoAcoplamento(1L,
                                                                               null,
                                                                               (short) 0,
                                                                               true,
                                                                               false);
        final VeiculoEstadoAcoplamento veiculo2 = new VeiculoEstadoAcoplamento(2L,
                                                                               null,
                                                                               (short) 0,
                                                                               true,
                                                                               false);
        veiculosEstadoAcoplamento.put(veiculo1.getCodVeiculo(), veiculo1);
        veiculosEstadoAcoplamento.put(veiculo2.getCodVeiculo(), veiculo2);
        final AcomplamentoValidacaoHolder dadosBanco =
                new AcomplamentoValidacaoHolder(veiculosEstadoAcoplamento);

        final List<VeiculoAcoplamentoAcaoRealizada> acoesRealizadas = new ArrayList<>();
        acoesRealizadas.add(new VeiculoAcoplamentoAcaoRealizada(1L,
                                                                1L,
                                                                true,
                                                                VeiculoAcoplamentoAcaoEnum.DESACOPLADO,
                                                                (short) 1,
                                                                1000L));
        acoesRealizadas.add(new VeiculoAcoplamentoAcaoRealizada(2L,
                                                                1L,
                                                                false,
                                                                VeiculoAcoplamentoAcaoEnum.ACOPLADO,
                                                                (short) 1,
                                                                null));

        final VeiculoAcoplamentoProcessoRealizacao processoRealizacao =
                new VeiculoAcoplamentoProcessoRealizacao(5L,
                                                         "observação qualquer",
                                                         acoesRealizadas,
                                                         null);
        final VeiculoAcoplamentoValidator validator =
                new VeiculoAcoplamentoValidator(dadosBanco, processoRealizacao);

        final VeiculoAcoplamentoValidatorException throwable =
                assertThrows(VeiculoAcoplamentoValidatorException.class, validator::validate);
        assertThat(throwable).isNotNull();
        assertThat(throwable.getDetailedMessage())
                .isEqualToIgnoringCase("Os reboques não podem ocupar posições de tratores (1).");
    }

    @Test
    void validate_ThrowsException_WhenVeiculoMotorizadoNaoColetouKm() {
        final Map<Long, VeiculoEstadoAcoplamento> veiculosEstadoAcoplamento = new HashMap<>();
        final VeiculoEstadoAcoplamento veiculo1 = new VeiculoEstadoAcoplamento(1L,
                                                                               null,
                                                                               (short) 0,
                                                                               true,
                                                                               false);
        final VeiculoEstadoAcoplamento veiculo2 = new VeiculoEstadoAcoplamento(2L,
                                                                               null,
                                                                               (short) 0,
                                                                               true,
                                                                               false);
        veiculosEstadoAcoplamento.put(veiculo1.getCodVeiculo(), veiculo1);
        veiculosEstadoAcoplamento.put(veiculo2.getCodVeiculo(), veiculo2);
        final AcomplamentoValidacaoHolder dadosBanco =
                new AcomplamentoValidacaoHolder(veiculosEstadoAcoplamento);

        final List<VeiculoAcoplamentoAcaoRealizada> acoesRealizadas = new ArrayList<>();
        acoesRealizadas.add(new VeiculoAcoplamentoAcaoRealizada(1L,
                                                                1L,
                                                                true,
                                                                VeiculoAcoplamentoAcaoEnum.ACOPLADO,
                                                                (short) 1,
                                                                null));
        acoesRealizadas.add(new VeiculoAcoplamentoAcaoRealizada(2L,
                                                                1L,
                                                                false,
                                                                VeiculoAcoplamentoAcaoEnum.ACOPLADO,
                                                                (short) 2,
                                                                null));

        final VeiculoAcoplamentoProcessoRealizacao processoRealizacao =
                new VeiculoAcoplamentoProcessoRealizacao(5L,
                                                         "observação qualquer",
                                                         acoesRealizadas,
                                                         null);
        final VeiculoAcoplamentoValidator validator =
                new VeiculoAcoplamentoValidator(dadosBanco, processoRealizacao);

        final VeiculoAcoplamentoValidatorException throwable =
                assertThrows(VeiculoAcoplamentoValidatorException.class, validator::validate);
        assertThat(throwable).isNotNull();
        assertThat(throwable.getDetailedMessage())
                .isEqualToIgnoringCase("Não foi realizado a coleta do KM dos tratores.");
    }

    @Test
    void validate_ThrowsException_WhenReboquesComHubodometroNaoColetaramKm() {
        final Map<Long, VeiculoEstadoAcoplamento> veiculosEstadoAcoplamento = new HashMap<>();
        final VeiculoEstadoAcoplamento veiculo1 = new VeiculoEstadoAcoplamento(1L,
                                                                               null,
                                                                               (short) 0,
                                                                               true,
                                                                               false);
        final VeiculoEstadoAcoplamento veiculo2 = new VeiculoEstadoAcoplamento(2L,
                                                                               null,
                                                                               (short) 0,
                                                                               false,
                                                                               true);
        veiculosEstadoAcoplamento.put(veiculo1.getCodVeiculo(), veiculo1);
        veiculosEstadoAcoplamento.put(veiculo2.getCodVeiculo(), veiculo2);
        final AcomplamentoValidacaoHolder dadosBanco =
                new AcomplamentoValidacaoHolder(veiculosEstadoAcoplamento);

        final List<VeiculoAcoplamentoAcaoRealizada> acoesRealizadas = new ArrayList<>();
        acoesRealizadas.add(new VeiculoAcoplamentoAcaoRealizada(1L,
                                                                1L,
                                                                true,
                                                                VeiculoAcoplamentoAcaoEnum.ACOPLADO,
                                                                (short) 1,
                                                                1000L));
        acoesRealizadas.add(new VeiculoAcoplamentoAcaoRealizada(2L,
                                                                1L,
                                                                false,
                                                                VeiculoAcoplamentoAcaoEnum.ACOPLADO,
                                                                (short) 2,
                                                                null));

        final VeiculoAcoplamentoProcessoRealizacao processoRealizacao =
                new VeiculoAcoplamentoProcessoRealizacao(5L,
                                                         "observação qualquer",
                                                         acoesRealizadas,
                                                         null);
        final VeiculoAcoplamentoValidator validator =
                new VeiculoAcoplamentoValidator(dadosBanco, processoRealizacao);

        final VeiculoAcoplamentoValidatorException throwable =
                assertThrows(VeiculoAcoplamentoValidatorException.class, validator::validate);
        assertThat(throwable).isNotNull();
        assertThat(throwable.getDetailedMessage())
                .isEqualToIgnoringCase("Não foi realizado a coleta do KM dos roboques com hubodometro.");
    }

    @Test
    void validate_ThrowsException_WhenVeiculosPertencemProcessoDistintos() {
        final Map<Long, VeiculoEstadoAcoplamento> veiculosEstadoAcoplamento = new HashMap<>();
        final VeiculoEstadoAcoplamento veiculo1 = new VeiculoEstadoAcoplamento(1L,
                                                                               1L,
                                                                               (short) 1,
                                                                               true,
                                                                               false);
        final VeiculoEstadoAcoplamento veiculo2 = new VeiculoEstadoAcoplamento(2L,
                                                                               2L,
                                                                               (short) 2,
                                                                               false,
                                                                               false);
        veiculosEstadoAcoplamento.put(veiculo1.getCodVeiculo(), veiculo1);
        veiculosEstadoAcoplamento.put(veiculo2.getCodVeiculo(), veiculo2);
        final AcomplamentoValidacaoHolder dadosBanco =
                new AcomplamentoValidacaoHolder(veiculosEstadoAcoplamento);

        final List<VeiculoAcoplamentoAcaoRealizada> acoesRealizadas = new ArrayList<>();
        acoesRealizadas.add(new VeiculoAcoplamentoAcaoRealizada(1L,
                                                                1L,
                                                                true,
                                                                VeiculoAcoplamentoAcaoEnum.ACOPLADO,
                                                                (short) 1,
                                                                1000L));
        acoesRealizadas.add(new VeiculoAcoplamentoAcaoRealizada(2L,
                                                                1L,
                                                                false,
                                                                VeiculoAcoplamentoAcaoEnum.ACOPLADO,
                                                                (short) 2,
                                                                null));

        final VeiculoAcoplamentoProcessoRealizacao processoRealizacao =
                new VeiculoAcoplamentoProcessoRealizacao(5L,
                                                         "observação qualquer",
                                                         acoesRealizadas,
                                                         1L);
        final VeiculoAcoplamentoValidator validator =
                new VeiculoAcoplamentoValidator(dadosBanco, processoRealizacao);

        final VeiculoAcoplamentoValidatorException throwable =
                assertThrows(VeiculoAcoplamentoValidatorException.class, validator::validate);
        assertThat(throwable).isNotNull();
        assertThat(throwable.getDetailedMessage())
                .isEqualToIgnoringCase("Os veículos do processo pertencem a diferentes processos de acoplamento: "
                                               + dadosBanco.getCodProcessosAcoplamentosDistintos());
    }

    @Test
    void validate_ThrowsException_WhenVeiculosNaoPercentemProcessoSendoEditado() {
        final Map<Long, VeiculoEstadoAcoplamento> veiculosEstadoAcoplamento = new HashMap<>();
        final VeiculoEstadoAcoplamento veiculo1 = new VeiculoEstadoAcoplamento(1L,
                                                                               2L,
                                                                               (short) 1,
                                                                               true,
                                                                               false);
        final VeiculoEstadoAcoplamento veiculo2 = new VeiculoEstadoAcoplamento(2L,
                                                                               2L,
                                                                               (short) 2,
                                                                               false,
                                                                               false);
        veiculosEstadoAcoplamento.put(veiculo1.getCodVeiculo(), veiculo1);
        veiculosEstadoAcoplamento.put(veiculo2.getCodVeiculo(), veiculo2);
        final AcomplamentoValidacaoHolder dadosBanco =
                new AcomplamentoValidacaoHolder(veiculosEstadoAcoplamento);

        final List<VeiculoAcoplamentoAcaoRealizada> acoesRealizadas = new ArrayList<>();
        acoesRealizadas.add(new VeiculoAcoplamentoAcaoRealizada(1L,
                                                                1L,
                                                                true,
                                                                VeiculoAcoplamentoAcaoEnum.ACOPLADO,
                                                                (short) 1,
                                                                1000L));
        acoesRealizadas.add(new VeiculoAcoplamentoAcaoRealizada(2L,
                                                                1L,
                                                                false,
                                                                VeiculoAcoplamentoAcaoEnum.ACOPLADO,
                                                                (short) 2,
                                                                null));

        final VeiculoAcoplamentoProcessoRealizacao processoRealizacao =
                new VeiculoAcoplamentoProcessoRealizacao(5L,
                                                         "observação qualquer",
                                                         acoesRealizadas,
                                                         1L);
        final VeiculoAcoplamentoValidator validator =
                new VeiculoAcoplamentoValidator(dadosBanco, processoRealizacao);

        final VeiculoAcoplamentoValidatorException throwable =
                assertThrows(VeiculoAcoplamentoValidatorException.class, validator::validate);
        assertThat(throwable).isNotNull();
        assertThat(throwable.getDetailedMessage())
                .isEqualToIgnoringCase("Veículos no BD estão nos processos de acoplamento de códigos "
                                               + dadosBanco.getCodProcessosAcoplamentosDistintos() +
                                               " porém o processo sendo editado é o de código 1.");
    }

    @Test
    void validate_ThrowsException_WhenVeiculosJaAcopladosPresentesEmNovoProcesso() {
        final Map<Long, VeiculoEstadoAcoplamento> veiculosEstadoAcoplamento = new HashMap<>();
        final VeiculoEstadoAcoplamento veiculo1 = new VeiculoEstadoAcoplamento(1L,
                                                                               2L,
                                                                               (short) 1,
                                                                               true,
                                                                               false);
        final VeiculoEstadoAcoplamento veiculo2 = new VeiculoEstadoAcoplamento(2L,
                                                                               2L,
                                                                               (short) 2,
                                                                               false,
                                                                               false);
        veiculosEstadoAcoplamento.put(veiculo1.getCodVeiculo(), veiculo1);
        veiculosEstadoAcoplamento.put(veiculo2.getCodVeiculo(), veiculo2);
        final AcomplamentoValidacaoHolder dadosBanco =
                new AcomplamentoValidacaoHolder(veiculosEstadoAcoplamento);

        final List<VeiculoAcoplamentoAcaoRealizada> acoesRealizadas = new ArrayList<>();
        acoesRealizadas.add(new VeiculoAcoplamentoAcaoRealizada(1L,
                                                                1L,
                                                                true,
                                                                VeiculoAcoplamentoAcaoEnum.ACOPLADO,
                                                                (short) 1,
                                                                1000L));
        acoesRealizadas.add(new VeiculoAcoplamentoAcaoRealizada(2L,
                                                                1L,
                                                                false,
                                                                VeiculoAcoplamentoAcaoEnum.ACOPLADO,
                                                                (short) 2,
                                                                null));

        final VeiculoAcoplamentoProcessoRealizacao processoRealizacao =
                new VeiculoAcoplamentoProcessoRealizacao(5L,
                                                         "observação qualquer",
                                                         acoesRealizadas,
                                                         null);
        final VeiculoAcoplamentoValidator validator =
                new VeiculoAcoplamentoValidator(dadosBanco, processoRealizacao);

        final VeiculoAcoplamentoValidatorException throwable =
                assertThrows(VeiculoAcoplamentoValidatorException.class, validator::validate);
        assertThat(throwable).isNotNull();
        assertThat(throwable.getDetailedMessage())
                .isEqualToIgnoringCase(String.format(
                        "Um novo acoplamento está sendo inserido, porém, algum dos veículos recebidos" +
                                " já está acoplado: %s.", dadosBanco.getCodVeiculos()));
    }

    @Test
    void validateAcao_ThrowsException_WhenVeiculosAcopladosEstaoLivres() {
        final Map<Long, VeiculoEstadoAcoplamento> veiculosEstadoAcoplamento = new HashMap<>();
        final VeiculoEstadoAcoplamento veiculo1 = new VeiculoEstadoAcoplamento(1L,
                                                                               1L,
                                                                               (short) 1,
                                                                               true,
                                                                               false);
        final VeiculoEstadoAcoplamento veiculo2 = new VeiculoEstadoAcoplamento(2L,
                                                                               null,
                                                                               (short) 0,
                                                                               false,
                                                                               false);
        veiculosEstadoAcoplamento.put(veiculo1.getCodVeiculo(), veiculo1);
        veiculosEstadoAcoplamento.put(veiculo2.getCodVeiculo(), veiculo2);
        final AcomplamentoValidacaoHolder dadosBanco =
                new AcomplamentoValidacaoHolder(veiculosEstadoAcoplamento);

        final List<VeiculoAcoplamentoAcaoRealizada> acoesRealizadas = new ArrayList<>();
        acoesRealizadas.add(new VeiculoAcoplamentoAcaoRealizada(1L,
                                                                1L,
                                                                true,
                                                                VeiculoAcoplamentoAcaoEnum.ACOPLADO,
                                                                (short) 1,
                                                                1000L));
        acoesRealizadas.add(new VeiculoAcoplamentoAcaoRealizada(2L,
                                                                1L,
                                                                false,
                                                                VeiculoAcoplamentoAcaoEnum.ACOPLADO,
                                                                (short) 2,
                                                                null));

        final VeiculoAcoplamentoProcessoRealizacao processoRealizacao =
                new VeiculoAcoplamentoProcessoRealizacao(5L,
                                                         "observação qualquer",
                                                         acoesRealizadas,
                                                         1L);
        final VeiculoAcoplamentoValidator validator =
                new VeiculoAcoplamentoValidator(dadosBanco, processoRealizacao);

        final VeiculoAcoplamentoValidatorException throwable =
                assertThrows(VeiculoAcoplamentoValidatorException.class, validator::validate);
        assertThat(throwable).isNotNull();
        assertThat(throwable.getDetailedMessage())
                .isEqualToIgnoringCase("Não é possível acoplar o veículo 1 pois ele já está acoplado.");
    }

    @Test
    void validateAcao_ThrowsException_WhenVeiculosDesacopladosJaEstaoDesacopladosEmEdicaoProcesso() {
        final Map<Long, VeiculoEstadoAcoplamento> veiculosEstadoAcoplamento = new HashMap<>();
        final VeiculoEstadoAcoplamento veiculo1 = new VeiculoEstadoAcoplamento(1L,
                                                                               null,
                                                                               (short) 0,
                                                                               true,
                                                                               false);
        final VeiculoEstadoAcoplamento veiculo2 = new VeiculoEstadoAcoplamento(2L,
                                                                               null,
                                                                               (short) 0,
                                                                               false,
                                                                               false);
        veiculosEstadoAcoplamento.put(veiculo1.getCodVeiculo(), veiculo1);
        veiculosEstadoAcoplamento.put(veiculo2.getCodVeiculo(), veiculo2);
        final AcomplamentoValidacaoHolder dadosBanco =
                new AcomplamentoValidacaoHolder(veiculosEstadoAcoplamento);

        final List<VeiculoAcoplamentoAcaoRealizada> acoesRealizadas = new ArrayList<>();
        acoesRealizadas.add(new VeiculoAcoplamentoAcaoRealizada(1L,
                                                                1L,
                                                                true,
                                                                VeiculoAcoplamentoAcaoEnum.DESACOPLADO,
                                                                (short) 1,
                                                                1000L));
        acoesRealizadas.add(new VeiculoAcoplamentoAcaoRealizada(2L,
                                                                1L,
                                                                false,
                                                                VeiculoAcoplamentoAcaoEnum.DESACOPLADO,
                                                                (short) 2,
                                                                null));

        final VeiculoAcoplamentoProcessoRealizacao processoRealizacao =
                new VeiculoAcoplamentoProcessoRealizacao(5L,
                                                         "observação qualquer",
                                                         acoesRealizadas,
                                                         1L);
        final VeiculoAcoplamentoValidator validator =
                new VeiculoAcoplamentoValidator(dadosBanco, processoRealizacao);

        final VeiculoAcoplamentoValidatorException throwable =
                assertThrows(VeiculoAcoplamentoValidatorException.class, validator::validate);
        assertThat(throwable).isNotNull();
        assertThat(throwable.getDetailedMessage())
                .isEqualToIgnoringCase(String.format(
                        "Não é possível desacoplar um veículo que não está acoplado." +
                                "\nVeículo: %d." +
                                "\nProcesso: %d.", 1, 1));
    }

    @Test
    void validateAcao_ThrowsException_WhenVeiculosDesacopladosEmNovoProcesso() {
        final Map<Long, VeiculoEstadoAcoplamento> veiculosEstadoAcoplamento = new HashMap<>();
        final VeiculoEstadoAcoplamento veiculo1 = new VeiculoEstadoAcoplamento(1L,
                                                                               null,
                                                                               (short) 0,
                                                                               true,
                                                                               false);
        final VeiculoEstadoAcoplamento veiculo2 = new VeiculoEstadoAcoplamento(2L,
                                                                               null,
                                                                               (short) 0,
                                                                               false,
                                                                               false);
        veiculosEstadoAcoplamento.put(veiculo1.getCodVeiculo(), veiculo1);
        veiculosEstadoAcoplamento.put(veiculo2.getCodVeiculo(), veiculo2);
        final AcomplamentoValidacaoHolder dadosBanco =
                new AcomplamentoValidacaoHolder(veiculosEstadoAcoplamento);

        final List<VeiculoAcoplamentoAcaoRealizada> acoesRealizadas = new ArrayList<>();
        acoesRealizadas.add(new VeiculoAcoplamentoAcaoRealizada(1L,
                                                                1L,
                                                                true,
                                                                VeiculoAcoplamentoAcaoEnum.DESACOPLADO,
                                                                (short) 1,
                                                                1000L));
        acoesRealizadas.add(new VeiculoAcoplamentoAcaoRealizada(2L,
                                                                1L,
                                                                false,
                                                                VeiculoAcoplamentoAcaoEnum.DESACOPLADO,
                                                                (short) 2,
                                                                null));

        final VeiculoAcoplamentoProcessoRealizacao processoRealizacao =
                new VeiculoAcoplamentoProcessoRealizacao(5L,
                                                         "observação qualquer",
                                                         acoesRealizadas,
                                                         null);
        final VeiculoAcoplamentoValidator validator =
                new VeiculoAcoplamentoValidator(dadosBanco, processoRealizacao);

        final VeiculoAcoplamentoValidatorException throwable =
                assertThrows(VeiculoAcoplamentoValidatorException.class, validator::validate);
        assertThat(throwable).isNotNull();
        assertThat(throwable.getDetailedMessage())
                .isEqualToIgnoringCase(String.format("Não é possível desacoplar o veículo %d pois nenhum " +
                                                             "código de processo para edição foi recebido.", 1));
    }

    @Test
    void validateAcao_ThrowsException_WhenVeiculosMantidosPosicaoNaoEstaoNaMesmaPosicaoEmEdicaoProcesso() {
        final Map<Long, VeiculoEstadoAcoplamento> veiculosEstadoAcoplamento = new HashMap<>();
        final VeiculoEstadoAcoplamento veiculo1 = new VeiculoEstadoAcoplamento(1L,
                                                                               1L,
                                                                               (short) 1,
                                                                               true,
                                                                               false);
        final VeiculoEstadoAcoplamento veiculo2 = new VeiculoEstadoAcoplamento(2L,
                                                                               1L,
                                                                               (short) 3,
                                                                               false,
                                                                               false);
        final VeiculoEstadoAcoplamento veiculo3 = new VeiculoEstadoAcoplamento(3L,
                                                                               1L,
                                                                               (short) 2,
                                                                               false,
                                                                               false);
        veiculosEstadoAcoplamento.put(veiculo1.getCodVeiculo(), veiculo1);
        veiculosEstadoAcoplamento.put(veiculo2.getCodVeiculo(), veiculo2);
        veiculosEstadoAcoplamento.put(veiculo3.getCodVeiculo(), veiculo3);
        final AcomplamentoValidacaoHolder dadosBanco =
                new AcomplamentoValidacaoHolder(veiculosEstadoAcoplamento);

        final List<VeiculoAcoplamentoAcaoRealizada> acoesRealizadas = new ArrayList<>();
        acoesRealizadas.add(new VeiculoAcoplamentoAcaoRealizada(1L,
                                                                1L,
                                                                true,
                                                                VeiculoAcoplamentoAcaoEnum.PERMANECEU,
                                                                (short) 1,
                                                                1000L));
        acoesRealizadas.add(new VeiculoAcoplamentoAcaoRealizada(2L,
                                                                1L,
                                                                false,
                                                                VeiculoAcoplamentoAcaoEnum.PERMANECEU,
                                                                (short) 2,
                                                                null));
        acoesRealizadas.add(new VeiculoAcoplamentoAcaoRealizada(3L,
                                                                1L,
                                                                false,
                                                                VeiculoAcoplamentoAcaoEnum.PERMANECEU,
                                                                (short) 3,
                                                                null));

        final VeiculoAcoplamentoProcessoRealizacao processoRealizacao =
                new VeiculoAcoplamentoProcessoRealizacao(5L,
                                                         "observação qualquer",
                                                         acoesRealizadas,
                                                         1L);
        final VeiculoAcoplamentoValidator validator =
                new VeiculoAcoplamentoValidator(dadosBanco, processoRealizacao);

        final VeiculoAcoplamentoValidatorException throwable =
                assertThrows(VeiculoAcoplamentoValidatorException.class, validator::validate);
        assertThat(throwable).isNotNull();
        assertThat(throwable.getDetailedMessage())
                .isEqualToIgnoringCase(String.format(
                        "O veículo %d foi mantido na posição %d porém ele não está acoplado nesta posição no processo" +
                                " " +
                                "de código %d.", 2, 2, 1));
    }

    @Test
    void validateAcao_ThrowsException_WhenVeiculosMantidosPosicaoNaoEstaoNaMesmaPosicaoEmNovoProcesso() {
        final Map<Long, VeiculoEstadoAcoplamento> veiculosEstadoAcoplamento = new HashMap<>();
        final VeiculoEstadoAcoplamento veiculo1 = new VeiculoEstadoAcoplamento(1L,
                                                                               null,
                                                                               (short) 0,
                                                                               true,
                                                                               false);
        final VeiculoEstadoAcoplamento veiculo2 = new VeiculoEstadoAcoplamento(2L,
                                                                               null,
                                                                               (short) 0,
                                                                               false,
                                                                               false);
        veiculosEstadoAcoplamento.put(veiculo1.getCodVeiculo(), veiculo1);
        veiculosEstadoAcoplamento.put(veiculo2.getCodVeiculo(), veiculo2);
        final AcomplamentoValidacaoHolder dadosBanco =
                new AcomplamentoValidacaoHolder(veiculosEstadoAcoplamento);

        final List<VeiculoAcoplamentoAcaoRealizada> acoesRealizadas = new ArrayList<>();
        acoesRealizadas.add(new VeiculoAcoplamentoAcaoRealizada(1L,
                                                                1L,
                                                                true,
                                                                VeiculoAcoplamentoAcaoEnum.PERMANECEU,
                                                                (short) 1,
                                                                1000L));
        acoesRealizadas.add(new VeiculoAcoplamentoAcaoRealizada(2L,
                                                                1L,
                                                                false,
                                                                VeiculoAcoplamentoAcaoEnum.PERMANECEU,
                                                                (short) 2,
                                                                null));

        final VeiculoAcoplamentoProcessoRealizacao processoRealizacao =
                new VeiculoAcoplamentoProcessoRealizacao(5L,
                                                         "observação qualquer",
                                                         acoesRealizadas,
                                                         null);
        final VeiculoAcoplamentoValidator validator =
                new VeiculoAcoplamentoValidator(dadosBanco, processoRealizacao);

        final VeiculoAcoplamentoValidatorException throwable =
                assertThrows(VeiculoAcoplamentoValidatorException.class, validator::validate);
        assertThat(throwable).isNotNull();
        assertThat(throwable.getDetailedMessage())
                .isEqualToIgnoringCase(String.format(
                        "Não é possível manter na posição o veículo %d pois nenhum código de processo para edição " +
                                "foi recebido.", 1));
    }

    @Test
    void validateAcao_ThrowsException_WhenVeiculosMudaramPosicaoEstaoEmPosicaoIguaisEmEdicaoProcesso() {
        final Map<Long, VeiculoEstadoAcoplamento> veiculosEstadoAcoplamento = new HashMap<>();
        final VeiculoEstadoAcoplamento veiculo1 = new VeiculoEstadoAcoplamento(1L,
                                                                               1L,
                                                                               (short) 1,
                                                                               true,
                                                                               false);
        final VeiculoEstadoAcoplamento veiculo2 = new VeiculoEstadoAcoplamento(2L,
                                                                               1L,
                                                                               (short) 2,
                                                                               false,
                                                                               false);
        final VeiculoEstadoAcoplamento veiculo3 = new VeiculoEstadoAcoplamento(3L,
                                                                               1L,
                                                                               (short) 3,
                                                                               false,
                                                                               false);
        veiculosEstadoAcoplamento.put(veiculo1.getCodVeiculo(), veiculo1);
        veiculosEstadoAcoplamento.put(veiculo2.getCodVeiculo(), veiculo2);
        veiculosEstadoAcoplamento.put(veiculo3.getCodVeiculo(), veiculo3);
        final AcomplamentoValidacaoHolder dadosBanco = new AcomplamentoValidacaoHolder(veiculosEstadoAcoplamento);

        final List<VeiculoAcoplamentoAcaoRealizada> acoesRealizadas = new ArrayList<>();
        acoesRealizadas.add(new VeiculoAcoplamentoAcaoRealizada(1L,
                                                                1L,
                                                                true,
                                                                VeiculoAcoplamentoAcaoEnum.PERMANECEU,
                                                                (short) 1,
                                                                1000L));
        acoesRealizadas.add(new VeiculoAcoplamentoAcaoRealizada(2L,
                                                                1L,
                                                                false,
                                                                VeiculoAcoplamentoAcaoEnum.MUDOU_POSICAO,
                                                                (short) 2,
                                                                null));
        acoesRealizadas.add(new VeiculoAcoplamentoAcaoRealizada(3L,
                                                                1L,
                                                                false,
                                                                VeiculoAcoplamentoAcaoEnum.MUDOU_POSICAO,
                                                                (short) 3,
                                                                null));

        final VeiculoAcoplamentoProcessoRealizacao processoRealizacao =
                new VeiculoAcoplamentoProcessoRealizacao(5L,
                                                         "observação qualquer",
                                                         acoesRealizadas,
                                                         1L);
        final VeiculoAcoplamentoValidator validator =
                new VeiculoAcoplamentoValidator(dadosBanco, processoRealizacao);

        final VeiculoAcoplamentoValidatorException throwable =
                assertThrows(VeiculoAcoplamentoValidatorException.class, validator::validate);
        assertThat(throwable).isNotNull();
        assertThat(throwable.getDetailedMessage())
                .isEqualToIgnoringCase(String.format(
                        "Não é possível realizar uma mudança de posição pois o veículo não " +
                                "está acoplado em outra posição deste processo." +
                                "\nVeículo: %d." +
                                "\nPosição acoplado: %d." +
                                "\nProcesso editado: %d.", 2, 2, 1));
    }

    @Test
    void validateAcao_ThrowsException_WhenVeiculosMudaramPosicaoEstaoEmPosicaoIguaisEmNovoProcesso() {
        final AcomplamentoValidacaoHolder dadosBanco = AcoplamentoCreator.createAcomplamentoValidacaoHolder(
                VeiculoEstadoAcoplamento.builder()
                        .withCodVeiculo(1L)
                        .withMotorizado(true)
                        .withPossuiHubodometro(false)
                        .build(),
                VeiculoEstadoAcoplamento.builder()
                        .withCodVeiculo(2L)
                        .withMotorizado(false)
                        .withPossuiHubodometro(false)
                        .build(),
                VeiculoEstadoAcoplamento.builder()
                        .withCodVeiculo(3L)
                        .withMotorizado(false)
                        .withPossuiHubodometro(false)
                        .build());

        final VeiculoAcoplamentoProcessoRealizacao processoRealizacao = AcoplamentoCreator.createAcoesRealizadas(
                null,
                VeiculoAcoplamentoAcaoRealizada.builder()
                        .withCodVeiculo(1L)
                        .withCodDiagramaVeiculo(1L)
                        .withMotorizado(true)
                        .withAcaoRealizada(VeiculoAcoplamentoAcaoEnum.MUDOU_POSICAO)
                        .withPosicaoAcaoRealizada((short) 1)
                        .withKmColetado(1000L)
                        .build(),
                VeiculoAcoplamentoAcaoRealizada.builder()
                        .withCodVeiculo(2L)
                        .withCodDiagramaVeiculo(1L)
                        .withMotorizado(false)
                        .withAcaoRealizada(VeiculoAcoplamentoAcaoEnum.MUDOU_POSICAO)
                        .withPosicaoAcaoRealizada((short) 2)
                        .build(),
                VeiculoAcoplamentoAcaoRealizada.builder()
                        .withCodVeiculo(3L)
                        .withCodDiagramaVeiculo(1L)
                        .withMotorizado(false)
                        .withAcaoRealizada(VeiculoAcoplamentoAcaoEnum.MUDOU_POSICAO)
                        .withPosicaoAcaoRealizada((short) 3)
                        .build());
        final String errorMessage = String.format("Não é possível mudar de posição o veículo %d pois nenhum código " +
                                                          "de processo para edição foi recebido.", 1);
        validateThrowsWithErrorMessage(dadosBanco, processoRealizacao, errorMessage);
    }
}