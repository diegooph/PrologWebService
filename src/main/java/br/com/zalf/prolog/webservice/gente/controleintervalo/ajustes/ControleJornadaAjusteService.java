package br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes;

import br.com.zalf.prolog.webservice.Injection;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 04/09/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class ControleJornadaAjusteService {

    @NotNull
    final ControleJornadaAjusteDao dao = Injection.provideControleJornadaAjustesDao();
}
