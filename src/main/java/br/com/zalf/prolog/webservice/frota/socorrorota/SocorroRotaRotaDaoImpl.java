package br.com.zalf.prolog.webservice.frota.socorrorota;

import br.com.zalf.prolog.webservice.commons.util.SqlType;
import br.com.zalf.prolog.webservice.commons.util.date.Now;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.frota.socorrorota._model.SocorroRotaAbertura;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Created on 09/12/19.
 *
 * @author Wellington Moraes (https://github.com/wvinim)
 */

public final class SocorroRotaRotaDaoImpl extends DatabaseConnection implements SocorroRotaDao {

    @NotNull
    @Override
    public Long aberturaSocorro(@NotNull final SocorroRotaAbertura socorroRotaAbertura) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_SOCORRO_ROTA_ABERTURA(" +
                    "F_COD_UNIDADE := ?," +
                    "F_COD_COLABORADOR_ABERTURA := ?," +
                    "F_COD_VEICULO_PROBLEMA := ?," +
                    "F_KM_VEICULO_ABERTURA := ?," +
                    "F_COD_PROBLEMA_SOCORRO_ROTA := ?," +
                    "F_DESCRICAO_PROBLEMA := ?::TEXT," +
                    "F_DATA_HORA_ABERTURA := ?," +
                    "F_URL_FOTO_1_ABERTURA := ?::TEXT," +
                    "F_URL_FOTO_2_ABERTURA := ?::TEXT," +
                    "F_URL_FOTO_3_ABERTURA := ?::TEXT," +
                    "F_LATITUDE_ABERTURA := ?::TEXT," +
                    "F_LONGITUDE_ABERTURA := ?::TEXT," +
                    "F_PRECISAO_LOCALIZACAO_ABERTURA_METROS := ?," +
                    "F_ENDERECO_AUTOMATICO := ?," +
                    "F_PONTO_REFERENCIA := ?::TEXT," +
                    "F_VERSAO_APP_MOMENTO_ABERTURA := ?," +
                    "F_DEVICE_ID_ABERTURA := ?::TEXT," +
                    "F_DEVICE_IMEI_ABERTURA := ?::TEXT," +
                    "F_DEVICE_UPTIME_MILLIS_ABERTURA := ?," +
                    "F_ANDROID_API_VERSION_ABERTURA := ?," +
                    "F_MARCA_DEVICE_ABERTURA := ?::TEXT," +
                    "F_MODELO_DEVICE_ABERTURA := ?::TEXT) AS CODIGO;");
            final Long codUnidade = socorroRotaAbertura.getCodUnidade();
            stmt.setLong(1, codUnidade);
            stmt.setLong(2, socorroRotaAbertura.getCodColaborador());
            stmt.setLong(3, socorroRotaAbertura.getCodVeiculoProblema());
            stmt.setLong(4, socorroRotaAbertura.getKmVeiculoAbertura());
            stmt.setLong(5, socorroRotaAbertura.getCodProblemaSocorroRota());
            stmt.setString(6, socorroRotaAbertura.getDescricaoProblema());
            // Ignoramos a data/hora do objeto e usamos a do WS.
            stmt.setObject(7, Now.offsetDateTimeUtc());
            stmt.setString(8, socorroRotaAbertura.getUrlFoto1Abertura());
            stmt.setString(9, socorroRotaAbertura.getUrlFoto2Abertura());
            stmt.setString(10, socorroRotaAbertura.getUrlFoto3Abertura());
            stmt.setString(11, socorroRotaAbertura.getLocalizacao().getLatitude());
            stmt.setString(12, socorroRotaAbertura.getLocalizacao().getLongitude());
            stmt.setObject(13, socorroRotaAbertura.getLocalizacao().getPrecisaoLocalizacaoMetros(), SqlType.NUMERIC.asIntTypeJava());
            stmt.setString(14, socorroRotaAbertura.getEnderecoAutomatico());
            stmt.setString(15, socorroRotaAbertura.getPontoReferencia());
            stmt.setLong(16, socorroRotaAbertura.getVersaoAppAtual());
            stmt.setString(17, socorroRotaAbertura.getDeviceId());
            stmt.setString(18, socorroRotaAbertura.getDeviceImei());
            stmt.setLong(19, socorroRotaAbertura.getDeviceUptimeMillis());
            stmt.setInt(20, socorroRotaAbertura.getAndroidApiVersion());
            stmt.setString(21, socorroRotaAbertura.getMarcaDevice());
            stmt.setString(22, socorroRotaAbertura.getModeloDevice());
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return rSet.getLong("CODIGO");
            } else {
                throw new Throwable("Erro ao abrir uma solitação de socorro");
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }
}