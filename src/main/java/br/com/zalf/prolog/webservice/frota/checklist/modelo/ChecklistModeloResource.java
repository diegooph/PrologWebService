package br.com.zalf.prolog.webservice.frota.checklist.modelo;

import br.com.zalf.prolog.webservice.commons.imagens.Galeria;
import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.util.Platform;
import br.com.zalf.prolog.webservice.commons.util.Required;
import br.com.zalf.prolog.webservice.commons.util.UsedBy;
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

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.InputStream;
import java.util.List;

@DebugLog
@Path("/checklists/modelos")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class ChecklistModeloResource {

    private final ChecklistModeloService service = new ChecklistModeloService();

    @POST
    @Secured(permissions = {Pilares.Frota.Checklist.Modelo.ALTERAR,
            Pilares.Frota.Checklist.Modelo.CADASTRAR})
    public Response insertModeloChecklist(ModeloChecklistInsercao modeloChecklist) {
        service.insertModeloChecklist(modeloChecklist);
        return Response.ok("Modelo de checklist inserido com sucesso");
    }

    @GET
    @Secured(permissions = {Pilares.Frota.Checklist.Modelo.VISUALIZAR,
            Pilares.Frota.Checklist.Modelo.ALTERAR,
            Pilares.Frota.Checklist.Modelo.CADASTRAR})
    @Path("/{codUnidade}")
    public List<ModeloChecklistListagem> getModelosChecklistByCodUnidade(
            @PathParam("codUnidade") @Required Long codUnidade) {
        return service.getModelosChecklistListagemByCodUnidadeByCodFuncao(codUnidade, "%");
    }

    @GET
    @Secured(permissions = {Pilares.Frota.Checklist.Modelo.VISUALIZAR,
            Pilares.Frota.Checklist.Modelo.ALTERAR,
            Pilares.Frota.Checklist.Modelo.CADASTRAR})
    @Path("/{codUnidade}/cargos/{codCargo}")
    public List<ModeloChecklistListagem> getModelosChecklistByCodUnidadeByCodCargo(
            @PathParam("codUnidade") @Required Long codUnidade,
            @PathParam("codCargo") @Required String codFuncao) {
        return service.getModelosChecklistListagemByCodUnidadeByCodFuncao(codUnidade, codFuncao);
    }

    @GET
    @Secured(permissions = {Pilares.Frota.Checklist.Modelo.VISUALIZAR,
            Pilares.Frota.Checklist.Modelo.ALTERAR,
            Pilares.Frota.Checklist.Modelo.CADASTRAR})
    @Path("/{codUnidade}/{codModelo}")
    public ModeloChecklistVisualizacao getModeloChecklist(
            @PathParam("codUnidade") Long codUnidade,
            @PathParam("codModelo") Long codModelo) {
        return service.getModeloChecklist(codUnidade, codModelo);
    }

    @PUT
    @UsedBy(platforms = Platform.WEBSITE)
    @Secured(permissions = {Pilares.Frota.Checklist.Modelo.VISUALIZAR,
            Pilares.Frota.Checklist.Modelo.ALTERAR,
            Pilares.Frota.Checklist.Modelo.CADASTRAR})
    @Path("/{codUnidade}/{codModelo}")
    public Response updateModeloChecklist(@HeaderParam("Authorization") String token,
                                          @PathParam("codUnidade") Long codUnidade,
                                          @PathParam("codModelo") Long codModelo,
                                          ModeloChecklistEdicao modeloChecklist) throws Exception {
        return service.updateModeloChecklist(token, codUnidade, codModelo, modeloChecklist);
    }

    @GET
    @Secured(permissions = {Pilares.Frota.Checklist.Modelo.VISUALIZAR,
            Pilares.Frota.Checklist.Modelo.ALTERAR,
            Pilares.Frota.Checklist.Modelo.CADASTRAR,
            Pilares.Frota.Checklist.REALIZAR})
    @Path("/perguntas/{codUnidade}/{codModelo}")
    public List<PerguntaRespostaChecklist> getPerguntas(@PathParam("codUnidade") Long codUnidade,
                                                        @PathParam("codModelo") Long codModelo) {
        return service.getPerguntas(codUnidade, codModelo);
    }

    @PUT
    @Secured(permissions = {Pilares.Frota.Checklist.Modelo.ALTERAR})
    @Path("/{codUnidade}/{codModelo}/status-ativo")
    public Response updateStatus(
            @PathParam("codUnidade") @Required final Long codUnidade,
            @PathParam("codModelo") @Required final Long codModelo,
            final ModeloChecklistEdicao modeloChecklist) throws Throwable {
        return service.updateStatusAtivo(codUnidade, codModelo, modeloChecklist);
    }

    @GET
    @Secured(permissions = {Pilares.Frota.Checklist.Modelo.ALTERAR,
            Pilares.Frota.Checklist.Modelo.CADASTRAR})
    @Path("/prolog")
    public List<ModeloChecklistVisualizacao> getModelosChecklistProLog() {
        return service.getModelosChecklistProLog();
    }

    //
    // Métodos referente ao uso da galeria
    //
    @GET
    @Path("/url-imagens/{codUnidade}/{codFuncao}")
    @Secured(permissions = Pilares.Frota.Checklist.REALIZAR)
    public List<String> getUrlImagensPerguntas(@PathParam("codUnidade") Long codUnidade,
                                               @PathParam("codFuncao") Long codFuncao) {
        return service.getUrlImagensPerguntas(codUnidade, codFuncao);
    }

    @GET
    @Secured(permissions = {Pilares.Frota.Checklist.Modelo.ALTERAR,
            Pilares.Frota.Checklist.Modelo.CADASTRAR})
    @Path("/galeria-prolog")
    public Galeria getGaleriaImagensPublicas() {
        return service.getGaleriaImagensPublicas();
    }

    @GET
    @Secured(permissions = {Pilares.Frota.Checklist.Modelo.ALTERAR,
            Pilares.Frota.Checklist.Modelo.CADASTRAR})
    @Path("/galerias/{codEmpresa}")
    public Galeria getGaleriaImagensEmpresa(@PathParam("codEmpresa") Long codEmpresa) {
        return service.getGaleriaImagensEmpresa(codEmpresa);
    }

    @POST
    @Secured(permissions = {Pilares.Frota.Checklist.Modelo.ALTERAR,
            Pilares.Frota.Checklist.Modelo.CADASTRAR})
    @Path("/galerias/{codEmpresa}/upload")
    @Consumes({MediaType.MULTIPART_FORM_DATA})
    public AbstractResponse insertImagemGaleria(@PathParam("codEmpresa") Long codEmpresa,
                                                @FormDataParam("file") InputStream fileInputStream,
                                                @FormDataParam("file") FormDataContentDisposition fileDetail) {
        Preconditions.checkNotNull(codEmpresa, "Código da empresa não pode ser null!");
        return service.insertImagem(codEmpresa, fileInputStream, fileDetail);
    }
}
