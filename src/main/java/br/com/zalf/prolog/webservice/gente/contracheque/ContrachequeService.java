package br.com.zalf.prolog.webservice.gente.contracheque;

import br.com.zalf.prolog.gente.pre_contracheque.Contracheque;

import java.sql.SQLException;

/**
 * Created by Zalf on 23/11/16.
 */
public class ContrachequeService {

    ContrachequeDaoImpl dao = new ContrachequeDaoImpl();

    public Contracheque getPreContracheque(Long cpf, Long codUnidade, int ano, int mes){
        try{
            return dao.getPreContracheque(cpf, codUnidade, ano, mes);
        }catch (SQLException e){
            e.printStackTrace();
            return null;
        }
    }
}
