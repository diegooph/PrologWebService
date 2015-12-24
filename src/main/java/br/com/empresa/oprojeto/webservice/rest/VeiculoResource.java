package br.com.empresa.oprojeto.webservice.rest;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import br.com.empresa.oprojeto.models.Veiculo;
import br.com.empresa.oprojeto.webservice.services.VeiculoService;

@Path("veiculos")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class VeiculoResource {
	private VeiculoService service = new VeiculoService();
	
	@GET
	@Path("/unidade/{codUnidade}")
	public List<Veiculo> getVeiculosByUnidade(@PathParam("codUnidade") Long codUnidade) {
		return service.getVeiculosAtivosByUnidade(codUnidade);
	}
	
	@GET
	@Path("/unidade/colaborador/{cpf}")
	public List<Veiculo> getVeiculosAtivosByUnidadeByColaborador(@PathParam("cpf") Long cpf) {
		return service.getVeiculosAtivosByUnidadeByColaborador(cpf);
	}
}
