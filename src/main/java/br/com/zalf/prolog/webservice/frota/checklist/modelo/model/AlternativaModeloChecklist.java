package br.com.zalf.prolog.webservice.frota.checklist.modelo.model;

import br.com.zalf.prolog.webservice.frota.checklist.model.PrioridadeAlternativa;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 06/12/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public abstract class AlternativaModeloChecklist {
    @NotNull
    public abstract Long getCodigo();

    @NotNull
    public abstract Long getCodigoContexto();

    @NotNull
    public abstract String getDescricao();

    @NotNull
    public abstract PrioridadeAlternativa getPrioridade();

    public abstract boolean isTipoOutros();

    public abstract int getOrdemExibicao();

    public abstract boolean isDeveAbrirOrdemServico();

    @NotNull
    public abstract CapturaFotoChecklistEnum getCapturaFotos();
}