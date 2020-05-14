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
                rSet.getLong("CODIGO"),
                rSet.getString("PLACA"),
                rSet.getLong("COD_UNIDADE"),
                rSet.getString("KM"),
                rSet.getString("STATUS_ATIVO"),
                rSet.getLong("COD_TIPO"),
                rSet.getLong("COD_MODELO"),
                rSet.getLong("COD_DIAGRAMA"),
                rSet.getString("IDENTIFICADOR_FROTA"),
                rSet.getLong("COD_REGIONAL_ALOCADO"),
                rSet.getString("MODELO"),
                rSet.getString("NOME_DIAGRAMA"),
                rSet.getString("DIANTEIRO"),
                rSet.getString("TRASEIRO"),
                rSet.getString("TIPO"),
                rSet.getString("MARCA"),
                rSet.getLong("COD_MARCA"));
    }

    public static VeiculoVisualizacao createVeiculoVisualizacao(@NotNull final ResultSet rSet) throws SQLException {
        return new VeiculoVisualizacao(
                rSet.getLong("CODIGO"),
                rSet.getString("PLACA"),
                rSet.getLong("COD_UNIDADE"),
                rSet.getString("KM"),
                rSet.getString("STATUS_ATIVO"),
                rSet.getLong("COD_TIPO"),
                rSet.getLong("COD_MODELO"),
                rSet.getLong("COD_DIAGRAMA"),
                rSet.getString("IDENTIFICADOR_FROTA"),
                rSet.getLong("COD_REGIONAL_ALOCADO"),
                rSet.getString("MODELO"),
                rSet.getString("NOME_DIAGRAMA"),
                rSet.getString("DIANTEIRO"),
                rSet.getString("TRASEIRO"),
                rSet.getString("TIPO"),
                rSet.getString("MARCA"),
                rSet.getLong("COD_MARCA"));
    }

    public static VeiculoVisualizacaoPneu createVeiculoVisualizacaoPneu(ResultSet rSet) throws SQLException {
        return new VeiculoVisualizacaoPneu(
        rSet.getLong("CODIGO"),
        rSet.getString("CODIGO_CLIENTE"),
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
        rSet.getString("STATUS"),
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
        rSet.getString("NOMENCLATURA"),
        rSet.getLong("COD_VEICULO_APLICADO"),
        rSet.getString("PLACA_APLICADO"));
    }
}
