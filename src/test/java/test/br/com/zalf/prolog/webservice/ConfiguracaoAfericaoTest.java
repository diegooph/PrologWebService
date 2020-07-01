package test.br.com.zalf.prolog.webservice;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.gson.GsonUtils;
import br.com.zalf.prolog.webservice.database.DatabaseManager;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.configuracao.ConfiguracaoAfericaoDao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.configuracao._model.ConfiguracaoTipoVeiculoAferivelInsercao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.configuracao._model.ConfiguracaoTipoVeiculoAferivelListagem;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.configuracao._model.FormaColetaDadosAfericaoEnum;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;

/**
 * Created on 08/05/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ConfiguracaoAfericaoTest extends BaseTest {

    @Override
    @BeforeAll
    public void initialize() throws Throwable {
        DatabaseManager.init();
    }

    @Override
    @AfterAll
    public void destroy() {
        DatabaseManager.finish();
    }

    @Test
    public void testInsertConfiguracoesTipoAfericao() throws Throwable {
        final ConfiguracaoAfericaoDao dao = Injection.provideConfiguracaoAfericaoDao();
        final List<ConfiguracaoTipoVeiculoAferivelInsercao> configs = new ArrayList<>();
        final ConfiguracaoTipoVeiculoAferivelInsercao config = new ConfiguracaoTipoVeiculoAferivelInsercao(
                266L,
                13L,
                true,
                FormaColetaDadosAfericaoEnum.EQUIPAMENTO_MANUAL,
                FormaColetaDadosAfericaoEnum.EQUIPAMENTO_MANUAL,
                FormaColetaDadosAfericaoEnum.EQUIPAMENTO_MANUAL,
                FormaColetaDadosAfericaoEnum.EQUIPAMENTO);
        configs.add(config);
        dao.insertOrUpdateConfiguracoesTiposVeiculosAferiveis(215L, configs);

        final List<ConfiguracaoTipoVeiculoAferivelListagem> configuracoes =
                dao.getConfiguracoesTipoAfericaoVeiculo(215L);

        System.out.println(GsonUtils.getGson().toJson(configuracoes));
        assertThat(configuracoes).isNotEmpty();
    }

    @Test
    public void testGetConfiguracoesTipoAfericao() throws Throwable {
        final ConfiguracaoAfericaoDao dao = Injection.provideConfiguracaoAfericaoDao();
        final List<ConfiguracaoTipoVeiculoAferivelListagem> configuracoes =
                dao.getConfiguracoesTipoAfericaoVeiculo(215L);

        System.out.println(GsonUtils.getGson().toJson(configuracoes));
        assertThat(configuracoes).isNotEmpty();
    }

    @Test
    public void testUpdateAndGetConfiguracoesTipoAfericao() throws Throwable {
        final ConfiguracaoAfericaoDao dao = Injection.provideConfiguracaoAfericaoDao();
        final List<ConfiguracaoTipoVeiculoAferivelInsercao> configs = new ArrayList<>();
        final ConfiguracaoTipoVeiculoAferivelInsercao config = new ConfiguracaoTipoVeiculoAferivelInsercao(null,
                63L,
                false,
                FormaColetaDadosAfericaoEnum.BLOQUEADO,
                FormaColetaDadosAfericaoEnum.EQUIPAMENTO,
                FormaColetaDadosAfericaoEnum.EQUIPAMENTO,
                FormaColetaDadosAfericaoEnum.EQUIPAMENTO);
        configs.add(config);
        dao.insertOrUpdateConfiguracoesTiposVeiculosAferiveis(5L, configs);

        final List<ConfiguracaoTipoVeiculoAferivelListagem> configuracoes = dao.getConfiguracoesTipoAfericaoVeiculo(5L);

        System.out.println(GsonUtils.getGson().toJson(configuracoes));
        assertThat(configuracoes).isEmpty();
    }

    @Test
    public void testUpdateSameConfiguracoesTipoAfericao() throws Throwable {
        final ConfiguracaoAfericaoDao dao = Injection.provideConfiguracaoAfericaoDao();
        final List<ConfiguracaoTipoVeiculoAferivelInsercao> configs = new ArrayList<>();
        final ConfiguracaoTipoVeiculoAferivelInsercao config = new ConfiguracaoTipoVeiculoAferivelInsercao(null,
                63L,
                false,
                FormaColetaDadosAfericaoEnum.EQUIPAMENTO,
                FormaColetaDadosAfericaoEnum.EQUIPAMENTO,
                FormaColetaDadosAfericaoEnum.EQUIPAMENTO,
                FormaColetaDadosAfericaoEnum.EQUIPAMENTO);
        configs.add(config);
        dao.insertOrUpdateConfiguracoesTiposVeiculosAferiveis(5L, configs);

        final List<ConfiguracaoTipoVeiculoAferivelListagem> configuracoes = dao.getConfiguracoesTipoAfericaoVeiculo(5L);

        System.out.println(GsonUtils.getGson().toJson(configuracoes));
        assertThat(configuracoes).isEmpty();
    }
}
