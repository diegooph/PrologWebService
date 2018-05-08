package br.com.zalf.prolog.webservice.frota.pneu.afericao.configuracao;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.ConfiguracaoTipoVeiculoAfericao;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.List;

/**
 * Created on 03/05/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class ConfiguracaoAfericaoService {

    private final ConfiguracaoAfericaoDao dao = Injection.provideConfiguracaoAfericaoDao();

    public Response updateConfiguracao(@NotNull final Long codUnidade,
                                       @NotNull final ConfiguracaoTipoVeiculoAfericao configuracao) throws Exception {
        try {
            dao.insertOrUpdateConfiguracao(codUnidade, configuracao);
            return Response.ok("Configuração atualizada com sucesso!");
        } catch (SQLException e) {
            //TODO - Logar Exception do ProLog
            throw new Exception();
        }
    }

    public List<ConfiguracaoTipoVeiculoAfericao> getConfiguracoesTipoAfericaoVeiculo(@NotNull final Long codUnidade) throws Exception {
        try {
            return dao.getConfiguracoesTipoAfericaoVeiculo(codUnidade);
        } catch (SQLException e) {
            //TODO - Logar Exception do ProLog
            throw new Exception();
        }
    }
}
