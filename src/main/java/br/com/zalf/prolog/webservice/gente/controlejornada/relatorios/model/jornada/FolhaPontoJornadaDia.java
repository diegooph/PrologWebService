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
    private final List<FolhaPontoJornadas> jornadasDia;
    @NotNull
    private final List<FolhaPontoMarcacoes> marcacoesForaJornada;

    public FolhaPontoJornadaDia(@NotNull final LocalDate diaReferencia,
                                @NotNull final List<FolhaPontoJornadas> jornadasDia,
                                @NotNull final List<FolhaPontoMarcacoes> marcacoesForaJornada) {
        this.diaReferencia = diaReferencia;
        this.jornadasDia = jornadasDia;
        this.marcacoesForaJornada = marcacoesForaJornada;
    }

    @NotNull
    static FolhaPontoJornadaDia getDummy() {
        final List<FolhaPontoJornadas> jornadasDia = new ArrayList<>();
        jornadasDia.add(FolhaPontoJornadas.getDummy());
        final List<FolhaPontoMarcacoes> marcacoesExtraJornada = new ArrayList<>();
        return new FolhaPontoJornadaDia(LocalDate.now(), jornadasDia, marcacoesExtraJornada);
    }

    @NotNull
    public LocalDate getDiaReferencia() {
        return diaReferencia;
    }

    @NotNull
    public List<FolhaPontoJornadas> getJornadasDia() {
        return jornadasDia;
    }

    @NotNull
    public List<FolhaPontoMarcacoes> getMarcacoesForaJornada() {
        return marcacoesForaJornada;
    }
}