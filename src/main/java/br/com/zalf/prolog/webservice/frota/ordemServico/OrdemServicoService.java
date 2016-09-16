package br.com.zalf.prolog.webservice.frota.ordemServico;

import br.com.zalf.prolog.frota.checklist.os.ItemOrdemServico;
import br.com.zalf.prolog.frota.checklist.os.ManutencaoHolder;
import br.com.zalf.prolog.frota.checklist.os.OrdemServico;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by jean on 11/08/16.
 */
public class OrdemServicoService {

    private OrdemServicoDao dao = new OrdemServicoDaoImpl();

    public List<OrdemServico> getOs(String placa, String status, Long codUnidade,
                                    String tipoVeiculo, Integer limit, Long offset){
        try{
            return dao.getOs(placa, status, codUnidade, tipoVeiculo, limit, offset);
        }catch (SQLException e){
            e.printStackTrace();
            return null;
        }
    }

    public boolean consertaItem (Long codUnidade,ItemOrdemServico item, String placa){
        try{
            return dao.consertaItem(codUnidade, item, placa);
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
