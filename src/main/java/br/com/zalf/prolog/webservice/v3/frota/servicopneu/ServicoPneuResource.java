package br.com.zalf.prolog.webservice.v3.frota.servicopneu;

import br.com.zalf.prolog.webservice.interceptors.ApiExposed;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.interceptors.debug.ConsoleDebugLog;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import br.com.zalf.prolog.webservice.v3.frota.servicopneu._model.FiltroServicoListagemDto;
import br.com.zalf.prolog.webservice.v3.frota.servicopneu._model.ServicoPneuEntity;
import br.com.zalf.prolog.webservice.v3.frota.servicopneu._model.ServicoPneuListagemDto;
import br.com.zalf.prolog.webservice.v3.frota.servicopneu._model.ServicoPneuStatus;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created on 2021-05-21
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@ConsoleDebugLog
@Path("/api/v3/servicos-pneu")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RestController
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class ServicoPneuResource implements ServicoPneuApiDoc {
    private final ServicoPneuService service;
    private final ServicoPneuListagemMapper mapper;

    @GET
    @ApiExposed
    @Secured(permissions = Pilares.Frota.OrdemServico.Pneu.VISUALIZAR)
    @Override
    @NotNull
    public List<ServicoPneuListagemDto> getServicosByUnidadeAndStatus(@QueryParam("codUnidades")
                                                                      @NotNull final List<Long> codUnidades,
                                                                      @QueryParam("statusServicoPneu")
                                                                          @Nullable final ServicoPneuStatus status,
                                                                      @QueryParam("codVeiculo")
                                                                      @Nullable final Long codVeiculo,
                                                                      @QueryParam("codPneu")
                                                                      @Nullable final Long codPneu,
                                                                      @QueryParam("limit") final int limit,
                                                                      @QueryParam("offset") final int offset) {

        final FiltroServicoListagemDto filtro = FiltroServicoListagemDto.of(codUnidades,
                                                                            codVeiculo,
                                                                            codPneu,
                                                                            status,
                                                                            limit,
                                                                            offset);
        final List<ServicoPneuEntity> servicosPneu = this.service.findServicosPneuByFilter(filtro);
        return this.mapper.toDto(servicosPneu);
    }
}
