package br.com.zalf.prolog.webservice.frota.pneu.afericao.configuracao;

import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.configuracao._model.*;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.interceptors.log.DebugLog;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created on 03/05/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
@Path("/afericoes/configuracoes")
@DebugLog
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Secured(permissions = Pilares.Frota.Afericao.ConfiguracaoAfericao.CONFIGURAR)
public final class ConfiguracaoAfericaoResource {
    @NotNull
    private final ConfiguracaoAfericaoService service = new ConfiguracaoAfericaoService();

    @PUT
    @Path("/tipos-veiculo/{codUnidade}")
    public Response updateConfiguracaoTiposVeiculosAferiveis(
            @PathParam("codUnidade") @NotNull final Long codUnidade,
            @NotNull final List<ConfiguracaoTipoVeiculoAferivel> configuracoes) {
        return service.updateConfiguracaoTiposVeiculosAferiveis(codUnidade, configuracoes);
    }

    @GET
    @Path("/tipos-veiculo/{codUnidade}")
    public List<ConfiguracaoTipoVeiculoAferivel> getConfiguracoesTipoAfericaoVeiculo(
            @PathParam("codUnidade") @NotNull final Long codUnidade) {
        return service.getConfiguracoesTipoAfericaoVeiculo(codUnidade);
    }

    @PUT
    @Path("/alertas-sulcos")
    public Response updateConfiguracaoAlertaColetaSulco(
            @NotNull final List<ConfiguracaoAlertaColetaSulco> configuracoes) {
        return service.updateConfiguracaoAlertaColetaSulco(configuracoes);
    }

    @GET
    @Path("/alertas-sulcos")
    public List<ConfiguracaoAlertaColetaSulco> getConfiguracoesAlertaColetaSulco(
            @QueryParam("codColaborador") @NotNull final Long codColaborador) {
        return service.getConfiguracoesAlertaColetaSulco(codColaborador);
    }

    @PUT
    @Path("/cronograma-servicos")
    public Response upsertConfiguracoesCronogramaServicos(
            @HeaderParam("Authorization") String userToken,
            @NotNull final List<ConfiguracaoCronogramaServicoUpsert> configuracoes) {
        return service.upsertConfiguracoesCronogramaServicos(userToken, configuracoes);
    }

    @GET
    @Path("/cronograma-servicos")
    public List<ConfiguracaoCronogramaServico> getConfiguracoesCronogramaServicos(
            @QueryParam("codColaborador") @NotNull final Long codColaborador) {
        return service.getConfiguracoesCronogramaServicos(codColaborador);
    }

    @GET
    @Path("/cronograma-servicos-historico")
    public List<ConfiguracaoCronogramaServicoHistorico> getConfiguracoesCronogramaServicosHistorico(
            @QueryParam("codRestricaoUnidade") @NotNull final Long codRestricaoUnidade) {
        return service.getConfiguracoesCronogramaServicosHistorico(codRestricaoUnidade);
    }
}