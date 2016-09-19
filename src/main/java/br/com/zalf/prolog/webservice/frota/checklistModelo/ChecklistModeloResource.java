package br.com.zalf.prolog.webservice.frota.checklistModelo;

import br.com.zalf.prolog.commons.network.Response;
import br.com.zalf.prolog.frota.checklist.ModeloChecklist;
import br.com.zalf.prolog.frota.checklist.PerguntaRespostaChecklist;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/modeloChecklist")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class ChecklistModeloResource {

	private ChecklistModeloService service = new ChecklistModeloService();

	@GET
	@Secured
	@Path("{codUnidade}/{codFuncao}")
	public List<ModeloChecklist> getModelosChecklistByCodUnidadeByCodFuncao(
			@PathParam("codUnidade")Long codUnidade,
			@PathParam("codFuncao") String codFuncao) {
		return service.getModelosChecklistByCodUnidadeByCodFuncao(codUnidade, codFuncao);
	}

	@GET
	@Secured
	@Path("/modelo/{codUnidade}/{codModelo}")
	public List<ModeloChecklist> getModeloChecklist(
			@PathParam("codModelo") Long codModelo,
			@PathParam("codUnidade") Long codUnidade){
		return service.getModeloChecklist(codModelo, codUnidade);
	}
	
	@DELETE
	@Secured
	@Path("modelo/{codUnidade}/{codModelo}")
	public Response setModeloChecklistInativo (
			@PathParam("codUnidade") Long codUnidade, 
			@PathParam("codModelo") Long codModelo){
		if(service.setModeloChecklistInativo(codUnidade, codModelo)){
			return Response.Ok("Modelo desativado com sucesso");
		}else{
			return Response.Error("Erro ao desativar o modelo");
		}
	}

	@GET
	@Secured
	@Path("/perguntas/{codUnidade}/{codModelo}")
	public List<PerguntaRespostaChecklist> getPerguntas(@PathParam("codUnidade") Long codUnidade, @PathParam("codModelo") Long codModelo){
		return service.getPerguntas(codUnidade, codModelo);
	}

	@POST
	@Secured
	public Response insertModeloChecklist(ModeloChecklist modeloChecklist){
		if(service.insertModeloChecklist(modeloChecklist)){
			return Response.Ok("Modelo de checklist inserido com sucesso");
		} else {
			return Response.Error("Erro ao inserir modelo de checklist");
		}
	}
}
