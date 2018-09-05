package br.com.zalf.prolog.webservice.gente.controleintervalo.justificativa_ajuste;

import br.com.zalf.prolog.webservice.Injection;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 05/09/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class JustificativaAjusteService {

    @NotNull
    final JustificativaAjusteDao dao = Injection.provideJustificativaAjusteDao();
}
