package br.com.zalf.prolog.webservice.frota.veiculo.validator;

import br.com.zalf.prolog.webservice.errorhandling.sql.DuplicateKeyException;

/**
 * Created on 19/06/18.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public class VeiculoDuplicadoException extends DuplicateKeyException {

    VeiculoDuplicadoException() {
        super("ERRO!\nEste veículo já está cadastrado");
    }
}
