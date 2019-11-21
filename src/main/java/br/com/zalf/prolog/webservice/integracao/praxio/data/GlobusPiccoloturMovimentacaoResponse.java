package br.com.zalf.prolog.webservice.integracao.praxio.data;

import br.com.zalf.prolog.webservice.commons.gson.GsonUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created on 11/19/19
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class GlobusPiccoloturMovimentacaoResponse {
    /**
     * Indica o sucesso (<code>TRUE</code>) ou erro (<code>FALSE</code>) do processo de movimentação realizado.
     */
    private final boolean sucesso;
    /**
     * Lista de ids retornados da operação. Para cada movimentação, um novo <code>id</code> é criado.
     * O id é um valor alfanumérico.
     */
    @Nullable
    private final List<String> idExterno;
    /**
     * Lista de erros que ocorreram no processo de movimentação. Cada movimentação poderá retornar o seu próprio erro.
     */
    @Nullable
    private final List<String> errors;

    public GlobusPiccoloturMovimentacaoResponse(final boolean sucesso,
                                                @Nullable final List<String> idExterno,
                                                @Nullable final List<String> errors) {
        this.sucesso = sucesso;
        this.idExterno = idExterno;
        this.errors = errors;
    }

    @NotNull
    static GlobusPiccoloturMovimentacaoResponse generateFromString(@NotNull final String jsonBody) {
        return GsonUtils.getGson().fromJson(jsonBody, GlobusPiccoloturMovimentacaoResponse.class);
    }

    public boolean isSucesso() {
        return sucesso;
    }

    @Nullable
    public List<String> getIdExterno() {
        return idExterno;
    }

    @Nullable
    public List<String> getErrors() {
        return errors;
    }

    @NotNull
    public String getPrettyErrors() {
        if (this.errors == null || this.errors.isEmpty()) {
            return "";
        }

        return String.join("\n", this.errors);
    }
}
