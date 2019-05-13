package br.com.zalf.prolog.webservice.frota.veiculo.transferencia;

import br.com.zalf.prolog.webservice.frota.pneu.transferencia.model.realizacao.PneuTransferenciaRealizacao;
import br.com.zalf.prolog.webservice.frota.veiculo.transferencia.realizacao.VeiculoEnvioTransferencia;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 09/05/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
final class VeiculoTransferenciaConverter {

    private VeiculoTransferenciaConverter() {
        throw new IllegalStateException(VeiculoTransferenciaConverter.class.getSimpleName()
                + "cannot be instantiated!");
    }

    @NotNull
    static PneuTransferenciaRealizacao toPneuTransferenciaRealizacao(
            final long codUnidadeOrigem,
            final long codUnidadeDestino,
            final long codColaboradorRealizacaoTransferencia,
            @NotNull final VeiculoEnvioTransferencia veiculoEnvioTransferencia) {
        final PneuTransferenciaRealizacao pneuTransferenciaRealizacao = new PneuTransferenciaRealizacao();
        pneuTransferenciaRealizacao.setCodUnidadeOrigem(codUnidadeOrigem);
        pneuTransferenciaRealizacao.setCodUnidadeDestino(codUnidadeDestino);
        pneuTransferenciaRealizacao.setCodColaboradorRealizacaoTransferencia(codColaboradorRealizacaoTransferencia);
        pneuTransferenciaRealizacao.setCodPneus(veiculoEnvioTransferencia.getCodPneusAplicadosVeiculo());
        return pneuTransferenciaRealizacao;
    }
}
