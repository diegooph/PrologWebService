package br.com.zalf.prolog.webservice.frota.veiculo.acoplamento._model;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created on 2020-11-04
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
@Data
public final class VeiculoAcoplamentoResponse {
    @NotNull
    private final Long codProcesso;
    @NotNull
    private final String unidade;
    @NotNull
    private final String colaborador;
    @Nullable
    private final LocalDateTime dataHora;
    @Nullable
    private final String observacao;
    @NotNull
    private final List<VeiculoAcoplamento> veiculoAcoplamentos;
}