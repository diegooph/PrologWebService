package br.com.zalf.prolog.webservice.frota.pneu.v3;

import br.com.zalf.prolog.webservice.frota.pneu.v3._model.PneuEntity;
import br.com.zalf.prolog.webservice.frota.pneu.v3._model.dto.PneuCadastro;
import br.com.zalf.prolog.webservice.frota.pneu.v3.mapper.PneuMapper;
import br.com.zalf.prolog.webservice.frota.pneu.v3.service.PneuV3Service;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.interceptors.debug.ConsoleDebugLog;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;

/**
 * Created on 2021-03-12
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Controller
@ConsoleDebugLog
@Path("/v3/pneus")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class PneuV3Resource implements PneuV3ApiDoc {

    private static final String TAG = PneuV3Resource.class.getSimpleName();

    private final PneuV3Service service;
    private final PneuMapper<PneuEntity, PneuCadastro> pneuCadastroMapper;

    @Autowired
    public PneuV3Resource(@NotNull final PneuV3Service service,
                          @NotNull final PneuMapper<PneuEntity, PneuCadastro> pneuCadastroMapper) {
        this.service = service;
        this.pneuCadastroMapper = pneuCadastroMapper;
    }

    @POST
    @Secured(permissions = Pilares.Frota.Pneu.CADASTRAR)
    @Override
    @NotNull
    public Response insert(@NotNull final PneuCadastro pneuCadastro) {
        final PneuEntity dtoConvertedToEntity = this.pneuCadastroMapper.toEntity(pneuCadastro);
        final PneuEntity savedPneu = this.service.create(dtoConvertedToEntity);
        return Response
                .created(URI.create("/v3/pneus/" + savedPneu.getId()))
                .header("Location-id", savedPneu.getId().toString())
                .build();
    }
}