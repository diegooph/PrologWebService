package br.com.zalf.prolog.webservice.colaborador;

import br.com.zalf.prolog.webservice.commons.colaborador.Colaborador;
import br.com.zalf.prolog.webservice.commons.login.Autenticacao;
import br.com.zalf.prolog.webservice.commons.login.LoginHolder;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import br.com.zalf.prolog.webservice.autenticacao.AutenticacaoResource;
import br.com.zalf.prolog.webservice.autenticacao.AutenticacaoService;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.util.L;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Date;
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
			return Response.Ok("Colaborador inserido com sucesso");
		} else {
			return Response.Error("Erro ao inserir colaborador");
		}
	}
	
	@PUT
	@Path("/{cpf}")
	@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
	@Secured(permissions = { Pilares.Gente.Colaborador.EDITAR, Pilares.Gente.Colaborador.CADASTRAR })
	public Response update(@PathParam("cpf") Long cpfAntigo, Colaborador colaborador) {
		if (service.update(cpfAntigo, colaborador)) {
			return Response.Ok("Colaborador atualizado com sucesso");
		} else {
			return Response.Error("Erro ao atualizar o colaborador");
		}
	}
	
	@GET
	@Secured
	@Path("/getByCod/{cpf}")
	public Colaborador getByCod(@PathParam("cpf") Long cpf) {
		L.d(TAG, cpf.toString());
		return service.getByCod(cpf);
	}
	
	@GET
	@Secured
	@Path("/loginHolder/{cpf}")
	public LoginHolder getLoginHolder(@PathParam("cpf") Long cpf) {
		return service.getLoginHolder(cpf);
	}
	
	@GET
	@Path("/{codUnidade}/")
	@Secured(permissions = Pilares.Gente.Colaborador.VISUALIZAR)
	public List<Colaborador> getAll(@PathParam("codUnidade") Long codUnidade) {
		return service.getAll(codUnidade);
	}
	
	@DELETE
	@Path("/{cpf}")
	@Secured(permissions = { Pilares.Gente.Colaborador.EDITAR, Pilares.Gente.Colaborador.CADASTRAR })
 	public Response delete(@PathParam("cpf") Long cpf) {
		if (service.delete(cpf)) {
			return Response.Ok("Colaborador deletado com sucesso");
		} else {
			return Response.Error("Falha ao deletar colaborador");
		}
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
		
		L.d(TAG, String.valueOf(cpf) + "data: " + String.valueOf(dataNascimento));
		if (new AutenticacaoService().verifyLogin(cpf, new Date(dataNascimento))) {
			AutenticacaoService autenticacaoService = new AutenticacaoService();
			Autenticacao autenticacao = autenticacaoService.insertOrUpdate(cpf);
			L.d(TAG, autenticacao.getToken());
			return autenticacao;
		} else {
			return new Autenticacao(Autenticacao.ERROR, cpf, "-1");
		}
	}
}
