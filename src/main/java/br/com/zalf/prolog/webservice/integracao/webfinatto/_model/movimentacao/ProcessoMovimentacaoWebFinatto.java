package br.com.zalf.prolog.webservice.integracao.webfinatto._model.movimentacao;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProcessoMovimentacaoWebFinatto {
    @NotNull
    private final String codEmpresa;
    @NotNull
    private final String codFilial;
    @NotNull
    private final String cpfColaboradorMovimentacao;
    @NotNull
    private final LocalDateTime dataHoraMovimentacaoUtc;
    @NotNull
    private final LocalDateTime dataHoraMovimentacaoTimeZoneAplicado;
    @Nullable
    private final String observacaoProcessoMovimentacao;
    @Nullable
    private final String respostasCamposPersonalizados;
    @NotNull
    private final VeiculoMovimentacaoWebFinatto veiculoMovimentacao;
    @NotNull
    private final List<MovimentacaoWebFinatto> movimentacoes;
}
