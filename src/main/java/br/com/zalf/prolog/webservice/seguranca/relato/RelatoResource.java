package br.com.zalf.prolog.webservice.seguranca.relato;

import br.com.zalf.prolog.webservice.commons.network.PrologCustomHeaders;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.network.metadata.Required;
import br.com.zalf.prolog.webservice.commons.util.datetime.DateUtils;
import br.com.zalf.prolog.webservice.gente.colaborador.ColaboradorBackwardHelper;
import br.com.zalf.prolog.webservice.interceptors.auth.ColaboradorAutenticado;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.interceptors.debug.ConsoleDebugLog;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import br.com.zalf.prolog.webservice.seguranca.relato.model.Relato;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Date;
import java.util.List;

@Path("/v2/relatos")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
@ConsoleDebugLog
public class RelatoResource {
    @NotNull
    private final RelatoService service = new RelatoService();

    @Inject
    private Provider<ColaboradorAutenticado> colaboradorAutenticadoProvider;

    @POST
    @Secured(permissions = Pilares.Seguranca.Relato.REALIZAR)
    public Response insert(final Relato relato,
                           @HeaderParam(PrologCustomHeaders.AppVersionAndroid.PROLOG_APP_VERSION) final Integer versaoApp) {
        relato.getColaboradorRelato().setCodigo(
                ColaboradorBackwardHelper.getCodColaboradorByCpf(colaboradorAutenticadoProvider.get().getCodigo(),
                                                                 relato.getColaboradorRelato().getCpf().toString()));
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
    public Relato getByCod(@PathParam("codigo") final Long codigo,
                           @HeaderParam("Authorization") final String userToken) {
        return service.getByCod(codigo, userToken);
    }

    @GET
    @Path("/{status}/colaborador/{cpf}")
    @Secured(permissions = {Pilares.Seguranca.Relato.REALIZAR,
            Pilares.Seguranca.Relato.VISUALIZAR,
            Pilares.Seguranca.Relato.CLASSIFICAR,
            Pilares.Seguranca.Relato.FECHAR})
    public List<Relato> getByColaborador(@PathParam("status") final String status,
                                         @PathParam("cpf") final Long cpf,
                                         @QueryParam("limit") final int limit,
                                         @QueryParam("offset") final long offset,
                                         @QueryParam("latitude") final double latitude,
                                         @QueryParam("longitude") final double longitude,
                                         @QueryParam("isOrderByDate") final boolean isOrderByDate) {
        final Long codColaborador =
                ColaboradorBackwardHelper.getCodColaboradorByCpf(colaboradorAutenticadoProvider.get().getCodigo(),
                                                                 cpf.toString());
        return service.getRealizadosByColaborador(codColaborador,
                                                  limit,
                                                  offset,
                                                  latitude,
                                                  longitude,
                                                  isOrderByDate,
                                                  status,
                                                  "realizados");
    }

    @GET
    @Path("/{status}/cod-colaborador/{codColaborador}")
    @Secured(permissions = {Pilares.Seguranca.Relato.REALIZAR,
            Pilares.Seguranca.Relato.VISUALIZAR,
            Pilares.Seguranca.Relato.CLASSIFICAR,
            Pilares.Seguranca.Relato.FECHAR})
    public List<Relato> getByCodColaborador(@PathParam("status") @Required final String status,
                                            @PathParam("codColaborador") @Required final Long codColaborador,
                                            @QueryParam("limit") final int limit,
                                            @QueryParam("offset") final long offset,
                                            @QueryParam("latitude") final double latitude,
                                            @QueryParam("longitude") final double longitude,
                                            @QueryParam("isOrderByDate") final boolean isOrderByDate) {

        return service.getRealizadosByColaborador(codColaborador,
                                                  limit,
                                                  offset,
                                                  latitude,
                                                  longitude,
                                                  isOrderByDate,
                                                  status,
                                                  "realizados");
    }

    @GET
    @Path("/classificados/{cpf}/{status}")
    @Secured(permissions = {Pilares.Seguranca.Relato.REALIZAR,
            Pilares.Seguranca.Relato.VISUALIZAR,
            Pilares.Seguranca.Relato.CLASSIFICAR})
    public List<Relato> getClassificadosByColaborador(
            @PathParam("cpf") final Long cpf,
            @PathParam("status") final String status,
            @QueryParam("limit") final int limit,
            @QueryParam("offset") final long offset,
            @QueryParam("latitude") final double latitude,
            @QueryParam("longitude") final double longitude,
            @QueryParam("isOrderByDate") final boolean isOrderByDate) {

        final Long codColaborador =
                ColaboradorBackwardHelper.getCodColaboradorByCpf(colaboradorAutenticadoProvider.get().getCodigo(),
                                                                 cpf.toString());
        return service.getRealizadosByColaborador(codColaborador,
                                                  limit,
                                                  offset,
                                                  latitude,
                                                  longitude,
                                                  isOrderByDate,
                                                  status,
                                                  "classificados");
    }

    @GET
    @Path("/classificados/by-cod-colaborador/{codColaborador}/{status}")
    @Secured(permissions = {Pilares.Seguranca.Relato.REALIZAR,
            Pilares.Seguranca.Relato.VISUALIZAR,
            Pilares.Seguranca.Relato.CLASSIFICAR})
    public List<Relato> getClassificadosByCodColaborador(
            @PathParam("codColaborador") @Required final Long codColaborador,
            @PathParam("status") @Required final String status,
            @QueryParam("limit") final int limit,
            @QueryParam("offset") final long offset,
            @QueryParam("latitude") final double latitude,
            @QueryParam("longitude") final double longitude,
            @QueryParam("isOrderByDate") final boolean isOrderByDate) {

        return service.getRealizadosByColaborador(codColaborador,
                                                  limit,
                                                  offset,
                                                  latitude,
                                                  longitude,
                                                  isOrderByDate,
                                                  status,
                                                  "classificados");
    }

    @GET
    @Path("{status}/exceto/colaborador/{cpf}")
    @Secured(permissions = {Pilares.Seguranca.Relato.REALIZAR,
            Pilares.Seguranca.Relato.VISUALIZAR})
    public List<Relato> getAllExcetoColaborador(
            @PathParam("cpf") final Long cpf,
            @PathParam("status") final String status,
            @QueryParam("limit") final int limit,
            @QueryParam("offset") final long offset,
            @QueryParam("latitude") final double latitude,
            @QueryParam("longitude") final double longitude,
            @QueryParam("isOrderByDate") final boolean isOrderByDate) {
        final Long codColaborador =
                ColaboradorBackwardHelper.getCodColaboradorByCpf(colaboradorAutenticadoProvider.get().getCodigo(),
                                                                 cpf.toString());
        return service.getAllExcetoColaborador(codColaborador,
                                               limit,
                                               offset,
                                               latitude,
                                               longitude,
                                               isOrderByDate,
                                               status);
    }

    @GET
    @Path("/fechados/{cpf}/{status}")
    @Secured(permissions = {Pilares.Seguranca.Relato.VISUALIZAR,
            Pilares.Seguranca.Relato.CLASSIFICAR,
            Pilares.Seguranca.Relato.FECHAR})
    public List<Relato> getFechadosByColaborador(
            @PathParam("cpf") final Long cpf,
            @PathParam("status") final String status,
            @QueryParam("limit") final int limit,
            @QueryParam("offset") final long offset,
            @QueryParam("latitude") final double latitude,
            @QueryParam("longitude") final double longitude,
            @QueryParam("isOrderByDate") final boolean isOrderByDate) {
        final Long codColaborador =
                ColaboradorBackwardHelper.getCodColaboradorByCpf(colaboradorAutenticadoProvider.get().getCodigo(),
                                                                 cpf.toString());
        return service.getRealizadosByColaborador(codColaborador,
                                                  limit,
                                                  offset,
                                                  latitude,
                                                  longitude,
                                                  isOrderByDate,
                                                  status,
                                                  "fechados");
    }

    @GET
    @Path("/fechados/{codColaborador}/{status}")
    @Secured(permissions = {Pilares.Seguranca.Relato.VISUALIZAR,
            Pilares.Seguranca.Relato.CLASSIFICAR,
            Pilares.Seguranca.Relato.FECHAR})
    public List<Relato> getFechadosByCodColaborador(
            @PathParam("codColaborador") @Required final Long codColaborador,
            @PathParam("status") @Required final String status,
            @QueryParam("limit") final int limit,
            @QueryParam("offset") final long offset,
            @QueryParam("latitude") final double latitude,
            @QueryParam("longitude") final double longitude,
            @QueryParam("isOrderByDate") final boolean isOrderByDate) {
        return service.getRealizadosByColaborador(codColaborador,
                                                  limit,
                                                  offset,
                                                  latitude,
                                                  longitude,
                                                  isOrderByDate,
                                                  status,
                                                  "fechados");
    }

    @GET
    @Path("/unidade")
    @Secured(permissions = {Pilares.Seguranca.Relato.REALIZAR,
            Pilares.Seguranca.Relato.VISUALIZAR,
            Pilares.Seguranca.Relato.CLASSIFICAR,
            Pilares.Seguranca.Relato.FECHAR})
    public List<Relato> getAllByUnidade(
            @QueryParam("codUnidade") final Long codUnidade,
            @QueryParam("equipe") final String equipe,
            @QueryParam("status") final String status,
            @QueryParam("dataInicial") final long dataInicial,
            @QueryParam("dataFinal") final long dataFinal,
            @QueryParam("limit") final long limit,
            @QueryParam("offset") final long offset) {
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
    public List<Relato> getAll(@PathParam("codUnidade") final Long codUnidade,
                               @PathParam("status") final String status,
                               @QueryParam("limit") final int limit,
                               @QueryParam("offset") final long offset) {
        return service.getAll(codUnidade, limit, offset, 0, 0, true, status);
    }

    @PUT
    @Path("/classificar")
    @Secured(permissions = Pilares.Seguranca.Relato.CLASSIFICAR)
    public Response classificaRelato(final Relato relato) {
        relato.getColaboradorClassificacao().setCodigo(
                ColaboradorBackwardHelper.getCodColaboradorByCpf(colaboradorAutenticadoProvider.get().getCodigo(),
                                                                 relato.getColaboradorRelato().getCpf().toString()));
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
    public Response fechaRelato(final Relato relato) {
        relato.getColaboradorFechamento()
                .setCodigo(ColaboradorBackwardHelper.getCodColaboradorByCpf(colaboradorAutenticadoProvider.get()
                                                                                    .getCodigo(),
                                                                            relato.getColaboradorFechamento()
                                                                                    .getCpf()
                                                                                    .toString()));
        if (service.fechaRelato(relato)) {
            return Response.ok("Relato fechado com sucesso");
        } else {
            return Response.error("Erro ao fechar o relato");
        }
    }

    @DELETE
    @Path("/{codigo}")
    @Secured(permissions = Pilares.Seguranca.Relato.FECHAR)
    public Response delete(@PathParam("codigo") final Long codigo) {
        if (service.delete(codigo)) {
            return Response.ok("Relato deletado com sucesso");
        } else {
            return Response.error("Erro ao deletar relato");
        }
    }
}