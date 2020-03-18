package br.com.zalf.prolog.webservice.integracao.rodoparhorizonte.model.token;

import br.com.zalf.prolog.webservice.gente.colaborador.model.Colaborador;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 13/06/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class ProtheusRodalogCredentialCreator {
    private static final String DEFAULT_GRANT_TYPE = "password";

    private ProtheusRodalogCredentialCreator() {
        throw new IllegalStateException(
                ProtheusRodalogCredentialCreator.class.getSimpleName() + "cannot be instantiated!");
    }

    @NotNull
    public static RodoparCredentials createCredentials(@NotNull final Colaborador colaboradorRequisicao) {
        return new RodoparCredentials(
                Colaborador.formatCpf(colaboradorRequisicao.getCpf()),
                colaboradorRequisicao.getDataNascimentoAsString(),
                DEFAULT_GRANT_TYPE);
    }
}
