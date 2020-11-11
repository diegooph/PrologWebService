package br.com.zalf.prolog.webservice.frota.veiculo.acoplamento._model.realizacao;

import br.com.zalf.prolog.webservice.frota.veiculo.acoplamento._model.VeiculoAcoplamentoAcao;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

/**
 * Created on 2020-11-10
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Data
public final class VeiculoAcoplamento {
    @NotNull
    private final Long codVeiculo;
    @NotNull
    private final Long codDiagramaVeiculo;
    @NotNull
    private final Boolean motorizado;
    @NotNull
    private final VeiculoAcoplamentoAcao acaoRealizada;
    @Range(min = 1, max = 6)
    private final short posicaoAcaoRealizada;
    @Nullable
    private final Long kmColetado;

    public boolean coletouKm() {
        return kmColetado != null;
    }
}
