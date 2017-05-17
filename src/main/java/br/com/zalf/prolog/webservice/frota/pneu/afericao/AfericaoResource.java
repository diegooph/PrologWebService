package br.com.zalf.prolog.webservice.frota.pneu.afericao;

import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.Afericao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.NovaAfericao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.SelecaoPlacaAfericao;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.Restricao;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Date;
import java.util.List;

@Path("/afericao")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class AfericaoResource {

    private AfericaoService service = new AfericaoService();

    @POST
    @Secured(permissions = Pilares.Frota.Afericao.REALIZAR)
    @Path("/{codUnidade}")
    public Response insert(Afericao afericao,
                           @PathParam("codUnidade") Long codUnidade) {
        afericao.setDataHora(new Date(System.currentTimeMillis()));
        if (service.Insert(afericao, codUnidade)) {
            return Response.Ok("Aferição inserida com sucesso");
        } else {
            return Response.Error("Erro ao inserir aferição");
        }
    }

    @PUT
    @Secured
    public Response update(Afericao afericao) {
        if (service.updateKmAfericao(afericao)) {
            return Response.Ok("Km atualizado com sucesso");
        } else {
            return Response.Error("Erro ao atualizar o KM");
        }
    }

    @GET
    @Secured(permissions = Pilares.Frota.Afericao.REALIZAR)
    @Path("/listaAfericao/{codUnidade}")
    public SelecaoPlacaAfericao getSelecaoPlacasAfericao(
            @PathParam("codUnidade") Long codUnidade) {
        return service.getSelecaoPlacaAfericao(codUnidade);
    }

    @GET
    @Path("/{placaVeiculo}")
    @Secured(permissions = Pilares.Frota.Afericao.REALIZAR)
    public NovaAfericao getNovaAfericao(@PathParam("placaVeiculo") String placa) {
        return service.getNovaAfericao(placa);
    }

    @GET
    @Secured(permissions = {Pilares.Frota.Afericao.VISUALIZAR, Pilares.Frota.Afericao.REALIZAR,
            Pilares.Frota.OrdemServico.Pneu.VISUALIZAR, Pilares.Frota.OrdemServico.Pneu.CONSERTAR_ITEM})
    @Path("/getAll")
    public List<Afericao> getAfericoesByCodUnidadeByPlaca(
            @QueryParam("codUnidades") List<String> codUnidades,
            @QueryParam("placas") List<String> placas,
            @QueryParam("limit") long limit,
            @QueryParam("offset") long offset) {
        return service.getAfericoesByCodUnidadeByPlaca(codUnidades, placas, limit, offset);
    }

    @GET
    @Secured(permissions = {Pilares.Frota.Afericao.VISUALIZAR, Pilares.Frota.Afericao.REALIZAR})
    @Path("/{codUnidade}/{codAfericao}")
    public Afericao getByCod(@PathParam("codAfericao") Long codAfericao, @PathParam("codUnidade") Long codUnidade) {
        return service.getByCod(codAfericao, codUnidade);
    }

    @GET
    @Secured(permissions = {Pilares.Frota.Afericao.VISUALIZAR, Pilares.Frota.Afericao.REALIZAR})
    @Path("/restricoes/{codUnidade}")
    public Restricao getRestricoesByCodUnidade(@PathParam("codUnidade") Long codUnidade) {
        return service.getRestricoesByCodUnidade(codUnidade);
    }
}