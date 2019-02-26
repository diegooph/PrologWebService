package br.com.zalf.prolog.webservice.gente.controlejornada.relatorios.model.jornada;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 09/01/19
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public class FolhaPontoJornada {
    @Nullable
    private final LocalDateTime dataHoraInicioJornada;
    @Nullable
    private final LocalDateTime dataHoraFimJornada;
    @NotNull
    private final Long codTipoMarcacao;
    @NotNull
    private final Long codTipoMarcacaoPorUnidade;
    @NotNull
    private final List<FolhaPontoMarcacao> marcacoes;
    @SerializedName("jornadaBrutaEmSegundos")
    private Duration jornadaBruta;
    @SerializedName("jornadaLiquidaEmSegundos")
    private Duration jornadaLiquida;
    /**
     * Indica se as marcações de início e fim foram feitas em dias diferentes.
     */
    private final boolean trocouDia;
    private final boolean marcacaoInicioAjustada;
    private final boolean marcacaoFimAjustada;

    public FolhaPontoJornada(@Nullable final LocalDateTime dataHoraInicioJornada,
                             @Nullable final LocalDateTime dataHoraFimJornada,
                             @NotNull final Long codTipoMarcacao,
                             @NotNull final Long codTipoMarcacaoPorUnidade,
                             @NotNull final List<FolhaPontoMarcacao> marcacoes,
                             @NotNull final Duration jornadaBruta,
                             @NotNull final Duration jornadaLiquida,
                             final boolean trocouDia,
                             final boolean marcacaoInicioAjustada,
                             final boolean marcacaoFimAjustada) {
        this.dataHoraInicioJornada = dataHoraInicioJornada;
        this.dataHoraFimJornada = dataHoraFimJornada;
        this.codTipoMarcacao = codTipoMarcacao;
        this.codTipoMarcacaoPorUnidade = codTipoMarcacaoPorUnidade;
        this.marcacoes = marcacoes;
        this.trocouDia = trocouDia;
        this.marcacaoInicioAjustada = marcacaoInicioAjustada;
        this.marcacaoFimAjustada = marcacaoFimAjustada;
        this.jornadaBruta = jornadaBruta;
        this.jornadaLiquida = jornadaLiquida;
    }

    @NotNull
    static FolhaPontoJornada getDummy() {
        final List<FolhaPontoMarcacao> marcacoes = new ArrayList<>();
        marcacoes.add(FolhaPontoMarcacao.getDummy());
        marcacoes.add(FolhaPontoMarcacao.getDummy());
        return new FolhaPontoJornada(
                LocalDateTime.now(),
                LocalDateTime.now(),
                1L,
                1L,
                marcacoes,
                Duration.ofHours(9),
                Duration.ofHours(8),
                false,
                false,
                true);
    }

    @Nullable
    public LocalDateTime getDataHoraInicioJornada() {
        return dataHoraInicioJornada;
    }

    @Nullable
    public LocalDateTime getDataHoraFimJornada() {
        return dataHoraFimJornada;
    }

    @NotNull
    public Long getCodTipoMarcacao() {
        return codTipoMarcacao;
    }

    @NotNull
    public Long getCodTipoMarcacaoPorUnidade() {
        return codTipoMarcacaoPorUnidade;
    }

    @NotNull
    public List<FolhaPontoMarcacao> getMarcacoes() {
        return marcacoes;
    }

    public boolean isTrocouDia() {
        return trocouDia;
    }

    public boolean isMarcacaoInicioAjustada() {
        return marcacaoInicioAjustada;
    }

    public boolean isMarcacaoFimAjustada() {
        return marcacaoFimAjustada;
    }

    public Duration getJornadaBruta() {
        return jornadaBruta;
    }

    public Duration getJornadaLiquida() {
        return jornadaLiquida;
    }

    public void addMarcacaoToJornada(@NotNull final FolhaPontoMarcacao folhaPontoMarcacao) {
        this.marcacoes.add(folhaPontoMarcacao);

        // Pode ser 0 no caso de marcações que tem apenas início ou fim ou que foram iniciadas e finalizadas no
        // mesmo segundo. Vai saber...
        if (folhaPontoMarcacao.getDiferencaInicioFimEmSegundos() == 0)
            return;

        if (folhaPontoMarcacao.isDescontaJornadaBruta()) {
            // Como a jornada líquida é a jornada bruta com mais alguns descontos extras,
            // Ex: Jornada Líquida = Jornada Bruta - Descanso. Quando temos alguma marcação que deve descontar da
            // jornada bruta, também descontados da líquida.
            calculaJornadaBruta(folhaPontoMarcacao.getDiferencaInicioFimEmSegundos());
            calculaJornadaLiquida(folhaPontoMarcacao.getDiferencaInicioFimEmSegundos());
        } else if (folhaPontoMarcacao.isDescontaJornadaLiquida()) {
            calculaJornadaLiquida(folhaPontoMarcacao.getDiferencaInicioFimEmSegundos());
        }
    }

    private void calculaJornadaBruta(final long diferencaInicioFimEmSegundos) {
        // Se marcação tem extensão maior que a duração da jornada, o tempo total de jornada bruta será negativo.
        // Optamos por deixar assim para fazer o erro ser vísivel no relatório.
        jornadaBruta = jornadaBruta.minusSeconds(diferencaInicioFimEmSegundos);
    }

    private void calculaJornadaLiquida(final long diferencaInicioFimEmSegundos) {
        // Se marcação tem extensão maior que a duração da jornada, o tempo total de jornada líquida será negativo.
        // Optamos  por deixar assim para fazer o erro ser vísivel no relatório.
        jornadaLiquida = jornadaLiquida.minusSeconds(diferencaInicioFimEmSegundos);
    }
}