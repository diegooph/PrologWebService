package br.com.zalf.prolog.webservice.integracao.praxio.data;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 11/27/19
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
@Data
public final class ApiAutenticacaoHolder {
    @NotNull
    private final String prologTokenIntegracao;
    @NotNull
    private final String url;
    @NotNull
    private final String apiTokenClient;
    @NotNull
    private final Long apiShortCode;
}
