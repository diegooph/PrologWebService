package br.com.zalf.prolog.webservice.gente.prontuarioCondutor;

import br.com.zalf.prolog.webservice.gente.prontuarioCondutor.model.ProntuarioCondutor;

import java.sql.SQLException;

/**
 * Created by Zart on 03/07/2017.
 */
public class ProntuarioCondutorService {

    ProntuarioCondutorDao dao = new ProntuarioCondutorDaoImpl();

    public ProntuarioCondutor getProntuario(Long cpf) {
       try{
           return dao.getProntuario(cpf);
       }catch (SQLException e) {
           e.printStackTrace();
           return null;
       }
    }

}
