package br.com.zalf.prolog.webservice.empresa;

import br.com.zalf.prolog.webservice.colaborador.Empresa;
import br.com.zalf.prolog.webservice.colaborador.Equipe;
import br.com.zalf.prolog.webservice.colaborador.Funcao;
import br.com.zalf.prolog.webservice.colaborador.Setor;
import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.commons.network.Request;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.permissao.Visao;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilar;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
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
	public Response updateEquipe(@PathParam("codEquipe") Long codEquipe, Equipe equipe) {
		if(service.updateEquipe(codEquipe, equipe)){
			return Response.Ok("Equipe editada com sucesso");
		}else{
			return Response.Error("Errro ao editar a equipe");
		}
	}

	@POST
	@Secured(permissions = {Pilares.Gente.Colaborador.CADASTRAR, Pilares.Gente.Colaborador.EDITAR})
	@Path("/unidades/{codUnidade}/setores")
	public AbstractResponse insertSetor(@PathParam("codUnidade") Long codUnidade, Setor setor) {
		return service.insertSetor(codUnidade, setor);
	}

	@GET
	@Secured(permissions = Pilares.Gente.Equipe.VISUALIZAR)
	@Path("/unidades/{codUnidade}/equipes")
	public List<Equipe> getEquipesByCodUnidade(@PathParam("codUnidade") Long codUnidade) {
		return service.getEquipesByCodUnidade(codUnidade);
	}

	@GET
	@Secured
	@Path("/unidades/{codUnidade}/funcoes")
	public List<Funcao> getFuncoesByCodUnidade(@PathParam("codUnidade") Long codUnidade) {
		return service.getFuncoesByCodUnidade(codUnidade);
	}

	@GET
	@Secured
	@Path("/unidades/{codUnidade}/visao")
	public Visao getVisaoByUnidade(@PathParam("codUnidade") Long codUnidade){
		return service.getVisaoUnidade(codUnidade);
	}

	@GET
	@Secured
	@Path("/unidades/{codUnidade}/cargos/{codCargo}/visao")
	public Visao getVisaoByCargo(@PathParam("codUnidade") Long codUnidade,
								 @PathParam("codCargo") Long codCargo){
		return service.getVisaoCargo(codUnidade, codCargo);
	}

	@POST
	@Secured(permissions = {Pilares.Gente.Colaborador.CADASTRAR, Pilares.Gente.Colaborador.EDITAR})
	@Path("/unidades/{codUnidade}/cargos/{codCargo}/visao")
	public Response alterarVisaoCargo(Visao visao,
									  @PathParam("codUnidade") Long codUnidade,
									  @PathParam("codCargo") Long codCargo) {
		if (service.alterarVisaoCargo(visao, codUnidade, codCargo)) {
			return Response.Ok("Funções inseridas com sucesso");
		} else {
			return Response.Error("Erro ao inserir as funções");
		}
	}

	@GET
	@Secured
	@Path("/unidades/{codUnidade}/setores")
	public List<Setor> getSetorByCodUnidade(@PathParam("codUnidade") Long codUnidade) {
		return service.getSetorByCodUnidade(codUnidade);
	}

	@GET
	@Secured(permissions = Pilares.Entrega.Upload.VERIFICACAO_DADOS)
	@Path("/unidades/{codUnidade}/mapa-tracking/resumo-dados/{ano}/{mes}")
	public List<HolderMapaTracking> getResumoDadosMapaTracking(@PathParam("codUnidade") Long codUnidade,
															   @PathParam("ano") int ano,
															   @PathParam("mes") int mes) {
		return service.getResumoAtualizacaoDados(ano, mes, codUnidade);
	}

	@GET
	@Secured
	@Path("/filtros/{cpf}")
	public List<Empresa> getFiltros(
			@PathParam("cpf") Long cpf) {
		return service.getFiltros(cpf);
	}

	/**
	 * @deprecated in v0.0.10. Use {@link #insertSetor(Long, Setor)} instead
	 */
	@POST
	@Secured(permissions = Pilares.Gente.Equipe.VISUALIZAR)
	@Path("/getEquipesByCodUnidade/{codUnidade}")
	@Deprecated
	public List<Equipe> DEPRECATED_GET_EQUIPES_UNIDADE(@PathParam("codUnidade") Long codUnidade) {
		return service.getEquipesByCodUnidade(codUnidade);
	}

	/**
	 * @deprecated in v0.0.10. Use {@link #insertSetor(Long, Setor)} instead
	 */
	@POST
	@Secured(permissions = { Pilares.Gente.Colaborador.CADASTRAR, Pilares.Gente.Colaborador.EDITAR })
	@Path("/setores/{codUnidade}")
	@Deprecated
	public AbstractResponse DEPRECATED_INSERT_SETOR(String nome, @PathParam("codUnidade") Long codUnidade) {
		return service.insertSetor(nome,codUnidade);
	}

	/**
	 * @deprecated in v0.0.10. Use {@link #insertEquipe(Long, Equipe)} instead
	 */
	@POST
	@Path("/insertEquipe")
	@Secured(permissions = {Pilares.Gente.Equipe.CADASTRAR, Pilares.Gente.Equipe.EDITAR})
	@Deprecated
	public Response DEPRECATED_INSERT(Request<Equipe> request) {
		if (service.createEquipe(request)) {
			return Response.Ok("Equipe inserida com sucesso");
		} else {
			return Response.Error("Erro ao inserir equipe");
		}
	}

	/**
	 * @deprecated in v0.0.10. Use {@link #updateEquipe(Long, Equipe)} instead
	 */
	@PUT
	@Path("/updateEquipe")
	@Secured(permissions = Pilares.Gente.Equipe.EDITAR)
	@Deprecated
	public boolean DEPRECATED_UPDATE(Request<Equipe> request) {
		return service.updateEquipe(request);
	}


	/**
	 * @deprecated in v0.0.10. Use {@link #getFuncoesByCodUnidade(Long)} instead
	 */
	@GET
	@Secured
	@Path("/funcoes/{codUnidade}")
	@Deprecated
	public List<Funcao> DEPRECATED(@PathParam("codUnidade") Long codUnidade) {
		return service.getFuncoesByCodUnidade(codUnidade);
	}

	/**
	 * @deprecated in v0.0.10. Use {@link #getVisaoByUnidade(Long)} instead
	 */
	@GET
	@Secured
	@Path("/permissoes/{codUnidade}")
	@Deprecated
	public List<Pilar> DEPRECATED_VISAO_UNIDADE(@PathParam("codUnidade") Long codUnidade){
		return service.getVisaoUnidade(codUnidade).getPilares();
	}

	/**
	 * @deprecated in v0.0.10. Use {@link #getVisaoByCargo(Long, Long)} instead
	 */
	@GET
	@Secured
	@Path("/permissoes/{codUnidade}/{codCargo}")
	@Deprecated
	public List<Pilar> DEPRECATED_VISAO_CARGO(@PathParam("codUnidade") Long codUnidade,
											  @PathParam("codCargo") Long codCargo){
		return service.getVisaoCargo(codUnidade, codCargo).getPilares();
	}

	/**
	 * @deprecated in v0.0.10. Use {@link #getSetorByCodUnidade(Long)} instead
	 */
	@GET
	@Secured
	@Path("/setores/{codUnidade}")
	@Deprecated
	public List<Setor> DEPRECATED_SETORES_UNIDADE(@PathParam("codUnidade") Long codUnidade) {
		return service.getSetorByCodUnidade(codUnidade);
	}

	/**
	 * @deprecated in v0.0.10. Use {@link #getResumoDadosMapaTracking(Long, int, int)} instead
	 */
	@GET
	@Secured(permissions = Pilares.Entrega.Upload.VERIFICACAO_DADOS)
	@Path("/resumoDados/{codUnidade}/{ano}/{mes}")
	@Deprecated
	public List<HolderMapaTracking> DEPRECATED_RESUMO_DADOS_MAPA_TRACKING(@PathParam("ano")int ano,
																		  @PathParam("mes") int mes,
																		  @PathParam("codUnidade") Long codUnidade) {
		return service.getResumoAtualizacaoDados(ano, mes, codUnidade);
	}

	/**
	 * @deprecated in v0.0.10. Use {@link #alterarVisaoCargo(Visao, Long, Long)} instead
	 */
	@POST
	@Secured(permissions = {Pilares.Gente.Colaborador.CADASTRAR, Pilares.Gente.Colaborador.EDITAR})
	@Path("/funcoesProlog/{codUnidade}/{codCargo}")
	@Deprecated
	public Response DEPRECATED_ALTERAR_VISAO_CARGO(List<Pilar> pilares,
												   @PathParam("codUnidade") Long codUnidade,
												   @PathParam("codCargo") Long codCargo) {
		Visao visao = new Visao();
		visao.setPilares(pilares);
		if (service.alterarVisaoCargo(visao, codUnidade, codCargo)) {
			return Response.Ok("Funções inseridas com sucesso");
		} else {
			return Response.Error("Erro ao inserir as funções");
		}
	}
}