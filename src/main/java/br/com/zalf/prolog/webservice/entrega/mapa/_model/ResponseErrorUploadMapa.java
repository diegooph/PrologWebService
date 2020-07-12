package br.com.zalf.prolog.webservice.entrega.mapa._model;

import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 2020-05-22
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class ResponseErrorUploadMapa extends AbstractResponse {
    @NotNull
    private final List<CelulaPlanilhaMapaErro> errosUploadMapa;

    public ResponseErrorUploadMapa(@NotNull final List<CelulaPlanilhaMapaErro> errosUploadMapa) {
        setStatus(ERROR);
        setMsg("A planilha de mapa contém erros.");
        this.errosUploadMapa = errosUploadMapa;
    }

    @NotNull
    public List<CelulaPlanilhaMapaErro> getErrosUploadMapa() {
        return errosUploadMapa;
    }
}
