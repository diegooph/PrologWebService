package br.com.zalf.prolog.webservice.frota.pneu.v3;

import br.com.zalf.prolog.webservice.commons.network.SuccessResponse;
import br.com.zalf.prolog.webservice.frota.pneu.v3._model.dto.PneuCadastroDto;
import org.jetbrains.annotations.NotNull;

import javax.validation.Valid;
import javax.ws.rs.core.Response;

/**
 * Created on 2021-03-12
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
public interface PneuV3ApiDoc {

    @NotNull
    SuccessResponse insert(@Valid final PneuCadastroDto pneuCadastro);
}
