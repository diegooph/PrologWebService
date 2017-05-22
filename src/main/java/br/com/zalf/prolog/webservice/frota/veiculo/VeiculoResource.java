package br.com.zalf.prolog.webservice.frota.veiculo;

import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.util.Android;
import br.com.zalf.prolog.webservice.commons.util.Site;
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
            return Response.Ok("Veículo inserido com sucesso");
        } else {
            return Response.Error("Erro ao inserir o veículo");
        }
    }

    @PUT
    @Secured(permissions = {Pilares.Frota.Veiculo.ALTERAR, Pilares.Frota.Veiculo.CADASTRAR})
    @Path("/{placaOriginal}")
    public Response update(Veiculo veiculo, @PathParam("placaOriginal") String placaOriginal) {
        if (service.update(veiculo, placaOriginal)) {
            return Response.Ok("Veículo atualizado com sucesso");
        } else {
            return Response.Error("Erro ao atualizar o veículo");
        }
    }

    @DELETE
    @Secured(permissions = {Pilares.Frota.Veiculo.ALTERAR, Pilares.Frota.Veiculo.CADASTRAR})
    @Path("/{placa}")
    public Response delete(@PathParam("placa") String placa) {
        if (service.delete(placa)) {
            return Response.Ok("Veículo deletado com sucesso.");
        } else {
            return Response.Error("Erro ao deletar o veículo.");
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
    public List<Veiculo> getVeiculosAtivosByUnidade(@PathParam("codUnidade") Long codUnidade) {
        return service.getVeiculosAtivosByUnidade(codUnidade);
    }

    @POST
    @Secured(permissions = {Pilares.Frota.Veiculo.CADASTRAR, Pilares.Frota.Veiculo.ALTERAR})
    @Path("/{codUnidade}/tipo")
    public Response insertTipoVeiculo(TipoVeiculo tipoVeiculo, @PathParam("codUnidade") Long codUnidade) {
        if (service.insertTipoVeiculo(tipoVeiculo, codUnidade)) {
            return Response.Ok("Tipo de veículo inserido com sucesso");
        } else {
            return Response.Error("Erro ao inserir o tipo de veículo");
        }
    }

    @Android
    @GET
    @Secured(permissions = {Pilares.Frota.Veiculo.VISUALIZAR, Pilares.Frota.Veiculo.ALTERAR,
            Pilares.Frota.Veiculo.CADASTRAR})
    @Path("/byTipo/{codUnidade}/{codTipo}")
    public List<String> getVeiculosByTipo(@PathParam("codUnidade") Long codUnidade,
                                          @PathParam("codTipo") String codTipo) {
        return service.getVeiculosByTipo(codUnidade, codTipo);
    }

    @GET
    @Android
    @Site
    @Secured
    @Path("/{codUnidade}/tipo")
    public List<TipoVeiculo> getTipoVeiculosByUnidade(@PathParam("codUnidade") Long codUnidade) {
        return service.getTipoVeiculosByUnidade(codUnidade);
    }

    @POST
    @Secured(permissions = {Pilares.Frota.Veiculo.CADASTRAR, Pilares.Frota.Veiculo.ALTERAR})
    @Path("/modelo/{codEmpresa}/{codMarca}")
    public Response insertModeloVeiculo(Modelo modelo, @PathParam("codEmpresa") long codEmpresa, @PathParam("codMarca") long codMarca) {
        if (service.insertModeloVeiculo(modelo, codEmpresa, codMarca)) {
            return Response.Ok("Modelo inserido com sucesso");
        } else {
            return Response.Error("Erro ao inserir o modelo");
        }
    }

    @GET
    @Secured(permissions = {Pilares.Frota.Veiculo.CADASTRAR, Pilares.Frota.Veiculo.ALTERAR})
    @Path("/marcaModelos/{codEmpresa}")
    public List<Marca> getMarcaModeloVeiculoByCodEmpresa(@PathParam("codEmpresa") Long codEmpresa) {
        return service.getMarcaModeloVeiculoByCodEmpresa(codEmpresa);
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
    public Veiculo getVeiculoByPlacaComPneus(@PathParam("placa") String placa) {
        return service.getVeiculoByPlaca(placa, true);
    }

    @GET
    @Secured(permissions = {Pilares.Frota.Veiculo.VISUALIZAR, Pilares.Frota.Veiculo.CADASTRAR, Pilares.Frota.Veiculo.ALTERAR})
    @Path("/sem-pneus/{placa}")
    public Veiculo getVeiculoByPlacaSemPneus(@PathParam("placa") String placa) {
        return service.getVeiculoByPlaca(placa, false);
    }
}