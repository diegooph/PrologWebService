package br.com.zalf.prolog.webservice.frota.checklist.model.insercao;

import org.jetbrains.annotations.Nullable;

/**
 * Created on 08/11/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class ChecklistAlternativaResposta {
    private Long codAlternativa;
    private boolean alternativaSelecionada;
    private boolean tipoOutros;
    @Nullable
    private String respostaTipoOutros;

    public ChecklistAlternativaResposta() {

    }

    public Long getCodAlternativa() {
        return codAlternativa;
    }

    public void setCodAlternativa(final Long codAlternativa) {
        this.codAlternativa = codAlternativa;
    }

    public boolean isAlternativaSelecionada() {
        return alternativaSelecionada;
    }

    public void setAlternativaSelecionada(final boolean alternativaSelecionada) {
        this.alternativaSelecionada = alternativaSelecionada;
    }

    public boolean isTipoOutros() {
        return tipoOutros;
    }

    public void setTipoOutros(final boolean tipoOutros) {
        this.tipoOutros = tipoOutros;
    }

    @Nullable
    public String getRespostaTipoOutros() {
        return respostaTipoOutros;
    }

    public void setRespostaTipoOutros(@Nullable final String respostaTipoOutros) {
        this.respostaTipoOutros = respostaTipoOutros;
    }
}