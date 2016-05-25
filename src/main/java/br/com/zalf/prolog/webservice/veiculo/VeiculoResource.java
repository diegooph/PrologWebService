package br.com.zalf.prolog.webservice.veiculo;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import br.com.zalf.prolog.models.Request;
import br.com.zalf.prolog.models.Response;
import br.com.zalf.prolog.models.TipoVeiculo;
import br.com.zalf.prolog.models.Veiculo;
import br.com.zalf.prolog.webservice.auth.Secured;

@Path("veiculos")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class VeiculoResource {
	private VeiculoService service = new VeiculoService();
	
	@POST
	@Path("/unidade/colaborador")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public List<Veiculo> getVeiculosAtivosByUnidadeByColaborador(@FormParam("cpf") Long cpf) {
		return service.getVeiculosAtivosByUnidadeByColaborador(cpf);
	}
	
	@GET
	@Secured
	@Path("/{codUnidade}/tipo")
	public List<TipoVeiculo> getTipoVeiculosByUnidade(@PathParam("codUnidade") Long codUnidade) {
		return service.getTipoVeiculosByUnidade(codUnidade);
	}
	
	/**
	 * Busca todos os veículos de uma unidade
	 * @param request onde contém os dados do solicitante e dados da unidade a ser buscada
	 * @return uma lista de Veiculo
	 */
	@POST
	@Path("/unidade/getAll")
	public List<Veiculo> getAll(Request request) {
		return service.getAll(request);
	}
	
	
	@PUT	
	@Path("/update")
	@Secured
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response update(
			@FormParam("placa") String placa,
			@FormParam("placaEditada") String placaEditada,
			@FormParam("modelo") String modelo,
			@FormParam("isAtivo") boolean isAtivo) {
		if (service.update(placa, placaEditada, modelo, isAtivo)) {
			return Response.Ok("Veículo atualizado com sucesso");
		} else {
			return Response.Error("Erro ao atualizar o veículo");
		}
	}
	

	@POST
	@Path("/insert")
	public Response insert(Request<Veiculo> request) {
		if (service.insert(request)) {
			return Response.Ok("Veículo inserido com sucesso");
		} else {
			return Response.Error("Erro ao inserir o veículo");
		}
	}
}
