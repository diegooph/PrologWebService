package br.com.zalf.prolog.webservice.frota.veiculo.acoplamento.historico._model;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 2020-11-04
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
@Data
public final class VeiculoAcoplamentoHistorico {
    @NotNull
    private final String placa;
    @Nullable
    private final String identificadorFrota;
    @NotNull
    private final Boolean isMotorizado;
    @NotNull
    private final Long km;
    @NotNull
    private final Long codPosicao;
    @NotNull
    private final String nomePosicao;
    @NotNull
    private final String acao;
}