package br.com.zalf.prolog.webservice.gente.colaborador;

import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.network.metadata.Optional;
import br.com.zalf.prolog.webservice.commons.network.metadata.Platform;
import br.com.zalf.prolog.webservice.commons.network.metadata.Required;
import br.com.zalf.prolog.webservice.commons.network.metadata.UsedBy;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.gente.colaborador.model.*;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.interceptors.debug.ConsoleDebugLog;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@ConsoleDebugLog
@Path("/v2/colaboradores")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class ColaboradorResource {

    private static final String TAG = ColaboradorResource.class.getSimpleName();
    private final ColaboradorService service = new ColaboradorService();

    @POST
    @Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Secured(permissions = Pilares.Gente.Colaborador.CADASTRAR)
    public Response insert(@Valid final ColaboradorInsercao colaborador, @HeaderParam("Authorization") final String userToken)
            throws Throwable {
        service.insert(colaborador, userToken);
        return Response.ok("Colaborador inserido com sucesso");
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Secured(permissions = {Pilares.Gente.Colaborador.EDITAR, Pilares.Gente.Colaborador.CADASTRAR})
    public Response update(@Valid final ColaboradorEdicao colaborador, @HeaderParam("Authorization") final String userToken)
            throws Throwable {
        service.update(colaborador, userToken);
        return Response.ok("Colaborador atualizado com sucesso");
    }

    @PUT
    @Path("/{cpf}/status")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Secured(permissions = {Pilares.Gente.Colaborador.EDITAR, Pilares.Gente.Colaborador.CADASTRAR})
    public Response updateStatus(@PathParam("cpf") final Long cpf, final Colaborador colaborador) {
        if (service.updateStatus(cpf, colaborador)) {
            return Response.ok("Colaborador atualizado com sucesso");
        } else {
            return Response.error("Erro ao atualizar o colaborador");
        }
    }

    @GET
    @Secured
    @Path("/getByCod/{cpf}")
    public Colaborador getByCpf(@PathParam("cpf") final Long cpf) throws ProLogException {
        Log.d(TAG, cpf.toString());
        return service.getByCpf(cpf);
    }

    @POST
    @Secured
    @Path("/login/app")
    public LoginHolder getLoginHolder(final LoginRequest loginRequest) {
        return service.getLoginHolder(loginRequest);
    }

    @GET
    @Path("unidades/{codUnidade}/")
    @Secured(permissions = {
            Pilares.Gente.Colaborador.CADASTRAR,
            Pilares.Gente.Colaborador.VISUALIZAR,
            Pilares.Gente.Colaborador.EDITAR,
            Pilares.Gente.Relatorios.INTERVALOS,
            Pilares.Entrega.EscalaDiaria.INSERIR_REGISTRO,
            Pilares.Entrega.EscalaDiaria.EDITAR,
            Pilares.Entrega.Produtividade.CONSOLIDADO,
            Pilares.Entrega.Relatorios.PRODUTIVIDADE,
            Pilares.Entrega.Relatorios.INDICADORES,
            Pilares.Entrega.RaizenProdutividade.INSERIR_REGISTROS,
            Pilares.Entrega.RaizenProdutividade.EDITAR})
    public List<Colaborador> getAllByUnidade(@PathParam("codUnidade") @Required final Long codUnidade,
                                             @QueryParam("apenasAtivos") @Optional final boolean apenasAtivos)
            throws ProLogException {
        return service.getAllByUnidade(codUnidade, apenasAtivos);
    }

    @GET
    @Secured(permissions = {
            Pilares.Gente.Colaborador.CADASTRAR,
            Pilares.Gente.Colaborador.VISUALIZAR,
            Pilares.Gente.Colaborador.EDITAR,
            Pilares.Gente.Relatorios.INTERVALOS,
            Pilares.Entrega.EscalaDiaria.INSERIR_REGISTRO,
            Pilares.Entrega.EscalaDiaria.EDITAR,
            Pilares.Entrega.Produtividade.CONSOLIDADO,
            Pilares.Entrega.Relatorios.PRODUTIVIDADE,
            Pilares.Entrega.Relatorios.INDICADORES,
            Pilares.Entrega.RaizenProdutividade.INSERIR_REGISTROS,
            Pilares.Entrega.RaizenProdutividade.EDITAR})
    @UsedBy(platforms = {Platform.ANDROID, Platform.WEBSITE})
    @Path("/listagem")
    public List<ColaboradorListagem> getAllByUnidades(@QueryParam("codUnidades") @Required final List<Long> codUnidades,
                                                      @QueryParam("apenasAtivos") @Optional final boolean apenasAtivos) {
        return service.getAllByUnidades(codUnidades, apenasAtivos);
    }

    @GET
    @Path("empresas/{codEmpresa}/")
    @Secured(permissions = {
            Pilares.Gente.Colaborador.CADASTRAR,
            Pilares.Gente.Colaborador.VISUALIZAR,
            Pilares.Gente.Colaborador.EDITAR})
    public List<Colaborador> getAllByEmpresa(@PathParam("codEmpresa") @Required final Long codEmrpesa,
                                             @QueryParam("apenasAtivos") @Optional final boolean apenasAtivos)
            throws ProLogException {
        return service.getAllByEmpresa(codEmrpesa, apenasAtivos);
    }

    @GET
    @Path("/{codUnidade}/motoristas-e-ajudantes")
    @Secured(permissions = Pilares.Gente.Colaborador.VISUALIZAR)
    public List<Colaborador> getMotoristasAndAjudantes(@PathParam("codUnidade") final Long codUnidade) {
        return service.getMotoristasAndAjudantes(codUnidade);
    }

    @DELETE
    @Path("/{cpf}")
    @Secured(permissions = {Pilares.Gente.Colaborador.EDITAR, Pilares.Gente.Colaborador.CADASTRAR})
    public Response delete(@PathParam("cpf") final Long cpf) {
        if (service.delete(cpf)) {
            return Response.ok("Colaborador deletado com sucesso");
        } else {
            return Response.error("Falha ao deletar colaborador");
        }
    }
}