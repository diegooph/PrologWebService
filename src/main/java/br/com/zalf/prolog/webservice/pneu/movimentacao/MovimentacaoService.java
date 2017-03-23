package br.com.zalf.prolog.webservice.pneu.movimentacao;

import br.com.zalf.prolog.commons.network.Response;
import br.com.zalf.prolog.frota.pneu.movimentacao.Movimentacao;
import br.com.zalf.prolog.frota.pneu.movimentacao.OrigemDestinoInvalidaException;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by Zart on 03/03/17.
 */
public class MovimentacaoService {

    MovimentacaoDaoImpl dao = new MovimentacaoDaoImpl();

    public Response insert(List<Movimentacao> movimentacoes, Long codUnidade) {
        try{
            if(dao.insert(movimentacoes, codUnidade)){
                return Response.Ok("Movimentações realizadas com sucesso");
            }else{
                return Response.Error("Erro ao inserir movimentações");
            }
        }catch (SQLException | OrigemDestinoInvalidaException e){
            e.printStackTrace();
            return null;
        }
    }

}
