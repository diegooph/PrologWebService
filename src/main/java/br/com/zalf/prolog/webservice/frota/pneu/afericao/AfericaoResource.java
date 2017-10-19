package br.com.zalf.prolog.webservice.frota.pneu.afericao;

import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.Afericao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.CronogramaAfericao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.NovaAfericao;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.Restricao;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.interceptors.log.DebugLog;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created on 10/11/17
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Path("/afericoes")
@DebugLog
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class AfericaoResource {

    private final AfericaoService service = new AfericaoService();

    @POST
    @Secured(permissions = Pilares.Frota.Afericao.REALIZAR)
    @Path("/{codUnidade}")
    public Response insert(Afericao afericao,
                           @PathParam("codUnidade") Long codUnidade,
                           @HeaderParam("Authorization") String userToken) {
        if (service.insert(afericao, codUnidade, userToken)) {
            return Response.ok("Aferição inserida com sucesso");
        } else {
            return Response.error("Erro ao inserir aferição");
        }
    }

    @PUT
    @Secured
    public Response update(Afericao afericao) {
        if (service.updateKmAfericao(afericao)) {
            return Response.ok("Km atualizado com sucesso");
        } else {
            return Response.error("Erro ao atualizar o KM");
        }
    }

    @GET
    @Secured(permissions = Pilares.Frota.Afericao.REALIZAR)
    @Path("/cronogramas/{codUnidade}")
    public CronogramaAfericao getCronogramaAfericao(@PathParam("codUnidade") Long codUnidade,
                                                    @HeaderParam("Authorization") String userToken) {
        return service.getCronogramaAfericao(codUnidade, userToken);
    }

    @GET
    @Path("/{placaVeiculo}")
    @Secured(permissions = Pilares.Frota.Afericao.REALIZAR)
    public NovaAfericao getNovaAfericao(@PathParam("placaVeiculo") String placa,
                                        @HeaderParam("Authorization") String userToken) {
        return service.getNovaAfericao(placa, userToken);
    }

    @GET
    @Path("/{codUnidade}/{codTipoVeiculo}/{placaVeiculo}")
    @Secured(permissions = {
            Pilares.Frota.Afericao.VISUALIZAR,
            Pilares.Frota.Afericao.REALIZAR,
            Pilares.Frota.OrdemServico.Pneu.VISUALIZAR,
            Pilares.Frota.OrdemServico.Pneu.CONSERTAR_ITEM})
    public List<Afericao> getAfericoesByCodUnidadeByPlaca(
            @PathParam("codUnidade") Long codUnidade,
            @PathParam("codTipoVeiculo") String codTipoVeiculo,
            @PathParam("placaVeiculo") String placaVeiculo,
            @QueryParam("dataInicial") long dataInicial,
            @QueryParam("dataFinal") long dataFinal,
            @QueryParam("limit") long limit,
            @QueryParam("offset") long offset,
            @HeaderParam("Authorization") String userToken) {
        return service.getAfericoes(
                codUnidade,
                codTipoVeiculo,
                placaVeiculo,
                dataInicial,
                dataFinal,
                limit,
                offset,
                userToken);
    }

    @GET
    @Secured(permissions = {
            Pilares.Frota.Afericao.VISUALIZAR,
            Pilares.Frota.Afericao.REALIZAR,
            Pilares.Frota.OrdemServico.Pneu.VISUALIZAR,
            Pilares.Frota.OrdemServico.Pneu.CONSERTAR_ITEM})
    @Path("/{codUnidade}/{codAfericao}")
    public Afericao getByCod(@PathParam("codUnidade") Long codUnidade,
                             @PathParam("codAfericao") Long codAfericao,
                             @HeaderParam("Authorization") String userToken) {
        return service.getByCod(codUnidade, codAfericao, userToken);
    }

    @GET
    @Secured(permissions = {
            Pilares.Frota.Afericao.VISUALIZAR,
            Pilares.Frota.Afericao.REALIZAR,
            Pilares.Frota.OrdemServico.Pneu.VISUALIZAR,
            Pilares.Frota.OrdemServico.Pneu.CONSERTAR_ITEM})
    @Path("/restricoes/{codUnidade}")
    public Restricao getRestricaoByCodUnidade(@PathParam("codUnidade") Long codUnidade) {
        return service.getRestricaoByCodUnidade(codUnidade);
    }
}