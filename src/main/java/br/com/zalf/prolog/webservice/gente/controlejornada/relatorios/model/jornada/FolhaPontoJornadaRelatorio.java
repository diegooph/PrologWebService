package br.com.zalf.prolog.webservice.gente.controlejornada.relatorios.model.jornada;

import br.com.zalf.prolog.webservice.gente.controlejornada.relatorios.model.FolhaPontoTipoIntervalo;
import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created on 09/01/19
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public class FolhaPontoJornadaRelatorio {
    @NotNull
    private final String cpfColaborador;
    @NotNull
    private final String nomeColaborador;
    @NotNull
    private final List<FolhaPontoJornadaDia> jornadasDia;
    private Set<FolhaPontoTipoIntervalo> tiposMarcacoesMarcadas;
    @SerializedName("dataHoraGeracaoRelatorio")
    @NotNull
    private final LocalDateTime dataHoraGeracaoRelatorioZoned;
    private Duration totalJornadaBrutaPeriodo;
    private Duration totalJornadaLiquidaPeriodo;

    public FolhaPontoJornadaRelatorio(@NotNull final String cpfColaborador,
                                      @NotNull final String nomeColaborador,
                                      @NotNull final List<FolhaPontoJornadaDia> jornadasDia,
                                      @NotNull final LocalDateTime dataHoraGeracaoRelatorioZoned) {
        this.cpfColaborador = cpfColaborador;
        this.nomeColaborador = nomeColaborador;
        this.jornadasDia = jornadasDia;
        this.dataHoraGeracaoRelatorioZoned = dataHoraGeracaoRelatorioZoned;
    }

    @NotNull
    public static FolhaPontoJornadaRelatorio getDummy() {
        final List<FolhaPontoJornadaDia> jornadasDia = new ArrayList<>();
        jornadasDia.add(FolhaPontoJornadaDia.getDummy());
        final FolhaPontoJornadaRelatorio folhaPonto = new FolhaPontoJornadaRelatorio(
                "03383283194",
                "Zalf Sistemas",
                jornadasDia,
                LocalDateTime.now());
        final Set<FolhaPontoTipoIntervalo> tiposMarcacoesMarcadas = new HashSet<>();
        tiposMarcacoesMarcadas.add(FolhaPontoTipoIntervalo.getDummy());
        folhaPonto.setTiposMarcacoesMarcadas(tiposMarcacoesMarcadas);
        folhaPonto.setTotalJornadaBrutaPeriodo(Duration.ofHours(9*4));
        folhaPonto.setTotalJornadaLiquidaPeriodo(Duration.ofHours(8*4));
        return folhaPonto;
    }

    @NotNull
    public String getCpfColaborador() {
        return cpfColaborador;
    }

    @NotNull
    public String getNomeColaborador() {
        return nomeColaborador;
    }

    @NotNull
    public List<FolhaPontoJornadaDia> getJornadasDia() {
        return jornadasDia;
    }

    @NotNull
    public Set<FolhaPontoTipoIntervalo> getTiposMarcacoesMarcadas() {
        return tiposMarcacoesMarcadas;
    }

    public void setTiposMarcacoesMarcadas(@NotNull final Set<FolhaPontoTipoIntervalo> tiposMarcacoesMarcadas) {
        this.tiposMarcacoesMarcadas = tiposMarcacoesMarcadas;
    }

    @NotNull
    public LocalDateTime getDataHoraGeracaoRelatorioZoned() {
        return dataHoraGeracaoRelatorioZoned;
    }

    @NotNull
    public Duration getTotalJornadaBrutaPeriodo() {
        return totalJornadaBrutaPeriodo;
    }

    public void setTotalJornadaBrutaPeriodo(@NotNull final Duration totalJornadaBrutaPeriodo) {
        this.totalJornadaBrutaPeriodo = totalJornadaBrutaPeriodo;
    }

    @NotNull
    public Duration getTotalJornadaLiquidaPeriodo() {
        return totalJornadaLiquidaPeriodo;
    }

    public void setTotalJornadaLiquidaPeriodo(@NotNull final Duration totalJornadaLiquidaPeriodo) {
        this.totalJornadaLiquidaPeriodo = totalJornadaLiquidaPeriodo;
    }
}
