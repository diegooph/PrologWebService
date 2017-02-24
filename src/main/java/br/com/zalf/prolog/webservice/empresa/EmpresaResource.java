package br.com.zalf.prolog.webservice.empresa;

import br.com.zalf.prolog.commons.colaborador.Empresa;
import br.com.zalf.prolog.commons.colaborador.Equipe;
import br.com.zalf.prolog.commons.colaborador.Funcao;
import br.com.zalf.prolog.commons.colaborador.Setor;
import br.com.zalf.prolog.commons.imports.HolderMapaTracking;
import br.com.zalf.prolog.commons.network.AbstractResponse;
import br.com.zalf.prolog.commons.network.Request;
import br.com.zalf.prolog.commons.network.Response;
import br.com.zalf.prolog.permissao.pilares.Pilar;
import br.com.zalf.prolog.permissao.pilares.Pilares;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.SQLException;
import java.util.List;

@Path("/empresa")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class EmpresaResource {

	private EmpresaService service = new EmpresaService();

    @POST
    @Path("/unidades/{codUnidade}/equipes")
    @Secured(permissions = {Pilares.Gente.Equipe.CADASTRAR, Pilares.Gente.Equipe.EDITAR})
    public Response insertEquipe(@PathParam("codUnidade") Long codUnidade, Equipe equipe) {
        if (service.insertEquipe(codUnidade, equipe)) {
            return Response.Ok("Equipe inserida com sucesso");
        } else {
            return Response.Error("Erro ao inserir equipe");
        }
    }

    @PUT
    @Path("/equipes/{codEquipe}")
    @Secured(permissions = Pilares.Gente.Equipe.EDITAR)
    public boolean updateEquipe(@PathParam("codEquipe") Long codEquipe, Equipe equipe) throws SQLException {
        return service.updateEquipe(codEquipe, equipe);
    }

    @POST
    @Path("/insertEquipe")
    @Secured(permissions = {Pilares.Gente.Equipe.CADASTRAR, Pilares.Gente.Equipe.EDITAR})
    @Deprecated
    public Response createEquipe(Request<Equipe> request) {
        if (service.createEquipe(request)) {
            return Response.Ok("Equipe inserida com sucesso");
        } else {
            return Response.Error("Erro ao inserir equipe");
        }
    }

	@PUT
	@Path("/updateEquipe")
	@Secured(permissions = Pilares.Gente.Equipe.EDITAR)
    @Deprecated
	public boolean updateEquipe (Request<Equipe> request) throws SQLException {
		return service.updateEquipe(request);
	}

    @POST
    @Secured(permissions = Pilares.Gente.Equipe.VISUALIZAR)
    @Path("/getEquipesByCodUnidade/{codUnidade}")
    @Deprecated
    public List<Equipe> getEquipesByCodUnidade(@PathParam("codUnidade") Long codUnidade) throws SQLException {
        return service.getEquipesByCodUnidade(codUnidade);
    }

	@GET
	@Secured
	@Path("/funcoes/{codUnidade}")
	public List<Funcao> getFuncoesByCodUnidade(
			@PathParam("codUnidade") Long codUnidade) {
		return service.getFuncoesByCodUnidade(codUnidade);
	}

	@GET
	@Secured
	@Path("/permissoes/{codUnidade}")
	public List<Pilar> getPermissoesByUnidade(@PathParam("codUnidade") Long codUnidade){
		return service.getPermissoesByUnidade(codUnidade);
	}

	@GET
	@Secured
	@Path("/permissoes/{codUnidade}/{codCargo}")
	public List<Pilar> getPermissoesByCargo(@PathParam("codUnidade") Long codUnidade,
											@PathParam("codCargo") Long codCargo){
		return service.getPermissoesByCargo(codUnidade, codCargo);
	}

	@GET
	@Secured
	@Path("/setores/{codUnidade}")
	public List<Setor> getSetorByCodUnidade(@PathParam("codUnidade") Long codUnidade) {
		return service.getSetorByCodUnidade(codUnidade);
	}

	@POST
	@Secured(permissions = { Pilares.Gente.Colaborador.CADASTRAR, Pilares.Gente.Colaborador.EDITAR })
	@Path("/setores/{codUnidade}")
	public AbstractResponse insertSetor(String nome, @PathParam("codUnidade") Long codUnidade) {
		return service.insertSetor(nome,codUnidade);
	}

	@GET
	@Secured(permissions = Pilares.Entrega.Upload.VERIFICACAO_DADOS)
	@Path("/resumoDados/{codUnidade}/{ano}/{mes}")
	public List<HolderMapaTracking> getResumoAtualizacaoDados(@PathParam("ano")int ano,
															  @PathParam("mes") int mes,
															  @PathParam("codUnidade") Long codUnidade) {
			return service.getResumoAtualizacaoDados(ano, mes, codUnidade);
	}

	@GET
	@Secured
	@Path("/filtros/{cpf}")
	public List<Empresa> getFiltros(
			@PathParam("cpf") Long cpf) {
		return service.getFiltros(cpf);
	}

	@POST
	@Secured(permissions = { Pilares.Gente.Colaborador.CADASTRAR, Pilares.Gente.Colaborador.EDITAR })
	@Path("/funcoesProlog/{codUnidade}/{codCargo}")
	public Response insertOrUpdateCargoFuncaoProlog(List<Pilar> pilares,
												   @PathParam("codUnidade") Long codUnidade,
												   @PathParam("codCargo") Long codCargo) throws SQLException{
		if (service.insertOrUpdateCargoFuncaoProlog(pilares, codUnidade, codCargo)) {
			return Response.Ok("Funções inseridas com sucesso");
		} else {
			return Response.Error("Erro ao inserir as funções");
		}
	}
}