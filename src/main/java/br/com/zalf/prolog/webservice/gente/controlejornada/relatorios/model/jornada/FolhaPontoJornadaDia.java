package br.com.zalf.prolog.webservice.gente.controlejornada.relatorios.model.jornada;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 09/01/19
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public class FolhaPontoJornadaDia {
    @NotNull
    private final LocalDate diaReferencia;
    @NotNull
    private final List<FolhaPontoJornada> jornadasDia;
    @NotNull
    private final List<FolhaPontoMarcacao> marcacoesForaJornada;

    public FolhaPontoJornadaDia(@NotNull final LocalDate diaReferencia,
                                @NotNull final List<FolhaPontoJornada> jornadasDia,
                                @NotNull final List<FolhaPontoMarcacao> marcacoesForaJornada) {
        this.diaReferencia = diaReferencia;
        this.jornadasDia = jornadasDia;
        this.marcacoesForaJornada = marcacoesForaJornada;
    }

    @NotNull
    static FolhaPontoJornadaDia getDummy() {
        final List<FolhaPontoJornada> jornadasDia = new ArrayList<>();
        jornadasDia.add(FolhaPontoJornada.getDummy());
        final List<FolhaPontoMarcacao> marcacoesForaJornada = new ArrayList<>();
        marcacoesForaJornada.add(FolhaPontoMarcacao.getDummy());
        marcacoesForaJornada.add(FolhaPontoMarcacao.getDummy());
        return new FolhaPontoJornadaDia(LocalDate.now(), jornadasDia, marcacoesForaJornada);
    }

    @NotNull
    public LocalDate getDiaReferencia() {
        return diaReferencia;
    }

    @NotNull
    public List<FolhaPontoJornada> getJornadasDia() {
        return jornadasDia;
    }

    @NotNull
    public List<FolhaPontoMarcacao> getMarcacoesForaJornada() {
        return marcacoesForaJornada;
    }
}