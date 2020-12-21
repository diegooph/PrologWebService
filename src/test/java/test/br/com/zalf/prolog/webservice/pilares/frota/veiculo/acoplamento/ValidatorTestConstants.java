package test.br.com.zalf.prolog.webservice.pilares.frota.veiculo.acoplamento;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 2020-12-18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class ValidatorTestConstants {
    @NotNull
    public static final Long COD_UNIDADE_TESTES = 5L;

    private ValidatorTestConstants() {
        throw new IllegalStateException(ValidatorTestConstants.class.getSimpleName() + " cannot be instantiated!");
    }
}
