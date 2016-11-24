package br.com.zalf.prolog.webservice.gente.preContracheque;

import br.com.zalf.prolog.gente.pre_contracheque.Contracheque;
import br.com.zalf.prolog.gente.pre_contracheque.ItemContracheque;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by Zalf on 23/11/16.
 */
public class PreContrachequeService {

    PreContrachequeDaoImpl dao = new PreContrachequeDaoImpl();

    public Contracheque getPreContracheque(Long cpf, Long codUnidade, int ano, int mes){
        try{
            return dao.getPreContracheque(cpf, codUnidade, ano, mes);
        }catch (SQLException e){
            e.printStackTrace();
            return null;
        }
    }
}
