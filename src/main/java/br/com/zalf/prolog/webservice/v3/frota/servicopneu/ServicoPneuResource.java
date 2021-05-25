package br.com.zalf.prolog.webservice.v3.frota.servicopneu;

import br.com.zalf.prolog.webservice.interceptors.debug.ConsoleDebugLog;
import br.com.zalf.prolog.webservice.v3.frota.servicopneu._model.FiltroServicoListagemDto;
import br.com.zalf.prolog.webservice.v3.frota.servicopneu._model.ServicoPneuEntity;
import br.com.zalf.prolog.webservice.v3.frota.servicopneu._model.ServicoPneuListagemDto;
import br.com.zalf.prolog.webservice.v3.frota.servicopneu._model.ServicoPneuStatus;
import lombok.AllArgsConstructor;
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
    @Override
    public List<ServicoPneuListagemDto> getServicosByUnidadeAndStatus(@QueryParam("codUnidades") final List<Long> codUnidades,
                                                                      @QueryParam("statusServicoPneu")
                                                                      @DefaultValue("DEFAULT") final ServicoPneuStatus status,
                                                                      @QueryParam("codVeiculo") final Long codVeiculo,
                                                                      @QueryParam("codPneu") final Long codPneu,
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
