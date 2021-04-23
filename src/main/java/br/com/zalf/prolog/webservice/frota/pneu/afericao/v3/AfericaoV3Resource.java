package br.com.zalf.prolog.webservice.frota.pneu.afericao.v3;

import br.com.zalf.prolog.webservice.commons.network.metadata.Platform;
import br.com.zalf.prolog.webservice.commons.network.metadata.UsedBy;
import br.com.zalf.prolog.webservice.commons.util.datetime.DateUtils;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.v3._model.dto.AfericaoAvulsaDto;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.v3._model.dto.AfericaoPlacaDto;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.v3._model.dto.busca.DadosGeraisFiltro;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.v3._model.dto.busca.FiltroAfericaoAvulsa;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.v3._model.dto.busca.FiltroAfericaoPlaca;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.v3._model.projections.AfericaoAvulsaProjection;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.v3._model.projections.AfericaoPlacaProjection;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.v3.mapper.AfericaoAvulsaMapper;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.v3.mapper.AfericaoPlacaMapper;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.v3.service.AfericaoV3Service;
import br.com.zalf.prolog.webservice.interceptors.ApiExposed;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.interceptors.debug.ConsoleDebugLog;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
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
@Path("/api/v3/afericoes")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Controller
public class AfericaoV3Resource implements AfericaoV3ResourceApiDoc {

    @NotNull
    private final AfericaoV3Service service;
    @NotNull
    private final AfericaoPlacaMapper afericaoPlacaMapper;
    @NotNull
    private final AfericaoAvulsaMapper afericaoAvulsaMapper;

    @Autowired
    AfericaoV3Resource(@NotNull final AfericaoV3Service service,
                       @NotNull final AfericaoPlacaMapper afericaoPlacaMapper,
                       @NotNull final AfericaoAvulsaMapper afericaoAvulsaMapper) {
        this.service = service;
        this.afericaoPlacaMapper = afericaoPlacaMapper;
        this.afericaoAvulsaMapper = afericaoAvulsaMapper;
    }

    @ApiExposed
    @GET
    @Path("/placas")
    @Secured(permissions = {
            Pilares.Frota.Afericao.VISUALIZAR_TODAS_AFERICOES,
            Pilares.Frota.Afericao.REALIZAR_AFERICAO_PLACA,
            Pilares.Frota.OrdemServico.Pneu.VISUALIZAR,
            Pilares.Frota.OrdemServico.Pneu.CONSERTAR_ITEM})
    @UsedBy(platforms = {Platform.ANDROID, Platform.WEBSITE})
    @Override
    public List<AfericaoPlacaDto> getAfericoesPlacas(@QueryParam("unidades") final @NotNull List<Long> codUnidades,
                                                     @QueryParam("placa") final @Nullable String placaVeiculo,
                                                     @QueryParam("codTipoVeiculo") final @Nullable Long codTipoVeiculo,
                                                     @QueryParam("dataInicial") final @NotNull String dataInicial,
                                                     @QueryParam("dataFinal") final @NotNull String dataFinal,
                                                     @QueryParam("limit") final int limit,
                                                     @QueryParam("offset") final int offset) {
        final DadosGeraisFiltro dadosGeraisFiltro = generateDadosGerais(codUnidades,
                                                                        dataInicial,
                                                                        dataFinal,
                                                                        limit,
                                                                        offset);
        final FiltroAfericaoPlaca filtro = FiltroAfericaoPlaca.of(codTipoVeiculo,
                                                                  placaVeiculo,
                                                                  dadosGeraisFiltro);
        final List<AfericaoPlacaProjection> projections = this.service.getAfericoesPlacas(filtro);
        return this.afericaoPlacaMapper.toDtos(projections);
    }

    @ApiExposed
    @GET
    @Path("/avulsas")
    @Secured(permissions = {
            Pilares.Frota.Afericao.VISUALIZAR_TODAS_AFERICOES,
            Pilares.Frota.Afericao.REALIZAR_AFERICAO_PNEU_AVULSO,
            Pilares.Frota.OrdemServico.Pneu.VISUALIZAR,
            Pilares.Frota.OrdemServico.Pneu.CONSERTAR_ITEM})
    @UsedBy(platforms = {Platform.ANDROID, Platform.WEBSITE})
    @Override
    public List<AfericaoAvulsaDto> getAfericoesAvulsas(@QueryParam("unidades") final @NotNull List<Long> codUnidades,
                                                       @QueryParam("dataInicial") final @NotNull String dataInicial,
                                                       @QueryParam("dataFinal") final @NotNull String dataFinal,
                                                       @QueryParam("limit") final int limit,
                                                       @QueryParam("offset") final int offset) {
        final DadosGeraisFiltro dadosGeraisFiltro = generateDadosGerais(codUnidades,
                                                                        dataInicial,
                                                                        dataFinal,
                                                                        limit,
                                                                        offset);
        final FiltroAfericaoAvulsa filtro = FiltroAfericaoAvulsa.of(dadosGeraisFiltro);
        final List<AfericaoAvulsaProjection> projections = this.service.getAfericoesAvulsas(filtro);
        return this.afericaoAvulsaMapper.toDtos(projections);
    }

    @NotNull
    private DadosGeraisFiltro generateDadosGerais(@NotNull final List<Long> codUnidades,
                                                  @NotNull final String dataInicial,
                                                  @NotNull final String dataFinal,
                                                  final int limit,
                                                  final int offset) {
        return DadosGeraisFiltro.of(codUnidades,
                                    DateUtils.parseDate(dataInicial),
                                    DateUtils.parseDate(dataFinal),
                                    limit,
                                    offset);
    }

}
