package br.com.zalf.prolog.webservice.pneu;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import br.com.zalf.prolog.models.Response;
import br.com.zalf.prolog.models.pneu.Pneu;
import br.com.zalf.prolog.webservice.auth.Secured;

@Path("/pneus")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class PneuResource{
	private PneuService service = new PneuService();

	@POST
	@Secured
	@Path("/{codUnidade}")
	public Response insert(Pneu pneu,@PathParam("codUnidade") Long codUnidade){
		if (service.insert(pneu, codUnidade)) {
			return Response.Ok("Pneu inserido com sucesso.");
		}else{
			return Response.Error("Erro ao inserir o pneu");
		}
	}
	
	@GET
	@Secured
	@Path("/{codUnidade}/{status}")
	public List<Pneu> getPneuByCodUnidadeByStatus(@PathParam("codUnidade") Long codUnidade,@PathParam("status") String status){
		return service.getPneuByCodUnidadeByStatus(codUnidade, status);
	}

}



