package br.com.zalf.prolog.webservice.gente.solicitacaoFolga;

import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.util.date.DateUtils;
import br.com.zalf.prolog.webservice.empresa.EmpresaService;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.Date;
import java.util.List;

@Path("/solicitacaoFolga")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Deprecated
public class DeprecatedSolicitacaoFolgaResource {

	private SolicitacaoFolgaService service = new SolicitacaoFolgaService();

	@POST
	@Secured(permissions = Pilares.Gente.SolicitacaoFolga.REALIZAR)
	public AbstractResponse insert(SolicitacaoFolga solicitacaoFolga) {
		return service.insert(solicitacaoFolga);
	}

	@PUT
	@Secured(permissions = Pilares.Gente.SolicitacaoFolga.FEEDBACK_SOLICITACAO)
	public Response update(SolicitacaoFolga solicitacaoFolga) {
		if (service.update(solicitacaoFolga)) {
			return Response.ok("Solicitação atualizada com sucesso");
		} else {
			return Response.error("Erro ao atualizar a solicitação");
		}
	}

	@POST
	@Secured(permissions = {Pilares.Gente.SolicitacaoFolga.VISUALIZAR, Pilares.Gente.SolicitacaoFolga.REALIZAR,
			Pilares.Gente.SolicitacaoFolga.FEEDBACK_SOLICITACAO})
	@Path("/colaborador")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public List<SolicitacaoFolga> getByColaborador(@FormParam("cpf") Long cpf,
												   @FormParam("token") String token) {
		// Ignoramos o token nesse resource antigo
		return service.getByColaborador(cpf);
	}

	@POST
	@Secured(permissions = {Pilares.Gente.SolicitacaoFolga.VISUALIZAR, Pilares.Gente.SolicitacaoFolga.FEEDBACK_SOLICITACAO})
	@Path("/getAllByColaborador")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public List<SolicitacaoFolga> getAll(
			@FormParam("dataIncial") long dataInicial,
			@FormParam("dataFinal") long dataFinal,
			@FormParam("codUnidade") Long codUnidade,
			@FormParam("codEquipe") String codEquipe,
			@FormParam("status") String status,
			@FormParam("cpfColaborador") Long cpfColaborador) {
		return service.getAll(DateUtils.toLocalDate(new Date(dataInicial)), DateUtils.toLocalDate(new Date(dataFinal)),
				codUnidade, codEquipe, status, String.valueOf(cpfColaborador));
	}

	@POST
	@Secured(permissions = {Pilares.Gente.SolicitacaoFolga.VISUALIZAR, Pilares.Gente.SolicitacaoFolga.FEEDBACK_SOLICITACAO})
	@Path("/getAll")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public List<SolicitacaoFolga> getAll(
			@FormParam("dataIncial") long dataInicial,
			@FormParam("dataFinal") long dataFinal,
			@FormParam("codUnidade") Long codUnidade,
			@FormParam("codEquipe") String codEquipe,
			@FormParam("status") String status) {
		// Os requests realizados nesse resource antigo, estão enviando o nome da equipe no local do código da equipe
		// por isso precisamos disso antes:
		codEquipe = String.valueOf(new EmpresaService().getCodEquipeByCodUnidadeByNome(codUnidade, codEquipe));

		return service.getAll(DateUtils.toLocalDate(new Date(dataInicial)), DateUtils.toLocalDate(new Date(dataFinal)),
				codUnidade, codEquipe, status, "%");
	}

	@DELETE
	@Secured(permissions = Pilares.Gente.SolicitacaoFolga.REALIZAR)
	@Path("{codigo}")
	public Response delete(@PathParam("codigo") Long codigo) {
		if (service.delete(codigo)) {
			return Response.ok("Solicitação deletada com sucesso");
		} else {
			return Response.error("Erro ao deletar a solicitação");
		}
	}
}