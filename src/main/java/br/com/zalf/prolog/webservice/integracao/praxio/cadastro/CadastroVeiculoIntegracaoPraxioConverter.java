package br.com.zalf.prolog.webservice.integracao.praxio.cadastro;

import br.com.zalf.prolog.webservice.frota.veiculo.transferencia.model.realizacao.ProcessoTransferenciaVeiculoRealizacao;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

/**
 * Created on 05/07/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class CadastroVeiculoIntegracaoPraxioConverter {

    private CadastroVeiculoIntegracaoPraxioConverter() {
        throw new IllegalStateException(
                CadastroVeiculoIntegracaoPraxioConverter.class.getSimpleName() + " cannot be instantiated");
    }

    public static ProcessoTransferenciaVeiculoRealizacao convert(
            @NotNull final Long codColaborador,
            @NotNull final Long codVeiculo,
            @NotNull final VeiculoTransferenciaPraxio veiculoTransferenciaPraxio) {
        return new ProcessoTransferenciaVeiculoRealizacao(
                veiculoTransferenciaPraxio.getCodUnidadeOrigem(),
                veiculoTransferenciaPraxio.getCodUnidadeDestino(),
                codColaborador,
                Collections.singletonList(codVeiculo),
                "Transferência realizada através do Sistema Globus");
    }
}
