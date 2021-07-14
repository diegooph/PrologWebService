package br.com.zalf.prolog.webservice.v3.fleet.servicopneu;

import br.com.zalf.prolog.webservice.commons.network.metadata.Optional;
import br.com.zalf.prolog.webservice.commons.network.metadata.Required;
import br.com.zalf.prolog.webservice.interceptors.ApiExposed;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.interceptors.debug.ConsoleDebugLog;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import br.com.zalf.prolog.webservice.v3.fleet.servicopneu._model.FiltroServicoListagemDto;
import br.com.zalf.prolog.webservice.v3.fleet.servicopneu._model.ServicoPneuEntity;
import br.com.zalf.prolog.webservice.v3.fleet.servicopneu._model.ServicoPneuListagemDto;
import br.com.zalf.prolog.webservice.v3.fleet.servicopneu._model.ServicoPneuStatus;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
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
    @NotNull
    private final ServicoPneuService service;
    @NotNull
    private final ServicoPneuListagemMapper mapper;

    @GET
    @ApiExposed
    @Secured(permissions = Pilares.Frota.OrdemServico.Pneu.VISUALIZAR)
    @Override
    public List<ServicoPneuListagemDto> getServicosByUnidadeAndStatus(
            @QueryParam("codUnidades") @Required final List<Long> codUnidades,
            @QueryParam("statusServicoPneu") @Optional final ServicoPneuStatus status,
            @QueryParam("codVeiculo") @Optional final Long codVeiculo,
            @QueryParam("codPneu") @Optional final Long codPneu,
            @QueryParam("limit") final int limit,
            @QueryParam("offset") final int offset) {
        final FiltroServicoListagemDto filtro =
                FiltroServicoListagemDto.of(codUnidades, codVeiculo, codPneu, status, limit, offset);
        final List<ServicoPneuEntity> servicosPneu = this.service.findServicosPneuByFilter(filtro);
        return this.mapper.toDto(servicosPneu);
    }
}
