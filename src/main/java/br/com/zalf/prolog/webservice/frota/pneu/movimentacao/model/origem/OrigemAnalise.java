package br.com.zalf.prolog.webservice.frota.pneu.movimentacao.model.origem;

import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.model.OrigemDestinoConstants;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Zart on 02/03/17.
 */
public final class OrigemAnalise extends Origem {

    @Nullable
    private Long codTipoServicoRecapadora;

    public OrigemAnalise() {
        super(OrigemDestinoConstants.ANALISE);
    }

    @Nullable
    public Long getCodTipoServicoRecapadora() {
        return codTipoServicoRecapadora;
    }

    public void setCodTipoServicoRecapadora(@Nullable final Long codTipoServicoRecapadora) {
        this.codTipoServicoRecapadora = codTipoServicoRecapadora;
    }

    @Override
    public String toString() {
        return "OrigemAnalise{" +
                "codTipoServicoRecapadora=" + codTipoServicoRecapadora +
                '}';
    }
}
