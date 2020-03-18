package br.com.zalf.prolog.webservice.gente.colaborador.error;

import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogExceptionHandler;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 19/06/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public class ColaboradorExceptionHandler extends ProLogExceptionHandler {

    public ColaboradorExceptionHandler(@NotNull final ColaboradorSqlExceptionTranslator sqlTranslator) {
        super(sqlTranslator);
    }
}
