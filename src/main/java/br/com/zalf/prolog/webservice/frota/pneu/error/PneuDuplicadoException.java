package br.com.zalf.prolog.webservice.frota.pneu.error;

import br.com.zalf.prolog.webservice.errorhandling.exception.GenericException;
import br.com.zalf.prolog.webservice.errorhandling.sql.DuplicateKeyException;

/**
 * Created on 21/06/18.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public class PneuDuplicadoException extends DuplicateKeyException {

    PneuDuplicadoException() {
        super("ERRO!\nEste pneu já está cadastrado", "", "", GenericException.NO_LOGS_INTO_SENTRY);
    }
}
