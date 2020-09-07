package br.com.zalf.prolog.webservice.frota.veiculo.model.diagrama;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Data
public final class DiagramaVeiculoPosicaoNomenclatura {
    @NotNull
    private final String nomenclatura;
    @Nullable
    private final String codAuxiliar;
    private final int posicaoProlog;
}