package test.br.com.zalf.prolog.webservice.v3.frota.pneu;

import br.com.zalf.prolog.webservice.v3.frota.pneu._model.PneuCadastroDto;

import java.math.BigDecimal;

/**
 * Created on 2021-03-16
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
public class PneuCadastroFactory {

    public static PneuCadastroDto createCorrectPneuCadastro() {
        return PneuCadastroDto.of(3L,
                                  215L,
                                  "teste04",
                                  131L,
                                  10L,
                                  2,
                                  3,
                                  100.00,
                                  "0001",
                                  new BigDecimal("1500"),
                                  false,
                                  11L,
                                  new BigDecimal("100"));
    }
}
