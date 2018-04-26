package br.com.zalf.prolog.webservice.gente.controleintervalo.relatorios;

import br.com.zalf.prolog.webservice.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.gente.controleintervalo.model.TipoIntervalo;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

public final class FolhaPontoRelatorio {
    @NotNull
    private final Colaborador colaborador;
    @NotNull
    private final Set<TipoIntervalo> tiposIntervalosMarcados;
    @NotNull
    private final List<FolhaPontoDia> marcacoesDias;

    public FolhaPontoRelatorio(@NotNull Colaborador colaborador,
                               @NotNull Set<TipoIntervalo> tiposIntervalosMarcados,
                               @NotNull List<FolhaPontoDia> marcacoesDias) {
        this.colaborador = colaborador;
        this.tiposIntervalosMarcados = tiposIntervalosMarcados;
        this.marcacoesDias = marcacoesDias;
    }

    @NotNull
    public Colaborador getColaborador() {
        return colaborador;
    }

    @NotNull
    public Set<TipoIntervalo> getTiposIntervalosMarcados() {
        return tiposIntervalosMarcados;
    }

    @NotNull
    public List<FolhaPontoDia> getMarcacoesDias() {
        return marcacoesDias;
    }
}