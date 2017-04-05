package br.com.zalf.prolog.webservice.pneu.movimentacao;

import br.com.zalf.prolog.commons.network.Response;
import br.com.zalf.prolog.frota.pneu.movimentacao.ProcessoMovimentacao;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by Zart on 03/03/17.
 */
@Path("/movimentacoes")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class MovimentacaoResource {

    MovimentacaoService service = new MovimentacaoService();

//    @Secured
    @POST
    public Response insert(ProcessoMovimentacao movimentacao){
        return service.insert(movimentacao);
    }
}
