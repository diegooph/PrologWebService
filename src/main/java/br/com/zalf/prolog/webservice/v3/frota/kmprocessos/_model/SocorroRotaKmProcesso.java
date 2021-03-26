package br.com.zalf.prolog.webservice.v3.frota.kmprocessos._model;

import br.com.zalf.prolog.webservice.v3.frota.kmprocessos.visitor.AlteracaoKmProcesso;
import br.com.zalf.prolog.webservice.v3.frota.kmprocessos.visitor.AlteracaoKmProcessoVisitor;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 2021-03-26
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class SocorroRotaKmProcesso extends AlteracaoKmProcesso {

    public SocorroRotaKmProcesso(@NotNull final Long codEmpresa,
                                 @NotNull final Long codProcesso,
                                 final long novoKm) {
        super(codEmpresa, codProcesso, novoKm);
    }

    @Override
    protected void accept(@NotNull final AlteracaoKmProcessoVisitor visitor) {
        visitor.visitSocorroRota(this);
    }
}
