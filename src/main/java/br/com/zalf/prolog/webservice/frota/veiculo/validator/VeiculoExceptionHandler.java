package br.com.zalf.prolog.webservice.frota.veiculo.validator;

import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogExceptionHandler;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 19/06/18.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public class VeiculoExceptionHandler extends ProLogExceptionHandler {

    public VeiculoExceptionHandler(@NotNull final VeiculoSqlExceptionTranslator sqlTranslator) {
        super(sqlTranslator);
    }
}
