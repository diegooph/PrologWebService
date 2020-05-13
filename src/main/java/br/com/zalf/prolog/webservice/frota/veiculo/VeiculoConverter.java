package br.com.zalf.prolog.webservice.frota.veiculo;

import br.com.zalf.prolog.webservice.frota.veiculo.model.VeiculoListagem;
import br.com.zalf.prolog.webservice.frota.veiculo.model.VeiculoVisualizacao;
import br.com.zalf.prolog.webservice.frota.veiculo.model.VeiculoVisualizacaoPneu;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

/**
 * Created on 05/05/2020.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public class VeiculoConverter {
    public static VeiculoListagem createVeiculoListagem(@NotNull final ResultSet rSet) throws SQLException {
        return new VeiculoListagem(
                rSet.getString("PLACA"),
                rSet.getLong("COD_UNIDADE"),
                rSet.getString("KM"),
                rSet.getString("STATUS_ATIVO"),
                rSet.getLong("COD_TIPO"),
                rSet.getLong("COD_MODELO"),
                rSet.getLong("COD_EIXOS"),
                rSet.getObject("DATA_HORA_CADASTRO", LocalDateTime.class),
                rSet.getString("COD_UNIDADE_CADASTRO"),
                rSet.getLong("CODIGO"),
                rSet.getLong("COD_EMPRESA"),
                rSet.getLong("COD_DIAGRAMA"),
                rSet.getString("NUMERO_FROTA"),
                rSet.getLong("COD_REGIONAL_ALOCADO"),
                rSet.getString("MODELO"),
                rSet.getString("EIXOS"),
                rSet.getString("DIANTEIRO"),
                rSet.getString("TRASEIRO"),
                rSet.getString("TIPO"),
                rSet.getString("MARCA"),
                rSet.getLong("COD_MARCA"));
    }

    public static VeiculoVisualizacao createVeiculoVisualizacao(@NotNull final ResultSet rSet) throws SQLException {
        return new VeiculoVisualizacao(
                rSet.getString("PLACA"),
                rSet.getLong("COD_UNIDADE"),
                rSet.getString("KM"),
                rSet.getString("STATUS_ATIVO"),
                rSet.getLong("COD_TIPO"),
                rSet.getLong("COD_MODELO"),
                rSet.getLong("COD_EIXOS"),
                rSet.getObject("DATA_HORA_CADASTRO", LocalDateTime.class),
                rSet.getString("COD_UNIDADE_CADASTRO"),
                rSet.getLong("CODIGO"),
                rSet.getLong("COD_EMPRESA"),
                rSet.getLong("COD_DIAGRAMA"),
                rSet.getString("NUMERO_FROTA"),
                rSet.getLong("COD_REGIONAL_ALOCADO"),
                rSet.getString("MODELO"),
                rSet.getString("EIXOS"),
                rSet.getString("DIANTEIRO"),
                rSet.getString("TRASEIRO"),
                rSet.getString("TIPO"),
                rSet.getString("MARCA"),
                rSet.getLong("COD_MARCA"));
    }

    public static VeiculoVisualizacaoPneu createVeiculoVisualizacaoPneu(ResultSet rSet) throws SQLException {
        return new VeiculoVisualizacaoPneu(
        rSet.getLong("CODIGO"),
        rSet.getLong("CODIGO_CLIENTE"),
        rSet.getString("NOME_MARCA_PNEU"),
        rSet.getLong("COD_MARCA_PNEU"),
        rSet.getLong("COD_UNIDADE_ALOCADO"),
        rSet.getLong("COD_REGIONAL_ALOCADO"),
        rSet.getDouble("PRESSAO_ATUAL"),
        rSet.getInt("VIDA_ATUAL"),
        rSet.getInt("VIDA_TOTAL"),
        rSet.getBoolean("PNEU_NOVO_NUNCA_RODADO"),
        rSet.getString("NOME_MODELO_PNEU"),
        rSet.getLong("COD_MODELO_PNEU"),
        rSet.getInt("QT_SULCOS_MODELO_PNEU"),
        rSet.getDouble("ALTURA_SULCOS_MODELO_PNEU"),
        rSet.getInt("ALTURA"),
        rSet.getInt("LARGURA"),
        rSet.getDouble("ARO"),
        rSet.getLong("COD_DIMENSAO"),
        rSet.getDouble("PRESSAO_RECOMENDADA"),
        rSet.getDouble("ALTURA_SULCO_CENTRAL_INTERNO"),
        rSet.getDouble("ALTURA_SULCO_CENTRAL_EXTERNO"),
        rSet.getDouble("ALTURA_SULCO_INTERNO"),
        rSet.getDouble("ALTURA_SULCO_EXTERNO"),
        rSet.getBoolean("STATUS"),
        rSet.getString("DOT"),
        rSet.getDouble("VALOR"),
        rSet.getLong("COD_MODELO_BANDA"),
        rSet.getString("NOME_MODELO_BANDA"),
        rSet.getInt("QT_SULCOS_MODELO_BANDA"),
        rSet.getDouble("ALTURA_SULCOS_MODELO_BANDA"),
        rSet.getLong("COD_MARCA_BANDA"),
        rSet.getString("NOME_MARCA_BANDA"),
        rSet.getDouble("VALOR_BANDA"),
        rSet.getInt("POSICAO_PNEU"),
        rSet.getInt("POSICAO_APLICADO_CLIENTE"),
        rSet.getLong("COD_VEICULO_APLICADO"),
        rSet.getString("PLACA_APLICADO"));
    }
}
