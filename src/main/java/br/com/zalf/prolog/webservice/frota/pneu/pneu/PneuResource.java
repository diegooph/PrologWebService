package br.com.zalf.prolog.webservice.frota.pneu.pneu;

import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.ModeloBanda;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.Pneu;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Marca;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Modelo;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/pneus")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class PneuResource {

    private PneuService service = new PneuService();

    @POST
    @Secured(permissions = Pilares.Frota.Pneu.CADASTRAR)
    @Path("/{codUnidade}")
    public Response insert(Pneu pneu, @PathParam("codUnidade") Long codUnidade) {
        if (service.insert(pneu, codUnidade)) {
            return Response.Ok("Pneu inserido com sucesso.");
        } else {
            return Response.Error("Erro ao inserir o pneu");
        }
    }

    @PUT
//	@Secured(permissions = { Pilares.Frota.Pneu.CADASTRAR, Pilares.Frota.Pneu.ALTERAR })
    @Path("/{codUnidade}/{codPneuOriginal}")
    public Response update(Pneu pneu, @PathParam("codUnidade") Long codUnidade, @PathParam("codPneuOriginal") Long codOriginal) {
        if (service.update(pneu, codUnidade, codOriginal)) {
            return Response.Ok("Pneu atualizado com sucesso.");
        } else {
            return Response.Error("Erro ao atualizar o pneu.");
        }
    }

    @POST
    @Secured(permissions = {Pilares.Frota.Pneu.CADASTRAR, Pilares.Frota.Pneu.ALTERAR})
    @Path("/modelo/{codEmpresa}/{codMarca}")
    public Response insertModeloPneu(Modelo modelo, @PathParam("codEmpresa") long codEmpresa, @PathParam("codMarca") long codMarca) {
        if (service.insertModeloPneu(modelo, codEmpresa, codMarca)) {
            return Response.Ok("Modelo de pneu inserido com sucesso.");
        } else {
            return Response.Error("Erro ao inserir o modelo de pneu.");
        }
    }

    @GET
    @Secured(permissions = {Pilares.Frota.Pneu.VISUALIZAR, Pilares.Frota.Pneu.CADASTRAR, Pilares.Frota.Pneu.ALTERAR,
            Pilares.Frota.OrdemServico.Pneu.CONSERTAR_ITEM})
    @Path("/{codUnidade}/{status}")
    public List<Pneu> getPneuByCodUnidadeByStatus(@PathParam("codUnidade") Long codUnidade, @PathParam("status") String status) {
        return service.getPneuByCodUnidadeByStatus(codUnidade, status);
    }

    @GET
    @Secured(permissions = {Pilares.Frota.Pneu.CADASTRAR, Pilares.Frota.Pneu.ALTERAR})
    @Path("/marcaModelos/{codEmpresa}")
    public List<Marca> getMarcaModeloPneuByCodEmpresa(@PathParam("codEmpresa") Long codEmpresa) {
        return service.getMarcaModeloPneuByCodEmpresa(codEmpresa);
    }

    @GET
    @Secured(permissions = {Pilares.Frota.Pneu.CADASTRAR, Pilares.Frota.Pneu.ALTERAR})
    @Path("/modelos/{codModelo}")
    public Modelo getModeloPneu(@PathParam("codModelo") Long codModelo) {
        return service.getModeloPneu(codModelo);
    }

    @GET
    @Secured(permissions = {Pilares.Frota.Pneu.CADASTRAR, Pilares.Frota.Pneu.ALTERAR})
    @Path("/dimensao")
    public List<Pneu.Dimensao> getDimensoes() {
        return service.getDimensoes();
    }

    @POST
    @Secured(permissions = Pilares.Frota.Pneu.VINCULAR_VEICULO)
    @Path("/vincular/{placa}")
    public Response vinculaPneuVeiculo(@PathParam("placa") String placa, List<Pneu> pneus) {
        if (service.vinculaPneuVeiculo(placa, pneus)) {
            return Response.Ok("Pneus vinculados com sucesso.");
        } else {
            return Response.Error("Erro ao víncular os pneus ao veículo");
        }
    }

    @GET
    @Secured(permissions = {Pilares.Frota.Pneu.MOVIMENTAR,
            Pilares.Frota.Pneu.CADASTRAR,
            Pilares.Frota.Pneu.ALTERAR})
    @Path("/bandas/{codEmpresa}")
    public List<Marca> getMarcaModeloBanda(@PathParam("codEmpresa") Long codEmpresa) {
        return service.getMarcaModeloBanda(codEmpresa);
    }

    @POST
    @Secured(permissions = {Pilares.Frota.Pneu.MOVIMENTAR,
            Pilares.Frota.Pneu.CADASTRAR,
            Pilares.Frota.Pneu.ALTERAR})
    @Path("/bandas/marcas/{codEmpresa}")
    public AbstractResponse insertMarcaBanda(Marca marca, @PathParam("codEmpresa") Long codEmpresa) {
        return service.insertMarcaBanda(marca, codEmpresa);
    }

    @POST
    @Secured(permissions = {Pilares.Frota.Pneu.MOVIMENTAR,
            Pilares.Frota.Pneu.CADASTRAR,
            Pilares.Frota.Pneu.ALTERAR})
    @Path("/bandas/modelos/{codEmpresa}/{codMarcaBanda}")
    public AbstractResponse insertModeloBanda(ModeloBanda modelo, @PathParam("codMarcaBanda") Long codMarcaBanda,
                                              @PathParam("codEmpresa") Long codEmpresa) {
        return service.insertModeloBanda(modelo, codMarcaBanda, codEmpresa);
    }

    @PUT
    @Secured
    @Path("bandas/marcas/{codEmpresa}")
    public Response updateMarcaBanda(Marca marca, @PathParam("codEmpresa") Long codEmpresa) {
        if(service.updateMarcaBanda(marca, codEmpresa)){
            return Response.Ok("Marca atualizada com sucesso");
        }else {
            return Response.Error("Erro ao atualizar a marca");
        }
    }

    @PUT
    @Secured
    @Path("bandas/modelos")
    public Response updateModeloBanda(Modelo modelo) {
        if (service.updateModeloBanda(modelo)) {
            return Response.Ok("Modelo de banda atualizado com sucesso");
        }else {
            return Response.Error("Erro ao atualizar o modelo de banda");
        }
    }

    @GET
    @Secured
    @Path("/unidades/{codUnidade}/{codPneu}")
    public Pneu getPneuByCod(@PathParam("codPneu") String codPneu, @PathParam("codUnidade") Long codUnidade) {
        return service.getPneuByCod(codPneu, codUnidade);
    }
}



