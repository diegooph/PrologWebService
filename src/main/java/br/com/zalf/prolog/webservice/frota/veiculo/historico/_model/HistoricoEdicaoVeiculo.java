package br.com.zalf.prolog.webservice.frota.veiculo.historico._model;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created on 2020-09-14
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
@Data
public final class HistoricoEdicaoVeiculo {
    @Nullable
    private final Long codColaboradorEdicao;
    @Nullable
    private final String nomeColaboradorEdicao;
    @NotNull
    private final OrigemAcaoEnum origemEdicao;
    @NotNull
    private final String origemEdicaoLegivel;
    @NotNull
    private final LocalDateTime dataHoraEdicao;
    @Nullable
    private final String informacoesExtras;
    private final int totalEdicoes;
    @NotNull
    private final List<EdicaoVeiculo> edicoes;
}
