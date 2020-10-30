package br.com.zalf.prolog.webservice.frota.veiculo.evolucaoKm._model;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
/**
 * Created on 2020-10-07
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
@Data
public final class VeiculoEvolucaoKm {
    @NotNull
    private final ProcessoEvolucaoKmEnum processo;
    @NotNull
    private final Long codProcesso;
    @NotNull
    private final LocalDateTime dataHora;
    @NotNull
    private final String placa;
    @NotNull
    private final Long kmColetado;
    @Nullable
    private final Long variacaoKmEntreColetas;
    @NotNull
    private final Long diferencaKmAtualKmColetado;
}