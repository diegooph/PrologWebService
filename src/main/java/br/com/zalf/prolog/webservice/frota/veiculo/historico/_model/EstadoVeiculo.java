package br.com.zalf.prolog.webservice.frota.veiculo.historico._model;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Created on 2020-09-15
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
@Data
public final class EstadoVeiculo {
    @NotNull
    private final Long codVeiculo;
    @Nullable
    private final Long codColaboradorEdicao;
    @Nullable
    private final String nomeColaboradorEdicao;
    @Nullable
    private final String origemEdicao;
    @Nullable
    private final String origemEdicaoLegivel;
    @Nullable
    private final LocalDateTime dataHoraEdicao;
    private final int totalEdicoes;
    @Nullable
    private final String informacoesExtras;
    @NotNull
    private final Map<String, Object> valoresModificaveis;
}
