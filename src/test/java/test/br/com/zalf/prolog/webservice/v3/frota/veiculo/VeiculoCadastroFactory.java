package test.br.com.zalf.prolog.webservice.v3.frota.veiculo;

import br.com.zalf.prolog.webservice.v3.fleet.vehicle._model.VeiculoCreateDto;
import org.jetbrains.annotations.NotNull;

public class VeiculoCadastroFactory {
    @NotNull
    public static VeiculoCreateDto createVeiculoCadastroToInsert() {
        return new VeiculoCreateDto(3L,
                                    215L,
                                    "TST0001",
                                    "FRT - 0001",
                                    120L,
                                    63L,
                                    44231L,
                                    false);
    }
}
