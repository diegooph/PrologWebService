package test.br.com.zalf.prolog.webservice;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.gson.GsonUtils;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.configuracao.ConfiguracaoAfericaoDao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.configuracao._model.ConfiguracaoTipoVeiculoAferivel;
import br.com.zalf.prolog.webservice.frota.veiculo.model.TipoVeiculo;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 08/05/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class ConfiguracaoAfericaoTest extends BaseTest {
    @Override
    public void initialize() {
        // Do nothing
    }

    @Test
    public void testInsertConfiguracoesTipoAfericao() throws Throwable {
        final ConfiguracaoAfericaoDao dao = Injection.provideConfiguracaoAfericaoDao();
        final List<ConfiguracaoTipoVeiculoAferivel> configs = new ArrayList<>();
        final ConfiguracaoTipoVeiculoAferivel config = new ConfiguracaoTipoVeiculoAferivel();
        config.setCodUnidade(5L);
        final TipoVeiculo tipoVeiculo = new TipoVeiculo();
        tipoVeiculo.setCodigo(13L);
        config.setTipoVeiculo(tipoVeiculo);
        config.setPodeAferirSulco(true);
        config.setPodeAferirPressao(true);
        config.setPodeAferirSulcoPressao(false);
        config.setPodeAferirEstepe(true);
        configs.add(config);
        dao.insertOrUpdateConfiguracoesTiposVeiculosAferiveis(5L, configs);

        List<ConfiguracaoTipoVeiculoAferivel> configuracoes = dao.getConfiguracoesTipoAfericaoVeiculo(5L);

        System.out.println(GsonUtils.getGson().toJson(configuracoes));
        Assert.assertFalse(configuracoes.isEmpty());
    }

    @Test
    public void testGetConfiguracoesTipoAfericao() throws Throwable {
        final ConfiguracaoAfericaoDao dao = Injection.provideConfiguracaoAfericaoDao();
        List<ConfiguracaoTipoVeiculoAferivel> configuracoes = dao.getConfiguracoesTipoAfericaoVeiculo(5L);

        System.out.println(GsonUtils.getGson().toJson(configuracoes));
        Assert.assertFalse(configuracoes.isEmpty());
    }

    @Test
    public void testUpdateAndGetConfiguracoesTipoAfericao() throws Throwable {
        final ConfiguracaoAfericaoDao dao = Injection.provideConfiguracaoAfericaoDao();
        final List<ConfiguracaoTipoVeiculoAferivel> configs = new ArrayList<>();
        final ConfiguracaoTipoVeiculoAferivel config = new ConfiguracaoTipoVeiculoAferivel();
        config.setCodUnidade(5L);
        final TipoVeiculo tipoVeiculo = new TipoVeiculo();
        tipoVeiculo.setCodigo(63L);
        config.setTipoVeiculo(tipoVeiculo);
        config.setPodeAferirSulco(false);
        config.setPodeAferirPressao(false);
        config.setPodeAferirSulcoPressao(true);
        config.setPodeAferirEstepe(false);
        configs.add(config);
        dao.insertOrUpdateConfiguracoesTiposVeiculosAferiveis(5L, configs);

        List<ConfiguracaoTipoVeiculoAferivel> configuracoes = dao.getConfiguracoesTipoAfericaoVeiculo(5L);

        System.out.println(GsonUtils.getGson().toJson(configuracoes));
        Assert.assertFalse(configuracoes.isEmpty());
    }

    @Test
    public void testUpdateSameConfiguracoesTipoAfericao() throws Throwable {
        final ConfiguracaoAfericaoDao dao = Injection.provideConfiguracaoAfericaoDao();
        final List<ConfiguracaoTipoVeiculoAferivel> configs = new ArrayList<>();
        final ConfiguracaoTipoVeiculoAferivel config = new ConfiguracaoTipoVeiculoAferivel();
        config.setCodUnidade(5L);
        final TipoVeiculo tipoVeiculo = new TipoVeiculo();
        tipoVeiculo.setCodigo(63L);
        config.setTipoVeiculo(tipoVeiculo);
        config.setPodeAferirSulco(false);
        config.setPodeAferirPressao(false);
        config.setPodeAferirSulcoPressao(false);
        config.setPodeAferirEstepe(false);
        configs.add(config);
        dao.insertOrUpdateConfiguracoesTiposVeiculosAferiveis(5L, configs);

        List<ConfiguracaoTipoVeiculoAferivel> configuracoes = dao.getConfiguracoesTipoAfericaoVeiculo(5L);

        System.out.println(GsonUtils.getGson().toJson(configuracoes));
        Assert.assertFalse(configuracoes.isEmpty());
    }
}
