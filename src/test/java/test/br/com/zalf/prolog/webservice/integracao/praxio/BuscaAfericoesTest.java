package test.br.com.zalf.prolog.webservice.integracao.praxio;

import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.TipoMedicaoColetadaAfericao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.TipoProcessoColetaAfericao;
import br.com.zalf.prolog.webservice.integracao.praxio.IntegracaoPraxioService;
import br.com.zalf.prolog.webservice.integracao.praxio.afericao.MedicaoIntegracaoPraxio;
import org.junit.Assert;
import org.junit.Test;
import test.br.com.zalf.prolog.webservice.BaseTest;

import java.util.List;

/**
 * Created on 12/12/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class BuscaAfericoesTest extends BaseTest {
    private static final String TOKEN_PICCOLOTUR = "kffdm2ba5ai3lsk79kqur9rb3mq7hv59qa8pr0sho4mcr56clck";
    private IntegracaoPraxioService service;

    @Override
    public void initialize() {
        service = new IntegracaoPraxioService();
    }

    @Test
    public void testBuscaAfericoes() throws ProLogException {
        final List<MedicaoIntegracaoPraxio> medicoesRealizadas =
                service.getAfericoesRealizadas(TOKEN_PICCOLOTUR, 10L);

        Assert.assertNotNull(medicoesRealizadas);
        Assert.assertTrue(medicoesRealizadas.size() > 0);

        for (final MedicaoIntegracaoPraxio afericao : medicoesRealizadas) {
            Assert.assertNotNull(afericao.getCodigo());
            Assert.assertTrue(afericao.getCodigo() > 0);
            Assert.assertNotNull(afericao.getCodUnidadeAfericao());
            Assert.assertTrue(afericao.getCodUnidadeAfericao() == 39 || afericao.getCodUnidadeAfericao() == 96);
            Assert.assertNotNull(afericao.getCpfColaborador());
            Assert.assertFalse(afericao.getCpfColaborador().isEmpty());
            Assert.assertNotNull(afericao.getCodPneuAferido());
            Assert.assertTrue(afericao.getCodPneuAferido() > 0);
            Assert.assertNotNull(afericao.getNumeroFogoPneu());
            Assert.assertFalse(afericao.getNumeroFogoPneu().isEmpty());
            Assert.assertNotNull(afericao.getTempoRealizacaoEmSegundos());
            Assert.assertTrue(afericao.getTempoRealizacaoEmSegundos() > 0);
            Assert.assertNotNull(afericao.getVidaPneuMomentoAfericao());
            Assert.assertTrue(afericao.getVidaPneuMomentoAfericao() >= 0);
            Assert.assertNotNull(afericao.getDataHoraAfericao());

            if (afericao.getTipoProcessoColetaAfericao().equals(TipoProcessoColetaAfericao.PLACA)
                    && afericao.getTipoMedicaoColetadaAfericao().equals(TipoMedicaoColetadaAfericao.SULCO_PRESSAO)) {
                Assert.assertNotNull(afericao.getPlacaVeiculoAferido());
                Assert.assertFalse(afericao.getPlacaVeiculoAferido().isEmpty());
                Assert.assertNotNull(afericao.getAlturaSulcoInterno());
                Assert.assertTrue(afericao.getAlturaSulcoInterno() >= 0);
                Assert.assertNotNull(afericao.getAlturaSulcoCentralInterno());
                Assert.assertTrue(afericao.getAlturaSulcoCentralInterno() >= 0);
                Assert.assertNotNull(afericao.getAlturaSulcoCentralExterno());
                Assert.assertTrue(afericao.getAlturaSulcoCentralExterno() >= 0);
                Assert.assertNotNull(afericao.getAlturaSulcoExterno());
                Assert.assertTrue(afericao.getAlturaSulcoExterno() >= 0);
                Assert.assertNotNull(afericao.getPressao());
                Assert.assertTrue(afericao.getPressao() >= 0);
                Assert.assertNotNull(afericao.getKmVeiculoMomentoAfericao());
                Assert.assertTrue(afericao.getKmVeiculoMomentoAfericao() >= 0);
                Assert.assertNotNull(afericao.getPosicaoPneuMomentoAfericao());
                Assert.assertTrue(afericao.getPosicaoPneuMomentoAfericao() >= 111
                        && afericao.getPosicaoPneuMomentoAfericao() <= 999);
            } else if (afericao.getTipoProcessoColetaAfericao().equals(TipoProcessoColetaAfericao.PLACA)
                    && afericao.getTipoMedicaoColetadaAfericao().equals(TipoMedicaoColetadaAfericao.SULCO)) {
                Assert.assertNotNull(afericao.getPlacaVeiculoAferido());
                Assert.assertFalse(afericao.getPlacaVeiculoAferido().isEmpty());
                Assert.assertNotNull(afericao.getAlturaSulcoInterno());
                Assert.assertTrue(afericao.getAlturaSulcoInterno() >= 0);
                Assert.assertNotNull(afericao.getAlturaSulcoCentralInterno());
                Assert.assertTrue(afericao.getAlturaSulcoCentralInterno() >= 0);
                Assert.assertNotNull(afericao.getAlturaSulcoCentralExterno());
                Assert.assertTrue(afericao.getAlturaSulcoCentralExterno() >= 0);
                Assert.assertNotNull(afericao.getAlturaSulcoExterno());
                Assert.assertTrue(afericao.getAlturaSulcoExterno() >= 0);
                Assert.assertNotNull(afericao.getKmVeiculoMomentoAfericao());
                Assert.assertTrue(afericao.getKmVeiculoMomentoAfericao() >= 0);
                Assert.assertNotNull(afericao.getPosicaoPneuMomentoAfericao());
                Assert.assertTrue(afericao.getPosicaoPneuMomentoAfericao() >= 111
                        && afericao.getPosicaoPneuMomentoAfericao() <= 999);
            } else if (afericao.getTipoProcessoColetaAfericao().equals(TipoProcessoColetaAfericao.PLACA)
                    && afericao.getTipoMedicaoColetadaAfericao().equals(TipoMedicaoColetadaAfericao.PRESSAO)) {
                Assert.assertNotNull(afericao.getPlacaVeiculoAferido());
                Assert.assertFalse(afericao.getPlacaVeiculoAferido().isEmpty());
                Assert.assertNotNull(afericao.getPressao());
                Assert.assertTrue(afericao.getPressao() >= 0);
                Assert.assertNotNull(afericao.getKmVeiculoMomentoAfericao());
                Assert.assertTrue(afericao.getKmVeiculoMomentoAfericao() >= 0);
                Assert.assertNotNull(afericao.getPosicaoPneuMomentoAfericao());
                Assert.assertTrue(afericao.getPosicaoPneuMomentoAfericao() >= 111
                        && afericao.getPosicaoPneuMomentoAfericao() <= 999);
            } else if (afericao.getTipoProcessoColetaAfericao().equals(TipoProcessoColetaAfericao.PNEU_AVULSO)
                    && afericao.getTipoMedicaoColetadaAfericao().equals(TipoMedicaoColetadaAfericao.SULCO)) {
                Assert.assertNotNull(afericao.getAlturaSulcoInterno());
                Assert.assertTrue(afericao.getAlturaSulcoInterno() >= 0);
                Assert.assertNotNull(afericao.getAlturaSulcoCentralInterno());
                Assert.assertTrue(afericao.getAlturaSulcoCentralInterno() >= 0);
                Assert.assertNotNull(afericao.getAlturaSulcoCentralExterno());
                Assert.assertTrue(afericao.getAlturaSulcoCentralExterno() >= 0);
                Assert.assertNotNull(afericao.getAlturaSulcoExterno());
                Assert.assertTrue(afericao.getAlturaSulcoExterno() >= 0);
            } else {
                throw new IllegalStateException("Não foi mapeando nenhuma combinação de Tipo Medição e Tipo Processo");
            }
        }
    }
}
