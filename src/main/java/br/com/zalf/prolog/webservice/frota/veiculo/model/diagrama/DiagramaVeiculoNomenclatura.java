package br.com.zalf.prolog.webservice.frota.veiculo.model.diagrama;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Data
public final class DiagramaVeiculoNomenclatura {
    @NotNull
    private final Short codigo;
    @NotNull
    private final String nome;
    @NotNull
    private final List<DiagramaVeiculoPosicaoNomenclatura> nomenclaturasPosicoes;
}