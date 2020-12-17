package br.com.zalf.prolog.webservice.frota.veiculo.acoplamento._model.realizacao;

import br.com.zalf.prolog.webservice.frota.veiculo.acoplamento._model.VeiculoAcoplamentoAcaoEnum;
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
public final class VeiculoAcoplamentoAcaoRealizada {
    @NotNull
    public static final short POSICAO_ACOPLAMENTO_TRATOR = 1;
    @NotNull
    private final Long codVeiculo;
    @NotNull
    private final Long codDiagramaVeiculo;
    @NotNull
    private final Boolean motorizado;
    @NotNull
    private final VeiculoAcoplamentoAcaoEnum acaoRealizada;
    @Range(min = 1, max = 6)
    private final short posicaoAcaoRealizada;
    @Nullable
    private final Long kmColetado;

    public boolean foiAcopladoOuMantidoNaComposicao() {
        return acaoRealizada != VeiculoAcoplamentoAcaoEnum.DESACOPLADO;
    }

    public boolean foiAcoplado() {
        return acaoRealizada == VeiculoAcoplamentoAcaoEnum.ACOPLADO;
    }

    public boolean foiDesacoplado() {
        return acaoRealizada == VeiculoAcoplamentoAcaoEnum.DESACOPLADO;
    }

    public boolean foiMantidoPosicao() {
        return acaoRealizada == VeiculoAcoplamentoAcaoEnum.PERMANECEU;
    }

    public boolean foiMudadoPosicao() {
        return acaoRealizada == VeiculoAcoplamentoAcaoEnum.MUDOU_POSICAO;
    }

    public boolean isMotorizado() {
        return motorizado;
    }

    public boolean isRebocado() {
        return !motorizado;
    }

    public boolean naoColetouKm() {
        return kmColetado == null;
    }
}
