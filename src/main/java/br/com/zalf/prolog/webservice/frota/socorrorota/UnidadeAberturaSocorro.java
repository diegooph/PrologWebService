package br.com.zalf.prolog.webservice.frota.socorrorota;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
/**
 * Created on 12/19/19
 *
 * @author Wellington Moraes (https://github.com/wvinim)
 */
public class UnidadeAberturaSocorro {
    @NotNull
    private final Long codUnidade;

    @NotNull
    private final String nomeUnidade;

    public UnidadeAberturaSocorro(@NotNull final Long codUnidade,
                                  @NotNull final String nomeUnidade){
        this.codUnidade = codUnidade;
        this.nomeUnidade = nomeUnidade;
    }

    @NotNull
    public Long getCodUnidade() { return codUnidade; }

    @NotNull
    public String getNomeUnidade() { return nomeUnidade; }
}