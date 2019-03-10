package br.com.zalf.prolog.webservice.frota.checklist.offline.model;

import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.PerguntaModeloChecklist;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 10/03/19
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class ModeloChecklistOffline {
    @NotNull
    private final Long codModelo;
    @NotNull
    private final String nomeModelo;
    @NotNull
    private final Long codUnidadeModelo;
    @NotNull
    private final List<CargoChecklistOffline> cargosLiberados;
    @NotNull
    private final List<TipoVeiculoChecklistOffline> tiposVeiculosLiberados;
    @NotNull
    private final List<PerguntaModeloChecklist> perguntasModeloChecklist;

    public ModeloChecklistOffline(@NotNull final Long codModelo,
                                  @NotNull final String nomeModelo,
                                  @NotNull final Long codUnidadeModelo,
                                  @NotNull final List<CargoChecklistOffline> cargosLiberados,
                                  @NotNull final List<TipoVeiculoChecklistOffline> tiposVeiculosLiberados,
                                  @NotNull final List<PerguntaModeloChecklist> perguntasModeloChecklist) {
        this.codModelo = codModelo;
        this.nomeModelo = nomeModelo;
        this.codUnidadeModelo = codUnidadeModelo;
        this.cargosLiberados = cargosLiberados;
        this.tiposVeiculosLiberados = tiposVeiculosLiberados;
        this.perguntasModeloChecklist = perguntasModeloChecklist;
    }

    @NotNull
    public Long getCodModelo() {
        return codModelo;
    }

    @NotNull
    public String getNomeModelo() {
        return nomeModelo;
    }

    @NotNull
    public Long getCodUnidadeModelo() {
        return codUnidadeModelo;
    }

    @NotNull
    public List<CargoChecklistOffline> getCargosLiberados() {
        return cargosLiberados;
    }

    @NotNull
    public List<TipoVeiculoChecklistOffline> getTiposVeiculosLiberados() {
        return tiposVeiculosLiberados;
    }

    @NotNull
    public List<PerguntaModeloChecklist> getPerguntasModeloChecklist() {
        return perguntasModeloChecklist;
    }
}