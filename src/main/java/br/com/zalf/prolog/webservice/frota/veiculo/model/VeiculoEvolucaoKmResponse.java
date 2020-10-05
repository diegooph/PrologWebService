package br.com.zalf.prolog.webservice.frota.veiculo.model;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Data
public class VeiculoEvolucaoKmResponse {
    @NotNull
    private final Long kmAtual;
    @NotNull
    private final List<VeiculoEvolucaoKm> veiculoEvolucaoKms;
}
