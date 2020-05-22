package br.com.zalf.prolog.webservice.entrega.mapa;

import br.com.zalf.prolog.webservice.commons.network.Response;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 2020-05-22
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class ResponseErrorUploadMapa extends Response {
    @NotNull
    private final List<String> mensagensErro;

    public ResponseErrorUploadMapa(@NotNull final List<String> mensagensErro) {
        setMsg("A planilha de mapa contém erros.");
        setStatus(ERROR);
        this.mensagensErro = mensagensErro;
    }

    @NotNull
    public List<String> getMensagensErro() {
        return mensagensErro;
    }
}
