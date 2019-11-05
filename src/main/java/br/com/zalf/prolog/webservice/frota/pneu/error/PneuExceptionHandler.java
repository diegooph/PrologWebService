package br.com.zalf.prolog.webservice.frota.pneu.error;

import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogExceptionHandler;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 21/06/18.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public class PneuExceptionHandler extends ProLogExceptionHandler {

    public PneuExceptionHandler(@NotNull final PneuSqlExceptionTranslator sqlTranslator) {
        super(sqlTranslator);
    }
}
