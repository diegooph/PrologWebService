package br.com.zalf.prolog.webservice.frota.socorrorota;

import br.com.zalf.prolog.webservice.commons.network.ResponseWithCod;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.util.Platform;
import br.com.zalf.prolog.webservice.commons.util.Required;
import br.com.zalf.prolog.webservice.commons.util.UsedBy;
import br.com.zalf.prolog.webservice.frota.socorrorota._model.*;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.interceptors.log.DebugLog;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created on 09/12/19.
 *
 * @author Wellington Moraes (https://github.com/wvinim)
 */
@Path("socorro-rota")
@DebugLog
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
public final class SocorroRotaResource {
    @NotNull
    private SocorroRotaService service = new SocorroRotaService();

    /**
     * Resource para realizar a abertura de uma solicitação de socorro.
     */
    @POST
    @Secured(permissions = Pilares.Frota.SocorroRota.SOLICITAR_SOCORRO)
    @UsedBy(platforms = {Platform.ANDROID, Platform.WEBSITE})
    @Path("/abertura")
    public ResponseWithCod aberturaSocorro(@Required final SocorroRotaAbertura socorroRotaAbertura) {
        return service.aberturaSocorro(socorroRotaAbertura);
    }

    /**
    * Resource para buscar as unidades disponíveis para a abertura de socorro por colaborador
    * */
    @GET
    @Path("/abertura/unidades-selecao")
    public List<UnidadeAberturaSocorro> getUnidadesDisponiveisAberturaSocorroByCodColaborador(
            @QueryParam("codColaborador") @Required final Long codColaborador){

        return service.getUnidadesDisponiveisAberturaSocorroByCodColaborador(codColaborador);
    }

    /**
     * Resource para buscar os veículos disponíveis para a abertura de socorro por unidade
     * */
    @GET
    @Path("/abertura/veiculos-selecao")
    public List<VeiculoAberturaSocorro> getVeiculosDisponiveisAberturaSocorroByUnidade(
            @QueryParam("codUnidade") @Required final Long codUnidade) {

        return service.getVeiculosDisponiveisAberturaSocorroByUnidade(codUnidade);
    }

    /**
     * Resource para buscar as opções de problemas disponíveis para a abertura de socorro por empresa
     * */
    @GET
    @Path("/abertura/opcoes-problemas")
    public List<OpcaoProblemaAberturaSocorro> getOpcoesProblemasDisponiveisAberturaSocorroByEmpresa(
            @QueryParam("codEmpresa") @Required final Long codEmpresa){

        return service.getOpcoesProblemasDisponiveisAberturaSocorroByEmpresa(codEmpresa);
    }

    /**
     * Resource para buscar uma lista de socorros em rota por data inicial, final e unidades
     * */
    @GET
    @Secured(permissions = {
            Pilares.Frota.SocorroRota.SOLICITAR_SOCORRO,
            Pilares.Frota.SocorroRota.TRATAR_SOCORRO,
            Pilares.Frota.SocorroRota.VISUALIZAR_SOCORROS_E_RELATORIOS})
    @UsedBy(platforms = {Platform.ANDROID, Platform.WEBSITE})
    public List<SocorroRotaListagem> getListagemSocorroRota(
            @QueryParam("codUnidades") @Required final List<Long> codUnidades,
            @QueryParam("dataInicial") @Required final String dataInicial,
            @QueryParam("dataFinal") @Required final String dataFinal,
            @HeaderParam("Authorization") @Required final String userToken){
        return service.getListagemSocorroRota(codUnidades, dataInicial, dataFinal, userToken);
    }

    /**
     * Resource para realizar a invalidação de uma solicitação de socorro.
     */
    @POST
    @Secured(permissions = {Pilares.Frota.SocorroRota.SOLICITAR_SOCORRO, Pilares.Frota.SocorroRota.TRATAR_SOCORRO})
    @UsedBy(platforms = {Platform.ANDROID, Platform.WEBSITE})
    @Path("/invalidacao")
    public ResponseWithCod invalidacaoSocorro(@Required final SocorroRotaInvalidacao socorroRotaInvalidacao) {
        return service.invalidacaoSocorro(socorroRotaInvalidacao);
    }

    /**
     * Resource para realizar o atendimento de uma solicitação de socorro.
     */
    @POST
    @Secured(permissions = {Pilares.Frota.SocorroRota.TRATAR_SOCORRO})
    @UsedBy(platforms = {Platform.ANDROID, Platform.WEBSITE})
    @Path("/atendimento")
    public ResponseWithCod atendimentoSocorro(@Required final SocorroRotaAtendimento socorroRotaAtendimento) {
        return service.atendimentoSocorro(socorroRotaAtendimento);
    }

    /**
     * Resource para realizar a finalização de uma solicitação de socorro.
     */
    @POST
    @Secured(permissions = {Pilares.Frota.SocorroRota.TRATAR_SOCORRO})
    @UsedBy(platforms = {Platform.ANDROID, Platform.WEBSITE})
    @Path("/finalizacao")
    public ResponseWithCod finalizacaoSocorro(@Required final SocorroRotaFinalizacao socorroRotaFinalizacao) {
        return service.finalizacaoSocorro(socorroRotaFinalizacao);
    }

    /**
     * Resource para realizar a visualização de uma solicitação de socorro.
     */
    @GET
    @Secured(permissions = {Pilares.Frota.SocorroRota.VISUALIZAR_SOCORROS_E_RELATORIOS})
    @UsedBy(platforms = {Platform.ANDROID, Platform.WEBSITE})
    @Path("/visualizacao")
    public SocorroRotaVisualizacao getVisualizacaoSocorroRota(
            @QueryParam("codSocorroRota") @Required final Long codSocorroRota){
        return service.getVisualizacaoSocorroRota(codSocorroRota);
    }

    /**
     * Resource para buscar as opções de problemas por empresa.
     * */
    @GET
    @Secured(permissions = {Pilares.Frota.SocorroRota.TRATAR_SOCORRO,
                            Pilares.Frota.SocorroRota.GERENCIAR_OPCOES_PROBLEMAS})
    @UsedBy(platforms = {Platform.ANDROID, Platform.WEBSITE})
    @Path("/opcoes-problemas/")
    public List<OpcaoProblemaSocorroRota> getOpcoesProblemasSocorroRotaByEmpresa(
            @QueryParam("codEmpresa") @Required final Long codEmpresa){
        return service.getOpcoesProblemasSocorroRotaByEmpresa(codEmpresa);
    }

    /**
     * Resource para buscar uma opção de problema específica.
     * */
    @GET
    @Secured(permissions = {Pilares.Frota.SocorroRota.TRATAR_SOCORRO,
            Pilares.Frota.SocorroRota.GERENCIAR_OPCOES_PROBLEMAS})
    @UsedBy(platforms = {Platform.ANDROID, Platform.WEBSITE})
    @Path("/opcao-problema/")
    public OpcaoProblemaSocorroRotaVisualizacao getOpcaoProblemaSocorroRotaVisualizacao(
            @QueryParam("codOpcaoProblema") @Required final Long codOpcaoProblema){
        return service.getOpcaoProblemaSocorroRotaVisualizacao(codOpcaoProblema);
    }

    /**
     * Resource para adicionar uma opção de problema.
     */
    @POST
    @Secured(permissions = Pilares.Frota.SocorroRota.GERENCIAR_OPCOES_PROBLEMAS)
    @UsedBy(platforms = {Platform.ANDROID, Platform.WEBSITE})
    @Path("/opcoes-problemas/")
    public ResponseWithCod insertOpcoesProblemas(
            @Required @Valid final OpcaoProblemaSocorroRotaCadastro opcaoProblemaSocorroRotaCadastro) {
        return service.insertOpcoesProblemas(opcaoProblemaSocorroRotaCadastro);
    }

    /**
     * Resource para editar uma opção de problema.
     */
    @PUT
    @Secured(permissions = Pilares.Frota.SocorroRota.GERENCIAR_OPCOES_PROBLEMAS)
    @Path("/opcoes-problemas/")
    public Response updateOpcoesProblemas(
            @Required @Valid final OpcaoProblemaSocorroRotaEdicao opcaoProblemaSocorroRotaEdicao) {
        return service.updateOpcoesProblemas(opcaoProblemaSocorroRotaEdicao);
    }
}