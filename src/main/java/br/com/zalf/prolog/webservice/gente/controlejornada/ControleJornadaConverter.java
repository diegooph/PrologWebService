package br.com.zalf.prolog.webservice.gente.controlejornada;

import br.com.zalf.prolog.webservice.gente.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.commons.FonteDataHora;
import br.com.zalf.prolog.webservice.gente.controlejornada.model.Intervalo;
import br.com.zalf.prolog.webservice.gente.controlejornada.model.Localizacao;
import br.com.zalf.prolog.webservice.gente.controlejornada.model.MarcacaoListagem;
import br.com.zalf.prolog.webservice.gente.controlejornada.tipomarcacao.TipoMarcacao;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Created on 25/09/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class ControleJornadaConverter {

    public ControleJornadaConverter() {
        throw new IllegalStateException(ControleJornadaConverter.class.getSimpleName() + " cannot be instantiated!");
    }

    @NotNull
    public static MarcacaoListagem createMarcacaoListagem(@NotNull final ResultSet rSet) throws Throwable {
        return new MarcacaoListagem(
                rSet.getLong("COD_UNIDADE"),
                rSet.getString("NOME_TIPO_INTERVALO"),
                rSet.getString("ICONE_TIPO_INTERVALO"),
                rSet.getString("CPF_COLABORADOR"),
                rSet.getString("NOME_COLABORADOR"),
                rSet.getBoolean("FOI_AJUSTADO_INICIO"),
                rSet.getBoolean("FOI_AJUSTADO_FIM"),
                rSet.getBoolean("STATUS_ATIVO_INICIO"),
                rSet.getBoolean("STATUS_ATIVO_FIM"),
                rSet.getLong("COD_MARCACAO_INICIO"),
                rSet.getLong("COD_MARCACAO_FIM"),
                rSet.getObject("DATA_HORA_INICIO", LocalDateTime.class),
                rSet.getObject("DATA_HORA_FIM", LocalDateTime.class),
                Duration.ofSeconds(rSet.getLong("DURACAO_EM_SEGUNDOS")),
                Duration.ofMinutes(rSet.getLong("TEMPO_RECOMENDADO_MINUTOS")),
                rSet.getString("JUSTIFICATIVA_ESTOURO"),
                rSet.getString("JUSTIFICATIVA_TEMPO_RECOMENDADO"));
    }

    @NotNull
    static Intervalo createIntervalo(@NotNull final ResultSet rSet) throws SQLException {
        final Intervalo intervalo = new Intervalo();

        final Colaborador colaborador = new Colaborador();
        colaborador.setCpf(rSet.getLong("CPF_COLABORADOR"));
        intervalo.setColaborador(colaborador);

        final TipoMarcacao tipoIntervalo = new TipoMarcacao();
        tipoIntervalo.setCodigo(rSet.getLong("COD_TIPO_INTERVALO"));
        tipoIntervalo.setNome(rSet.getString("NOME_TIPO_INTERVALO"));
        intervalo.setTipo(tipoIntervalo);

        intervalo.setDataHoraInicio(rSet.getObject("DATA_HORA_INICIO", LocalDateTime.class));
        intervalo.setDataHoraFim(rSet.getObject("DATA_HORA_FIM", LocalDateTime.class));
        final String fonteDataHoraInicio = rSet.getString("FONTE_DATA_HORA_INICIO");
        if (!rSet.wasNull()) {
            intervalo.setFonteDataHoraInicio(FonteDataHora.fromString(fonteDataHoraInicio));
        }
        final String fonteDataHoraFim = rSet.getString("FONTE_DATA_HORA_FIM");
        if (!rSet.wasNull()) {
            intervalo.setFonteDataHoraFim(FonteDataHora.fromString(fonteDataHoraFim));
        }
        intervalo.setJustificativaTempoRecomendado(rSet.getString("JUSTIFICATIVA_TEMPO_RECOMENDADO"));
        intervalo.setJustificativaEstouro(rSet.getString("JUSTIFICATIVA_ESTOURO"));

        final String latitudeInicio = rSet.getString("LATITUDE_MARCACAO_INICIO");
        if (!rSet.wasNull()) {
            final Localizacao localizacao = new Localizacao();
            localizacao.setLatitude(latitudeInicio);
            localizacao.setLongitude(rSet.getString("LONGITUDE_MARCACAO_INICIO"));
            intervalo.setLocalizacaoInicio(localizacao);
        }

        final String latitudeFim = rSet.getString("LATITUDE_MARCACAO_FIM");
        if (!rSet.wasNull()) {
            final Localizacao localizacao = new Localizacao();
            localizacao.setLatitude(latitudeFim);
            localizacao.setLongitude(rSet.getString("LONGITUDE_MARCACAO_FIM"));
            intervalo.setLocalizacaoFim(localizacao);
        }

        // Cálculo do tempo decorrido.
        final LocalDateTime dataHoraFim = intervalo.getDataHoraFim();
        final LocalDateTime dataHoraInicio = intervalo.getDataHoraInicio();
        if (dataHoraInicio != null && dataHoraFim != null) {
            intervalo.setTempoDecorrido(Duration.ofMillis(Math.abs(ChronoUnit.MILLIS.between(dataHoraInicio, dataHoraFim))));
        } else if (dataHoraFim == null) {
            // TODO: Precisamos trocar esse cálculo para contecer no app.
//            intervalo.setTempoDecorrido(Duration.ofMillis(Math.abs(ChronoUnit.MILLIS.between(dataHoraInicio, dataHoraFim))));
        }

        return intervalo;
    }
}