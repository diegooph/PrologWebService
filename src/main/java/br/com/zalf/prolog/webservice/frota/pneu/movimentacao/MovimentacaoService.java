package br.com.zalf.prolog.webservice.frota.pneu.movimentacao;

import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.model.OrigemDestinoInvalidaException;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.model.ProcessoMovimentacao;

import java.sql.SQLException;

/**
 * Created by Zart on 03/03/17.
 */
public class MovimentacaoService {

    MovimentacaoDaoImpl dao = new MovimentacaoDaoImpl();
    private static final String TAG = MovimentacaoService.class.getSimpleName();

    public Response insert(ProcessoMovimentacao movimentacao) {
        try{
            if(dao.insert(movimentacao)){
                return Response.ok("Movimentações realizadas com sucesso");
            }else{
                return Response.error("Erro ao realizar as movimentações");
            }
        }catch (SQLException | OrigemDestinoInvalidaException e){
            Log.e(TAG, "Erro ao inserir uma movimentação", e);
            return Response.error("Erro ao realizar as movimentações");
        }
    }

}
