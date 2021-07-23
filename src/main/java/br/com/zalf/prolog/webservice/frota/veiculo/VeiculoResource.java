package br.com.zalf.prolog.webservice.frota.veiculo;

import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.network.ResponseWithCod;
import br.com.zalf.prolog.webservice.commons.network.metadata.Optional;
import br.com.zalf.prolog.webservice.commons.network.metadata.Platform;
import br.com.zalf.prolog.webservice.commons.network.metadata.Required;
import br.com.zalf.prolog.webservice.commons.network.metadata.UsedBy;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Marca;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Modelo;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;
import br.com.zalf.prolog.webservice.frota.veiculo.model.diagrama.DiagramaVeiculo;
import br.com.zalf.prolog.webservice.frota.veiculo.model.diagrama.DiagramaVeiculoNomenclatura;
import br.com.zalf.prolog.webservice.frota.veiculo.model.edicao.VeiculoEdicao;
import br.com.zalf.prolog.webservice.frota.veiculo.model.edicao.VeiculoEdicaoStatus;
import br.com.zalf.prolog.webservice.frota.veiculo.model.listagem.VeiculoListagem;
import br.com.zalf.prolog.webservice.frota.veiculo.model.visualizacao.VeiculoDadosColetaKm;
import br.com.zalf.prolog.webservice.frota.veiculo.model.visualizacao.VeiculoVisualizacao;
import br.com.zalf.prolog.webservice.interceptors.auth.ColaboradorAutenticado;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.interceptors.debug.ConsoleDebugLog;
import br.com.zalf.prolog.webservice.interceptors.versioncodebarrier.AppVersionCodeHandler;
import br.com.zalf.prolog.webservice.interceptors.versioncodebarrier.DefaultAppVersionCodeHandler;
import br.com.zalf.prolog.webservice.interceptors.versioncodebarrier.VersionCodeHandlerMode;
import br.com.zalf.prolog.webservice.interceptors.versioncodebarrier.VersionNotPresentAction;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import br.com.zalf.prolog.webservice.v3.frota.veiculo._model.VeiculoCadastroDto;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@ConsoleDebugLog
@Path("/v2/veiculos")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
public final class VeiculoResource {

    @NotNull
    private final VeiculoService service = new VeiculoService();

    @Inject
    private Provider<ColaboradorAutenticado> colaboradorAutenticadoProvider;

    @POST
    @Secured(permissions = Pilares.Frota.Veiculo.CADASTRAR)
    @UsedBy(platforms = Platform.WEBSITE)
    public Response insert(@HeaderParam("Authorization") @Required final String userToken,
                           @Required final VeiculoCadastroDto veiculo) {
        return service.insert(userToken, veiculo);
    }

    @PUT
    @Secured(permissions = {Pilares.Frota.Veiculo.ALTERAR, Pilares.Frota.Veiculo.CADASTRAR})
    public Response update(@HeaderParam("Authorization") @Required final String userToken,
                           @Required final VeiculoEdicao veiculo) {
        return service.update(colaboradorAutenticadoProvider.get().getCodigo(), userToken, veiculo);
    }

    @PUT
    @Path("/status")
    @Secured(permissions = {Pilares.Frota.Veiculo.ALTERAR, Pilares.Frota.Veiculo.CADASTRAR})
    public Response updateStatus(@HeaderParam("Authorization") @Required final String userToken,
                                 @Required final VeiculoEdicaoStatus veiculo) throws ProLogException {
        return service.updateStatus(colaboradorAutenticadoProvider.get().getCodigo(), userToken, veiculo);
    }

    @POST
    @Path("/unidade/colaborador")
    @Secured(permissions = {
            Pilares.Frota.Veiculo.VISUALIZAR,
            Pilares.Frota.Veiculo.ALTERAR,
            Pilares.Frota.Veiculo.CADASTRAR,
            Pilares.Frota.Checklist.VISUALIZAR_TODOS})
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public List<Veiculo> getVeiculosAtivosByUnidadeByColaborador(@FormParam("cpf") final Long cpf) {
        return service.getVeiculosAtivosByUnidadeByColaborador(cpf);
    }

