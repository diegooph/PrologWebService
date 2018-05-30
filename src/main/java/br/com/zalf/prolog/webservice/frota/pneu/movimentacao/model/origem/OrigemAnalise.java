package br.com.zalf.prolog.webservice.frota.pneu.movimentacao.model.origem;

import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.model.OrigemDestinoConstants;

/**
 * Created by Zart on 02/03/17.
 */
public final class OrigemAnalise extends Origem {

    private Long codigoTipoServicoRecapadora;

    public OrigemAnalise() {
        super(OrigemDestinoConstants.ANALISE);
    }

    public Long getCodigoTipoServicoRecapadora() {
        return codigoTipoServicoRecapadora;
    }

    public void setCodigoTipoServicoRecapadora(final Long codigoTipoServicoRecapadora) {
        this.codigoTipoServicoRecapadora = codigoTipoServicoRecapadora;
    }

    @Override
    public String toString() {
        return "OrigemAnalise{" +
                "codigoTipoServicoRecapadora=" + codigoTipoServicoRecapadora +
                '}';
    }
}
