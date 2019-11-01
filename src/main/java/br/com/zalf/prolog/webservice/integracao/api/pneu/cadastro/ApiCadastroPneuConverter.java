package br.com.zalf.prolog.webservice.integracao.api.pneu.cadastro;

import br.com.zalf.prolog.webservice.frota.pneu.transferencia._model.realizacao.PneuTransferenciaRealizacao;
import br.com.zalf.prolog.webservice.integracao.api.pneu.cadastro.model.ApiPneuTransferencia;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 16/08/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class ApiCadastroPneuConverter {

    private ApiCadastroPneuConverter() {
        throw new IllegalStateException(ApiCadastroPneuConverter.class.getSimpleName() + " cannot be instantiated!");
    }

    @NotNull
    public static PneuTransferenciaRealizacao convert(@NotNull final Long codColaborador,
                                                      @NotNull final List<Long> codPneusTransferidos,
                                                      @NotNull final ApiPneuTransferencia pneuTransferencia) {
        return new PneuTransferenciaRealizacao(
                pneuTransferencia.getCodUnidadeOrigem(),
                pneuTransferencia.getCodUnidadeDestino(),
                codColaborador,
                codPneusTransferidos,
                "Transferência realizada através do Sistema Integrado");
    }
}
