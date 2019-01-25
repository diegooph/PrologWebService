package br.com.zalf.prolog.webservice.gente.controlejornada.relatorios.model.jornada;

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
    @NotNull
    private final Duration jornadaBruta;
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

    public void setJornadaLiquida(final Duration jornadaLiquida) {
        this.jornadaLiquida = jornadaLiquida;
    }

    public void addMarcacaoToJornada(@NotNull final FolhaPontoMarcacao folhaPontoMarcacao) {
        this.marcacoes.add(folhaPontoMarcacao);
        this.calculaJornadaLiquida(folhaPontoMarcacao.getDiferencaInicioFimEmSegundos());
    }

    public void calculaJornadaLiquida(final long diferencaInicioFimEmSegundos) {
        // Pode ser null no caso de marcações que tem apenas início ou fim ou que foram iniciadas e finalizadas no
        // mesmo segundo. Vai saber...
        if (diferencaInicioFimEmSegundos == 0)
            return;

        // Se marcação tem extensão maior que a duração da jornada, o tempo total de jornada líquida será negativo,
        // optamos, por deixar assim para fazer o erro ser vísivel no relatório.
        final long jornadaLiquidaSegundos = this.jornadaLiquida.minusSeconds(diferencaInicioFimEmSegundos).getSeconds();
        setJornadaLiquida(Duration.ofSeconds(jornadaLiquidaSegundos));
    }
}