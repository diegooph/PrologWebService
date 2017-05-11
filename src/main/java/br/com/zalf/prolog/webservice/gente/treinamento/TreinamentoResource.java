package br.com.zalf.prolog.webservice.gente.treinamento;

import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.network.ResponseWithCod;
import br.com.zalf.prolog.webservice.commons.util.DateUtils;
import br.com.zalf.prolog.webservice.gente.treinamento.model.Treinamento;
import br.com.zalf.prolog.webservice.gente.treinamento.model.TreinamentoColaborador;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.commons.util.Android;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

@Path("/treinamentos")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class TreinamentoResource {

    private TreinamentoService service = new TreinamentoService();

    @POST
    @Path("/upload")
    @Secured(permissions = {Pilares.Gente.Treinamentos.CRIAR, Pilares.Gente.Treinamentos.ALTERAR})
    @Consumes({MediaType.MULTIPART_FORM_DATA})
    public AbstractResponse uploadTreinamento(
            @FormDataParam("file") InputStream fileInputStream,
            @FormDataParam("file") FormDataContentDisposition fileDetail,
            @FormDataParam("treinamento") FormDataBodyPart jsonPart) {

        if (!fileDetail.getFileName().toLowerCase().endsWith(".pdf"))
            return Response.Error("ERRO! Arquivo não está no formato PDF!");

        jsonPart.setMediaType(MediaType.APPLICATION_JSON_TYPE);
        Treinamento treinamento = jsonPart.getValueAs(Treinamento.class);
        if (treinamento == null) {
            return Response.Error("ERRO! Treinamento veio nulo");
        } else {
            Long codTreinamento = service.insert(fileInputStream, treinamento);
            if (codTreinamento != null) {
                return ResponseWithCod.Ok("Treinamento inserido com sucesso", codTreinamento);
            } else {
                return Response.Error("Erro ao inserir treinamento");
            }
        }
    }

    @POST
    @Secured(permissions = Pilares.Gente.Treinamentos.VISUALIZAR_PROPRIOS)
    @Path("/visualizados/{codTreinamento}/{cpf}")
    public Response marcarTreinamentoComoVisto(@PathParam("codTreinamento") Long codTreinamento,
                                               @PathParam("cpf") Long cpf) {
        if (service.marcarTreinamentoComoVisto(codTreinamento, cpf)) {
            return Response.Ok("Treinamento marcado com sucesso");
        } else {
            return Response.Error("Erro ao marcar treinamento");
        }
    }

    @Deprecated
    @POST
    @Secured
    public Response DEPRECATED_MARCAR_TREINAMENTO_COMO_VISTO(TreinamentoColaborador treinamentoColaborador) {
        treinamentoColaborador.setDataVisualizacao(new Date(System.currentTimeMillis()));
        if (service.marcarTreinamentoComoVisto(treinamentoColaborador.getCodTreinamento(),
                treinamentoColaborador.getColaborador().getCpf())) {
            return Response.Ok("Treinamento marcado com sucesso");
        } else {
            return Response.Error("Erro ao marcar treinamento");
        }
    }

    @GET
    @Secured(permissions = Pilares.Gente.Treinamentos.VISUALIZAR_PROPRIOS)
    @Android
    @Path("/vistosColaborador/{cpf}")
    public List<Treinamento> getVistosByColaborador(@PathParam("cpf") Long cpf) {
        return service.getVistosByColaborador(cpf);
    }

    @GET
    @Secured(permissions = Pilares.Gente.Treinamentos.VISUALIZAR_PROPRIOS)
    @Android
    @Path("/naoVistosColaborador/{cpf}")
    public List<Treinamento> getNaoVistosByColaborador(@PathParam("cpf") Long cpf) {
        return service.getNaoVistosByColaborador(cpf);
    }

    @GET
    @Secured(permissions = {Pilares.Gente.Treinamentos.VISUALIZAR_PROPRIOS, Pilares.Gente.Treinamentos.ALTERAR,
            Pilares.Gente.Treinamentos.CRIAR})
    @Path("/{codUnidade}/{codFuncao}")
    public List<Treinamento> getAll(
            @PathParam("codUnidade") Long codUnidade,
            @PathParam("codFuncao") String codFuncao,
            @QueryParam("dataInicial") long dataInicial,
            @QueryParam("dataFinal") long dataFinal,
            @QueryParam("limit") long limit,
            @QueryParam("offset") long offset) {
        return service.getAll(DateUtils.toLocalDate(new java.sql.Date(dataInicial)),
                DateUtils.toLocalDate(new java.sql.Date(dataFinal)), codFuncao, codUnidade, limit, offset);
    }

    @GET
    @Secured
    @Path("/visualizacoes/{codUnidade}/{codTreinamento}")
    public List<TreinamentoColaborador> getVisualizacoesByTreinamento(
            @PathParam("codUnidade") Long codTreinamento,
            @PathParam("codTreinamento") Long codUnidade) {
        return service.getVisualizacoesByTreinamento(codTreinamento, codUnidade);
    }

    @PUT
    @Secured(permissions = {Pilares.Gente.Treinamentos.CRIAR, Pilares.Gente.Treinamentos.ALTERAR})
    public Response updateTreinamento(Treinamento treinamento) {
        if (service.updateTreinamento(treinamento)) {
            return Response.Ok("Treinamento atualizado com sucesso");
        } else {
            return Response.Error("Erro ao atualizar o treinamento");
        }
    }

    private Response updateUrlImagensTreinamento(List<String> urls, Long codTreinamento) {
        if(service.updateUrlImagensTreinamento(urls, codTreinamento)){
            return Response.Ok("Atualização bem sucedida");
        }else {
            return Response.Error("Erro ao atualizar");
        }
    }
}