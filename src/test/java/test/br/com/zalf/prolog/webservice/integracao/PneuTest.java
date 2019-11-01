package test.br.com.zalf.prolog.webservice.integracao;

import br.com.zalf.prolog.webservice.frota.pneu._model.PneuComum;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Created by luiz on 09/08/17.
 */
public class PneuTest {

    @Test
    public void testCastPressaoAtualToInt() {
        final PneuComum pneu = new PneuComum();
        pneu.setPressaoAtual(0);
        assertTrue(pneu.getPressaoAtualAsInt() == 0);

        pneu.setPressaoAtual(9.9);
        assertTrue(pneu.getPressaoAtualAsInt() == 10);

        pneu.setPressaoAtual(9.1);
        assertTrue(pneu.getPressaoAtualAsInt() == 9);

        pneu.setPressaoAtual(9.5);
        assertTrue(pneu.getPressaoAtualAsInt() == 9);

        pneu.setPressaoAtual(9.51);
        assertTrue(pneu.getPressaoAtualAsInt() == 10);
    }
}