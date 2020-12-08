package br.com.zalf.prolog.webservice.geral.unidade;

import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.network.SuccessResponse;
import br.com.zalf.prolog.webservice.geral.unidade._model.UnidadeEdicaoDto;
import br.com.zalf.prolog.webservice.geral.unidade._model.UnidadeEntity;
import br.com.zalf.prolog.webservice.geral.unidade._model.UnidadeVisualizacaoDto;
import br.com.zalf.prolog.webservice.interceptors.ApiExposed;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.interceptors.debug.ConsoleDebugLog;
import br.com.zalf.prolog.webservice.mappers.Mapper;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
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
@Path("/unidades")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Controller
public final class UnidadeResource implements UnidadeResourceApiDoc {
    @NotNull
    private final UnidadeService service;

    @NotNull
    private final Mapper<UnidadeEdicaoDto, UnidadeEntity> mapper;

    @Autowired
    public UnidadeResource(@NotNull final UnidadeService unidadeService,
                           @NotNull final Mapper<UnidadeEdicaoDto, UnidadeEntity> mapper) {
        this.service = unidadeService;
        this.mapper = mapper;
    }

    @ApiExposed
    @PUT
    @Path("/atualiza")
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
    public UnidadeVisualizacaoDto getUnidadeByCodigo(@PathParam("codUnidade") final Long codUnidade) {
        return service.getUnidadeByCodigo(codUnidade);
    }

    @ApiExposed
    @GET
    @Secured(permissions = {Pilares.Geral.Empresa.VISUALIZAR_ESTRUTURA, Pilares.Geral.Empresa.EDITAR_ESTRUTURA})
    @Override
    public List<UnidadeVisualizacaoDto> getUnidadesListagem(
            @QueryParam("codEmpresa") final Long codEmpresa,
            @QueryParam("codigosRegionais") final List<Long> codigosRegionais) {
        return service.getUnidadesListagem(codEmpresa, codigosRegionais);
    }

    /**
     * @deprecated em 09/11/2020. Deve ser utilizado o método {@link #updateUnidade}. Este método foi depreciado
     * para a criação de um método que contenha um retorno específico, com informações úteis acerca da atualização
     * ocorrida.
     */
    @Deprecated
    @ApiExposed
    @PUT
    @Secured(permissions = {Pilares.Geral.Empresa.EDITAR_ESTRUTURA})
    @Override
    public Response updateUnidadeOld(@Valid final UnidadeEdicaoDto unidadeEdicaoDto) {
        this.updateUnidade(unidadeEdicaoDto);
        return Response.ok("Unidade atualizada com sucesso.");
    }
}
