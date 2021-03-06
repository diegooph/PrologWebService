package test.br.com.zalf.prolog.webservice.v3.fleet.vehicle;

import br.com.zalf.prolog.webservice.v3.fleet.vehicle._model.VehicleCreateDto;
import org.jetbrains.annotations.NotNull;

public class VehicleFactory {
    @NotNull
    public static VehicleCreateDto createVehicleToInsert() {
        return new VehicleCreateDto(3L,
                                    215L,
                                    "TST0001",
                                    "FRT - 0001",
                                    120L,
                                    63L,
                                    44231L,
                                    false);
    }
}
