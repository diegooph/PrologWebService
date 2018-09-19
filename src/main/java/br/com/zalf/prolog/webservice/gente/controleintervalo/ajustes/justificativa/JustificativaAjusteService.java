package br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes.justificativa;

import br.com.zalf.prolog.webservice.Injection;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 05/09/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class JustificativaAjusteService {

    @NotNull
    final JustificativaAjusteDao dao = Injection.provideJustificativaAjusteDao();
}
