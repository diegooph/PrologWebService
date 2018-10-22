package br.com.zalf.prolog.webservice.frota.pneu.afericao.configuracao.model;

import br.com.zalf.prolog.webservice.frota.veiculo.model.TipoVeiculo;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;

/**
 * Created on 22/10/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class ConfiguracaoConverter {

    public ConfiguracaoConverter() {
        throw new IllegalStateException(ConfiguracaoConverter.class.getSimpleName() + " cannot be instantiated!");
    }

    @NotNull
    public static ConfiguracaoAlertaColetaSulco createConfiguracaoAlertaColetaSulco(@NotNull final ResultSet rSet)
            throws Throwable {
        return new ConfiguracaoAlertaColetaSulco(
                rSet.getLong("COD_UNIDADE"),
                rSet.getDouble("VARIACAO_ACEITA_SULCO_MENOR_MILIMETROS"),
                rSet.getDouble("VARIACAO_ACEITA_SULCO_MAIOR_MILIMETROS"));
    }

    @NotNull
    public static ConfiguracaoTipoVeiculoAferivel createConfiguracaoTipoVeiculoAfericao(@NotNull final ResultSet rSet)
            throws Throwable {
        final ConfiguracaoTipoVeiculoAferivel config = new ConfiguracaoTipoVeiculoAferivel();
        final long codigo = rSet.getLong("CODIGO");
        config.setCodigo(codigo == 0 ? null : codigo);
        config.setCodUnidade(rSet.getLong("COD_UNIDADE"));
        config.setTipoVeiculo(createTipoVeiculo(rSet));
        config.setPodeAferirSulco(rSet.getBoolean("PODE_AFERIR_SULCO"));
        config.setPodeAferirPressao(rSet.getBoolean("PODE_AFERIR_PRESSAO"));
        config.setPodeAferirSulcoPressao(rSet.getBoolean("PODE_AFERIR_SULCO_PRESSAO"));
        config.setPodeAferirEstepe(rSet.getBoolean("PODE_AFERIR_ESTEPE"));
        return config;
    }

    @NotNull
    private static TipoVeiculo createTipoVeiculo(@NotNull final ResultSet rSet) throws Throwable {
        final TipoVeiculo tipoVeiculo = new TipoVeiculo();
        tipoVeiculo.setCodigo(rSet.getLong("COD_TIPO_VEICULO"));
        tipoVeiculo.setNome(rSet.getString("NOME_TIPO_VEICULO"));
        return tipoVeiculo;
    }
}