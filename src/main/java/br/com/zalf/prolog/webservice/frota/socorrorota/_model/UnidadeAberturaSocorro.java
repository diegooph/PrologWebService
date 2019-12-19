package br.com.zalf.prolog.webservice.frota.socorrorota._model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
/**
 * Created on 12/19/19
 *
 * @author Wellington Moraes (https://github.com/wvinim)
 */
public class UnidadeAberturaSocorro {
    /*
    * Código único que identifica a unidade
    * */
    @NotNull
    private final Long codUnidade;

    /*
    * Nome da unidade
    * */
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