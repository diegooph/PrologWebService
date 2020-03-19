package br.com.zalf.prolog.webservice.customfields;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 2020-03-19
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Getter
@AllArgsConstructor
public enum TipoCampoPersonalizado {
    LISTA_SELECAO(1),
    TEXTO_MULTILINHAS(2);

    private final int codigoTipoCampo;

    @NotNull
    public static TipoCampoPersonalizado fromCodigo(final int codigoTipoCampo) {
        for (final TipoCampoPersonalizado tipoCampo : TipoCampoPersonalizado.values()) {
            if (tipoCampo.getCodigoTipoCampo() == codigoTipoCampo) {
                return tipoCampo;
            }
        }

        throw new IllegalArgumentException("Nenhum tipo de campo encontrado para o código: " + codigoTipoCampo);
    }
}
