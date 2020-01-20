package br.com.zalf.prolog.webservice.frota.socorrorota.opcaoproblema;

import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.network.ResponseWithCod;
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
@Path("socorro-rota/opcoes-problemas")
@DebugLog
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
public final class OpcaoProblemaResource {
    @NotNull
    private OpcaoProblemaService service = new OpcaoProblemaService();


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
     * Resource para buscar as opções de problemas por empresa.
     * */
    @GET
    @Secured(permissions = {Pilares.Frota.SocorroRota.TRATAR_SOCORRO,
                            Pilares.Frota.SocorroRota.GERENCIAR_OPCOES_PROBLEMAS})
    @UsedBy(platforms = {Platform.ANDROID, Platform.WEBSITE})
    public List<OpcaoProblemaSocorroRotaListagem> getOpcoesProblemasSocorroRotaByEmpresa(
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

    /**
     * Resource para ativar/inativar uma opção de problema.
     */
    @PUT
    @UsedBy(platforms = Platform.WEBSITE)
    @Secured(permissions = Pilares.Frota.SocorroRota.GERENCIAR_OPCOES_PROBLEMAS)
    @Path("/opcoes-problemas/status-ativo")
    public Response updateStatusOpcoesProblemas(
            @Required final OpcaoProblemaSocorroRotaStatus opcaoProblemaSocorroRotaStatus) {
        return service.updateStatusOpcoesProblemas(opcaoProblemaSocorroRotaStatus);
    }
}