package br.com.zalf.prolog.webservice.v3.geral.unidade;

import br.com.zalf.prolog.webservice.commons.network.SuccessResponse;
import br.com.zalf.prolog.webservice.interceptors.ApiExposed;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.interceptors.debug.ConsoleDebugLog;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import br.com.zalf.prolog.webservice.v3.geral.unidade._model.UnidadeEdicaoDto;
import br.com.zalf.prolog.webservice.v3.geral.unidade._model.UnidadeEntity;
import br.com.zalf.prolog.webservice.v3.geral.unidade._model.UnidadeMapper;
import br.com.zalf.prolog.webservice.v3.geral.unidade._model.UnidadeVisualizacaoListagemDto;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created on 2020-03-12
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
@ConsoleDebugLog
@Path("/api/v3/unidades")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Controller
public final class UnidadeResource implements UnidadeResourceApiDoc {
    @NotNull
    private final UnidadeService service;
    @NotNull
    private final UnidadeMapper mapper;

    @Autowired
    public UnidadeResource(@NotNull final UnidadeService unidadeService,
                           @NotNull final UnidadeMapper mapper) {
        this.service = unidadeService;
        this.mapper = mapper;
    }

    @ApiExposed
    @PUT
    @Secured(permissions = {Pilares.Geral.Empresa.EDITAR_ESTRUTURA})
    @Override
    public SuccessResponse updateUnidade(@Valid final UnidadeEdicaoDto unidadeEdicaoDto) {
        final UnidadeEntity unidade = mapper.toEntity(unidadeEdicaoDto);
        return service.updateUnidade(unidade);
    }

    @ApiExposed
    @GET
    @Secured(permissions = {Pilares.Geral.Empresa.VISUALIZAR_ESTRUTURA, Pilares.Geral.Empresa.EDITAR_ESTRUTURA})
    @Path("/{codUnidade}")
    @Override
    public UnidadeVisualizacaoListagemDto getUnidadeByCodigo(@PathParam("codUnidade") final Long codUnidade) {
        return mapper.toDto(service.getByCod(codUnidade));
    }

    @ApiExposed
    @GET
    @Secured(permissions = {Pilares.Geral.Empresa.VISUALIZAR_ESTRUTURA, Pilares.Geral.Empresa.EDITAR_ESTRUTURA})
    @Override
    public List<UnidadeVisualizacaoListagemDto> getUnidadesListagem(
            @QueryParam("codEmpresa") final Long codEmpresa,
            @QueryParam("codGrupos") final List<Long> codGrupos) {
        return mapper.toDto(service.getUnidadesListagem(codEmpresa, codGrupos));
    }
}
