package br.com.zalf.prolog.webservice.implantacao;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 2019-08-29
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public enum ImplatancaoImportTokens {
    IMPORT_VEICULO("VAN9tX7Be7egIAPliXXrKCDjDSMQ4K7aUUjP3rahyGPIRo4ANc2ipGS3ipTnAbOf795A1gs1xIJP8OZV");

    @NotNull
    private final String token;

    ImplatancaoImportTokens(@NotNull final String token) {
        ImplantacaoImportTokensValidator.ensureRightTokenLength(token);

        this.token = token;
    }

    @NotNull
    public String getToken() {
        return token;
    }
}