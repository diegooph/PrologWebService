package br.com.zalf.prolog.webservice.rest;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import br.com.zalf.prolog.models.Request;
import br.com.zalf.prolog.models.Response;
import br.com.zalf.prolog.models.Veiculo;
import br.com.zalf.prolog.webservice.services.VeiculoService;

@Path("veiculos")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class VeiculoResource {
	private VeiculoService service = new VeiculoService();
	
	@POST
	@Path("/unidade/colaborador")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public List<Veiculo> getVeiculosAtivosByUnidadeByColaborador(@FormParam("cpf") Long cpf, 
			@FormParam("token") String token) {
		return service.getVeiculosAtivosByUnidadeByColaborador(cpf, token);
	}
	
	@POST
	@Path("/unidade/getAll")
	public List<Veiculo> getAll(Request<?> request) {
		return service.getAll(request);
	}
	
	@PUT
	public Response update(Request<Veiculo> request) {
		if (service.update(request)) {
			return Response.Ok("Veículo atualizado com sucesso");
		} else {
			return Response.Error("Erro ao atualizar o veículo");
		}
	}
}
