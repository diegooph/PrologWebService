package br.com.zalf.prolog.webservice.pneu.movimentacao;

import br.com.zalf.prolog.frota.pneu.movimentacao.Movimentacao;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by Zart on 03/03/17.
 */
@Path("/movimentacoes")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class MovimentacaoResource {

    @Secured
    @POST
    public void insert(List<Movimentacao> movimentacao){
    }
}
