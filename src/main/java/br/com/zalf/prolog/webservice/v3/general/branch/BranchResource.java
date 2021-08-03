package br.com.zalf.prolog.webservice.v3.general.branch;

import br.com.zalf.prolog.webservice.commons.network.SuccessResponse;
import br.com.zalf.prolog.webservice.interceptors.ApiExposed;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.interceptors.debug.ConsoleDebugLog;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import br.com.zalf.prolog.webservice.v3.general.branch._model.BranchDto;
import br.com.zalf.prolog.webservice.v3.general.branch._model.BranchEntity;
import br.com.zalf.prolog.webservice.v3.general.branch._model.BranchMapper;
import br.com.zalf.prolog.webservice.v3.general.branch._model.BranchUpdateDto;
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
@Path(BranchResource.RESOURCE_PATH)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Controller
public final class BranchResource implements BranchResourceApiDoc {
    @NotNull
    public static final String RESOURCE_PATH = "/api/v3/unidades";
    @NotNull
    private final BranchService service;
    @NotNull
    private final BranchMapper mapper;

    @Autowired
    public BranchResource(@NotNull final BranchService branchService, @NotNull final BranchMapper mapper) {
        this.service = branchService;
        this.mapper = mapper;
    }

    @ApiExposed
    @PUT
    @Secured(permissions = {Pilares.Geral.Empresa.EDITAR_ESTRUTURA})
    @Override
    public SuccessResponse updateBranch(@Valid final BranchUpdateDto branchUpdateDto) {
        final BranchEntity branch = mapper.toEntity(branchUpdateDto);
        return service.updateBranch(branch);
    }

    @ApiExposed
    @GET
    @Secured(permissions = {Pilares.Geral.Empresa.VISUALIZAR_ESTRUTURA, Pilares.Geral.Empresa.EDITAR_ESTRUTURA})
    @Path("/{codUnidade}")
    @Override
    public BranchDto getBranchById(@PathParam("codUnidade") final Long branchId) {
        return mapper.toDto(service.getBranchById(branchId));
    }

    @ApiExposed
    @GET
    @Secured(permissions = {Pilares.Geral.Empresa.VISUALIZAR_ESTRUTURA, Pilares.Geral.Empresa.EDITAR_ESTRUTURA})
    @Override
    public List<BranchDto> getAllBranches(@QueryParam("codEmpresa") final Long companyId,
                                          @QueryParam("codGrupos") final List<Long> groupsId) {
        return mapper.toDto(service.getAllBranches(companyId, groupsId));
    }
}
