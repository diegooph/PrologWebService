package br.com.zalf.prolog.webservice.frota.checklist.modelo.model.edicao;

import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.PerguntaModeloChecklist;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 06/12/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public abstract class PerguntaModeloChecklistEdicao extends PerguntaModeloChecklist {

    @Nullable
    @Override
    public String getUrlImagem() {
        throw new UnsupportedOperationException(PerguntaModeloChecklistEdicao.class.getSimpleName()
                + " não tem UrlImagem");
    }
}