package test.br.com.zalf.prolog.webservice.pilares.gente.controlejornada;

import br.com.zalf.prolog.webservice.gente.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.commons.FonteDataHora;
import br.com.zalf.prolog.webservice.gente.controlejornada.model.Intervalo;
import br.com.zalf.prolog.webservice.gente.controlejornada.model.Localizacao;
import br.com.zalf.prolog.webservice.gente.controlejornada.tipomarcacao.TipoMarcacao;
import org.jetbrains.annotations.NotNull;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import test.br.com.zalf.prolog.webservice.BaseTest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Em setembro/2018 houve uma refatoração na forma como os intervalos são vinculados um ao outro (início com fim).
 * Por conta disso, a func_intervalos_agrupados, responsável pelo matching das marcações, foi totalmente refatorada.
 *
 * A nova estrutura garante o vínculo entre inícios e fins através de uma tabela que mapeia um ao outro. Não é mais algo
 * feito a cada consulta, através da data/hora da marcação.
 *
 * Esse teste é responsável por validar que nenhum vínculo foi alterado ou criado de forma errada (um início vinculado
 * a um fim que não deveria). Para fazer isso, ele busca as marcações utilizando a function antiga e também utilizando
 * a nova e compara todos os resultados das duas para garantir que são iguais.
 *
 * Created on 25/09/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public class AlteracaoFuncIntervalosAgrupados extends BaseTest {
    private static final String BUSCA_ANTIGOS = "SELECT * FROM OLD_FUNC_INTERVALOS_AGRUPADOS(NULL, NULL, NULL);";
    private static final String BUSCA_NOVOS = "SELECT * FROM FUNC_INTERVALOS_AGRUPADOS(NULL, NULL, NULL);";
    private Connection conn;

    @Before
    public void initialize() {
        conn = DatabaseConnection.getConnection();
    }

    @After
    public void destroy() {
        if (conn != null) {
            DatabaseConnection.close(conn);
        }
    }

    @Test
    public void testTodasMarcacoesIguaisComVinculosOk() throws Throwable {
        try {
            final List<Intervalo> antigos = getMarcaoes(conn, BUSCA_ANTIGOS);
            System.out.println("Marcações antigas buscadas");
            final List<Intervalo> novos = getMarcaoes(conn, BUSCA_NOVOS);
            System.out.println("Marcações novas buscadas");

            assertNotNull(antigos);
            assertNotNull(novos);
            assertEquals(antigos.size(), novos.size());

            System.out.println("Tamanho da lista para processar: " + antigos.size());
            for (int i = 0; i < antigos.size(); i++) {
                final Intervalo antigo = antigos.get(i);
                final Intervalo novo = novos.get(i);
                assertNotNull(antigo);
                assertNotNull(novo);

                // Esse objeto Intervalo não seta o código, assim não precisamos comparar este atributo.

                // Comparações do tipo da marcação.
                assertNotNull(antigo.getTipo());
                assertNotNull(novo.getTipo());
                assertEquals(antigo.getTipo().getCodigo(), novo.getTipo().getCodigo());
                assertEquals(antigo.getTipo().getCodigoPorUnidade(), novo.getTipo().getCodigoPorUnidade());

                // Comparações de data e hora.
                assertEquals(antigo.getDataHoraInicio(), novo.getDataHoraInicio());
                assertEquals(antigo.getFonteDataHoraInicio(), novo.getFonteDataHoraInicio());
                assertEquals(antigo.getDataHoraFim(), novo.getDataHoraFim());
                assertEquals(antigo.getFonteDataHoraFim(), novo.getFonteDataHoraFim());
                if ((antigo.getDataHoraInicio() == null && novo.getDataHoraInicio() == null)
                        && (antigo.getDataHoraFim() == null && novo.getDataHoraFim() == null)) {
                    throw new IllegalStateException("O intervalo precisa ter início ou fim (ou ambos).");
                }

                assertEquals(antigo.temInicioEFim(), novo.temInicioEFim());
                // Se tiver início e fim deve ter tempo decorrido.
                if (antigo.temInicioEFim()) {
                    assertNotNull(antigo.getTempoDecorrido());
                    assertNotNull(novo.getTempoDecorrido());
                    assertEquals(antigo.getTempoDecorrido(), novo.getTempoDecorrido());
                }

                // Colaborador.
                assertNotNull(antigo.getColaborador());
                assertNotNull(novo.getColaborador());
                assertEquals(antigo.getColaborador().getCpf(), novo.getColaborador().getCpf());

                // Tipos simples.
                assertEquals(antigo.getLocalizacaoInicio(), novo.getLocalizacaoInicio());
                assertEquals(antigo.getLocalizacaoFim(), novo.getLocalizacaoFim());
                assertEquals(antigo.getJustificativaEstouro(), novo.getJustificativaEstouro());
                assertEquals(antigo.getJustificativaTempoRecomendado(), novo.getJustificativaTempoRecomendado());
                assertEquals(antigo.isValido(), novo.isValido());
            }
        } finally {
            DatabaseConnection.close(conn);
        }
    }

    @NotNull
    private List<Intervalo> getMarcaoes(@NotNull final Connection conn,
                                        @NotNull final String sql) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement(sql);
            rSet = stmt.executeQuery();
            final List<Intervalo> intervalos = new ArrayList<>();
            while (rSet.next()) {
                intervalos.add(createIntervalo(rSet));
            }
            return intervalos;
        } finally {
            DatabaseConnection.close(stmt, rSet);
        }
    }

    @SuppressWarnings("Duplicates")
    @NotNull
    private Intervalo createIntervalo(@NotNull final ResultSet rSet) throws SQLException {
        final Intervalo intervalo = new Intervalo();

        final Colaborador colaborador = new Colaborador();
        colaborador.setCpf(rSet.getLong("CPF_COLABORADOR"));
        intervalo.setColaborador(colaborador);

        final TipoMarcacao tipoIntervalo = new TipoMarcacao();
        tipoIntervalo.setCodigo(rSet.getLong("COD_TIPO_INTERVALO"));
        tipoIntervalo.setCodigoPorUnidade(rSet.getLong("COD_TIPO_INTERVALO_POR_UNIDADE"));
        intervalo.setTipo(tipoIntervalo);

        final OffsetDateTime inicio = rSet.getObject("DATA_HORA_INICIO", OffsetDateTime.class);
        if (inicio != null) {
            intervalo.setDataHoraInicio(inicio.toLocalDateTime());
        }
        final OffsetDateTime fim = rSet.getObject("DATA_HORA_FIM", OffsetDateTime.class);
        if (fim != null) {
            intervalo.setDataHoraFim(fim.toLocalDateTime());
        }
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
//             Precisamos trocar esse cálculo para acontecer no app.
//            intervalo.setTempoDecorrido(Duration.ofMillis(Math.abs(ChronoUnit.MILLIS.between(dataHoraInicio, dataHoraFim))));
        }

        return intervalo;
    }
}