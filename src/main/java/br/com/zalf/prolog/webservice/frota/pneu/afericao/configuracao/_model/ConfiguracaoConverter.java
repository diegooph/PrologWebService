package br.com.zalf.prolog.webservice.frota.pneu.afericao.configuracao._model;

import br.com.zalf.prolog.webservice.commons.util.NullIf;
import br.com.zalf.prolog.webservice.frota.veiculo.model.TipoVeiculo;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.time.LocalDateTime;

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
    public static ConfiguracaoAlertaColetaSulco createConfiguracaoAlertaColetaSulco(
            @NotNull final ResultSet rSet) throws Throwable {
        final long codigo = rSet.getLong("CODIGO");
        return new ConfiguracaoAlertaColetaSulco(
                codigo == 0 ? null : codigo,
                rSet.getLong("COD_UNIDADE"),
                rSet.getString("NOME_UNIDADE"),
                rSet.getDouble("VARIACAO_ACEITA_SULCO_MENOR_MILIMETROS"),
                rSet.getDouble("VARIACAO_ACEITA_SULCO_MAIOR_MILIMETROS"));
    }

    @NotNull
    public static ConfiguracaoCronogramaServico createConfiguracaoAberturaServico(
            @NotNull final ResultSet rSet) throws Throwable {
        final long codigo = rSet.getLong("CODIGO");
        return new ConfiguracaoCronogramaServico(
                codigo == 0 ? null : codigo,
                rSet.getLong("CODIGO_EMPRESA"),
                rSet.getLong("CODIGO_REGIONAL"),
                rSet.getString("NOME_REGIONAL"),
                rSet.getLong("CODIGO_UNIDADE"),
                rSet.getString("NOME_UNIDADE"),
                rSet.getLong("COD_COLABORADOR_ULTIMA_ATUALIZACAO"),
                rSet.getDouble("TOLERANCIA_CALIBRAGEM"),
                rSet.getDouble("TOLERANCIA_INSPECAO"),
                rSet.getDouble("SULCO_MINIMO_RECAPAGEM"),
                rSet.getDouble("SULCO_MINIMO_DESCARTE"),
                rSet.getInt("PERIODO_AFERICAO_PRESSAO"),
                rSet.getInt("PERIODO_AFERICAO_SULCO"));
    }

    @NotNull
    public static ConfiguracaoCronogramaServicoHistorico createConfiguracaoAberturaServicoHistorico(
            @NotNull final ResultSet rSet) throws Throwable {
        return new ConfiguracaoCronogramaServicoHistorico(
                rSet.getString("NOME_UNIDADE"),
                rSet.getString("NOME_COLABORADOR"),
                rSet.getObject("DATA_HORA_ALTERACAO", LocalDateTime.class),
                rSet.getDouble("TOLERANCIA_CALIBRAGEM"),
                rSet.getDouble("TOLERANCIA_INSPECAO"),
                rSet.getDouble("SULCO_MINIMO_RECAPAGEM"),
                rSet.getDouble("SULCO_MINIMO_DESCARTE"),
                rSet.getInt("PERIODO_AFERICAO_PRESSAO"),
                rSet.getInt("PERIODO_AFERICAO_SULCO"),
                rSet.getBoolean("ATUAL"));
    }

    @NotNull
    public static ConfiguracaoTipoVeiculoAferivel createConfiguracaoTipoVeiculoAfericao(
            @NotNull final ResultSet rSet) throws Throwable {
        final ConfiguracaoTipoVeiculoAferivel config = new ConfiguracaoTipoVeiculoAferivel();
        config.setCodigo(NullIf.equalOrLess(rSet.getLong("COD_CONFIGURACAO"), 0));
        config.setCodUnidade(NullIf.equalOrLess(rSet.getLong("COD_UNIDADE_CONFIGURACAO"), 0));
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