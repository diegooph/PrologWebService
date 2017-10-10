package br.com.zalf.prolog.webservice.integracao.avacorpavilan;

import javax.annotation.Nonnull;

/**
 * Created on 10/10/17.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class DiagramaVeiculoProviderAvila implements DiagramaVeiculoProvider {

    @Override
    public Long getCodDiagramaBy(@Nonnull String tipoVeiculo) {
        switch (tipoVeiculo) {
            case "CVT":
            case "CT":
                return 1L;
            case "CVTR":
            case "CTR":
                return 2L;
            case "C3RRB":
            case "C3S":
            case "C3":
                return 3L;
            case "CTR2E":
            case "CTR2D":
                return 4L;
            case "C2RRR":
            case "C2RRB":
            case "C2RCB":
            case "C2RCR":
                return 5L;
            default:
                throw new IllegalStateException("Nenhum diagrama condizente à este tipo: "+tipoVeiculo);
        }
    }
}