    @GET
    @Secured(permissions = {
            Pilares.Frota.Veiculo.VISUALIZAR,
            Pilares.Frota.Veiculo.ALTERAR,
            Pilares.Frota.Veiculo.CADASTRAR,
            Pilares.Frota.Checklist.VISUALIZAR_TODOS})
    @Path("/busca/byUnidade")
    public List<VeiculoListagem> getVeiculosByUnidade(@HeaderParam("Authorization") @Required final String userToken,
                                                      @QueryParam("codUnidade") @Required final Long codUnidade,
                                                      @QueryParam("somenteAtivos") @Optional final boolean somenteAtivos) {
        return service.getVeiculosByUnidades(userToken,
                                             Collections.singletonList(codUnidade),
                                             somenteAtivos,
                                             null);
    }

    @GET
    @Secured(permissions = {
            Pilares.Frota.Veiculo.VISUALIZAR,
            Pilares.Frota.Veiculo.ALTERAR,
            Pilares.Frota.Veiculo.CADASTRAR,
            Pilares.Frota.Checklist.REALIZAR,
            Pilares.Frota.Checklist.VISUALIZAR_TODOS,
            Pilares.Frota.Relatorios.CHECKLIST,
            Pilares.Frota.OrdemServico.Pneu.VISUALIZAR,
            Pilares.Frota.OrdemServico.Checklist.VISUALIZAR,
            Pilares.Frota.OrdemServico.Checklist.RESOLVER_ITEM,
            Pilares.Frota.Afericao.REALIZAR_AFERICAO_PLACA,
            Pilares.Frota.Afericao.VISUALIZAR_TODAS_AFERICOES,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_VEICULO_ESTOQUE,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_ANALISE,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_DESCARTE})
    @Path("/listagem")
    public List<VeiculoListagem> getVeiculosByUnidades(@HeaderParam("Authorization") @Required final String userToken,
                                                       @QueryParam("codUnidades") @Required final List<Long> codUnidades,
                                                       @QueryParam("apenasAtivos") @Optional final boolean apenasAtivos,
                                                       @QueryParam("codTipoVeiculo") @Optional final Long codTipoVeiculo) {
        return service.getVeiculosByUnidades(userToken, codUnidades, apenasAtivos, codTipoVeiculo);
    }

    @GET
    @Secured(permissions = {Pilares.Frota.Veiculo.VISUALIZAR,
            Pilares.Frota.Veiculo.ALTERAR,
            Pilares.Frota.Veiculo.CADASTRAR,
            Pilares.Frota.Checklist.VISUALIZAR_TODOS,
            Pilares.Frota.Checklist.REALIZAR,
            Pilares.Frota.OrdemServico.Pneu.VISUALIZAR,
            Pilares.Frota.OrdemServico.Checklist.VISUALIZAR,
            Pilares.Frota.OrdemServico.Checklist.RESOLVER_ITEM,
            Pilares.Frota.Afericao.REALIZAR_AFERICAO_PLACA,
            Pilares.Frota.Afericao.VISUALIZAR_TODAS_AFERICOES,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_VEICULO_ESTOQUE,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_ANALISE,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_DESCARTE})
    @Path("/byTipo/{codUnidade}/{codTipo}")
    @UsedBy(platforms = Platform.ANDROID)
    public List<String> getVeiculosByTipo(@PathParam("codUnidade") final Long codUnidade,
                                          @PathParam("codTipo") final String codTipo,
                                          @HeaderParam("Authorization") final String userToken) {
        return service.getVeiculosByTipo(codUnidade, codTipo, userToken);
    }

    @GET
    @Secured(permissions = {Pilares.Frota.Veiculo.CADASTRAR, Pilares.Frota.Veiculo.ALTERAR})
    @Path("/marcas")
    public List<Marca> getMarcasVeiculosNivelProLog() throws ProLogException {
        return service.getMarcasVeiculosNivelProLog();
    }

    @GET
    @Secured(permissions = {Pilares.Frota.Veiculo.CADASTRAR, Pilares.Frota.Veiculo.ALTERAR})
    @Path("/marcas-modelos/{codEmpresa}")
    public List<Marca> getMarcasModelosVeiculosByEmpresa(@PathParam("codEmpresa") final Long codEmpresa)
            throws ProLogException {
        return service.getMarcasModelosVeiculosByEmpresa(codEmpresa);
    }

    @POST
    @Secured(permissions = {Pilares.Frota.Veiculo.CADASTRAR, Pilares.Frota.Veiculo.ALTERAR})
    @Path("/modelo/{codEmpresa}/{codMarca}")
    public ResponseWithCod insertModeloVeiculo(final Modelo modelo,
                                               @PathParam("codEmpresa") final Long codEmpresa,
                                               @PathParam("codMarca") final Long codMarca) throws ProLogException {
        return ResponseWithCod.ok(
                "Modelo cadastrado com sucesso",
                service.insertModeloVeiculo(modelo, codEmpresa, codMarca));
    }

