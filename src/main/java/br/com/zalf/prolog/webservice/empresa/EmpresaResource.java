package br.com.zalf.prolog.webservice.empresa;

import br.com.zalf.prolog.webservice.colaborador.model.Cargo;
import br.com.zalf.prolog.webservice.colaborador.model.Empresa;
import br.com.zalf.prolog.webservice.colaborador.model.Equipe;
import br.com.zalf.prolog.webservice.colaborador.model.Setor;
import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.util.Platform;
import br.com.zalf.prolog.webservice.commons.util.UsedBy;
import br.com.zalf.prolog.webservice.interceptors.auth.AuthType;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.permissao.Visao;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Date;
import java.util.List;

/**
 * Created by luiz on 07/06/17.
 */
@Path("/empresas")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class EmpresaResource {

    private final EmpresaService service = new EmpresaService();

    @POST
    @Path("/unidades/{codUnidade}/equipes")
    @Secured(permissions = {Pilares.Gente.Equipe.CADASTRAR, Pilares.Gente.Equipe.EDITAR})
    public AbstractResponse insertEquipe(@PathParam("codUnidade") Long codUnidade, Equipe equipe) {
        return service.insertEquipe(codUnidade, equipe);
    }

    @PUT
    @Path("/unidades/{codUnidade}/equipes/{codEquipe}")
    @Secured(permissions = Pilares.Gente.Equipe.EDITAR)
    public Response updateEquipe(@PathParam("codUnidade") Long codUnidade, @PathParam("codEquipe") Long codEquipe, Equipe equipe) {
        if (service.updateEquipe(codUnidade, codEquipe, equipe)) {
            return Response.ok("Equipe editada com sucesso");
        } else {
            return Response.error("Erro ao editar a equipe");
        }
    }

    @GET
    @Secured(permissions = {Pilares.Gente.Equipe.VISUALIZAR, Pilares.Gente.Equipe.EDITAR, Pilares.Gente.Equipe.CADASTRAR})
    @Path("/unidades/{codUnidade}/equipes/{codEquipe}")
    public Equipe getEquipe(@PathParam("codUnidade") Long codUnidade, @PathParam("codEquipe") Long codEquipe) {
        return service.getEquipe(codUnidade, codEquipe);
    }

    @POST
    @Secured(permissions = {Pilares.Gente.Colaborador.CADASTRAR, Pilares.Gente.Colaborador.EDITAR})
    @Path("/unidades/{codUnidade}/setores")
    public AbstractResponse insertSetor(@PathParam("codUnidade") Long codUnidade, Setor setor) {
        return service.insertSetor(codUnidade, setor);
    }

    @PUT
    @Secured(permissions = {Pilares.Gente.Colaborador.CADASTRAR, Pilares.Gente.Colaborador.EDITAR})
    @Path("/unidades/{codUnidade}/setores/{codSetor}")
    public AbstractResponse updateSetor(@PathParam("codUnidade") Long codUnidade,
                                        @PathParam("codSetor") Long codSetor,
                                        Setor setor) {
        if (service.updateSetor(codUnidade, codSetor, setor)) {
            return Response.ok("Setor editado com sucesso");
        } else {
            return Response.error("Erro ao editar a setor");
        }
    }

    @GET
    @Secured(permissions = {Pilares.Gente.Colaborador.CADASTRAR, Pilares.Gente.Colaborador.EDITAR})
    @Path("/unidades/{codUnidade}/setores/{codSetor}")
    public Setor getSetor(@PathParam("codUnidade") Long codUnidade, @PathParam("codSetor") Long codSetor) {
        return service.getSetor(codUnidade, codSetor);
    }

    @GET
    @Secured(permissions = Pilares.Gente.Equipe.VISUALIZAR)
    @Path("/unidades/{codUnidade}/equipes")
    public List<Equipe> getEquipesByCodUnidade(@PathParam("codUnidade") Long codUnidade) {
        return service.getEquipesByCodUnidade(codUnidade);
    }

    @GET
    @Secured
    @Path("/unidades/{codUnidade}/cargos")
    public List<Cargo> getFuncoesByCodUnidade(@PathParam("codUnidade") Long codUnidade) {
        return service.getCargosByCodUnidade(codUnidade);
    }

    @GET
    @Secured
    @Path("/{codEmpresa}/cargos/{codCargo}")
    public Cargo getFuncoesByCodUnidade(@PathParam("codEmpresa") Long codEmpresa,
                                        @PathParam("codCargo") Long codCargo) {
        return service.getCargoByCodEmpresa(codEmpresa, codCargo);
    }

    @GET
    @Secured
    @Path("/unidades/{codUnidade}/visao")
    public Visao getVisaoByUnidade(@PathParam("codUnidade") Long codUnidade) {
        return service.getVisaoUnidade(codUnidade);
    }

    @GET
    @Secured
    @Path("/unidades/{codUnidade}/cargos/{codCargo}/visao")
    public Visao getVisaoByCargo(@PathParam("codUnidade") Long codUnidade,
                                 @PathParam("codCargo") Long codCargo) {
        return service.getVisaoCargo(codUnidade, codCargo);
    }

    @POST
    @Secured(permissions = {Pilares.Gente.Colaborador.CADASTRAR, Pilares.Gente.Colaborador.EDITAR})
    @Path("/unidades/{codUnidade}/cargos/{codCargo}/visao")
    public Response alterarVisaoCargo(Visao visao,
                                      @PathParam("codUnidade") Long codUnidade,
                                      @PathParam("codCargo") Long codCargo) {
        if (service.alterarVisaoCargo(visao, codUnidade, codCargo)) {
            return Response.ok("Alterações realizadas com sucesso");
        } else {
            return Response.error("Erro ao alterar permissões");
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
    @Secured(permissions = {
            Pilares.Frota.Checklist.VISUALIZAR_TODOS,
            Pilares.Frota.Checklist.REALIZAR,
            Pilares.Frota.Veiculo.VISUALIZAR,
            Pilares.Frota.Veiculo.ALTERAR,
            Pilares.Frota.Veiculo.CADASTRAR,
            Pilares.Frota.Checklist.VISUALIZAR_TODOS,
            Pilares.Frota.Checklist.REALIZAR,
            Pilares.Frota.OrdemServico.Pneu.VISUALIZAR,
            Pilares.Frota.OrdemServico.Pneu.CONSERTAR_ITEM,
            Pilares.Frota.OrdemServico.Checklist.VISUALIZAR,
            Pilares.Frota.OrdemServico.Checklist.CONSERTAR_ITEM,
            Pilares.Frota.Afericao.REALIZAR,
            Pilares.Frota.Afericao.VISUALIZAR,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR,
            Pilares.Entrega.Produtividade.CONSOLIDADO,
            Pilares.Entrega.Indicadores.INDICADORES,
            Pilares.Entrega.Relatorios.INDICADORES,
            Pilares.Entrega.Upload.VERIFICACAO_DADOS,
            Pilares.Gente.SolicitacaoFolga.VISUALIZAR,
            Pilares.Gente.SolicitacaoFolga.FEEDBACK_SOLICITACAO,
            Pilares.Gente.ProntuarioCondutor.VISUALIZAR_TODOS})
    @Path("/filtros/{cpf}")
    public List<Empresa> getFiltros(
            @PathParam("cpf") Long cpf) {
        return service.getFiltros(cpf);
    }

    @POST
    @Secured(permissions = {Pilares.Gente.Colaborador.CADASTRAR, Pilares.Gente.Colaborador.EDITAR})
    @Path("/funcoes/{codUnidade}")
    public AbstractResponse insertFuncao(Cargo cargo, @PathParam("codUnidade") Long codUnidade) {
        return service.insertFuncao(cargo, codUnidade);
    }

    @GET
    @UsedBy(platforms = Platform.ANDROID)
    @Secured(authTypes = {AuthType.BASIC, AuthType.BEARER})
    @Path("/unidades/{codUnidade}/current-time")
    public Date getCurrentTimeUnidade(@PathParam("codUnidade") final Long codUnidade) {
        // TODO: pegar o tempo de cada unidade de acordo com o TimeZone dela no Banco
        return new Date(System.currentTimeMillis());
    }
}