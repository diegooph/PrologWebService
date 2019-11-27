package br.com.zalf.prolog.webservice.entrega.metas;

import br.com.zalf.prolog.webservice.commons.util.date.TimeUtils;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Created on 2019-11-27
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class MetasConverter {

    private MetasConverter() {
        throw new IllegalStateException(MetasConverter.class.getSimpleName() + " cannot be instantiated!");
    }

    @NotNull
    public static Optional<Metas> createMetas(@NotNull final ResultSet rSet) throws SQLException {
        final Metas meta = new Metas(
                rSet.getDouble("META_DEV_HL"),
                rSet.getDouble("META_DEV_PDV"),
                rSet.getDouble("META_TRACKING"),
                (int) rSet.getDouble("META_RAIO_TRACKING") * 1000,
                rSet.getDouble("META_TEMPO_LARGADA_MAPAS"),
                rSet.getDouble("META_TEMPO_ROTA_MAPAS"),
                rSet.getDouble("META_TEMPO_INTERNO_MAPAS"),
                rSet.getDouble("META_JORNADA_LIQUIDA_MAPAS"),
                TimeUtils.durationFromTime(rSet.getTime("META_TEMPO_LARGADA_HORAS")),
                TimeUtils.durationFromTime(rSet.getTime("META_TEMPO_ROTA_HORAS")),
                TimeUtils.durationFromTime(rSet.getTime("META_TEMPO_INTERNO_HORAS")),
                TimeUtils.durationFromTime(rSet.getTime("META_JORNADA_LIQUIDA_HORAS")),
                rSet.getInt("META_CAIXA_VIAGEM"),
                rSet.getDouble("META_DISPERSAO_KM"),
                rSet.getDouble("META_DISPERSAO_TEMPO"),
                rSet.getDouble("META_DEV_NF"));
        return Optional.of(meta);
    }
}