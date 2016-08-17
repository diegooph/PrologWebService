package br.com.zalf.prolog.webservice.frota.ordemServico;

import br.com.zalf.prolog.models.checklist.os.ItemOrdemServico;
import br.com.zalf.prolog.models.checklist.os.ManutencaoHolder;
import br.com.zalf.prolog.models.checklist.os.OsHolder;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jean on 11/08/16.
 */
public class OrdemServicoService {

    OrdemServicoDaoImpl dao = new OrdemServicoDaoImpl();

    public List<OsHolder> getOs(String placa, String status, Connection conn, Long codUnidade,
                                String tipoVeiculo, Integer limit, Long offset){
        try{
            return dao.getOs(placa, status, conn, codUnidade, tipoVeiculo, limit, offset);
        }catch (SQLException e){
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

//    public List<ManutencaoHolder> getManutencaoHolder (String placa, String codTipo, Long codUnidade, int limit,
//                                                       long offset, String status){
//        try{
//            return dao.getManutencaoHolder(placa, codTipo, codUnidade, limit, offset, status);
//        }catch (SQLException e){
//            e.printStackTrace();
//            return new ArrayList<>();
//        }
//    }

    public boolean consertaItem (Long codUnidade,ItemOrdemServico item){
        try{
            return dao.consertaItem(codUnidade, item);
        }catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    public List<ManutencaoHolder> getResumoManutencaoHolder(String placa, String codTipo, Long codUnidade, int limit,
                                                            long offset, String status){
        try{
            return dao.getResumoManutencaoHolder(placa, codTipo, codUnidade, limit, offset, status);
        }catch (SQLException e){
            e.printStackTrace();
            return null;
        }
    }

    public List<ItemOrdemServico> getItensOsManutencaoHolder(String placa, String status, int limit, long offset, String prioridade){
        try{
            return dao.getItensOsManutencaoHolder(placa, status, limit, offset, prioridade);
        }catch (SQLException e){
            e.printStackTrace();
            return null;
        }
    }
}
