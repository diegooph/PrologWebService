package test.br.com.zalf.prolog.webservice.pilares.frota.veiculo.acoplamento;

import br.com.zalf.prolog.webservice.frota.veiculo.acoplamento._model.realizacao.VeiculoAcoplamentoAcaoRealizada;
import br.com.zalf.prolog.webservice.frota.veiculo.acoplamento._model.realizacao.VeiculoAcoplamentoProcessoRealizacao;
import br.com.zalf.prolog.webservice.frota.veiculo.acoplamento.validator.AcomplamentoValidacaoHolder;
import br.com.zalf.prolog.webservice.frota.veiculo.acoplamento.validator.VeiculoEstadoAcoplamento;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 2020-12-18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class AcoplamentoCreator {
    @NotNull
    private static final Long COD_UNIDADE_TESTES = 5L;

    @NotNull
    public static AcomplamentoValidacaoHolder createAcomplamentoValidacaoHolder(
            @NotNull final VeiculoEstadoAcoplamento... veiculos) {
        return null;
    }

    @NotNull
    public static VeiculoAcoplamentoProcessoRealizacao createAcoesRealizadas(
            @NotNull final Long codProcessoAcoplamentoEditado,
            @NotNull final VeiculoAcoplamentoAcaoRealizada... acoesRealizadas) {
        return null;
    }
}
