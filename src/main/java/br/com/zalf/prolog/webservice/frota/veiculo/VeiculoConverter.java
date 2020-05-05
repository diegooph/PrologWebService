package br.com.zalf.prolog.webservice.frota.veiculo;

import br.com.zalf.prolog.webservice.frota.veiculo.model.VeiculoListagem;
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
                 rSet.getLong("COD_EIXOS"),
                 rSet.getString("TIPO"),
                 rSet.getString("MARCA"),
                 rSet.getLong("COD_MARCA"), placa1, cod_unidade1, km1, status_ativo1, cod_tipo1, cod_modelo1, cod_eixos2, data_hora_cadastro1, cod_unidade_cadastro1, codigo1, cod_empresa1, cod_diagrama1, numero_frota1, cod_regional_alocado1, modelo1, eixos1, dianteiro1, traseiro1, cod_eixos11, tipo1, marca1, cod_marca1);
    }
}
