package br.com.zalf.prolog.webservice.colaborador;

import br.com.zalf.prolog.webservice.autenticacao.Autenticacao;
import br.com.zalf.prolog.webservice.autenticacao.AutenticacaoResource;
import br.com.zalf.prolog.webservice.autenticacao.AutenticacaoService;
import br.com.zalf.prolog.webservice.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.colaborador.model.LoginHolder;
import br.com.zalf.prolog.webservice.colaborador.model.LoginRequest;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/colaboradores")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class ColaboradorResource {

	private static final String TAG = ColaboradorResource.class.getSimpleName();
	private ColaboradorService service = new ColaboradorService();

	@POST
	@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
	@Secured(permissions = Pilares.Gente.Colaborador.CADASTRAR)
	public Response insert(Colaborador colaborador) {
		if (service.insert(colaborador)) {
			return Response.ok("Colaborador inserido com sucesso");
		} else {
			return Response.error("Erro ao inserir colaborador");
		}
	}
	
	@PUT
	@Path("/{cpf}")
	@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
	@Secured(permissions = { Pilares.Gente.Colaborador.EDITAR, Pilares.Gente.Colaborador.CADASTRAR })
	public Response update(@PathParam("cpf") Long cpfAntigo, Colaborador colaborador) {
		if (service.update(cpfAntigo, colaborador)) {
			return Response.ok("Colaborador atualizado com sucesso");
		} else {
			return Response.error("Erro ao atualizar o colaborador");
		}
	}
	
	@GET
	@Secured
	@Path("/getByCod/{cpf}")
	public Colaborador getByCpf(@PathParam("cpf") Long cpf) {
		Log.d(TAG, cpf.toString());
		return service.getByCpf(cpf);
	}

	@POST
	@Secured
	@Path("/login/app")
	public LoginHolder getLoginHolder(LoginRequest loginRequest) {
		return service.getLoginHolder(loginRequest);
	}
	
	@GET
	@Path("/{codUnidade}/")
	@Secured(permissions = Pilares.Gente.Colaborador.VISUALIZAR)
	public List<Colaborador> getAll(@PathParam("codUnidade") Long codUnidade,
									@QueryParam("apenasAtivos") Boolean apenasAtivos) {
		return service.getAll(codUnidade, apenasAtivos);
	}

	@GET
	@Path("/{codUnidade}/motoristas-e-ajudantes")
	@Secured(permissions = Pilares.Gente.Colaborador.VISUALIZAR)
	public List<Colaborador> getMotoristasAndAjudantes(@PathParam("codUnidade") Long codUnidade) {
		return service.getMotoristasAndAjudantes(codUnidade);
	}
	
	@DELETE
	@Path("/{cpf}")
	@Secured(permissions = { Pilares.Gente.Colaborador.EDITAR, Pilares.Gente.Colaborador.CADASTRAR })
 	public Response delete(@PathParam("cpf") Long cpf) {
		if (service.delete(cpf)) {
			return Response.ok("Colaborador deletado com sucesso");
		} else {
			return Response.error("Falha ao deletar colaborador");
		}
	}

	/**
	 * @deprecated in v0.0.29. Use {@link #getLoginHolder(LoginRequest)} instead.
	 */
	@GET
	@Secured
	@Path("/loginHolder/{cpf}")
	@Deprecated
	public LoginHolder DEPRECATE_GET_LOGIN_HOLDER(@PathParam("cpf") Long cpf) {
		return service.getLoginHolder(cpf);
	}

	/**
	 * @deprecated in v0.0.10. Use {@link AutenticacaoResource#verifyLogin(Long, long)} instead
	 */
	@POST
	@Path("/verifyLogin")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Deprecated
	public Autenticacao DEPRECATED_VERIFY_LOGIN(@FormParam("cpf") Long cpf,
												@FormParam("dataNascimento") long dataNascimento) {
		
		Log.d(TAG, String.valueOf(cpf) + "data: " + String.valueOf(dataNascimento));
		if (new AutenticacaoService().verifyIfUserExists(cpf, dataNascimento, true)) {
			AutenticacaoService autenticacaoService = new AutenticacaoService();
			Autenticacao autenticacao = autenticacaoService.insertOrUpdate(cpf);
			Log.d(TAG, autenticacao.getToken());
			return autenticacao;
		} else {
			return new Autenticacao(Autenticacao.ERROR, cpf, "-1");
		}
	}
}
