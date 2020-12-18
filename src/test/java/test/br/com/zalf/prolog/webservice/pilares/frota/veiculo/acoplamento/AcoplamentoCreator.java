package test.br.com.zalf.prolog.webservice.pilares.frota.veiculo.acoplamento;

import br.com.zalf.prolog.webservice.frota.veiculo.acoplamento._model.realizacao.VeiculoAcoplamentoAcaoRealizada;
import br.com.zalf.prolog.webservice.frota.veiculo.acoplamento._model.realizacao.VeiculoAcoplamentoProcessoRealizacao;
import br.com.zalf.prolog.webservice.frota.veiculo.acoplamento.validator.AcomplamentoValidacaoHolder;
import br.com.zalf.prolog.webservice.frota.veiculo.acoplamento.validator.VeiculoEstadoAcoplamento;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created on 2020-12-18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class AcoplamentoCreator {

    @NotNull
    public static AcomplamentoValidacaoHolder createAcomplamentoValidacaoHolder(
            @NotNull final List<VeiculoEstadoAcoplamento> veiculos) {
        final Map<Long, VeiculoEstadoAcoplamento> veiculosEstadoAcoplamento = new HashMap<>();
        veiculos.forEach(veiculo -> veiculosEstadoAcoplamento.put(veiculo.getCodVeiculo(), veiculo));
        return new AcomplamentoValidacaoHolder(veiculosEstadoAcoplamento);
    }

    @NotNull
    public static AcomplamentoValidacaoHolder createAcomplamentoValidacaoHolder(
            @NotNull final VeiculoEstadoAcoplamento... veiculos) {
        final Map<Long, VeiculoEstadoAcoplamento> veiculosEstadoAcoplamento = new HashMap<>();
        Arrays.stream(veiculos)
                .forEach(veiculo -> veiculosEstadoAcoplamento.put(veiculo.getCodVeiculo(), veiculo));
        return new AcomplamentoValidacaoHolder(veiculosEstadoAcoplamento);
    }

    @NotNull
    public static VeiculoAcoplamentoProcessoRealizacao createAcoesRealizadas(
            @Nullable final Long codProcessoAcoplamentoEditado,
            @NotNull final VeiculoAcoplamentoAcaoRealizada... acoesRealizadas) {
        return new VeiculoAcoplamentoProcessoRealizacao(ValidatorTestConstants.COD_UNIDADE_TESTES,
                                                        "Teste Validator",
                                                        Arrays.asList(acoesRealizadas),
                                                        codProcessoAcoplamentoEditado);
    }
}
