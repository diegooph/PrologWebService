package br.com.zalf.prolog.webservice.integracao.api.controlejornada;

import br.com.zalf.prolog.webservice.integracao.api.controlejornada.model.ApiTipoMarcacao;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalTime;

/**
 * Created on 29/08/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
final class ApiMarcacaoCreator {

    private ApiMarcacaoCreator() {
        throw new IllegalStateException(ApiMarcacaoCreator.class.getSimpleName() + "cannot be instantiated!");
    }

    @NotNull
    static ApiTipoMarcacao createTipoMarcacao(@NotNull final ResultSet rSet) throws SQLException {
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
}
