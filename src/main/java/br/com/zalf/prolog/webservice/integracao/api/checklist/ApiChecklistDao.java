package br.com.zalf.prolog.webservice.integracao.api.checklist;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 07/08/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public interface ApiChecklistDao {

    @NotNull
    List<ApiAlternativaModeloChecklist> getAlternativasModeloChecklist(
            @NotNull final String tokenIntegracao,
            final boolean apenasModelosAtivos,
            final boolean apenasPerguntasAtivas,
            final boolean apenasAlternativasAtivas) throws Throwable;
}
