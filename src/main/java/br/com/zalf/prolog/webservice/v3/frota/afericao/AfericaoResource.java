package br.com.zalf.prolog.webservice.v3.frota.afericao;

import br.com.zalf.prolog.webservice.commons.util.datetime.DateUtils;
import br.com.zalf.prolog.webservice.interceptors.ApiExposed;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.interceptors.debug.ConsoleDebugLog;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import br.com.zalf.prolog.webservice.v3.frota.afericao._model.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created on 2021-02-10
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@ConsoleDebugLog
@Path("/v3/afericoes")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Controller
public class AfericaoResource implements AfericaoResourceApiDoc {
    @NotNull
    private final AfericaoService service;
    @NotNull
    private final AfericaoMapper afericaoMapper;

    @Autowired
    AfericaoResource(@NotNull final AfericaoService service,
                     @NotNull final AfericaoMapper afericaoMapper) {
        this.service = service;
        this.afericaoMapper = afericaoMapper;
    }

    @ApiExposed
    @GET
    @Path("/placas")
    @Secured(permissions = {
            Pilares.Frota.Afericao.VISUALIZAR_TODAS_AFERICOES,
            Pilares.Frota.Afericao.REALIZAR_AFERICAO_PLACA,
            Pilares.Frota.OrdemServico.Pneu.VISUALIZAR,
            Pilares.Frota.OrdemServico.Pneu.CONSERTAR_ITEM})
    @Override
    public List<AfericaoPlacaDto> getAfericoesPlacas(@QueryParam("codUnidades") @NotNull final List<Long> codUnidades,
                                                     @QueryParam("placa") @Nullable final String placaVeiculo,
                                                     @QueryParam("codTipoVeiculo") @Nullable final Long codTipoVeiculo,
                                                     @QueryParam("dataInicial") @NotNull final String dataInicial,
                                                     @QueryParam("dataFinal") @NotNull final String dataFinal,
                                                     @QueryParam("limit") final int limit,
                                                     @QueryParam("offset") final int offset,
                                                     @QueryParam("incluirMedidas") final boolean incluirMedidas) {
        final FiltroAfericaoPlaca filtro = FiltroAfericaoPlaca.of(codUnidades,
                                                                  placaVeiculo,
                                                                  codTipoVeiculo,
                                                                  DateUtils.parseDate(dataInicial),
                                                                  DateUtils.parseDate(dataFinal),
                                                                  limit,
                                                                  offset,
                                                                  incluirMedidas);
        final List<AfericaoPlacaProjection> afericoesPlacas = service.getAfericoesPlacas(filtro);
        return afericaoMapper.toAfericaoPlacaDto(afericoesPlacas);
    }

    @ApiExposed
    @GET
    @Path("/avulsas")
    @Secured(permissions = {
            Pilares.Frota.Afericao.VISUALIZAR_TODAS_AFERICOES,
            Pilares.Frota.Afericao.REALIZAR_AFERICAO_PNEU_AVULSO,
            Pilares.Frota.OrdemServico.Pneu.VISUALIZAR,
            Pilares.Frota.OrdemServico.Pneu.CONSERTAR_ITEM})
    @Override
    public List<AfericaoAvulsaDto> getAfericoesAvulsas(@QueryParam("codUnidades") @NotNull final List<Long> codUnidades,
                                                       @QueryParam("dataInicial") @NotNull final String dataInicial,
                                                       @QueryParam("dataFinal") @NotNull final String dataFinal,
                                                       @QueryParam("limit") final int limit,
                                                       @QueryParam("offset") final int offset,
                                                       @QueryParam("incluirMedidas") final boolean incluirMedidas) {
        final FiltroAfericaoAvulsa filtro = FiltroAfericaoAvulsa.of(codUnidades,
                                                                    DateUtils.parseDate(dataInicial),
                                                                    DateUtils.parseDate(dataFinal),
                                                                    limit,
                                                                    offset,
                                                                    incluirMedidas);
        final List<AfericaoAvulsaProjection> afericoesAvulsas = service.getAfericoesAvulsas(filtro);
        return afericaoMapper.toAfericaoAvulsaDto(afericoesAvulsas);
    }
}
