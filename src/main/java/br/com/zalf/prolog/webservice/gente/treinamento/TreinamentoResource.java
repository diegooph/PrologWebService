package br.com.zalf.prolog.webservice.gente.treinamento;

import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.network.ResponseWithCod;
import br.com.zalf.prolog.webservice.gente.colaborador.ColaboradorBackwardHelper;
import br.com.zalf.prolog.webservice.gente.treinamento.model.Treinamento;
import br.com.zalf.prolog.webservice.gente.treinamento.model.TreinamentoColaborador;
import br.com.zalf.prolog.webservice.interceptors.auth.ColaboradorAutenticado;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.interceptors.debug.ConsoleDebugLog;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.InputStream;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

@ConsoleDebugLog
@Path("/v2/treinamentos")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class TreinamentoResource {
    @Inject
    private Provider<ColaboradorAutenticado> colaboradorAutenticadoProvider;

    private final TreinamentoService service = new TreinamentoService();

    @POST
    @Path("/upload")
    @Secured(permissions = {Pilares.Gente.Treinamentos.CRIAR, Pilares.Gente.Treinamentos.ALTERAR})
    @Consumes({MediaType.MULTIPART_FORM_DATA})
    public AbstractResponse insertTreinamento(
            @FormDataParam("file") final InputStream fileInputStream,
            @FormDataParam("file") final FormDataContentDisposition fileDetail,
            @FormDataParam("treinamento") final FormDataBodyPart jsonPart) {

        if (!fileDetail.getFileName().toLowerCase().endsWith(".pdf")) {
            return Response.error("ERRO! Arquivo não está no formato PDF!");
        }

        jsonPart.setMediaType(MediaType.APPLICATION_JSON_TYPE);
        final Treinamento treinamento = jsonPart.getValueAs(Treinamento.class);
        if (treinamento == null) {
            return Response.error("ERRO! Treinamento veio nulo");
        } else {
            final Long codTreinamento = service.insert(fileInputStream, treinamento);
            if (codTreinamento != null) {
                return ResponseWithCod.ok("Treinamento inserido com sucesso", codTreinamento);
            } else {
                return Response.error("Erro ao inserir treinamento");
            }
        }
    }

    @GET
    @Secured(permissions = {
            Pilares.Gente.Treinamentos.ALTERAR,
            Pilares.Gente.Treinamentos.CRIAR})
    @Path("/{codUnidade}/{codTreinamento}")
    public Treinamento getTreinamentoByCod(@PathParam("codUnidade") final Long codUnidade,
                                           @PathParam("codTreinamento") final Long codTreinamento) {
        return service.getByCod(codTreinamento, codUnidade);
    }

    @PUT
    @Secured(permissions = {Pilares.Gente.Treinamentos.CRIAR, Pilares.Gente.Treinamentos.ALTERAR})
    @Path("/{codigo}")
    public Response updateTreinamento(final Treinamento treinamento) {
        if (service.updateTreinamento(treinamento)) {
            return Response.ok("Treinamento atualizado com sucesso");
        } else {
            return Response.error("Erro ao atualizar o treinamento");
        }
    }

    @Deprecated
    @POST
    @Secured(permissions = Pilares.Gente.Treinamentos.VISUALIZAR_PROPRIOS)
    @Path("/visualizados/{codTreinamento}/{cpf}")
    public Response marcarTreinamentoComoVisto(@PathParam("codTreinamento") final Long codTreinamento,
                                               @PathParam("cpf") final Long cpf) {
        if (service.marcarTreinamentoComoVisto(codTreinamento,
                                               ColaboradorBackwardHelper.getCodColaboradorByCpf(
                                                       colaboradorAutenticadoProvider.get().getCodigo(),
                                                       cpf.toString()))) {
            return Response.ok("Treinamento marcado com sucesso");
        } else {
            return Response.error("Erro ao marcar treinamento");
        }
    }

    @POST
    @Secured(permissions = Pilares.Gente.Treinamentos.VISUALIZAR_PROPRIOS)
    @Path("/visualizados/by-codigo/{codTreinamento}/{codColaborador}")
    public Response marcarTreinamentoComoVistoByCodColaborador(@PathParam("codTreinamento") final Long codTreinamento,
                                                               @PathParam("codColaborador") final Long codColaborador) {
        if (service.marcarTreinamentoComoVisto(codTreinamento, codColaborador)) {
            return Response.ok("Treinamento marcado com sucesso");
        } else {
            return Response.error("Erro ao marcar treinamento");
        }
    }

    @Deprecated
    @GET
    @Secured(permissions = Pilares.Gente.Treinamentos.VISUALIZAR_PROPRIOS)
    @Path("/visualizados/{cpf}")
    public List<Treinamento> getVistosByColaborador(@PathParam("cpf") final Long cpf) {
        return service.getVistosByColaborador(ColaboradorBackwardHelper.getCodColaboradorByCpf(
                colaboradorAutenticadoProvider.get().getCodigo(),
                cpf.toString()));
    }

    @GET
    @Secured(permissions = Pilares.Gente.Treinamentos.VISUALIZAR_PROPRIOS)
    @Path("/visualizados/by-codigo/{codColaborador}")
    public List<Treinamento> getVistosByColaboradorByCodColaborador(
            @PathParam("codColaborador") final Long codColaborador) {
        return service.getVistosByColaborador(codColaborador);
    }

    @Deprecated
    @GET
    @Secured(permissions = Pilares.Gente.Treinamentos.VISUALIZAR_PROPRIOS)
    @Path("/nao-visualizados/{cpf}")
    public List<Treinamento> getNaoVistosByColaborador(@PathParam("cpf") final Long cpf) {
        return service.getNaoVistosByColaborador(ColaboradorBackwardHelper.getCodColaboradorByCpf(
                colaboradorAutenticadoProvider.get().getCodigo(),
                cpf.toString()));
    }

    @GET
    @Secured(permissions = Pilares.Gente.Treinamentos.VISUALIZAR_PROPRIOS)
    @Path("/nao-visualizados/by-codigo/{codColaborador}")
    public List<Treinamento> getNaoVistosByCodColaborador(@PathParam("codColaborador") final Long codColaborador) {
        return service.getNaoVistosByColaborador(codColaborador);
    }

    @GET
    @Secured
    @Path("/visualizacoes/{codUnidade}/{codTreinamento}")
    public List<TreinamentoColaborador> getVisualizacoesByTreinamento(
            @PathParam("codUnidade") final Long codTreinamento,
            @PathParam("codTreinamento") final Long codUnidade) {
        return service.getVisualizacoesByTreinamento(codTreinamento, codUnidade);
    }

    @GET
    @Secured(permissions = {
            Pilares.Gente.Treinamentos.VISUALIZAR_PROPRIOS,
            Pilares.Gente.Treinamentos.ALTERAR,
            Pilares.Gente.Treinamentos.CRIAR})
    @Path("/{codUnidade}")
    public List<Treinamento> getAll(@PathParam("codUnidade") final Long codUnidade,
                                    @QueryParam("codCargo") final String codCargo,
                                    @QueryParam("dataInicial") final Long dataInicial,
                                    @QueryParam("dataFinal") final Long dataFinal,
                                    @QueryParam("comCargosLiberados") final Boolean comCargosLiberados,
                                    @QueryParam("apenasTreinamentosLiberados") final boolean apenasTreinametosLiberados,
                                    @QueryParam("limit") final long limit,
                                    @QueryParam("offset") final long offset) {
        return service.getAll(dataInicial, dataFinal, codCargo, codUnidade, comCargosLiberados,
                apenasTreinametosLiberados, limit, offset);
    }

    @PUT
    @Secured(permissions = {Pilares.Gente.Treinamentos.CRIAR, Pilares.Gente.Treinamentos.ALTERAR})
    @Deprecated
    public Response DEPRECATED_UPDATE(final Treinamento treinamento) {
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
    public List<Treinamento> DEPRECATED_GET_VISTOS_BY_COLABORADOR(@PathParam("cpf") final Long cpf) {
        return service.getVistosByColaborador(ColaboradorBackwardHelper.getCodColaboradorByCpf(
                colaboradorAutenticadoProvider.get().getCodigo(),
                cpf.toString()));
    }

    @GET
    @Secured(permissions = Pilares.Gente.Treinamentos.VISUALIZAR_PROPRIOS)
    @Path("/naoVistosColaborador/{cpf}")
    @Deprecated
    public List<Treinamento> DEPRECATED_GET_NAO_VISTOS_BY_COLABORADOR(@PathParam("cpf") final Long cpf) {
        return service.getNaoVistosByColaborador(ColaboradorBackwardHelper.getCodColaboradorByCpf(
                colaboradorAutenticadoProvider.get().getCodigo(),
                cpf.toString()));
    }

    @POST
    @Secured
    @Deprecated
    public Response DEPRECATED_MARCAR_TREINAMENTO_COMO_VISTO(final TreinamentoColaborador treinamentoColaborador) {
        treinamentoColaborador.setDataVisualizacao(LocalDateTime.now(Clock.systemUTC()));
        if (service.marcarTreinamentoComoVisto(treinamentoColaborador.getCodTreinamento(),
                treinamentoColaborador.getColaborador().getCodigo())) {
            return Response.ok("Treinamento marcado com sucesso");
        } else {
            return Response.error("Erro ao marcar treinamento");
        }
    }

    @DELETE
    @Path("/{codTreinamento}")
    @Secured(permissions = {Pilares.Gente.Treinamentos.CRIAR, Pilares.Gente.Treinamentos.ALTERAR})
    public Response deleteTreinamento(@PathParam("codTreinamento") final Long codTreinamento) {
        if (service.deleteTreinamento(codTreinamento)) {
            return Response.ok("Treinamento deletado com sucesso");
        } else {
            return Response.error("Erro ao deletar o treinamento");
        }
    }
}