    @GET
    @Secured(permissions = {Pilares.Frota.Veiculo.CADASTRAR, Pilares.Frota.Veiculo.ALTERAR,
            Pilares.Frota.Veiculo.VISUALIZAR})
    @Path("/modelos/{codUnidade}/{codModelo}")
    public Modelo getModeloVeiculo(@PathParam("codUnidade") final Long codUnidade,
                                   @PathParam("codModelo") final Long codModelo) {
        return service.getModeloVeiculo(codUnidade, codModelo);
    }

    @PUT
    @Secured(permissions = {Pilares.Frota.Veiculo.CADASTRAR, Pilares.Frota.Veiculo.ALTERAR})
    @Path("/modelos/{codUnidade}/{codMarca}/{codModelo}")
    public Response updateModelo(final Modelo modelo,
                                 @PathParam("codUnidade") final Long codUnidade,
                                 @PathParam("codMarca") final Long codMarca) {
        if (service.updateModelo(modelo, codUnidade, codMarca)) {
            return Response.ok("Modelo alterado com sucesso");
        } else {
            return Response.error("Erro ao atualizar o modelo");
        }
    }

    @DELETE
    @Secured(permissions = {Pilares.Frota.Veiculo.CADASTRAR, Pilares.Frota.Veiculo.ALTERAR})
    @Path("/modelos/{codUnidade}/{codModelo}")
    public Response deleteModelo(@PathParam("codModelo") final Long codModelo,
                                 @PathParam("codUnidade") final Long codUnidade) {
        if (service.deleteModelo(codModelo, codUnidade)) {
            return Response.ok("Modelo deletado com sucesso");
        } else {
            return Response.error("Erro ao deletar o modelo");
        }
    }

    @GET
    @Secured(permissions = {Pilares.Frota.Veiculo.CADASTRAR, Pilares.Frota.Veiculo.ALTERAR})
    @Path("/diagramas")
    public Set<DiagramaVeiculo> getDiagramasVeiculos() {
        return service.getDiagramasVeiculo();
    }

    @GET
    @Secured(permissions = {Pilares.Frota.Veiculo.CADASTRAR, Pilares.Frota.Veiculo.ALTERAR})
    @Path("/diagramas-nomenclaturas")
    public List<DiagramaVeiculoNomenclatura> getDiagramasVeiculosNomenclaturas(
            @QueryParam("codEmpresa") @NotNull final Long codEmpresa) {
        return service.getDiagramasVeiculosNomenclaturas(codEmpresa);
    }

    @GET
    @Secured(permissions = {Pilares.Frota.Veiculo.VISUALIZAR,
            Pilares.Frota.Veiculo.CADASTRAR,
            Pilares.Frota.Veiculo.ALTERAR,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_VEICULO_ESTOQUE,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_VEICULO_ESTOQUE,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_ANALISE,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_DESCARTE})
    @Path("/visualizacao")
    @AppVersionCodeHandler(
            implementation = DefaultAppVersionCodeHandler.class,
            targetVersionCode = 68,
            versionCodeHandlerMode = VersionCodeHandlerMode.BLOCK_THIS_VERSION_AND_BELOW,
            actionIfVersionNotPresent = VersionNotPresentAction.BLOCK_ANYWAY)
    public VeiculoVisualizacao getVeiculoByCodigo(@HeaderParam("Authorization") final String userToken,
                                                  @QueryParam("codVeiculo") final Long codVeiculo) {
        return service.getVeiculoByCodigo(userToken, codVeiculo);
    }

    @GET
    @Secured(permissions = {Pilares.Frota.Veiculo.VISUALIZAR,
            Pilares.Frota.Veiculo.CADASTRAR,
            Pilares.Frota.Veiculo.ALTERAR})
    @Path("/sem-pneus/{placa}")
    @AppVersionCodeHandler(
            targetVersionCode = 124,
            versionCodeHandlerMode = VersionCodeHandlerMode.BLOCK_THIS_VERSION_AND_BELOW,
            actionIfVersionNotPresent = VersionNotPresentAction.BLOCK_ANYWAY)
    public Veiculo getVeiculoByPlacaSemPneus(@HeaderParam("Authorization") final String userToken,
                                             @PathParam("placa") final String placa,
                                             @QueryParam("codUnidade") final Long codUnidade) {
        return service.getVeiculoByPlaca(userToken, placa, codUnidade, false);
    }

