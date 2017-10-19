package br.com.zalf.prolog.webservice.gente.treinamento;

import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.network.ResponseWithCod;
import br.com.zalf.prolog.webservice.commons.util.DateUtils;
import br.com.zalf.prolog.webservice.gente.treinamento.model.Treinamento;
import br.com.zalf.prolog.webservice.gente.treinamento.model.TreinamentoColaborador;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
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
    public AbstractResponse insertTreinamento(
            @FormDataParam("file") InputStream fileInputStream,
            @FormDataParam("file") FormDataContentDisposition fileDetail,
            @FormDataParam("treinamento") FormDataBodyPart jsonPart) {

        if (!fileDetail.getFileName().toLowerCase().endsWith(".pdf"))
            return Response.error("ERRO! Arquivo não está no formato PDF!");

        jsonPart.setMediaType(MediaType.APPLICATION_JSON_TYPE);
        Treinamento treinamento = jsonPart.getValueAs(Treinamento.class);
        if (treinamento == null) {
            return Response.error("ERRO! Treinamento veio nulo");
        } else {
            Long codTreinamento = service.insert(fileInputStream, treinamento);
            if (codTreinamento != null) {
                return ResponseWithCod.ok("Treinamento inserido com sucesso", codTreinamento);
            } else {
                return Response.error("Erro ao inserir treinamento");
            }
        }
    }

    @PUT
    @Secured(permissions = {Pilares.Gente.Treinamentos.CRIAR, Pilares.Gente.Treinamentos.ALTERAR})
    @Path("/{codigo}")
    public Response updateTreinamento(Treinamento treinamento) {
        if (service.updateTreinamento(treinamento)) {
            return Response.ok("Treinamento atualizado com sucesso");
        } else {
            return Response.error("Erro ao atualizar o treinamento");
        }
    }

    @POST
    @Secured(permissions = Pilares.Gente.Treinamentos.VISUALIZAR_PROPRIOS)
    @Path("/visualizados/{codTreinamento}/{cpf}")
    public Response marcarTreinamentoComoVisto(@PathParam("codTreinamento") Long codTreinamento,
                                               @PathParam("cpf") Long cpf) {
        if (service.marcarTreinamentoComoVisto(codTreinamento, cpf)) {
            return Response.ok("Treinamento marcado com sucesso");
        } else {
            return Response.error("Erro ao marcar treinamento");
        }
    }

    @GET
    @Secured(permissions = Pilares.Gente.Treinamentos.VISUALIZAR_PROPRIOS)
    @Path("/visualizados/{cpf}")
    public List<Treinamento> getVistosByColaborador(@PathParam("cpf") Long cpf) {
        return service.getVistosByColaborador(cpf);
    }

    @GET
    @Secured(permissions = Pilares.Gente.Treinamentos.VISUALIZAR_PROPRIOS)
    @Path("/nao-visualizados/{cpf}")
    public List<Treinamento> getNaoVistosByColaborador(@PathParam("cpf") Long cpf) {
        return service.getNaoVistosByColaborador(cpf);
    }

    @GET
    @Secured
    @Path("/visualizacoes/{codUnidade}/{codTreinamento}")
    public List<TreinamentoColaborador> getVisualizacoesByTreinamento(
            @PathParam("codUnidade") Long codTreinamento,
            @PathParam("codTreinamento") Long codUnidade) {
        return service.getVisualizacoesByTreinamento(codTreinamento, codUnidade);
    }

    @GET
    @Secured(permissions = {Pilares.Gente.Treinamentos.VISUALIZAR_PROPRIOS, Pilares.Gente.Treinamentos.ALTERAR,
            Pilares.Gente.Treinamentos.CRIAR})
    @Path("/{codUnidade}/{codFuncao}")
    public List<Treinamento> getAll(@PathParam("codUnidade") Long codUnidade,
                                    @PathParam("codFuncao") String codFuncao,
                                    @QueryParam("dataInicial") long dataInicial,
                                    @QueryParam("dataFinal") long dataFinal,
                                    @QueryParam("limit") long limit,
                                    @QueryParam("offset") long offset) {
        return service.getAll(DateUtils.toLocalDate(new java.sql.Date(dataInicial)),
                DateUtils.toLocalDate(new java.sql.Date(dataFinal)), codFuncao, codUnidade, limit, offset);
    }

    @GET
    @Secured(permissions = {Pilares.Gente.Treinamentos.VISUALIZAR_PROPRIOS, Pilares.Gente.Treinamentos.ALTERAR,
            Pilares.Gente.Treinamentos.CRIAR})
    @Path("/{codUnidade}")
    public List<Treinamento> getAll(@PathParam("codUnidade") Long codUnidade,
                                    @QueryParam("dataInicial") long dataInicial,
                                    @QueryParam("dataFinal") long dataFinal,
                                    @QueryParam("limit") long limit,
                                    @QueryParam("offset") long offset) {
        return service.getAll(DateUtils.toLocalDate(new java.sql.Date(dataInicial)),
                DateUtils.toLocalDate(new java.sql.Date(dataFinal)), null, codUnidade, limit, offset);
    }

    @PUT
    @Secured(permissions = {Pilares.Gente.Treinamentos.CRIAR, Pilares.Gente.Treinamentos.ALTERAR})
    @Deprecated
    public Response DEPRECATED_UPDATE(Treinamento treinamento) {
        if (service.updateTreinamento(treinamento)) {
            return Response.ok("Treinamento atualizado com sucesso");
        } else {
            return Response.error("Erro ao atualizar o treinamento");
        }
    }

    @GET
    @Secured(permissions = Pilares.Gente.Treinamentos.VISUALIZAR_PROPRIOS)
    @Path("/vistosColaborador/{cpf}")
    @Deprecated
    public List<Treinamento> DEPRECATED_GET_VISTOS_BY_COLABORADOR(@PathParam("cpf") Long cpf) {
        return service.getVistosByColaborador(cpf);
    }

    @GET
    @Secured(permissions = Pilares.Gente.Treinamentos.VISUALIZAR_PROPRIOS)
    @Path("/naoVistosColaborador/{cpf}")
    @Deprecated
    public List<Treinamento> DEPRECATED_GET_NAO_VISTOS_BY_COLABORADOR(@PathParam("cpf") Long cpf) {
        return service.getNaoVistosByColaborador(cpf);
    }

    @POST
    @Secured
    @Deprecated
    public Response DEPRECATED_MARCAR_TREINAMENTO_COMO_VISTO(TreinamentoColaborador treinamentoColaborador) {
        treinamentoColaborador.setDataVisualizacao(new Date(System.currentTimeMillis()));
        if (service.marcarTreinamentoComoVisto(treinamentoColaborador.getCodTreinamento(),
                treinamentoColaborador.getColaborador().getCpf())) {
            return Response.ok("Treinamento marcado com sucesso");
        } else {
            return Response.error("Erro ao marcar treinamento");
        }
    }

    @DELETE
    @Path("/{codTreinamento}")
//    @Secured(permissions = {Pilares.Gente.Treinamentos.CRIAR, Pilares.Gente.Treinamentos.ALTERAR})
    public Response deleteTreinamento(@PathParam("codTreinamento") Long codTreinamento) {
        if(service.deleteTreinamento(codTreinamento)){
            return Response.ok("Treinamento deletado com sucesso");
        }else {
            return Response.error("Erro ao deletar o treinamento");
        }
    }
}