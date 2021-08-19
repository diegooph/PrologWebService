package br.com.zalf.prolog.webservice.v3.fleet.tire.tiresize;

import br.com.zalf.prolog.webservice.commons.network.PrologCustomHeaders;
import br.com.zalf.prolog.webservice.commons.network.SuccessResponse;
import br.com.zalf.prolog.webservice.commons.network.metadata.Required;
import br.com.zalf.prolog.webservice.interceptors.auth.ColaboradorAutenticado;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.interceptors.auth.authorization.AuthType;
import br.com.zalf.prolog.webservice.interceptors.debug.ConsoleDebugLog;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import br.com.zalf.prolog.webservice.v3.fleet.tire.tiresize.model.TireSizeEntity;
import br.com.zalf.prolog.webservice.v3.fleet.tire.tiresize.model.TireSizeCreateDto;
import br.com.zalf.prolog.webservice.v3.fleet.tire.tiresize.model.TireSizeDto;
import br.com.zalf.prolog.webservice.v3.fleet.tire.tiresize.model.TireSizeStatusChangeDto;
import br.com.zalf.prolog.webservice.v3.fleet.tire.tiresize.model.TireSizeUpdateDto;
import br.com.zalf.prolog.webservice.v3.validation.CompanyId;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import java.util.List;
import java.util.Optional;

@RestController
@ConsoleDebugLog
@Path(TireSizeResource.RESOURCE_PATH)
public class TireSizeResource {
    @NotNull
    public static final String RESOURCE_PATH = "/api/v3/tires/sizes";
    @NotNull
    private final TireSizeService service;
    @NotNull
    private final TireSizeMapper mapper;

    @Autowired
    public TireSizeResource(@NotNull final TireSizeService service, @NotNull final TireSizeMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @POST
    @Secured(authTypes = {AuthType.BEARER, AuthType.API}, permissions = Pilares.Frota.Pneu.CADASTRAR)
    public SuccessResponse insert(
            @HeaderParam(PrologCustomHeaders.AppVersionAndroid.PROLOG_APP_VERSION) @Required final Integer appVersion,
            @Context final SecurityContext securityContext,
            @Valid final TireSizeCreateDto tireSizeCreateDto) throws Throwable {
        final Optional<ColaboradorAutenticado> userPrincipal =
                Optional.ofNullable((ColaboradorAutenticado) securityContext.getUserPrincipal());
        final TireSizeEntity insertedTireSize = service.insert(tireSizeCreateDto, userPrincipal, appVersion);
        return new SuccessResponse(insertedTireSize.getId(), "Registro inserido com sucesso!");
    }

    @GET
    @Secured(authTypes = {AuthType.BEARER, AuthType.API},
            permissions = {Pilares.Frota.Pneu.CADASTRAR, Pilares.Frota.Pneu.ALTERAR, Pilares.Frota.Pneu.VISUALIZAR})
    public List<TireSizeDto> getAll(@NotNull @CompanyId @QueryParam("companyId") @Required final Long companyId,
                                    @QueryParam("statusActive") final Boolean statusActive) {
        final List<TireSizeEntity> tireSizeEntities = service.getAll(companyId, statusActive);
        return mapper.toTireSizeDto(tireSizeEntities);
    }

    @GET
    @Path("{tireSizeId}")
    @Secured(authTypes = {AuthType.BEARER, AuthType.API},
            permissions = {Pilares.Frota.Pneu.VISUALIZAR, Pilares.Frota.Pneu.ALTERAR})
    public TireSizeDto getById(@PathParam("tireSizeId") @Required final Long tireSizeId,
                               @NotNull @CompanyId @QueryParam("companyId") @Required final Long companyId) {
        final TireSizeEntity tireSizeEntity = service.getById(companyId, tireSizeId);
        return mapper.toTireSizeDto(tireSizeEntity);
    }

    @PATCH
    @Secured(authTypes = {AuthType.BEARER, AuthType.API}, permissions = Pilares.Frota.Pneu.ALTERAR)
    public SuccessResponse updateStatus(@Context final SecurityContext securityContext,
                                        @Valid final TireSizeStatusChangeDto tireSizeStatusChangeDto) {
        final Optional<ColaboradorAutenticado> userPrincipal =
                Optional.ofNullable((ColaboradorAutenticado) securityContext.getUserPrincipal());
        service.updateStatus(tireSizeStatusChangeDto, userPrincipal);
        return new SuccessResponse(null, "Alteração realizada com sucesso!");
    }

    @PUT
    @Secured(authTypes = {AuthType.BEARER, AuthType.API}, permissions = Pilares.Frota.Pneu.ALTERAR)
    public SuccessResponse updateTireSize(@Context final SecurityContext securityContext,
                                          @Valid @NotNull final TireSizeUpdateDto tireSizeUpdateDto) {
        final Optional<ColaboradorAutenticado> userPrincipal =
                Optional.ofNullable((ColaboradorAutenticado) securityContext.getUserPrincipal());
        service.updateTireSize(tireSizeUpdateDto, userPrincipal);
        return new SuccessResponse(null, "Alteração realizada com sucesso!");
    }
}
