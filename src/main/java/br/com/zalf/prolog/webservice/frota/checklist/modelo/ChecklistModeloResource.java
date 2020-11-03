package br.com.zalf.prolog.webservice.frota.checklist.modelo;

import br.com.zalf.prolog.webservice.commons.imagens.Galeria;
import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.util.Platform;
import br.com.zalf.prolog.webservice.commons.util.Required;
import br.com.zalf.prolog.webservice.commons.util.UsedBy;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.ModeloChecklistListagem;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.edicao.ModeloChecklistEdicao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.insercao.ModeloChecklistInsercao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.insercao.ResponseInsertModeloChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.insercao.ResultInsertModeloChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.realizacao.ModeloChecklistRealizacao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.realizacao.ModeloChecklistSelecao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.visualizacao.ModeloChecklistVisualizacao;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.interceptors.debug.ConsoleDebugLog;
import br.com.zalf.prolog.webservice.log.LogRequest;
import br.com.zalf.prolog.webservice.log._model.LogLevel;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import com.google.common.base.Preconditions;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.InputStream;
import java.util.List;

@ConsoleDebugLog
@Path("/checklists/modelos")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public final class ChecklistModeloResource {
    @NotNull
    private final ChecklistModeloService service = new ChecklistModeloService();

    @POST
    @LogRequest(logLevel = LogLevel.BODY)
    @UsedBy(platforms = Platform.WEBSITE)
    @Secured(permissions = {Pilares.Frota.Checklist.Modelo.ALTERAR, Pilares.Frota.Checklist.Modelo.CADASTRAR})
    public ResponseInsertModeloChecklist insertModeloChecklist(
            @HeaderParam("Authorization") @Required final String token,
            @Required final ModeloChecklistInsercao modeloChecklist) throws ProLogException {
        final ResultInsertModeloChecklist result = service.insertModeloChecklist(modeloChecklist, token);
        return ResponseInsertModeloChecklist.ok(result, "Modelo de checklist inserido com sucesso");
    }

    @PUT
    @LogRequest(logLevel = LogLevel.BODY)
    @UsedBy(platforms = Platform.WEBSITE)
    @Secured(permissions = {
            Pilares.Frota.Checklist.Modelo.VISUALIZAR,
            Pilares.Frota.Checklist.Modelo.ALTERAR,
            Pilares.Frota.Checklist.Modelo.CADASTRAR})
    @Path("/{codUnidade}/{codModelo}")
    public Response updateModeloChecklist(
            @HeaderParam("Authorization") @Required final String token,
            @PathParam("codUnidade") @Required final Long codUnidade,
            @PathParam("codModelo") @Required final Long codModelo,
            @Required final ModeloChecklistEdicao modeloChecklist) throws ProLogException {
        return service.updateModeloChecklist(codUnidade, codModelo, modeloChecklist, token);
    }

    @GET
    @UsedBy(platforms = Platform.WEBSITE)
    @Secured(permissions = {
            Pilares.Frota.Checklist.Modelo.VISUALIZAR,
            Pilares.Frota.Checklist.Modelo.ALTERAR,
            Pilares.Frota.Checklist.Modelo.CADASTRAR})
    @Path("/{codUnidade}")
    public List<ModeloChecklistListagem> getModelosChecklistByCodUnidade(
            @PathParam("codUnidade") @Required final Long codUnidade) throws ProLogException {
        return service.getModelosChecklistListagemByCodUnidade(codUnidade);
    }

    @GET
    @UsedBy(platforms = Platform.WEBSITE)
    @Secured(permissions = {
            Pilares.Frota.Checklist.Modelo.VISUALIZAR,
            Pilares.Frota.Checklist.Modelo.ALTERAR,
            Pilares.Frota.Checklist.Modelo.CADASTRAR})
    @Path("/{codUnidade}/{codModelo}")
    public ModeloChecklistVisualizacao getModeloChecklist(
            @PathParam("codUnidade") @Required final Long codUnidade,
            @PathParam("codModelo") @Required final Long codModelo) throws ProLogException {
        return service.getModeloChecklist(codUnidade, codModelo);
    }

    @PUT
    @UsedBy(platforms = Platform.WEBSITE)
    @Secured(permissions = {Pilares.Frota.Checklist.Modelo.ALTERAR})
    @Path("/{codUnidade}/{codModelo}/status-ativo")
    public Response updateStatus(
            @PathParam("codUnidade") @Required final Long codUnidade,
            @PathParam("codModelo") @Required final Long codModelo,
            @Required final ModeloChecklistEdicao modeloChecklist) throws ProLogException {
        return service.updateStatusAtivo(codUnidade, codModelo, modeloChecklist);
    }

    @GET
    @UsedBy(platforms = Platform.WEBSITE)
    @Secured(permissions = {Pilares.Frota.Checklist.Modelo.ALTERAR, Pilares.Frota.Checklist.Modelo.CADASTRAR})
    @Path("/prolog")
    public List<ModeloChecklistVisualizacao> getModelosChecklistProLog() throws ProLogException {
        return service.getModelosChecklistProLog();
    }

    @GET
    @UsedBy(platforms = Platform.ANDROID)
    @Secured(permissions = {Pilares.Frota.Checklist.REALIZAR})
    @Path("/selecao-realizacao")
    public List<ModeloChecklistSelecao> getModelosSelecaoRealizacao(
            @QueryParam("codUnidade") @Required final Long codUnidade,
            @QueryParam("codCargo") @Required final Long codCargo,
            @HeaderParam("Authorization") final String userToken) {
        return service.getModelosSelecaoRealizacao(codUnidade, codCargo, userToken);
    }

    @GET
    @Path("/realizacao")
    @Secured(permissions = Pilares.Frota.Checklist.REALIZAR)
    public ModeloChecklistRealizacao getModeloChecklistRealizacao(
            @QueryParam("codModeloChecklist") @Required final Long codModeloChecklist,
            @QueryParam("codVeiculo") @Required final Long codVeiculo,
            @QueryParam("placaVeiculo") @Required final String placaVeiculo,
            @QueryParam("tipoChecklist") @Required final String tipoChecklist,
            @HeaderParam("Authorization") @Required final String userToken) {
        return service.getModeloChecklistRealizacao(codModeloChecklist, codVeiculo, placaVeiculo, tipoChecklist, userToken);
    }

    //
    // Métodos referente ao uso da galeria.
    //
    @GET
    @Path("/url-imagens/{codUnidade}/{codFuncao}")
    @UsedBy(platforms = Platform.ANDROID)
    @Secured(permissions = Pilares.Frota.Checklist.REALIZAR)
    public List<String> getUrlImagensPerguntas(
            @PathParam("codUnidade") @Required final Long codUnidade,
            @PathParam("codFuncao") @Required final Long codFuncao) throws ProLogException {
        return service.getUrlImagensPerguntas(codUnidade, codFuncao);
    }

    @GET
    @UsedBy(platforms = Platform.WEBSITE)
    @Secured(permissions = {Pilares.Frota.Checklist.Modelo.ALTERAR, Pilares.Frota.Checklist.Modelo.CADASTRAR})
    @Path("/galeria-prolog")
    public Galeria getGaleriaImagensPublicas() throws ProLogException {
        return service.getGaleriaImagensPublicas();
    }

    @GET
    @UsedBy(platforms = Platform.WEBSITE)
    @Secured(permissions = {Pilares.Frota.Checklist.Modelo.ALTERAR,
            Pilares.Frota.Checklist.Modelo.CADASTRAR})
    @Path("/galerias/{codEmpresa}")
    public Galeria getGaleriaImagensEmpresa(
            @PathParam("codEmpresa") @Required final Long codEmpresa) throws ProLogException {
        return service.getGaleriaImagensEmpresa(codEmpresa);
    }

    @POST
    @UsedBy(platforms = Platform.WEBSITE)
    @Secured(permissions = {Pilares.Frota.Checklist.Modelo.ALTERAR, Pilares.Frota.Checklist.Modelo.CADASTRAR})
    @Consumes({MediaType.MULTIPART_FORM_DATA})
    @Path("/galerias/{codEmpresa}/upload")
    public AbstractResponse insertImagemGaleria(
            @PathParam("codEmpresa") @Required final Long codEmpresa,
            @FormDataParam("file") @Required final InputStream fileInputStream,
            @FormDataParam("file") @Required final FormDataContentDisposition fileDetail) throws ProLogException {
        Preconditions.checkNotNull(codEmpresa, "Código da empresa não pode ser null!");
        return service.insertImagem(codEmpresa, fileInputStream, fileDetail);
    }
}
