package br.com.zalf.prolog.webservice.frota.checklist.modelo.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created on 06/12/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public abstract class PerguntaModeloChecklist {
    @NotNull
    public abstract Long getCodigo();

    @NotNull
    public abstract Long getCodigoContexto();

    @NotNull
    public abstract String getDescricao();

    @Nullable
    public abstract Long getCodImagem();

    @Nullable
    public abstract String getUrlImagem();

    public abstract int getOrdemExibicao();

    public abstract boolean isSingleChoice();

    @NotNull
    public abstract CapturaFotoChecklistEnum getCapturaFotosRespostaOk();

    @NotNull
    public abstract List<AlternativaModeloChecklist> getAlternativas();
}