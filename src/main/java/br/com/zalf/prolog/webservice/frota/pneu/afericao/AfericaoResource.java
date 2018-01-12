package br.com.zalf.prolog.webservice.frota.pneu.afericao;

import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.util.Required;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.errorhandling.exception.TipoAfericaoNotSupported;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.Afericao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.CronogramaAfericao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.NovaAfericao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.TipoAfericao;
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
    @Path("/nova/{placaVeiculo}")
    @Secured(permissions = Pilares.Frota.Afericao.REALIZAR)
    public NovaAfericao getNovaAfericao(@PathParam("placaVeiculo") @Required String placa,
                                        @QueryParam("tipoAfericao") @Required String tipoAfericao,
                                        @HeaderParam("Authorization") @Required String userToken) throws Exception {
        return service.getNovaAfericao(placa, tipoAfericao, userToken);
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
            @QueryParam("limit") int limit,
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

    /**
     * @deprecated use {@link #getNovaAfericao(String, String, String)} instead.
     */
    @GET
    @Path("/{placaVeiculo}")
    @Secured(permissions = Pilares.Frota.Afericao.REALIZAR)
    @Deprecated
    public NovaAfericao DEPRECATED_GET_NOVA_AFERICAO(@PathParam("placaVeiculo") @Required String placa,
                                                     @HeaderParam("Authorization") @Required String userToken)
            throws Exception {
        // Mapeia fixo como se sempre estivesse iniciando uma aferição de SULCO_PRESSAO para não quebrar os
        // apps rodando com esse método. Não há problema em fazer isso atualmente pois essa informação de tipo
        // não é utilizada pelo ProLog. Apenas na integração com a Avilan para barrar certos tipos de aferição.
        return service.getNovaAfericao(placa, TipoAfericao.SULCO_PRESSAO.asString(), userToken);
    }
}