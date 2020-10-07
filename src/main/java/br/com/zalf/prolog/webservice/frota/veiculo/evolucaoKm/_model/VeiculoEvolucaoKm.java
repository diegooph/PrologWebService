package br.com.zalf.prolog.webservice.frota.veiculo.evolucaoKm._model;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 2020-10-07
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
import java.time.LocalDateTime;

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
