package br.com.zalf.prolog.webservice.raizen.produtividade;

import br.com.zalf.prolog.webservice.dashboard.base.IdentificadorTipoComponente;
import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

import static com.sun.corba.se.impl.util.Version.asString;

/**
 * Created on 09/07/18.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public enum RaizenProdutividadeAgrupamento {
    POR_COLABORADOR("por_colaborador"),
    POR_DATA("por_data");

    @NotNull
    private final String identificador;

    RaizenProdutividadeAgrupamento(String identificador) {
        this.identificador = identificador;
    }

    @Override
    public String toString() {
        return asString();
    }

    @NotNull
    public String asString() {
        return identificador;
    }

    public static RaizenProdutividadeAgrupamento fromString(@Nonnull final String string) {
        Preconditions.checkNotNull(string, "string cannot be null!");

        for (final RaizenProdutividadeAgrupamento tipo : RaizenProdutividadeAgrupamento.values()) {
            if (string.equals(tipo.identificador)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Nenhum tipo de componente encontrado para a string: " + string);
    }
}
