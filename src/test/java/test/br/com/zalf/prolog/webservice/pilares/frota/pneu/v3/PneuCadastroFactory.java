package test.br.com.zalf.prolog.webservice.pilares.frota.pneu.v3;

import br.com.zalf.prolog.webservice.frota.pneu.v3._model.dto.PneuCadastroDto;

/**
 * Created on 2021-03-16
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
public class PneuCadastroFactory {

    public static PneuCadastroDto createCorrectPneuCadastro() {
        return PneuCadastroDto.of(5L,
                                  3L,
                                  "teste04",
                                  28L,
                                  9L,
                                  100.00,
                                  18L,
                                  2,
                                  3,
                                  100.00,
                                  "0001",
                                  1500.00,
                                  false);
    }
}
