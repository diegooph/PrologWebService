package br.com.zalf.prolog.webservice.colaborador.error;

import br.com.zalf.prolog.webservice.errorhandling.sql.DuplicateKeyException;

/**
 * Created on 19/06/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
class ColaboradorDuplicadoException extends DuplicateKeyException {

    ColaboradorDuplicadoException() {
        super("Colaborador já cadastrado");
    }
}