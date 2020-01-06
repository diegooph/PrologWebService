package br.com.zalf.prolog.webservice.integracao.api.controlejornada;

import br.com.zalf.prolog.webservice.commons.util.NullIf;
import br.com.zalf.prolog.webservice.integracao.api.controlejornada.ajustes.model.ApiAcaoAjusteMarcacao;
import br.com.zalf.prolog.webservice.integracao.api.controlejornada.ajustes.model.ApiAjusteMarcacao;
import br.com.zalf.prolog.webservice.integracao.api.controlejornada._model.ApiCoordenadasMarcacao;
import br.com.zalf.prolog.webservice.integracao.api.controlejornada._model.ApiFonteDataHora;
import br.com.zalf.prolog.webservice.integracao.api.controlejornada._model.ApiMarcacao;
import br.com.zalf.prolog.webservice.integracao.api.controlejornada._model.ApiTipoInicioFim;
import br.com.zalf.prolog.webservice.integracao.api.controlejornada.tipomarcacao._model.ApiTipoMarcacao;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Created on 29/08/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class ApiMarcacaoCreator {

    private ApiMarcacaoCreator() {
        throw new IllegalStateException(ApiMarcacaoCreator.class.getSimpleName() + " cannot be instantiated!");
    }

    @NotNull
    public static ApiTipoMarcacao createTipoMarcacao(@NotNull final ResultSet rSet) throws SQLException {
        return new ApiTipoMarcacao(
                rSet.getLong("COD_EMPRESA"),
                rSet.getLong("COD_UNIDADE"),
                rSet.getLong("CODIGO"),
                rSet.getString("NOME"),
                rSet.getString("ICONE"),
                Duration.ofMinutes(rSet.getLong("TEMPO_RECOMENDADO_EM_MINUTOS")),
                Duration.ofMinutes(rSet.getLong("TEMPO_ESTOURO_EM_MINUTOS")),
                rSet.getObject("HORARIO_SUGERIDO_MARCAR", LocalTime.class),
                rSet.getBoolean("IS_TIPO_JORNADA"),
                rSet.getBoolean("DESCONTA_JORNADA_BRUTA"),
                rSet.getBoolean("DESCONTA_JORNADA_LIQUIDA"),
                rSet.getBoolean("STATUS_ATIVO"));
    }

    @NotNull
    public static ApiMarcacao createMarcacao(@NotNull final ResultSet rSet) throws SQLException {
        return new ApiMarcacao(
                rSet.getLong("COD_UNIDADE"),
                rSet.getLong("CODIGO"),
                NullIf.equalOrLess(rSet.getLong("COD_MARCACAO_VINCULO"), 0L),
                rSet.getLong("COD_TIPO_MARCACAO"),
                rSet.getString("CPF_COLABORADOR"),
                ApiTipoInicioFim.fromString(rSet.getString("TIPO_MARCACAO")),
                rSet.getObject("DATA_HORA_MARCACAO_UTC", LocalDateTime.class),
                ApiFonteDataHora.fromString(rSet.getString("FONTE_DATA_HORA")),
                rSet.getString("JUSTIFICATIVA_TEMPO_RECOMENDADO"),
                rSet.getString("JUSTIFICATIVA_ESTOURO"),
                createCoordenadasMarcacao(rSet),
                rSet.getObject("DATA_HORA_SINCRONIZACAO_UTC", LocalDateTime.class),
                rSet.getString("DEVICE_IMEI"),
                rSet.getString("DEVICE_ID"),
                rSet.getString("MARCA_DEVICE"),
                rSet.getString("MODELO_DEVICE"),
                NullIf.equalOrLess(rSet.getInt("VERSAO_APP_MOMENTO_MARCACAO"), 0),
                NullIf.equalOrLess(rSet.getInt("VERSAO_APP_MOMENTO_SINCRONIZACAO"), 0),
                rSet.getLong("DEVICE_UPTIME_REALIZACAO_MILLIS"),
                rSet.getLong("DEVICE_UPTIME_SINCRONIZACAO_MILLIS"),
                NullIf.equalOrLess(rSet.getInt("ANDROID_API_VERSION"), 0),
                rSet.getBoolean("STATUS_ATIVO"));
    }

    @NotNull
    public static ApiAjusteMarcacao createAjusteMarcacao(@NotNull final ResultSet rSet) throws SQLException {
        return new ApiAjusteMarcacao(
                rSet.getLong("CODIGO_EDICAO"),
                rSet.getLong("COD_JUSTIFICATIVA_SELECIONADA_AJUSTE"),
                rSet.getString("JUSTIFICATIVA_SELECIONADA_AJUSTE"),
                rSet.getString("OBSERVACAO_AJUSTE_MARCACAO"),
                ApiAcaoAjusteMarcacao.fromString(rSet.getString("ACAO_AJUSTE_MARCACAO")),
                rSet.getString("CPF_COLABORADOR_AJUSTE"),
                rSet.getObject("DATA_HORA_AJUSTE_UTC", LocalDateTime.class),
                createMarcacao(rSet));
    }

    @Nullable
    private static ApiCoordenadasMarcacao createCoordenadasMarcacao(@NotNull final ResultSet rSet) throws SQLException {
        final String latitude = rSet.getString("LATITUDE_MARCACAO");
        final String longitude = rSet.getString("LONGITUDE_MARCACAO");
        return (latitude == null || longitude == null) ? null : new ApiCoordenadasMarcacao(latitude, longitude);
    }
}
