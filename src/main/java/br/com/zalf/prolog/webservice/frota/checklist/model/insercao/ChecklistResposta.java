package br.com.zalf.prolog.webservice.frota.checklist.model.insercao;

import java.util.List;

/**
 * Created on 08/11/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class ChecklistResposta {
    private Long codPergunta;
    private List<ChecklistAlternativaResposta> alternativasRespostas;

    public ChecklistResposta() {

    }

    public Long getCodPergunta() {
        return codPergunta;
    }

    public void setCodPergunta(final Long codPergunta) {
        this.codPergunta = codPergunta;
    }

    public List<ChecklistAlternativaResposta> getAlternativasRespostas() {
        return alternativasRespostas;
    }

    public void setAlternativasRespostas(final List<ChecklistAlternativaResposta> alternativasRespostas) {
        this.alternativasRespostas = alternativasRespostas;
    }
}