    /**
     * @deprecated at 2020-05-07.
     * <p>
     * Este método foi depreciado pois um novo foi criado: {@link #getVeiculoByCodigo(Long)}
     */
    @Deprecated
    @GET
    @Secured(permissions = {Pilares.Frota.Veiculo.VISUALIZAR,
            Pilares.Frota.Veiculo.CADASTRAR,
            Pilares.Frota.Veiculo.ALTERAR,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_VEICULO_ESTOQUE,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_VEICULO_ESTOQUE,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_ANALISE,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_DESCARTE})
    @Path("/com-pneus/{placa}")
    @AppVersionCodeHandler(
            targetVersionCode = 124,
            versionCodeHandlerMode = VersionCodeHandlerMode.BLOCK_THIS_VERSION_AND_BELOW,
            actionIfVersionNotPresent = VersionNotPresentAction.BLOCK_ANYWAY)
    public Veiculo getVeiculoByPlacaComPneus(@HeaderParam("Authorization") final String userToken,
                                             @PathParam("placa") final String placa,
                                             @QueryParam("codUnidade") final Long codUnidade) {
        return service.getVeiculoByPlaca(userToken, placa, codUnidade, true);
    }

    /**
     * @deprecated at 2019-01-23.
     * <p>
     * Este método foi depreciado pois era utilizado em locais com diferentes finalidades:
     * 1 - No cadastro/edição de veículos, como seleção de qual a marca/modelo do veículo.
     * 2 - No cadastro/edição/listagem de marcas e modelos de veículos.
     * <p>
     * O problema, é que no primeiro caso, o método deveria retornar todas as marcas do BD, no segundo, apenas as marcas
     * para as quais a empresa tem modelos associados. Essa distinção não é lidada por esse método, por isso optamos
     * por depreciar e criar outros.
     * <p>
     * Dessa forma, separamos essa lógica em dois métodos, caso queira o caso 1, utilize
     * {@link #getMarcasVeiculosNivelProLog()} se for o caso 2, utilize
     * {@link #getMarcasModelosVeiculosByEmpresa(Long)}.
     */
    @Deprecated
    @GET
    @Secured(permissions = {Pilares.Frota.Veiculo.CADASTRAR, Pilares.Frota.Veiculo.ALTERAR})
    @Path("/marcaModelos/{codEmpresa}")
    public List<Marca> getMarcaModeloVeiculoByCodEmpresa(@PathParam("codEmpresa") final Long codEmpresa) {
        return service.getMarcaModeloVeiculoByCodEmpresa(codEmpresa);
    }

    /**
     * @deprecated at 2020-05-07.
     * <p>
     * Este método foi depreciado pois um novo foi criado: {@link #getVeiculosByUnidades}
     */
    @Deprecated
    @GET
    @Secured(permissions = {
            Pilares.Frota.Veiculo.VISUALIZAR,
            Pilares.Frota.Veiculo.ALTERAR,
            Pilares.Frota.Veiculo.CADASTRAR,
            Pilares.Frota.Checklist.VISUALIZAR_TODOS})
    @Path("/{codUnidade}")
    public List<Veiculo> getVeiculosAtivosByUnidade(@HeaderParam("Authorization") @Required final String userToken,
                                                    @PathParam("codUnidade") @Required final Long codUnidade,
                                                    @QueryParam("ativos") @Optional final Boolean ativos) {
        return service.getVeiculosAtivosByUnidade(userToken, codUnidade, ativos);
    }

    @GET
    @Secured(permissions = {
            Pilares.Frota.Veiculo.VISUALIZAR,
            Pilares.Frota.Veiculo.ALTERAR,
            Pilares.Frota.Veiculo.CADASTRAR,
            Pilares.Frota.Veiculo.VISUALIZAR_RELATORIOS,
            Pilares.Frota.Checklist.REALIZAR,
            Pilares.Frota.OrdemServico.Checklist.RESOLVER_ITEM,
            Pilares.Frota.Afericao.REALIZAR_AFERICAO_PLACA,
            Pilares.Frota.SocorroRota.SOLICITAR_SOCORRO,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_VEICULO_ESTOQUE,
            Pilares.Frota.OrdemServico.Pneu.CONSERTAR_ITEM})
    @Path("/dados-coleta-km")
    public VeiculoDadosColetaKm getDadosColetaKmByCodigo(@HeaderParam("Authorization") @Required final String userToken,
                                                         @QueryParam("codVeiculo") final Long codVeiculo) {
        return service.getDadosColetaKmByCodigo(userToken, codVeiculo);
    }
}