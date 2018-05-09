package br.com.zalf.prolog.webservice.frota.pneu.afericao.configuracao;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.errorhandling.exception.GenericException;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.ConfiguracaoTipoVeiculoAfericao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.ConfiguracaoAfericaoValidator;
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
                                       @NotNull final List<ConfiguracaoTipoVeiculoAfericao> configuracoes) throws Exception {
        ConfiguracaoAfericaoValidator.validateUpdate(configuracoes);
        try {
            dao.insertOrUpdateConfiguracao(codUnidade, configuracoes);
            return Response.ok("Configurações atualizadas com sucesso!");
        } catch (SQLException e) {
            throw new GenericException("Não foi possível atualizar as configurações de Aferição",
                    "Algo deu errado no servidor. Não foi possível atualizar as configurações de Aferição",
                    e);
        }
    }

    public List<ConfiguracaoTipoVeiculoAfericao> getConfiguracoesTipoAfericaoVeiculo(@NotNull final Long codUnidade) throws Exception {
        try {
            return dao.getConfiguracoesTipoAfericaoVeiculo(codUnidade);
        } catch (SQLException e) {
            throw new GenericException("Não foi possível buscar as configurações de Aferição",
                    "Algo deu errado no servidor. Não foi possível buscar as configurações de Aferição",
                    e);
        }
    }
}
