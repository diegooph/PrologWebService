package br.com.zalf.prolog.webservice.frota.checklist.modelo;

import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.frota.checklist.PerguntaRespostaChecklist;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/modeloChecklist")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class ChecklistModeloResource {

	private ChecklistModeloService service = new ChecklistModeloService();

	@POST
	@Secured(permissions = {Pilares.Frota.Checklist.Modelo.ALTERAR, Pilares.Frota.Checklist.Modelo.CADASTRAR})
	public Response insertModeloChecklist(ModeloChecklist modeloChecklist) {
		if (service.insertModeloChecklist(modeloChecklist)) {
			return Response.Ok("Modelo de checklist inserido com sucesso");
		} else {
			return Response.Error("Erro ao inserir modelo de checklist");
		}
	}

	@GET
	@Secured(permissions = {Pilares.Frota.Checklist.Modelo.VISUALIZAR, Pilares.Frota.Checklist.Modelo.ALTERAR,
			Pilares.Frota.Checklist.Modelo.CADASTRAR})
	@Path("{codUnidade}/{codFuncao}")
	public List<ModeloChecklist> getModelosChecklistByCodUnidadeByCodFuncao(
			@PathParam("codUnidade")Long codUnidade,
			@PathParam("codFuncao") String codFuncao) {
		return service.getModelosChecklistByCodUnidadeByCodFuncao(codUnidade, codFuncao);
	}

	@GET
	@Secured(permissions = {Pilares.Frota.Checklist.Modelo.VISUALIZAR, Pilares.Frota.Checklist.Modelo.ALTERAR,
			Pilares.Frota.Checklist.Modelo.CADASTRAR})
	@Path("/modelo/{codUnidade}/{codModelo}")
	public ModeloChecklist getModeloChecklist(
			@PathParam("codModelo") Long codModelo,
			@PathParam("codUnidade") Long codUnidade) {
		return service.getModeloChecklist(codModelo, codUnidade);
	}

	@GET
	@Secured(permissions = {Pilares.Frota.Checklist.Modelo.VISUALIZAR, Pilares.Frota.Checklist.Modelo.ALTERAR,
			Pilares.Frota.Checklist.Modelo.CADASTRAR, Pilares.Frota.Checklist.REALIZAR})
	@Path("/perguntas/{codUnidade}/{codModelo}")
	public List<PerguntaRespostaChecklist> getPerguntas(@PathParam("codUnidade") Long codUnidade,
														@PathParam("codModelo") Long codModelo) {
		return service.getPerguntas(codUnidade, codModelo);
	}

	@DELETE
	@Secured(permissions = {Pilares.Frota.Checklist.Modelo.ALTERAR, Pilares.Frota.Checklist.Modelo.CADASTRAR})
	@Path("modelo/{codUnidade}/{codModelo}")
	public Response setModeloChecklistInativo(
			@PathParam("codUnidade") Long codUnidade,
			@PathParam("codModelo") Long codModelo){
		if (service.setModeloChecklistInativo(codUnidade, codModelo)) {
			return Response.Ok("Modelo desativado com sucesso");
		} else {
			return Response.Error("Erro ao desativar o modelo");
		}
	}
}