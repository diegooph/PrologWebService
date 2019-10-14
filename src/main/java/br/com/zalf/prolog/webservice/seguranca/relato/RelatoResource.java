package br.com.zalf.prolog.webservice.seguranca.relato;

import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.util.ProLogCustomHeaders;
import br.com.zalf.prolog.webservice.commons.util.date.DateUtils;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import br.com.zalf.prolog.webservice.seguranca.relato.model.Relato;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Date;
import java.util.List;

@Path("/relatos")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class RelatoResource {
    @NotNull
    private final RelatoService service = new RelatoService();

    @POST
    @Secured(permissions = Pilares.Seguranca.Relato.REALIZAR)
    public Response insert(Relato relato,
                           @HeaderParam(ProLogCustomHeaders.AppVersionAndroid.PROLOG_APP_VERSION) Integer versaoApp) {
        if (service.insert(relato, versaoApp)) {
            return Response.ok("Relato inserido com sucesso");
        } else {
            return Response.error("Erro ao inserir relato");
        }
    }

    @GET
    @Path("/{codigo}")
    @Secured(permissions = {Pilares.Seguranca.Relato.REALIZAR,
            Pilares.Seguranca.Relato.VISUALIZAR,
            Pilares.Seguranca.Relato.CLASSIFICAR,
            Pilares.Seguranca.Relato.FECHAR})
    public Relato getByCod(@PathParam("codigo") Long codigo, @HeaderParam("Authorization") String userToken) {
        return service.getByCod(codigo, userToken);
    }

    @GET
    @Path("/{status}/colaborador/{cpf}")
    @Secured(permissions = {Pilares.Seguranca.Relato.REALIZAR,
            Pilares.Seguranca.Relato.VISUALIZAR,
            Pilares.Seguranca.Relato.CLASSIFICAR,
            Pilares.Seguranca.Relato.FECHAR})
    public List<Relato> getByColaborador(@PathParam("status") String status,
                                         @PathParam("cpf") Long cpf,
                                         @QueryParam("limit") int limit,
                                         @QueryParam("offset") long offset,
                                         @QueryParam("latitude") double latitude,
                                         @QueryParam("longitude") double longitude,
                                         @QueryParam("isOrderByDate") boolean isOrderByDate){
        return service.getRealizadosByColaborador(cpf, limit, offset, latitude, longitude, isOrderByDate,
                status, "realizados" );
    }

    @GET
    @Path("/classificados/{cpf}/{status}")
    @Secured(permissions = {Pilares.Seguranca.Relato.REALIZAR,
            Pilares.Seguranca.Relato.VISUALIZAR,
            Pilares.Seguranca.Relato.CLASSIFICAR})
    public List<Relato> getClassificadosByColaborador(
            @PathParam("cpf") Long cpf,
            @PathParam("status") String status,
            @QueryParam("limit") int limit,
            @QueryParam("offset") long offset,
            @QueryParam("latitude") double latitude,
            @QueryParam("longitude") double longitude,
            @QueryParam("isOrderByDate") boolean isOrderByDate) {
        return service.getRealizadosByColaborador(cpf, limit, offset, latitude, longitude, isOrderByDate, status,
                "classificados");
    }

    @GET
    @Path("/fechados/{cpf}/{status}")
    @Secured(permissions = {Pilares.Seguranca.Relato.VISUALIZAR,
            Pilares.Seguranca.Relato.CLASSIFICAR,
            Pilares.Seguranca.Relato.FECHAR})
    public List<Relato> getFechadosByColaborador(
            @PathParam("cpf") Long cpf,
            @PathParam("status") String status,
            @QueryParam("limit") int limit,
            @QueryParam("offset") long offset,
            @QueryParam("latitude") double latitude,
            @QueryParam("longitude") double longitude,
            @QueryParam("isOrderByDate") boolean isOrderByDate) {
        return service.getRealizadosByColaborador(cpf, limit, offset, latitude, longitude, isOrderByDate, status,
                "fechados");
    }

    @GET
    @Path("{status}/exceto/colaborador/{cpf}")
    @Secured(permissions = {Pilares.Seguranca.Relato.REALIZAR,
            Pilares.Seguranca.Relato.VISUALIZAR})
    public List<Relato> getAllExcetoColaborador(
            @PathParam("cpf") Long cpf,
            @PathParam("status") String status,
            @QueryParam("limit") int limit,
            @QueryParam("offset") long offset,
            @QueryParam("latitude") double latitude,
            @QueryParam("longitude") double longitude,
            @QueryParam("isOrderByDate") boolean isOrderByDate) {
        return service.getAllExcetoColaborador(cpf, limit, offset, latitude, longitude, isOrderByDate, status);
    }

    @GET
    @Path("/unidade")
    @Secured(permissions = {Pilares.Seguranca.Relato.REALIZAR,
            Pilares.Seguranca.Relato.VISUALIZAR,
            Pilares.Seguranca.Relato.CLASSIFICAR,
            Pilares.Seguranca.Relato.FECHAR})
    public List<Relato> getAllByUnidade(
            @QueryParam("codUnidade") Long codUnidade,
            @QueryParam("equipe") String equipe,
            @QueryParam("status") String status,
            @QueryParam("dataInicial") long dataInicial,
            @QueryParam("dataFinal") long dataFinal,
            @QueryParam("limit") long limit,
            @QueryParam("offset") long offset) {
        return service.getAllByUnidade(DateUtils.toLocalDate(new Date(dataInicial)),
                DateUtils.toLocalDate(new Date(dataFinal)), equipe,
                codUnidade, limit, offset, status);
    }

    @GET
    @Path("/{codUnidade}/{status}")
    @Secured(permissions = {Pilares.Seguranca.Relato.REALIZAR,
            Pilares.Seguranca.Relato.VISUALIZAR,
            Pilares.Seguranca.Relato.CLASSIFICAR,
            Pilares.Seguranca.Relato.FECHAR})
    public List<Relato> getAll(@PathParam("codUnidade") Long codUnidade,
                               @PathParam("status") String status,
                               @QueryParam("limit") int limit,
                               @QueryParam("offset") long offset) {
        return service.getAll(codUnidade, limit, offset, 0, 0, true, status);
    }

    @PUT
    @Path("/classificar")
    @Secured(permissions = Pilares.Seguranca.Relato.CLASSIFICAR)
    public Response classificaRelato(Relato relato) {
        if (service.classificaRelato(relato)) {
            return Response.ok("Relato classificado com sucesso");
        } else {
            return Response.error("Erro ao classificar o relato");
        }
    }

    @PUT
    @Path("/fechar")
    @Secured(permissions = {Pilares.Seguranca.Relato.FECHAR,
            Pilares.Seguranca.Relato.CLASSIFICAR})
    public Response fechaRelato(Relato relato){
        if (service.fechaRelato(relato)) {
            return Response.ok("Relato fechado com sucesso");
        } else {
            return Response.error("Erro ao fechar o relato");
        }
    }

    @DELETE
    @Path("/{codigo}")
    @Secured(permissions = Pilares.Seguranca.Relato.FECHAR)
    public Response delete(@PathParam("codigo") Long codigo) {
        if (service.delete(codigo)) {
            return Response.ok("Relato deletado com sucesso");
        } else {
            return Response.error("Erro ao deletar relato");
        }
    }
}