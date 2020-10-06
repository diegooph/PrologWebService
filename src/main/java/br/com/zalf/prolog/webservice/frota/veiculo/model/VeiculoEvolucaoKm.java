package br.com.zalf.prolog.webservice.frota.veiculo.model;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;

@Data
public class VeiculoEvolucaoKm {
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
    private final Long diferencaKmAtualkmColetado;
}
