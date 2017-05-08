package br.com.zalf.prolog.webservice.pneu.movimentacao;

import br.com.zalf.prolog.commons.network.Response;
import br.com.zalf.prolog.frota.pneu.movimentacao.OrigemDestinoInvalidaException;
import br.com.zalf.prolog.frota.pneu.movimentacao.ProcessoMovimentacao;

import java.sql.SQLException;

/**
 * Created by Zart on 03/03/17.
 */
public class MovimentacaoService {

    MovimentacaoDaoImpl dao = new MovimentacaoDaoImpl();

    public Response insert(ProcessoMovimentacao movimentacao) {
        try{
            if(dao.insert(movimentacao)){
                return Response.Ok("Movimentações realizadas com sucesso");
            }else{
                return Response.Error("Erro ao realizar as movimentações");
            }
        }catch (SQLException | OrigemDestinoInvalidaException e){
            e.printStackTrace();
            return Response.Error("Erro ao realizar as movimentações");
        }
    }

}
