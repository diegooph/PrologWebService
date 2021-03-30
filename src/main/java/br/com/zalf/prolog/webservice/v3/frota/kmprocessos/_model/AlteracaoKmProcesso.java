package br.com.zalf.prolog.webservice.v3.frota.kmprocessos._model;

import br.com.zalf.prolog.webservice.frota.veiculo.model.VeiculoTipoProcesso;
import br.com.zalf.prolog.webservice.v3.frota.kmprocessos.visitor.AlteracaoKmProcessoVisitor;
import br.com.zalf.prolog.webservice.v3.frota.kmprocessos.visitor.Visitable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 2021-03-25
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Getter
@Builder(setterPrefix = "with")
@AllArgsConstructor
public class AlteracaoKmProcesso implements Visitable {
    @NotNull
    private final Long codEmpresa;
    @NotNull
    private final Long codVeiculo;
    @NotNull
    private final Long codProcesso;
    @NotNull
    private final VeiculoTipoProcesso tipoProcesso;
    @Nullable
    private final Long codColaboradorAlteracao;
    private final long novoKm;

    @Override
    public @NotNull AlteracaoKmResponse accept(final @NotNull AlteracaoKmProcessoVisitor visitor) {
        return null;
    }
}
