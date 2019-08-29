package br.com.zalf.prolog.webservice.implantacao;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 2019-08-29
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class ImplantacaoImportTokensValidator {
    @NotNull
    public static final String HEADER_PARAM = "ProLog-Token-Implantacao";
    private static final int DEFAULT_TOKEN_LENGTH = 80;

    public ImplantacaoImportTokensValidator() {
        throw new IllegalStateException(ImplantacaoImportTokensValidator.class.getSimpleName()
                + " cannot be instantiated!");
    }

    public static void ensureRightTokenLength(@NotNull final String secretToken) {
        if (secretToken.length() != DEFAULT_TOKEN_LENGTH) {
            // Nós não concatemos o token na exception para ela não ser exposta em sistemas de logs.
            throw new IllegalArgumentException("O token fornecido tem tamanho incorreto: " + secretToken.length());
        }
    }

    public static void validateTokenFor(@NotNull final ImplantacaoImportTokens typeToken,
                                        @NotNull final String token) {
        ensureRightTokenLength(token);

        if (!typeToken.getToken().equals(token)) {
            throw new IllegalArgumentException("O token fornecido para " + typeToken.name() + " é inválido!");
        }
    }
}