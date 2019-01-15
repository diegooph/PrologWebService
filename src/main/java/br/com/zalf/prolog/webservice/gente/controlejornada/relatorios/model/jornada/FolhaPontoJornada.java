package br.com.zalf.prolog.webservice.gente.controlejornada.relatorios.model.jornada;

import com.google.common.base.Preconditions;
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
    /**
     * Indica se as marcações de início e fim foram feitas em dias diferentes.
     */
    private final boolean trocouDia;
    private final boolean marcacaoInicioAjustada;
    private final boolean marcacaoFimAjustada;

    private Duration jornadaBruta;
    private Duration jornadaLiquida;

    public FolhaPontoJornada(@Nullable final LocalDateTime dataHoraInicioJornada,
                             @Nullable final LocalDateTime dataHoraFimJornada,
                             @NotNull final Long codTipoMarcacao,
                             @NotNull final Long codTipoMarcacaoPorUnidade,
                             @NotNull final List<FolhaPontoMarcacao> marcacoes,
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
    }

    @NotNull
    static FolhaPontoJornada getDummy() {
        final List<FolhaPontoMarcacao> marcacoes = new ArrayList<>();
        marcacoes.add(FolhaPontoMarcacao.getDummy());
        marcacoes.add(FolhaPontoMarcacao.getDummy());
        final FolhaPontoJornada jornada = new FolhaPontoJornada(
                LocalDateTime.now(),
                LocalDateTime.now(),
                1L,
                1L,
                marcacoes,
                false,
                false,
                true);
        jornada.setJornadaBruta(Duration.ofHours(9));
        jornada.setJornadaLiquida(Duration.ofHours(8));
        return jornada;
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

    public void setJornadaBruta(final Duration jornadaBruta) {
        this.jornadaBruta = jornadaBruta;
    }

    public Duration getJornadaLiquida() {
        return jornadaLiquida;
    }

    public void setJornadaLiquida(final Duration jornadaLiquida) {
        this.jornadaLiquida = jornadaLiquida;
    }

    public boolean hasInicioFim() {
        return this.dataHoraInicioJornada != null
                && this.dataHoraFimJornada != null;
    }

    public void addMarcacaoToJornada(@NotNull final FolhaPontoMarcacao folhaPontoMarcacao) {
        this.marcacoes.add(folhaPontoMarcacao);
    }

    public void calculaJornadaBruta(@Nullable final Long diferencaoInicioFimEmSegundos) {
        if (diferencaoInicioFimEmSegundos == null) {
            setJornadaBruta(Duration.ZERO);
        } else {
            setJornadaBruta(Duration.ofSeconds(diferencaoInicioFimEmSegundos));
        }
    }

    public void calculaJornadaLiquida(@Nullable final Long diferencaoInicioFimEmSegundos) {
        Preconditions.checkNotNull(this.jornadaBruta);

        if (diferencaoInicioFimEmSegundos == null)
            return;

        // A primeira vez que calculamos a jornada liquida, apenas setamos a bruta, para decrementar posteriormente.
        if (this.jornadaLiquida == null) {
            this.jornadaLiquida = Duration.ofSeconds(jornadaBruta.getSeconds());
            return;
        }

        final Long jornadaLiquida =
                Math.abs(this.jornadaLiquida.minus(Duration.ofSeconds(diferencaoInicioFimEmSegundos)).getSeconds());
        setJornadaLiquida(Duration.ofSeconds(jornadaLiquida));
    }
}