package br.com.zalf.prolog.webservice.frota.checklist.modelo;

import br.com.zalf.prolog.webservice.commons.imagens.Galeria;
import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.util.Platform;
import br.com.zalf.prolog.webservice.commons.util.Required;
import br.com.zalf.prolog.webservice.commons.util.UsedBy;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.frota.checklist.OLD.PerguntaRespostaChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.insercao.ModeloChecklistEdicao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.insercao.ModeloChecklistInsercao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.visualizacao.ModeloChecklistListagem;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.visualizacao.ModeloChecklistVisualizacao;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.interceptors.log.DebugLog;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import com.google.common.base.Preconditions;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.InputStream;
import java.util.List;

@DebugLog
@Path("/checklists/modelos")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class ChecklistModeloResource {
    @NotNull
    private final ChecklistModeloService service = new ChecklistModeloService();

    @POST
    @UsedBy(platforms = Platform.WEBSITE)
    @Secured(permissions = {Pilares.Frota.Checklist.Modelo.ALTERAR, Pilares.Frota.Checklist.Modelo.CADASTRAR})
    public Response insertModeloChecklist(
            @Required final ModeloChecklistInsercao modeloChecklist) throws ProLogException {
        return service.insertModeloChecklist(modeloChecklist);
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
        return service.getModelosChecklistListagemByCodUnidadeByCodCargo(codUnidade, "%");
    }

    @GET
    @UsedBy(platforms = Platform.WEBSITE)
    @Secured(permissions = {
            Pilares.Frota.Checklist.Modelo.VISUALIZAR,
            Pilares.Frota.Checklist.Modelo.ALTERAR,
            Pilares.Frota.Checklist.Modelo.CADASTRAR})
    @Path("/{codUnidade}/cargos/{codCargo}")
    public List<ModeloChecklistListagem> getModelosChecklistByCodUnidadeByCodCargo(
            @PathParam("codUnidade") @Required final Long codUnidade,
            @PathParam("codCargo") @Required final String codFuncao) throws ProLogException {
        return service.getModelosChecklistListagemByCodUnidadeByCodCargo(codUnidade, codFuncao);
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
        return service.updateModeloChecklist(token, codUnidade, codModelo, modeloChecklist);
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

    //
    // Métodos depreciados.
    //
    @GET
    @Consumes({MediaType.MULTIPART_FORM_DATA})
    @Secured(permissions = {
            Pilares.Frota.Checklist.Modelo.VISUALIZAR,
            Pilares.Frota.Checklist.Modelo.ALTERAR,
            Pilares.Frota.Checklist.Modelo.CADASTRAR,
            Pilares.Frota.Checklist.REALIZAR})
    @Path("/perguntas/{codUnidade}/{codModelo}")
    @Deprecated
    public List<PerguntaRespostaChecklist> getPerguntas(
            @PathParam("codUnidade") @Required final Long codUnidade,
            @PathParam("codModelo") @Required final Long codModelo) throws ProLogException {
        return service.getPerguntas(codUnidade, codModelo);
    }
}
