package br.com.zalf.prolog.webservice.gente.cargo;

import br.com.zalf.prolog.webservice.gente.cargo._model.*;
import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.network.metadata.Required;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created on 01/03/19
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Path("/v2/cargos")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
public final class CargoResource {
    @NotNull
    private final CargoService service = new CargoService();

    @POST
    @Secured(permissions = {
            Pilares.Gente.Permissao.VINCULAR_CARGO,
            Pilares.Gente.Colaborador.CADASTRAR,
            Pilares.Gente.Colaborador.EDITAR})
    public AbstractResponse insertCargo(@Required CargoInsercao cargo,
                                        @HeaderParam("Authorization") @Required final String userToken){
        return service.insertCargo(cargo, userToken);
    }

    @PUT
    @Secured(permissions = {
            Pilares.Gente.Permissao.VINCULAR_CARGO,
            Pilares.Gente.Colaborador.CADASTRAR,
            Pilares.Gente.Colaborador.EDITAR})
    public Response updateCargo(@Required final CargoEdicao cargo,
                                @HeaderParam("Authorization") @Required final String userToken) throws ProLogException {
        return service.updateCargo(cargo, userToken);
    }

    /**
     * Esse método não verifica nenhuma permissão. Isso porque ele poderá ser utilizado em diversas telas como
     * parâmetro para filtragem e pré-requisito de seleção em algum processo, como a criação de um modelo de checklist.
     * <p>
     * Controlar todos esses usos e permissões é muito complexo, e como esse método não revela nada demais, podemos
     * deixar sem a verificação. Apenas com {@link Secured secured} verificando se tem um token válido.
     */
    @GET
    @Secured
    @Path("/todos/por-unidade")
    public List<CargoSelecao> getTodosCargosUnidade(
            @QueryParam("codUnidade") @Required final Long codUnidade) throws ProLogException {
        return service.getTodosCargosUnidade(codUnidade);
    }

    @GET
    @Secured(permissions = {
            Pilares.Gente.Permissao.VINCULAR_CARGO,
            Pilares.Gente.Permissao.VISUALIZAR,
            Pilares.Gente.Colaborador.VISUALIZAR,
            Pilares.Gente.Colaborador.CADASTRAR,
            Pilares.Gente.Colaborador.EDITAR})
    @Path("/todos/por-empresa")
    public List<CargoListagemEmpresa> getTodosCargosEmpresa(
            @QueryParam("codEmpresa") @Required final Long codEmpresa) throws ProLogException {
        return service.getTodosCargosEmpresa(codEmpresa);
    }


    @GET
    @Secured(permissions = {
            Pilares.Gente.Permissao.VINCULAR_CARGO,
            Pilares.Gente.Permissao.VISUALIZAR,
            Pilares.Gente.Colaborador.VISUALIZAR,
            Pilares.Gente.Colaborador.CADASTRAR,
            Pilares.Gente.Colaborador.EDITAR})
    @Path("/em-uso")
    public List<CargoEmUso> getCargosEmUsoUnidade(
            @QueryParam("codUnidade") @Required final Long codUnidade) throws ProLogException {
        return service.getCargosEmUsoUnidade(codUnidade);
    }

    @GET
    @Secured(permissions = {
            Pilares.Gente.Permissao.VINCULAR_CARGO,
            Pilares.Gente.Permissao.VISUALIZAR,
            Pilares.Gente.Colaborador.VISUALIZAR,
            Pilares.Gente.Colaborador.CADASTRAR,
            Pilares.Gente.Colaborador.EDITAR})
    @Path("/nao-utilizados")
    public List<CargoNaoUtilizado> getCargosNaoUtilizadosUnidade(
            @QueryParam("codUnidade") @Required final Long codUnidade) throws ProLogException {
        return service.getCargosNaoUtilizadosUnidade(codUnidade);
    }

    @GET
    @Secured
    @Path("/permissoes")
    public CargoVisualizacao getPermissoesDetalhadasByUnidade(@QueryParam("codUnidade") @Required Long codUnidade,
                                                              @QueryParam("codCargo") @Required Long codCargo)
            throws ProLogException {
        return service.getPermissoesDetalhadasUnidade(codUnidade, codCargo);
    }

    @GET
    @Secured(permissions = {
            Pilares.Gente.Permissao.VINCULAR_CARGO,
            Pilares.Gente.Permissao.VISUALIZAR,
            Pilares.Gente.Colaborador.VISUALIZAR,
            Pilares.Gente.Colaborador.CADASTRAR,
            Pilares.Gente.Colaborador.EDITAR})
    public CargoEdicao getByCod(
            @QueryParam("codEmpresa") @Required final Long codEmpresa,
            @QueryParam("codigo") @Required final Long codigo) throws ProLogException {
        return service.getByCod(codEmpresa, codigo);
    }

    @DELETE
    @Secured(permissions = {
            Pilares.Gente.Permissao.VINCULAR_CARGO,
            Pilares.Gente.Colaborador.CADASTRAR,
            Pilares.Gente.Colaborador.EDITAR})
    @Path("/{codEmpresa}/{codigo}")
    public Response deleteCargo(@PathParam("codEmpresa") @Required final Long codEmpresa,
                                @PathParam("codigo") @Required final Long codigo,
                                @HeaderParam("Authorization") @Required final String userToken) throws ProLogException {
        return service.deleteCargo(codEmpresa, codigo, userToken);
    }
}