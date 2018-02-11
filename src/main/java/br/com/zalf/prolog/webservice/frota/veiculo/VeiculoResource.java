package br.com.zalf.prolog.webservice.frota.veiculo;

import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.util.*;
import br.com.zalf.prolog.webservice.frota.veiculo.model.*;
import br.com.zalf.prolog.webservice.frota.veiculo.model.diagrama.DiagramaVeiculo;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Set;

@Path("veiculos")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class VeiculoResource {

    private VeiculoService service = new VeiculoService();

    @POST
    @Secured(permissions = Pilares.Frota.Veiculo.CADASTRAR)
    @Path("/{codUnidade}")
    public Response insert(Veiculo veiculo, @PathParam("codUnidade") Long codUnidade) {
        if (service.insert(veiculo, codUnidade)) {
            return Response.ok("Veículo inserido com sucesso");
        } else {
            return Response.error("Erro ao inserir o veículo");
        }
    }

    @PUT
    @Secured(permissions = {Pilares.Frota.Veiculo.ALTERAR, Pilares.Frota.Veiculo.CADASTRAR})
    @Path("/{placaOriginal}")
    public Response update(Veiculo veiculo, @PathParam("placaOriginal") String placaOriginal) {
        if (service.update(veiculo, placaOriginal)) {
            return Response.ok("Veículo atualizado com sucesso");
        } else {
            return Response.error("Erro ao atualizar o veículo");
        }
    }

    @PUT
    @Path("/{codUnidade}/{placa}/status")
    @Secured(permissions = {Pilares.Frota.Veiculo.ALTERAR, Pilares.Frota.Veiculo.CADASTRAR})
    public Response updateStatus(@PathParam("codUnidade") @Required Long codUnidade,
                                 @PathParam("placa") @Required String placa,
                                 Veiculo veiculo) {
        if (service.updateStatus(codUnidade, placa, veiculo)) {
            return Response.ok("Colaborador atualizado com sucesso");
        } else {
            return Response.error("Erro ao atualizar o colaborador");
        }
    }

    @DELETE
    @Secured(permissions = {Pilares.Frota.Veiculo.ALTERAR, Pilares.Frota.Veiculo.CADASTRAR})
    @Path("/{placa}")
    public Response delete(@PathParam("placa") String placa) {
        if (service.delete(placa)) {
            return Response.ok("Veículo deletado com sucesso.");
        } else {
            return Response.error("Erro ao deletar o veículo.");
        }
    }

    @POST
    @Path("/unidade/colaborador")
    @Secured(permissions = {Pilares.Frota.Veiculo.VISUALIZAR, Pilares.Frota.Veiculo.ALTERAR,
            Pilares.Frota.Veiculo.CADASTRAR})
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public List<Veiculo> getVeiculosAtivosByUnidadeByColaborador(@FormParam("cpf") Long cpf) {
        return service.getVeiculosAtivosByUnidadeByColaborador(cpf);
    }

    @GET
    @Secured(permissions = {Pilares.Frota.Veiculo.VISUALIZAR, Pilares.Frota.Veiculo.ALTERAR,
            Pilares.Frota.Veiculo.CADASTRAR})
    @Path("/{codUnidade}")
    public List<Veiculo> getVeiculosAtivosByUnidade(@HeaderParam("Authorization") @Required String userToken,
                                                    @PathParam("codUnidade") @Required Long codUnidade,
                                                    @QueryParam("ativos") @Optional Boolean ativos) {
        return service.getVeiculosAtivosByUnidade(userToken, codUnidade, ativos);
    }

    @POST
    @Secured(permissions = {Pilares.Frota.Veiculo.CADASTRAR, Pilares.Frota.Veiculo.ALTERAR})
    @Path("/{codUnidade}/tipo")
    public Response insertTipoVeiculo(TipoVeiculo tipoVeiculo, @PathParam("codUnidade") Long codUnidade) {
        if (service.insertTipoVeiculo(tipoVeiculo, codUnidade)) {
            return Response.ok("Tipo de veículo inserido com sucesso");
        } else {
            return Response.error("Erro ao inserir o tipo de veículo");
        }
    }

    @GET
    @Secured(permissions = {Pilares.Frota.Veiculo.VISUALIZAR,
            Pilares.Frota.Veiculo.ALTERAR,
            Pilares.Frota.Veiculo.CADASTRAR,
            Pilares.Frota.Checklist.VISUALIZAR_TODOS,
            Pilares.Frota.Checklist.REALIZAR,
            Pilares.Frota.OrdemServico.Pneu.VISUALIZAR,
            Pilares.Frota.OrdemServico.Checklist.VISUALIZAR,
            Pilares.Frota.OrdemServico.Checklist.CONSERTAR_ITEM,
            Pilares.Frota.Afericao.REALIZAR,
            Pilares.Frota.Afericao.VISUALIZAR,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_GERAL})
    @Path("/byTipo/{codUnidade}/{codTipo}")
    @UsedBy(platforms = Platform.ANDROID)
    public List<String> getVeiculosByTipo(@PathParam("codUnidade") Long codUnidade,
                                          @PathParam("codTipo") String codTipo,
                                          @HeaderParam("Authorization") String userToken) {
        return service.getVeiculosByTipo(codUnidade, codTipo, userToken);
    }

    @GET
    @Secured(permissions = {Pilares.Frota.Veiculo.VISUALIZAR,
            Pilares.Frota.Veiculo.ALTERAR,
            Pilares.Frota.Veiculo.CADASTRAR,
            Pilares.Frota.Checklist.VISUALIZAR_TODOS,
            Pilares.Frota.Checklist.REALIZAR,
            Pilares.Frota.OrdemServico.Pneu.VISUALIZAR,
            Pilares.Frota.OrdemServico.Checklist.VISUALIZAR,
            Pilares.Frota.OrdemServico.Checklist.CONSERTAR_ITEM,
            Pilares.Frota.Afericao.REALIZAR,
            Pilares.Frota.Afericao.VISUALIZAR,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_GERAL})
    @Path("/{codUnidade}/tipo")
    @UsedBy(platforms = {Platform.WEBSITE, Platform.ANDROID})
    public List<TipoVeiculo> getTipoVeiculosByUnidade(@HeaderParam("Authorization") String userToken,
                                                      @PathParam("codUnidade") Long codUnidade) {
        return service.getTipoVeiculosByUnidade(userToken, codUnidade);
    }

    @GET
    @Secured(permissions = {Pilares.Frota.Veiculo.CADASTRAR, Pilares.Frota.Veiculo.ALTERAR})
    @Path("/marcaModelos/{codEmpresa}")
    public List<Marca> getMarcaModeloVeiculoByCodEmpresa(@PathParam("codEmpresa") Long codEmpresa) {
        return service.getMarcaModeloVeiculoByCodEmpresa(codEmpresa);
    }

    @POST
    @Secured(permissions = {Pilares.Frota.Veiculo.CADASTRAR, Pilares.Frota.Veiculo.ALTERAR})
    @Path("/modelo/{codEmpresa}/{codMarca}")
    public Response insertModeloVeiculo(Modelo modelo, @PathParam("codEmpresa") long codEmpresa, @PathParam("codMarca") long codMarca) {
        if (service.insertModeloVeiculo(modelo, codEmpresa, codMarca)) {
            return Response.ok("Modelo inserido com sucesso");
        } else {
            return Response.error("Erro ao inserir o modelo");
        }
    }

    @GET
    @Secured(permissions = {Pilares.Frota.Veiculo.CADASTRAR, Pilares.Frota.Veiculo.ALTERAR, Pilares.Frota.Veiculo.VISUALIZAR})
    @Path("/modelos/{codUnidade}/{codModelo}")
    public Modelo getModeloVeiculo(@PathParam("codUnidade") Long codUnidade, @PathParam("codModelo") Long codModelo) {
        return service.getModeloVeiculo(codUnidade, codModelo);
    }

    @PUT
    @Secured(permissions = {Pilares.Frota.Veiculo.CADASTRAR, Pilares.Frota.Veiculo.ALTERAR})
    @Path("/modelos/{codUnidade}/{codMarca}/{codModelo}")
    public Response updateModelo(Modelo modelo, @PathParam("codUnidade") Long codUnidade, @PathParam("codMarca") Long codMarca) {
        if(service.updateModelo(modelo, codUnidade, codMarca)){
            return Response.ok("Modelo alterado com sucesso");
        }else{
            return Response.error("Erro ao atualizar o modelo");
        }
    }

    @DELETE
    @Secured(permissions = {Pilares.Frota.Veiculo.CADASTRAR, Pilares.Frota.Veiculo.ALTERAR})
    @Path("/modelos/{codUnidade}/{codModelo}")
    public Response deleteModelo(@PathParam("codModelo") Long codModelo, @PathParam("codUnidade") Long codUnidade) {
        if(service.deleteModelo(codModelo, codUnidade)){
            return Response.ok("Modelo deletado com sucesso");
        }else{
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
    @Secured(permissions = {Pilares.Frota.Veiculo.VISUALIZAR, Pilares.Frota.Veiculo.CADASTRAR, Pilares.Frota.Veiculo.ALTERAR})
    @Path("/com-pneus/{placa}")
    public Veiculo getVeiculoByPlacaComPneus(@HeaderParam("Authorization") String userToken,
                                             @PathParam("placa") String placa) {
        return service.getVeiculoByPlaca(userToken, placa, true);
    }

    @GET
    @Secured(permissions = {Pilares.Frota.Veiculo.VISUALIZAR, Pilares.Frota.Veiculo.CADASTRAR, Pilares.Frota.Veiculo.ALTERAR})
    @Path("/sem-pneus/{placa}")
    public Veiculo getVeiculoByPlacaSemPneus(@HeaderParam("Authorization") String userToken,
                                             @PathParam("placa") String placa) {
        return service.getVeiculoByPlaca(userToken, placa, false);
    }

    @PUT
    @Secured(permissions = {Pilares.Frota.Veiculo.CADASTRAR, Pilares.Frota.Veiculo.ALTERAR})
    @Path("/tipos/{codUnidade}/{codTipo}")
    public Response updateTipoVeiculo(TipoVeiculo tipo, @PathParam("codUnidade") Long codUnidade) {
        if(service.updateTipoVeiculo(tipo, codUnidade)){
            return Response.ok("Tipo alterado com sucesso");
        }else{
            return Response.error("Erro ao alterar o tipo");
        }
    }

    @DELETE
    @Secured(permissions = {Pilares.Frota.Veiculo.CADASTRAR, Pilares.Frota.Veiculo.ALTERAR})
    @Path("/tipos/{codUnidade}/{codTipo}")
    public Response deleteTipoVeiculo(@PathParam("codTipo") Long codTipo, @PathParam("codUnidade") Long codUnidade) {
        if (service.deleteTipoVeiculo(codTipo, codUnidade)) {
            return Response.ok("Tipo deletado com sucesso");
        } else {
            return Response.error("Erro ao deletar o tipo");
        }
    }

    @GET
    @Secured(permissions = {Pilares.Frota.Veiculo.VISUALIZAR, Pilares.Frota.Veiculo.CADASTRAR, Pilares.Frota.Veiculo.ALTERAR})
    @Path("/tipos/{codUnidade}/{codTipo}")
    public TipoVeiculo getTipoVeiculo(@PathParam("codTipo") Long codTipo, @PathParam("codUnidade") Long codUnidade) {
        return service.getTipoVeiculo(codTipo, codUnidade);
    }
}