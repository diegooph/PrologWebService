package br.com.zalf.prolog.webservice.v3.frota.kmprocessos._model;

import br.com.zalf.prolog.webservice.frota.veiculo.model.VeiculoTipoProcesso;
import br.com.zalf.prolog.webservice.v3.frota.kmprocessos.visitor.AlteracaoKmProcessoVisitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 2021-03-25
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class ChecklistKmProcesso extends AlteracaoKmProcesso {

    public ChecklistKmProcesso(@NotNull final Long codEmpresa,
                               @NotNull final Long codVeiculo,
                               @NotNull final Long codProcesso,
                               @NotNull final VeiculoTipoProcesso tipoProcesso,
                               @Nullable final Long codColaboradorAlteracao,
                               final long novoKm) {
        super(codEmpresa, codVeiculo, codProcesso, tipoProcesso, codColaboradorAlteracao, novoKm);
    }

    @NotNull
    @Override
    public AlteracaoKmResponse accept(@NotNull final AlteracaoKmProcessoVisitor visitor) {
        return visitor.visit(this);
    }
}
