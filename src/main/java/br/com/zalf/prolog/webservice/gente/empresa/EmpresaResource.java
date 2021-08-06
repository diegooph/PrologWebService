package br.com.zalf.prolog.webservice.gente.empresa;

import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.network.metadata.Required;
import br.com.zalf.prolog.webservice.gente.colaborador.model.Cargo;
import br.com.zalf.prolog.webservice.gente.colaborador.model.Empresa;
import br.com.zalf.prolog.webservice.gente.colaborador.model.Equipe;
import br.com.zalf.prolog.webservice.gente.colaborador.model.Setor;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.interceptors.debug.ConsoleDebugLog;
import br.com.zalf.prolog.webservice.permissao.Visao;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by luiz on 07/06/17.
 */
@ConsoleDebugLog
@Path("/v2/empresas")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class EmpresaResource {

    private final EmpresaService service = new EmpresaService();

    @POST
    @Path("/unidades/{codUnidade}/equipes")
    @Secured(permissions = {Pilares.Gente.Equipe.CADASTRAR, Pilares.Gente.Equipe.EDITAR})
    public AbstractResponse insertEquipe(@PathParam("codUnidade") final Long codUnidade, final Equipe equipe) {
        return service.insertEquipe(codUnidade, equipe);
    }

    @PUT
    @Path("/unidades/{codUnidade}/equipes/{codEquipe}")
    @Secured(permissions = Pilares.Gente.Equipe.EDITAR)
    public Response updateEquipe(@PathParam("codUnidade") final Long codUnidade,
                                 @PathParam("codEquipe") final Long codEquipe,
                                 final Equipe equipe) {
        if (service.updateEquipe(codUnidade, codEquipe, equipe)) {
            return Response.ok("Equipe editada com sucesso");
        } else {
            return Response.error("Erro ao editar a equipe");
        }
    }

    @GET
    @Secured(permissions = {
            Pilares.Gente.Equipe.VISUALIZAR,
            Pilares.Gente.Equipe.CADASTRAR,
            Pilares.Gente.Equipe.EDITAR,
            Pilares.Gente.Colaborador.VISUALIZAR,
            Pilares.Gente.Colaborador.CADASTRAR,
            Pilares.Gente.Colaborador.EDITAR})
    @Path("/unidades/{codUnidade}/equipes/{codEquipe}")
    public Equipe getEquipe(@PathParam("codUnidade") final Long codUnidade,
                            @PathParam("codEquipe") final Long codEquipe) {
        return service.getEquipe(codUnidade, codEquipe);
    }

    @POST
    @Secured(permissions = {Pilares.Gente.Colaborador.CADASTRAR, Pilares.Gente.Colaborador.EDITAR})
    @Path("/unidades/{codUnidade}/setores")
    public AbstractResponse insertSetor(@PathParam("codUnidade") final Long codUnidade, final Setor setor) {
        return service.insertSetor(codUnidade, setor);
    }

    @PUT
    @Secured(permissions = {Pilares.Gente.Colaborador.CADASTRAR, Pilares.Gente.Colaborador.EDITAR})
    @Path("/unidades/{codUnidade}/setores/{codSetor}")
    public AbstractResponse updateSetor(@PathParam("codUnidade") final Long codUnidade,
                                        @PathParam("codSetor") final Long codSetor,
                                        final Setor setor) {
        if (service.updateSetor(codUnidade, codSetor, setor)) {
            return Response.ok("Setor editado com sucesso");
        } else {
            return Response.error("Erro ao editar a setor");
        }
    }

    @GET
    @Secured(permissions = {Pilares.Gente.Colaborador.CADASTRAR, Pilares.Gente.Colaborador.EDITAR})
    @Path("/unidades/{codUnidade}/setores/{codSetor}")
    public Setor getSetor(@PathParam("codUnidade") final Long codUnidade, @PathParam("codSetor") final Long codSetor) {
        return service.getSetor(codUnidade, codSetor);
    }

    @GET
    @Secured(permissions = {
            Pilares.Gente.Equipe.VISUALIZAR,
            Pilares.Gente.Colaborador.VISUALIZAR,
            Pilares.Gente.Colaborador.CADASTRAR,
            Pilares.Gente.Colaborador.EDITAR,
            Pilares.Frota.Checklist.VISUALIZAR_TODOS,
            Pilares.Seguranca.Relato.VISUALIZAR,
            Pilares.Seguranca.Relato.RELATORIOS})
    @Path("/unidades/{codUnidade}/equipes")
    public List<Equipe> getEquipesByCodUnidade(@PathParam("codUnidade") final Long codUnidade) {
        return service.getEquipesByCodUnidade(codUnidade);
    }

    @GET
    @Secured
    @Path("/{codEmpresa}/cargos/{codCargo}")
    public Cargo getFuncoesByCodUnidade(@PathParam("codEmpresa") final Long codEmpresa,
                                        @PathParam("codCargo") final Long codCargo) {
        return service.getCargoByCodEmpresa(codEmpresa, codCargo);
    }

    @GET
    @Secured
    @Path("/unidades/{codUnidade}/visao")
    public Visao getVisaoByUnidade(@PathParam("codUnidade") final Long codUnidade) {
        return service.getVisaoUnidade(codUnidade);
    }

    @GET
    @Secured
    @Path("/unidades/{codUnidade}/cargos/{codCargo}/visao")
    public Visao getVisaoByCargo(@PathParam("codUnidade") final Long codUnidade,
                                 @PathParam("codCargo") final Long codCargo) {
        return service.getVisaoCargo(codUnidade, codCargo);
    }

    @POST
    @Secured(permissions = {Pilares.Gente.Colaborador.CADASTRAR, Pilares.Gente.Colaborador.EDITAR,
            Pilares.Gente.Permissao.VINCULAR_CARGO})
    @Path("/unidades/{codUnidade}/cargos/{codCargo}/visao")
    public Response alterarVisaoCargo(final Visao visao,
                                      @PathParam("codUnidade") final Long codUnidade,
                                      @PathParam("codCargo") final Long codCargo) {
        if (service.alterarVisaoCargo(visao, codUnidade, codCargo)) {
            return Response.ok("Alterações realizadas com sucesso");
        } else {
            return Response.error("Erro ao alterar permissões");
        }
    }

    @GET
    @Secured
    @Path("/unidades/{codUnidade}/setores")
    public List<Setor> getSetorByCodUnidade(@PathParam("codUnidade") final Long codUnidade) {
        return service.getSetorByCodUnidade(codUnidade);
    }

    @GET
    @Secured(permissions = Pilares.Entrega.Upload.VERIFICACAO_DADOS)
    @Path("/unidades/{codUnidade}/mapa-tracking/resumo-dados/{ano}/{mes}")
    public List<HolderMapaTracking> getResumoDadosMapaTracking(@PathParam("codUnidade") final Long codUnidade,
                                                               @PathParam("ano") final int ano,
                                                               @PathParam("mes") final int mes) {
        return service.getResumoAtualizacaoDados(ano, mes, codUnidade);
    }

    /**
     * Deixamos apenas o {@link Secured} nesse método pois muitas funcionalidades no ProLog utilizam da busca de filtros
     * e seria muito difícil de manter um tracking de todas aqui.
     */
    @GET
    @Secured
    @Path("/filtros/{cpf}")
    public List<Empresa> getFiltros(@HeaderParam("Authorization") @Required final String userToken,
                                    @PathParam("cpf") final Long cpf) {
        return service.getFiltros(userToken, cpf);
    }

    @POST
    @Secured(permissions = {Pilares.Gente.Colaborador.CADASTRAR, Pilares.Gente.Colaborador.EDITAR})
    @Path("/funcoes/{codUnidade}")
    public AbstractResponse insertFuncao(final Cargo cargo, @PathParam("codUnidade") final Long codUnidade) {
        return service.insertFuncao(cargo, codUnidade);
    }
}