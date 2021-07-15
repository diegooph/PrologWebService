package test.br.com.zalf.prolog.webservice.v3.frota.veiculo;

import br.com.zalf.prolog.webservice.v3.frota.veiculo._model.VeiculoCadastroDto;
import org.jetbrains.annotations.NotNull;

public class VeiculoCadastroFactory {
    @NotNull
    public static VeiculoCadastroDto createVeiculoCadastroToInsert() {
        return new VeiculoCadastroDto(3L,
                                      215L,
                                      "TST0001",
                                      "FRT - 0001",
                                      120L,
                                      63L,
                                      44231L,
                                      false);
    }
}
