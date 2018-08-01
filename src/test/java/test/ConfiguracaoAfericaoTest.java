package test;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.gson.GsonUtils;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.configuracao.ConfiguracaoAfericaoDao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.ConfiguracaoTipoVeiculoAfericao;
import br.com.zalf.prolog.webservice.frota.veiculo.model.TipoVeiculo;
import org.junit.Assert;
import org.junit.Test;

import java.sql.SQLException;
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
    public void testInsertConfiguracoesTipoAfericao() throws SQLException {
        final ConfiguracaoAfericaoDao dao = Injection.provideConfiguracaoAfericaoDao();
        final List<ConfiguracaoTipoVeiculoAfericao> configs = new ArrayList<>();
        final ConfiguracaoTipoVeiculoAfericao config = new ConfiguracaoTipoVeiculoAfericao();
        config.setCodUnidade(5L);
        final TipoVeiculo tipoVeiculo = new TipoVeiculo();
        tipoVeiculo.setCodigo(13L);
        config.setTipoVeiculo(tipoVeiculo);
        config.setPodeAferirSulco(true);
        config.setPodeAferirPressao(true);
        config.setPodeAferirSulcoPressao(false);
        config.setPodeAferirEstepe(true);
        configs.add(config);
        dao.insertOrUpdateConfiguracao(5L, configs);

        List<ConfiguracaoTipoVeiculoAfericao> configuracoes = dao.getConfiguracoesTipoAfericaoVeiculo(5L);

        System.out.println(GsonUtils.getGson().toJson(configuracoes));
        Assert.assertFalse(configuracoes.isEmpty());
    }

    @Test
    public void testGetConfiguracoesTipoAfericao() throws SQLException {
        final ConfiguracaoAfericaoDao dao = Injection.provideConfiguracaoAfericaoDao();
        List<ConfiguracaoTipoVeiculoAfericao> configuracoes = dao.getConfiguracoesTipoAfericaoVeiculo(5L);

        System.out.println(GsonUtils.getGson().toJson(configuracoes));
        Assert.assertFalse(configuracoes.isEmpty());
    }

    @Test
    public void testUpdateAndGetConfiguracoesTipoAfericao() throws SQLException {
        final ConfiguracaoAfericaoDao dao = Injection.provideConfiguracaoAfericaoDao();
        final List<ConfiguracaoTipoVeiculoAfericao> configs = new ArrayList<>();
        final ConfiguracaoTipoVeiculoAfericao config = new ConfiguracaoTipoVeiculoAfericao();
        config.setCodUnidade(5L);
        final TipoVeiculo tipoVeiculo = new TipoVeiculo();
        tipoVeiculo.setCodigo(63L);
        config.setTipoVeiculo(tipoVeiculo);
        config.setPodeAferirSulco(false);
        config.setPodeAferirPressao(false);
        config.setPodeAferirSulcoPressao(true);
        config.setPodeAferirEstepe(false);
        configs.add(config);
        dao.insertOrUpdateConfiguracao(5L, configs);

        List<ConfiguracaoTipoVeiculoAfericao> configuracoes = dao.getConfiguracoesTipoAfericaoVeiculo(5L);

        System.out.println(GsonUtils.getGson().toJson(configuracoes));
        Assert.assertFalse(configuracoes.isEmpty());
    }

    @Test
    public void testUpdateSameConfiguracoesTipoAfericao() throws SQLException {
        final ConfiguracaoAfericaoDao dao = Injection.provideConfiguracaoAfericaoDao();
        final List<ConfiguracaoTipoVeiculoAfericao> configs = new ArrayList<>();
        final ConfiguracaoTipoVeiculoAfericao config = new ConfiguracaoTipoVeiculoAfericao();
        config.setCodUnidade(5L);
        final TipoVeiculo tipoVeiculo = new TipoVeiculo();
        tipoVeiculo.setCodigo(63L);
        config.setTipoVeiculo(tipoVeiculo);
        config.setPodeAferirSulco(false);
        config.setPodeAferirPressao(false);
        config.setPodeAferirSulcoPressao(false);
        config.setPodeAferirEstepe(false);
        configs.add(config);
        dao.insertOrUpdateConfiguracao(5L, configs);

        List<ConfiguracaoTipoVeiculoAfericao> configuracoes = dao.getConfiguracoesTipoAfericaoVeiculo(5L);

        System.out.println(GsonUtils.getGson().toJson(configuracoes));
        Assert.assertFalse(configuracoes.isEmpty());
    }
}
