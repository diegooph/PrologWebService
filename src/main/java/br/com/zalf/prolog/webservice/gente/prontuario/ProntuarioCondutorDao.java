package br.com.zalf.prolog.webservice.gente.prontuario;

import br.com.zalf.prolog.webservice.gente.prontuario.model.ProntuarioCondutor;

import java.sql.SQLException;

/**
 * Created by Zart on 03/07/2017.
 */
public interface ProntuarioCondutorDao {

    ProntuarioCondutor getProntuario(Long cpf) throws SQLException;

}
