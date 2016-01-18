package br.com.zalf.prolog.webservice.rest;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import br.com.zalf.prolog.models.Veiculo;
import br.com.zalf.prolog.webservice.services.VeiculoService;

@Path("veiculos")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class VeiculoResource {
	private VeiculoService service = new VeiculoService();
	
//	@GET
//	@Path("/unidade/{codUnidade}")
//	public List<Veiculo> getVeiculosByUnidade(@PathParam("codUnidade") Long codUnidade) {
//		return service.getVeiculosAtivosByUnidade(codUnidade);
//	}
	
	@POST
	@Path("/unidade/colaborador")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public List<Veiculo> getVeiculosAtivosByUnidadeByColaborador(@FormParam("cpf") Long cpf, 
			@FormParam("token") String token) {
		return service.getVeiculosAtivosByUnidadeByColaborador(cpf, token);
	}
}
