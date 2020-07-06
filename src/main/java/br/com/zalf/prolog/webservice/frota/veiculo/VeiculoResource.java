package br.com.zalf.prolog.webservice.frota.veiculo;

import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.network.ResponseWithCod;
import br.com.zalf.prolog.webservice.commons.util.Optional;
import br.com.zalf.prolog.webservice.commons.util.Platform;
import br.com.zalf.prolog.webservice.commons.util.Required;
import br.com.zalf.prolog.webservice.commons.util.UsedBy;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.frota.veiculo.model.*;
import br.com.zalf.prolog.webservice.frota.veiculo.model.diagrama.DiagramaVeiculo;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.interceptors.versioncodebarrier.AppVersionCodeHandler;
import br.com.zalf.prolog.webservice.interceptors.versioncodebarrier.DefaultAppVersionCodeHandler;
import br.com.zalf.prolog.webservice.interceptors.versioncodebarrier.VersionCodeHandlerMode;
import br.com.zalf.prolog.webservice.interceptors.versioncodebarrier.VersionNotPresentAction;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Path("veiculos")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
public final class VeiculoResource {

    private final VeiculoService service = new VeiculoService();

    @POST
    @Secured(permissions = Pilares.Frota.Veiculo.CADASTRAR)
    @UsedBy(platforms = Platform.WEBSITE)
    @Path("/")
    public Response insert(@HeaderParam("Authorization") @Required final String userToken,
                           @Required final VeiculoCadastro veiculo) throws ProLogException {
        return service.insert(userToken, veiculo);
    }

    @PUT
    @Secured(permissions = {Pilares.Frota.Veiculo.ALTERAR, Pilares.Frota.Veiculo.CADASTRAR})
    @Path("/{placaOriginal}")
    public Response update(@HeaderParam("Authorization") @Required final String userToken,
                           @PathParam("placaOriginal") @Required final String placaOriginal,
                           @Required final Veiculo veiculo) throws ProLogException {
        return service.update(userToken, placaOriginal, veiculo);
    }

    @PUT
    @Path("/{codUnidade}/{placa}/status")
    @Secured(permissions = {Pilares.Frota.Veiculo.ALTERAR, Pilares.Frota.Veiculo.CADASTRAR})
    public Response updateStatus(@HeaderParam("Authorization") @Required final String userToken,
                                 @PathParam("codUnidade") @Required final Long codUnidade,
                                 @PathParam("placa") @Required final String placa,
                                 @Required final Veiculo veiculo) throws ProLogException {
        return service.updateStatus(userToken, codUnidade, placa, veiculo);
    }

    @DELETE
    @Secured(permissions = {Pilares.Frota.Veiculo.ALTERAR, Pilares.Frota.Veiculo.CADASTRAR})
    @Path("/{placa}")
    public Response delete(@HeaderParam("Authorization") @Required final String userToken,
                           @PathParam("placa") @Required final String placa) throws ProLogException {
        return service.delete(userToken, placa);
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
    public List<VeiculoListagem> buscaVeiculosByUnidade(@HeaderParam("Authorization") @Required final String userToken,
                                                        @QueryParam("codUnidade") @Required final Long codUnidade,
                                                        @QueryParam("somenteAtivos") @Optional final boolean somenteAtivos) {
        return service.buscaVeiculosByUnidades(Collections.singletonList(codUnidade), somenteAtivos, null);
    }

    @GET
    @Secured(permissions = {
            Pilares.Frota.Veiculo.VISUALIZAR,
            Pilares.Frota.Veiculo.ALTERAR,
            Pilares.Frota.Veiculo.CADASTRAR,
            Pilares.Frota.Checklist.VISUALIZAR_TODOS})
    @Path("/listagem")
    public List<VeiculoListagem> buscaVeiculosByUnidades(@QueryParam("codUnidades") @Required final List<Long> codUnidades,
                                                         @QueryParam("apenasAtivos") @Optional final boolean apenasAtivos,
                                                         @QueryParam("codTipoVeiculo") @Optional final Long codTipoVeiculo) {
        return service.buscaVeiculosByUnidades(codUnidades, apenasAtivos, codTipoVeiculo);
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
    public List<Marca> getMarcasModelosVeiculosByEmpresa(@PathParam("codEmpresa") final Long codEmpresa) throws ProLogException {
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
    @Secured(permissions = {Pilares.Frota.Veiculo.CADASTRAR, Pilares.Frota.Veiculo.ALTERAR, Pilares.Frota.Veiculo.VISUALIZAR})
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
    @Secured
    @Path("/eixos")
    public List<Eixos> getEixos() {
        return service.getEixos();
    }

    @GET
    @Secured(permissions = {Pilares.Frota.Veiculo.CADASTRAR, Pilares.Frota.Veiculo.ALTERAR})
    @Path("/diagramas")
    public Set<DiagramaVeiculo> getDiagramasVeiculos() {
        return service.getDiagramasVeiculo();
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
    public VeiculoVisualizacao buscaVeiculoByCodigo(@HeaderParam("Authorization") final String userToken,
                                                    @QueryParam("codVeiculo") final Long codVeiculo) {
        return service.buscaVeiculoByCodigo(userToken, codVeiculo);
    }

    @GET
    @Secured(permissions = {Pilares.Frota.Veiculo.VISUALIZAR,
            Pilares.Frota.Veiculo.CADASTRAR,
            Pilares.Frota.Veiculo.ALTERAR})
    @Path("/sem-pneus/{placa}")
    public Veiculo getVeiculoByPlacaSemPneus(@HeaderParam("Authorization") final String userToken,
                                             @PathParam("placa") final String placa) {
        return service.getVeiculoByPlaca(userToken, placa, false);
    }

    /**
     * @deprecated at 2020-05-07.
     * <p>
     * Este método foi depreciado pois um novo foi criado: {@link #buscaVeiculoByCodigo(String, Long)}
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
            implementation = DefaultAppVersionCodeHandler.class,
            targetVersionCode = 68,
            versionCodeHandlerMode = VersionCodeHandlerMode.BLOCK_THIS_VERSION_AND_BELOW,
            actionIfVersionNotPresent = VersionNotPresentAction.BLOCK_ANYWAY)
    public Veiculo getVeiculoByPlacaComPneus(@HeaderParam("Authorization") final String userToken,
                                             @PathParam("placa") final String placa) {
        return service.getVeiculoByPlaca(userToken, placa, true);
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
     * {@link #getMarcasVeiculosNivelProLog()} se for o caso 2, utilize {@link #getMarcasModelosVeiculosByEmpresa(Long)}.
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
     * Este método foi depreciado pois um novo foi criado: {@link #buscaVeiculosByUnidades}
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

}