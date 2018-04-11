package br.com.zalf.prolog.webservice.imports.escala_diaria;

import br.com.zalf.prolog.webservice.Injection;

/**
 * Created on 10/04/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class EscalaDiariaService {

    private final EscalaDiariaDao escalaDiariaDao = Injection.provideEscalaDiariaDao();

}
