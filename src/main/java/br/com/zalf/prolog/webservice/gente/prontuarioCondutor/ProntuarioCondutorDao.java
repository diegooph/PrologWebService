package br.com.zalf.prolog.webservice.gente.prontuarioCondutor;

import br.com.zalf.prolog.webservice.gente.prontuarioCondutor.model.ProntuarioCondutor;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;

/**
 * Created by Zart on 03/07/2017.
 */
public interface ProntuarioCondutorDao {

    ProntuarioCondutor getProntuario(Long cpf) throws SQLException;

    boolean insertOrUpdate(String path) throws SQLException, IOException, ParseException;

    Double getPontuacaoProntuario(Long cpf) throws SQLException;

}
