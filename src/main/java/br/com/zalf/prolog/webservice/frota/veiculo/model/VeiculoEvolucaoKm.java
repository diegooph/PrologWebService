package br.com.zalf.prolog.webservice.frota.veiculo.model;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

@Data
public class VeiculoEvolucaoKm {
    @NotNull
    private final String processo;
    @NotNull
    private final Long codProcesso;
    @NotNull
    private final LocalDateTime dataHora;
    @NotNull
    private final String placa;
    @NotNull
    private final Long kmColetado;
    @NotNull
    private final Long diferencaKmAtualkmColetado;
}
