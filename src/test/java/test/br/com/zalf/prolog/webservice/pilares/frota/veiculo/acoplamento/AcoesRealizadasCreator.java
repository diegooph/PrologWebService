package test.br.com.zalf.prolog.webservice.pilares.frota.veiculo.acoplamento;

import br.com.zalf.prolog.webservice.frota.veiculo.acoplamento._model.VeiculoAcoplamentoAcaoEnum;
import br.com.zalf.prolog.webservice.frota.veiculo.acoplamento._model.realizacao.VeiculoAcoplamentoAcaoRealizada;
import br.com.zalf.prolog.webservice.frota.veiculo.acoplamento._model.realizacao.VeiculoAcoplamentoProcessoRealizacao;
import org.apache.commons.lang3.ObjectUtils;
import org.jetbrains.annotations.Nullable;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created on 2020-12-18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class AcoesRealizadasCreator {
    private List<Long> codVeiculos;
    private List<Long> codDiagramasVeiculos;
    private List<Short> posicoesAcoesRealizadas;
    private List<Boolean> motorizados;
    private List<VeiculoAcoplamentoAcaoEnum> acoesRealizadas;
    private List<Long> kmsColetados;

    private AcoesRealizadasCreator() {

    }

    @NotNull
    public static AcoesRealizadasCreator builder() {
        return new AcoesRealizadasCreator();
    }

    @NotNull
    public AcoesRealizadasCreator codVeiculos(@NotNull final Long... codVeiculos) {
        this.codVeiculos = Arrays.asList(codVeiculos);
        return this;
    }

    @NotNull
    public AcoesRealizadasCreator codDiagramasVeiculos(
            @NotNull final Long... codDiagramasVeiculos) {
        this.codDiagramasVeiculos = Arrays.asList(codDiagramasVeiculos);
        return this;
    }

    @NotNull
    public AcoesRealizadasCreator posicoesAcoesRealizadas(@NotNull final Integer... posicoesAcoesRealizadas) {
        this.posicoesAcoesRealizadas = Arrays.stream(posicoesAcoesRealizadas)
                .map(Integer::shortValue)
                .collect(Collectors.toList());
        return this;
    }

    @NotNull
    public AcoesRealizadasCreator motorizados(@NotNull final Boolean... motorizados) {
        this.motorizados = Arrays.asList(motorizados);
        return this;
    }

    @NotNull
    public AcoesRealizadasCreator acoesRealizadas(@NotNull final VeiculoAcoplamentoAcaoEnum... acoesRealizadas) {
        this.acoesRealizadas = Arrays.asList(acoesRealizadas);
        return this;
    }

    @NotNull
    public AcoesRealizadasCreator kmsColetados(@NotNull final Long... kmsColetados) {
        this.kmsColetados = Arrays.asList(kmsColetados);
        return this;
    }

    @NotNull
    public VeiculoAcoplamentoProcessoRealizacao build(@Nullable final Long codProcessoAcoplamentoEditado) {
        validaTodosNotNull();
        validaTodosMesmoTamanho();

        final List<VeiculoAcoplamentoAcaoRealizada> acoesRealizadas = new ArrayList<>();
        for (int i = 0; i < codVeiculos.size(); i++) {
            acoesRealizadas.add(VeiculoAcoplamentoAcaoRealizada.builder()
                                        .withCodVeiculo(codVeiculos.get(i))
                                        .withCodDiagramaVeiculo(codDiagramasVeiculos.get(i))
                                        .withMotorizado(motorizados.get(i))
                                        .withAcaoRealizada(this.acoesRealizadas.get(i))
                                        .withPosicaoAcaoRealizada(posicoesAcoesRealizadas.get(i))
                                        .withKmColetado(kmsColetados.get(i))
                                        .build());
        }

        return new VeiculoAcoplamentoProcessoRealizacao(ValidatorTestConstants.COD_UNIDADE_TESTES,
                                                        "Teste Validator",
                                                        acoesRealizadas,
                                                        codProcessoAcoplamentoEditado);
    }

    private void validaTodosNotNull() {
        if (!ObjectUtils.allNotNull(codVeiculos,
                                    codDiagramasVeiculos,
                                    posicoesAcoesRealizadas,
                                    motorizados,
                                    acoesRealizadas,
                                    kmsColetados)) {
            throw new IllegalArgumentException("Todos os atributos precisam ser diferentes de null.");
        }
    }

    private void validaTodosMesmoTamanho() {
        if (codVeiculos.size() != codDiagramasVeiculos.size() ||
                codVeiculos.size() != posicoesAcoesRealizadas.size() ||
                codVeiculos.size() != motorizados.size() ||
                codVeiculos.size() != acoesRealizadas.size() ||
                codVeiculos.size() != kmsColetados.size()) {
            throw new IllegalArgumentException("Todas as listas precisam ser do mesmo tamanho.");
        }
    }
}
