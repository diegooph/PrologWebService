package br.com.zalf.prolog.webservice.cargo;

import br.com.zalf.prolog.webservice.cargo.model.*;
import br.com.zalf.prolog.webservice.commons.util.Required;
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
@Path("cargos")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
public final class CargoResource {
    @NotNull
    private final CargoService service = new CargoService();

    /**
     * Esse método não verifica nenhuma permissão. Isso porque ele poderá ser utilizado em diversas telas como
     * parâmetro para filtragem e pré-requisito de seleção em algum processo, como a criação de um modelo de checklist.
     * <p>
     * Controlar todos esses usos e permissões é muito complexo, e como esse método não revela nada demais, podemos
     * deixar sem a verificação. Apenas com {@link Secured secured} verificando se tem um token válido.
     */
    @GET
    @Secured
    @Path("/todos")
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
}