package test.br.com.zalf.prolog.webservice.pilares.frota.pneu.v3;

import br.com.zalf.prolog.webservice.frota.pneu.v3._model.dto.PneuCadastroDto;

import java.math.BigDecimal;

/**
 * Created on 2021-03-16
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
public class PneuCadastroFactory {

    public static PneuCadastroDto createCorrectPneuCadastro() {
        return PneuCadastroDto.of(3L,
                                  5L,
                                  "teste04",
                                  28L,
                                  10L,
                                  2,
                                  3,
                                  100.00,
                                  "0001",
                                  new BigDecimal("1500"),
                                  false,
                                  9L,
                                  new BigDecimal("100"));
    }
}
