package br.com.zalf.prolog.webservice.v3.frota.kmprocessos._model;

import br.com.zalf.prolog.webservice.frota.veiculo.model.VeiculoTipoProcesso;
import br.com.zalf.prolog.webservice.v3.frota.kmprocessos.visitor.AlteracaoKmProcessoVisitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 2021-03-26
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class TransferenciaVeiculoKmProcesso extends AlteracaoKmProcesso {

    public TransferenciaVeiculoKmProcesso(@NotNull final Long codEmpresa,
                                          @NotNull final Long codProcesso,
                                          @NotNull final VeiculoTipoProcesso tipoProcesso,
                                          @Nullable final Long codColaboradorAlteracao,
                                          final long novoKm) {
        super(codEmpresa, codProcesso, tipoProcesso, codColaboradorAlteracao, novoKm);
    }

    @Override
    public void accept(@NotNull final AlteracaoKmProcessoVisitor visitor) {
        visitor.visit(this);
    }
}
