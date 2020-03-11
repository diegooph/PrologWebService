package test.br.com.zalf.prolog.webservice;

import br.com.zalf.prolog.webservice.gente.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.gente.unidade._model.Unidade;
import br.com.zalf.prolog.webservice.commons.FonteDataHora;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.database.DatabaseManager;
import br.com.zalf.prolog.webservice.gente.controlejornada.model.*;
import br.com.zalf.prolog.webservice.gente.controlejornada.tipomarcacao.TipoMarcacao;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 19/03/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public class IntervaloTest extends DatabaseConnection {

    @Before
    public void initialize() {
        DatabaseManager.init();
    }

    @Test
    public void testBuscaIntervalosAgrupados() {
        final List<IntervaloMarcacao> marcacoes = getTodasMarcacoes();
        Assert.assertNotNull(marcacoes);
        Assert.assertTrue(!marcacoes.isEmpty());

        final List<IntervaloAgrupadoTeste> intervalosAgrupados = getTodasMarcacoesAgrupadas();
        Assert.assertNotNull(intervalosAgrupados);
        Assert.assertTrue(!intervalosAgrupados.isEmpty());

        Assert.assertTrue(marcacoes.size() != intervalosAgrupados.size());

        checkEquals(marcacoes, intervalosAgrupados);
    }

    private void checkEquals(final List<IntervaloMarcacao> marcacoes, final List<IntervaloAgrupadoTeste> intervalosAgrupados) {
        for (final IntervaloMarcacao marcacao : marcacoes) {
            Assert.assertNotNull(marcacao);
            final IntervaloAgrupadoTeste agrupado = findByCod(intervalosAgrupados, marcacao.getCodigo());
            Assert.assertNotNull(agrupado);

            if (agrupado.getCodigoMarcacaoInicio() != null
                    && agrupado.getCodigoMarcacaoInicio().equals(marcacao.getCodigo())) {
                // INÍCIO.
                // Não precisamos verificar justificativa estouro e tempo recomendado porque marcações de início
                // não possuem esses campos.
                System.out.println(String.format("Código marcação início: %d", agrupado.getCodigoMarcacaoInicio()));
                Assert.assertEquals((marcacao.getCodigo()), agrupado.getCodigoMarcacaoInicio());
                Assert.assertEquals((marcacao.getCpfColaborador()), agrupado.getColaborador().getCpf());
                Assert.assertEquals((marcacao.getCodUnidade()), agrupado.getColaborador().getUnidade().getCodigo());
                Assert.assertEquals((marcacao.getCodTipoIntervalo()), agrupado.getTipo().getCodigo());
                Assert.assertEquals((marcacao.getDataHoraMaracao()), agrupado.getDataHoraInicio());
                Assert.assertEquals((marcacao.getFonteDataHora()), agrupado.getFonteDataHoraInicio());
                Assert.assertEquals((marcacao.getLocalizacaoMarcacao()), agrupado.getLocalizacaoInicio());
            } else if (agrupado.getCodigoMarcacaoFim() != null
                    && agrupado.getCodigoMarcacaoFim().equals(marcacao.getCodigo())) {
                System.out.println(String.format("Código marcação fim: %d", agrupado.getCodigoMarcacaoFim()));
                // FIM.
                Assert.assertEquals((marcacao.getCodigo()), agrupado.getCodigoMarcacaoFim());
                Assert.assertEquals((marcacao.getCpfColaborador()), agrupado.getColaborador().getCpf());
                Assert.assertEquals((marcacao.getCodUnidade()), agrupado.getColaborador().getUnidade().getCodigo());
                Assert.assertEquals((marcacao.getCodTipoIntervalo()), agrupado.getTipo().getCodigo());
                Assert.assertEquals((marcacao.getDataHoraMaracao()), agrupado.getDataHoraFim());
                Assert.assertEquals((marcacao.getFonteDataHora()), agrupado.getFonteDataHoraFim());
                Assert.assertEquals((marcacao.getLocalizacaoMarcacao()), agrupado.getLocalizacaoFim());
                Assert.assertEquals((marcacao.getJustificativaEstouro()), agrupado.getJustificativaEstouro());
                Assert.assertEquals((marcacao.getJustificativaTempoRecomendado()), agrupado.getJustificativaTempoRecomendado());
            } else {
                throw new IllegalStateException();
            }
        }
    }

    @NotNull
    private IntervaloAgrupadoTeste findByCod(@NotNull final List<IntervaloAgrupadoTeste> intervalos,
                                             @NotNull final Long codigo) {
        for (final IntervaloAgrupadoTeste intervaloAgrupado : intervalos) {
            if (intervaloAgrupado.getCodigoMarcacaoInicio() != null
                    && intervaloAgrupado.getCodigoMarcacaoInicio().equals(codigo)) {
                return intervaloAgrupado;
            } else if (intervaloAgrupado.getCodigoMarcacaoFim() != null
                    && intervaloAgrupado.getCodigoMarcacaoFim().equals(codigo)) {
                return intervaloAgrupado;
            } else if (intervaloAgrupado.getCodigoMarcacaoInicio() == null
                    && intervaloAgrupado.getCodigoMarcacaoFim() == null){
                throw new IllegalStateException("Código de inicio e fim são nulos!");
            }
        }

        throw new IllegalStateException("Nenhum intervalo agrupado encontrado!");
    }

    @SuppressWarnings("Duplicates")
    @NotNull
    private List<IntervaloMarcacao> getTodasMarcacoes() {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet rSet = null;
        final List<IntervaloMarcacao> marcacoes = new ArrayList<>();
        try {
            connection = getConnection();
            statement = connection.prepareStatement("SELECT " +
                    "  I.CODIGO                          AS CODIGO, " +
                    "  I.COD_UNIDADE                     AS COD_UNIDADE, " +
                    "  I.COD_TIPO_INTERVALO              AS COD_TIPO_INTERVALO, " +
                    "  I.CPF_COLABORADOR                 AS CPF_COLABORADOR, " +
                    "  I.DATA_HORA AT TIME ZONE (SELECT TIMEZONE FROM FUNC_GET_TIME_ZONE_UNIDADE(I.COD_UNIDADE))        AS DATA_HORA, " +
                    "  I.TIPO_MARCACAO                   AS TIPO_MARCACAO, " +
                    "  I.FONTE_DATA_HORA                 AS FONTE_DATA_HORA, " +
                    "  I.JUSTIFICATIVA_TEMPO_RECOMENDADO AS JUSTIFICATIVA_TEMPO_RECOMENDADO, " +
                    "  I.JUSTIFICATIVA_ESTOURO           AS JUSTIFICATIVA_ESTOURO, " +
                    "  I.LATITUDE_MARCACAO               AS LATITUDE_MARCACAO, " +
                    "  I.LONGITUDE_MARCACAO              AS LONGITUDE_MARCACAO " +
                    "FROM INTERVALO I;");
            rSet = statement.executeQuery();
            while (rSet.next()) {
                final IntervaloMarcacao intervaloMarcacao = new IntervaloMarcacao();
                intervaloMarcacao.setCodigo(rSet.getLong("CODIGO"));
                intervaloMarcacao.setCodUnidade(rSet.getLong("COD_UNIDADE"));
                intervaloMarcacao.setCpfColaborador(rSet.getLong("CPF_COLABORADOR"));
                intervaloMarcacao.setCodTipoIntervalo(rSet.getLong("COD_TIPO_INTERVALO"));
                intervaloMarcacao.setDataHoraMaracao(rSet.getObject("DATA_HORA", LocalDateTime.class));
                intervaloMarcacao.setFonteDataHora(FonteDataHora.fromString(rSet.getString("FONTE_DATA_HORA")));
                intervaloMarcacao.setTipoMarcacaoIntervalo(TipoInicioFim.fromString(rSet.getString("TIPO_MARCACAO")));
                intervaloMarcacao.setJustificativaTempoRecomendado(rSet.getString("JUSTIFICATIVA_TEMPO_RECOMENDADO"));
                intervaloMarcacao.setJustificativaEstouro(rSet.getString("JUSTIFICATIVA_ESTOURO"));

                final String latitudeMarcacao = rSet.getString("LATITUDE_MARCACAO");
                if (!rSet.wasNull()) {
                    final Localizacao localizacao = new Localizacao();
                    localizacao.setLatitude(latitudeMarcacao);
                    localizacao.setLongitude(rSet.getString("LONGITUDE_MARCACAO"));
                    intervaloMarcacao.setLocalizacaoMarcacao(localizacao);
                }
                marcacoes.add(intervaloMarcacao);
            }
            return marcacoes;
        } catch (SQLException e) {
            System.err.print(e);
            return null;
        } finally {
            closeConnection(connection, statement, rSet);
        }
    }

    private List<IntervaloAgrupadoTeste> getTodasMarcacoesAgrupadas() {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet rSet = null;
        final List<IntervaloAgrupadoTeste> marcacoes = new ArrayList<>();
        try {
            connection = getConnection();
            statement = connection.prepareStatement(QUERY_AGRUPADOS);
            rSet = statement.executeQuery();
            while (rSet.next()) {
                marcacoes.add(createIntervaloAgrupado(rSet));
            }
            return marcacoes;
        } catch (SQLException e) {
            System.err.print(e);
            return null;
        } finally {
            closeConnection(connection, statement, rSet);
        }
    }

    @SuppressWarnings("Duplicates")
    @NotNull
    private IntervaloAgrupadoTeste createIntervaloAgrupado(final ResultSet rSet) throws SQLException {
        final IntervaloAgrupadoTeste intervalo = new IntervaloAgrupadoTeste();

        intervalo.setCodigoMarcacaoInicio(rSet.getLong("codigo_inicio"));
        intervalo.setCodigoMarcacaoFim(rSet.getLong("codigo_fim"));

        final Colaborador colaborador = new Colaborador();
        colaborador.setCpf(rSet.getLong("CPF_COLABORADOR"));
        final Unidade unidade = new Unidade();
        unidade.setCodigo(rSet.getLong("COD_UNIDADE"));
        colaborador.setUnidade(unidade);
        intervalo.setColaborador(colaborador);

        final TipoMarcacao tipoIntervalo = new TipoMarcacao();
        tipoIntervalo.setCodigo(rSet.getLong("COD_TIPO_INTERVALO"));
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
//            intervalo.setTempoDecorrido(Duration.ofMillis(Math.abs(ChronoUnit.MILLIS.between(dataHoraInicio, dataHoraFim))));
        }
        return intervalo;
    }


    private static class IntervaloAgrupadoTeste {
        private Long codigoMarcacaoInicio;
        private Long codigoMarcacaoFim;
        private TipoMarcacao tipo;
        private LocalDateTime dataHoraInicio;
        private FonteDataHora fonteDataHoraInicio;
        private LocalDateTime dataHoraFim;
        private FonteDataHora fonteDataHoraFim;
        private Colaborador colaborador;
        private List<EdicaoIntervalo> edicoes;
        private Duration tempoDecorrido;
        private Localizacao localizacaoInicio;
        private Localizacao localizacaoFim;

        /**
         * Essa justificativa é obrigada a ser fornecida caso o {@link Colaborador} feche o intervalo
         * com um {@link #tempoDecorrido} <b>maior</b> que o {@link TipoMarcacao#tempoLimiteEstouro} do
         * {@link TipoMarcacao} ao qual ele é referente.
         */
        private String justificativaEstouro;

        /**
         * Essa justificativa é obrigada a ser fornecida caso o {@link Colaborador} feche o intervalo
         * com um {@link #tempoDecorrido} <b>menor</b> que o {@link TipoMarcacao#tempoRecomendado} do
         * {@link TipoMarcacao} ao qual ele é referente.
         */
        private String justificativaTempoRecomendado;

        /**
         * Quando um intervalo é criado, ele é por default válido (valido é {@code true}. Porém, alguém
         * do RH de uma {@link Unidade} pode invalidar esse intervalo. Ele não será deletado do BD mas
         * poderá ter uma visualização diferente quando exibido.
         */
        private boolean valido;

        public IntervaloAgrupadoTeste() {

        }

        public Long getCodigoMarcacaoInicio() {
            return codigoMarcacaoInicio;
        }

        public void setCodigoMarcacaoInicio(final Long codigoMarcacaoInicio) {
            this.codigoMarcacaoInicio = codigoMarcacaoInicio;
        }

        public Long getCodigoMarcacaoFim() {
            return codigoMarcacaoFim;
        }

        public void setCodigoMarcacaoFim(final Long codigoMarcacaoFim) {
            this.codigoMarcacaoFim = codigoMarcacaoFim;
        }

        public FonteDataHora getFonteDataHoraInicio() {
            return fonteDataHoraInicio;
        }

        public void setFonteDataHoraInicio(FonteDataHora fonteDataHoraInicio) {
            this.fonteDataHoraInicio = fonteDataHoraInicio;
        }

        public FonteDataHora getFonteDataHoraFim() {
            return fonteDataHoraFim;
        }

        public void setFonteDataHoraFim(FonteDataHora fonteDataHoraFim) {
            this.fonteDataHoraFim = fonteDataHoraFim;
        }


        public TipoMarcacao getTipo() {
            return tipo;
        }

        public void setTipo(TipoMarcacao tipo) {
            this.tipo = tipo;
        }

        public LocalDateTime getDataHoraInicio() {
            return dataHoraInicio;
        }

        public void setDataHoraInicio(LocalDateTime dataHoraInicio) {
            this.dataHoraInicio = dataHoraInicio;
        }

        public LocalDateTime getDataHoraFim() {
            return dataHoraFim;
        }

        public void setDataHoraFim(LocalDateTime dataHoraFim) {
            this.dataHoraFim = dataHoraFim;
        }

        public Localizacao getLocalizacaoInicio() {
            return localizacaoInicio;
        }

        public void setLocalizacaoInicio(Localizacao localizacaoInicio) {
            this.localizacaoInicio = localizacaoInicio;
        }

        public Localizacao getLocalizacaoFim() {
            return localizacaoFim;
        }

        public void setLocalizacaoFim(Localizacao localizacaoFim) {
            this.localizacaoFim = localizacaoFim;
        }

        public Colaborador getColaborador() {
            return colaborador;
        }

        public void setColaborador(Colaborador colaborador) {
            this.colaborador = colaborador;
        }

        public boolean isValido() {
            return valido;
        }

        public void setValido(boolean valido) {
            this.valido = valido;
        }

        public List<EdicaoIntervalo> getEdicoes() {
            return edicoes;
        }

        public void setEdicoes(List<EdicaoIntervalo> edicoes) {
            this.edicoes = edicoes;
        }

        public String getJustificativaEstouro() {
            return justificativaEstouro;
        }

        public void setJustificativaEstouro(String justificativaEstouro) {
            this.justificativaEstouro = justificativaEstouro;
        }

        public String getJustificativaTempoRecomendado() {
            return justificativaTempoRecomendado;
        }

        public void setJustificativaTempoRecomendado(String justificativaTempoRecomendado) {
            this.justificativaTempoRecomendado = justificativaTempoRecomendado;
        }

        public Duration getTempoDecorrido() {
            return tempoDecorrido;
        }

        public void setTempoDecorrido(Duration tempoDecorrido) {
            this.tempoDecorrido = tempoDecorrido;
        }
    }

    private static final String QUERY_AGRUPADOS =
            "WITH ordered_table AS (\n" +
                    "    SELECT\n" +
                    "      row_number()\n" +
                    "      OVER (\n" +
                    "        ORDER BY cpf_colaborador, cod_tipo_intervalo, data_hora ASC ) row_num,\n" +
                    "      *\n" +
                    "    FROM intervalo\n" +
                    "    ORDER BY row_num\n" +
                    "),\n" +
                    "\n" +
                    "    _inits AS (\n" +
                    "      SELECT\n" +
                    "        CASE\n" +
                    "        WHEN t1.tipo_marcacao = 'MARCACAO_INICIO'\n" +
                    "             AND t2.tipo_marcacao = 'MARCACAO_FIM'\n" +
                    "             AND t1.cpf_colaborador = t2.cpf_colaborador\n" +
                    "             AND t1.cod_tipo_intervalo = t2.cod_tipo_intervalo\n" +
                    "          THEN t2.fonte_data_hora\n" +
                    "        END                   AS fonte_data_hora_fim,\n" +
                    "        t1.fonte_data_hora as fonte_data_hora_inicio,\n" +
                    "        CASE\n" +
                    "        WHEN t1.tipo_marcacao = 'MARCACAO_INICIO'\n" +
                    "             AND t2.tipo_marcacao = 'MARCACAO_FIM'\n" +
                    "             AND t1.cpf_colaborador = t2.cpf_colaborador\n" +
                    "             AND t1.cod_tipo_intervalo = t2.cod_tipo_intervalo\n" +
                    "          THEN t2.justificativa_estouro\n" +
                    "        END                   AS justificativa_estouro,\n" +
                    "        CASE\n" +
                    "        WHEN t1.tipo_marcacao = 'MARCACAO_INICIO'\n" +
                    "             AND t2.tipo_marcacao = 'MARCACAO_FIM'\n" +
                    "             AND t1.cpf_colaborador = t2.cpf_colaborador\n" +
                    "             AND t1.cod_tipo_intervalo = t2.cod_tipo_intervalo\n" +
                    "          THEN t2.justificativa_tempo_recomendado\n" +
                    "        END                   AS justificativa_tempo_recomendado,\n" +
                    "        t1.latitude_marcacao as latitude_marcacao_inicio,\n" +
                    "        t1.longitude_marcacao as longitude_marcacao_inicio,\n" +
                    "        CASE\n" +
                    "        WHEN t1.tipo_marcacao = 'MARCACAO_INICIO'\n" +
                    "             AND t2.tipo_marcacao = 'MARCACAO_FIM'\n" +
                    "             AND t1.cpf_colaborador = t2.cpf_colaborador\n" +
                    "             AND t1.cod_tipo_intervalo = t2.cod_tipo_intervalo\n" +
                    "          THEN t2.latitude_marcacao\n" +
                    "        END                   AS latitude_marcacao_fim,\n" +
                    "        CASE\n" +
                    "        WHEN t1.tipo_marcacao = 'MARCACAO_INICIO'\n" +
                    "             AND t2.tipo_marcacao = 'MARCACAO_FIM'\n" +
                    "             AND t1.cpf_colaborador = t2.cpf_colaborador\n" +
                    "             AND t1.cod_tipo_intervalo = t2.cod_tipo_intervalo\n" +
                    "          THEN t2.longitude_marcacao\n" +
                    "        END                   AS longitude_marcacao_fim,\n" +
                    "        t1.cod_unidade,\n" +
                    "        t1.cpf_colaborador,\n" +
                    "        t1.cod_tipo_intervalo,\n" +
                    "        t1.data_hora          AS data_hora_inicio,\n" +
                    "        CASE\n" +
                    "        WHEN t1.tipo_marcacao = 'MARCACAO_INICIO'\n" +
                    "             AND t2.tipo_marcacao = 'MARCACAO_FIM'\n" +
                    "             AND t1.cpf_colaborador = t2.cpf_colaborador\n" +
                    "             AND t1.cod_tipo_intervalo = t2.cod_tipo_intervalo\n" +
                    "          THEN t2.data_hora\n" +
                    "        END                   AS data_hora_fim,\n" +
                    "        t1.codigo             AS codigo_inicio,\n" +
                    "        CASE\n" +
                    "        WHEN t1.tipo_marcacao = 'MARCACAO_INICIO'\n" +
                    "             AND t2.tipo_marcacao = 'MARCACAO_FIM'\n" +
                    "             AND t1.cpf_colaborador = t2.cpf_colaborador\n" +
                    "             AND t1.cod_tipo_intervalo = t2.cod_tipo_intervalo\n" +
                    "          THEN t2.codigo\n" +
                    "        END                   AS codigo_fim\n" +
                    "      FROM ordered_table AS t1\n" +
                    "        LEFT JOIN ordered_table AS t2 ON (\n" +
                    "          t1.row_num = t2.row_num - 1 AND\n" +
                    "          t1.cpf_colaborador = t2.cpf_colaborador AND\n" +
                    "          t1.cod_tipo_intervalo = t2.cod_tipo_intervalo\n" +
                    "          )\n" +
                    "      WHERE t1.tipo_marcacao = 'MARCACAO_INICIO'\n" +
                    "  ),\n" +
                    "\n" +
                    "    _ends AS (\n" +
                    "      SELECT\n" +
                    "        t2.fonte_data_hora as fonte_data_hora_fim,\n" +
                    "        null :: text as fonte_data_hora_inicio,\n" +
                    "        T2.justificativa_estouro,\n" +
                    "        T2.justificativa_tempo_recomendado,\n" +
                    "        null :: text as latitude_marcacao_inicio,\n" +
                    "        null :: text as longitude_marcacao_inicio,\n" +
                    "        t2.latitude_marcacao as latitude_marcacao_fim,\n" +
                    "        t2.longitude_marcacao as longitude_marcacao_fim,\n" +
                    "        t2.cod_unidade,\n" +
                    "        t2.cpf_colaborador,\n" +
                    "        t2.cod_tipo_intervalo,\n" +
                    "        NULL :: TIMESTAMP     AS data_hora_inicio,\n" +
                    "        CASE\n" +
                    "        WHEN (\n" +
                    "               t1.tipo_marcacao = 'MARCACAO_FIM' AND\n" +
                    "               t2.tipo_marcacao = 'MARCACAO_FIM'\n" +
                    "             )\n" +
                    "             OR\n" +
                    "             (t1.tipo_marcacao IS NULL) -- case when first record for cpf_colaborador and " +
                    "cod_tipo_intervalo is an END\n" +
                    "          THEN t2.data_hora\n" +
                    "        END                   AS data_hora_fim,\n" +
                    "        NULL :: INT           AS codigo_inicio,\n" +
                    "        t2.codigo             AS codigo_fim\n" +
                    "      FROM ordered_table AS t1\n" +
                    "        RIGHT JOIN ordered_table AS t2 ON (\n" +
                    "          t1.row_num = t2.row_num - 1 AND\n" +
                    "          t1.cpf_colaborador = t2.cpf_colaborador AND\n" +
                    "          t1.cod_tipo_intervalo = t2.cod_tipo_intervalo\n" +
                    "          )\n" +
                    "      WHERE t2.tipo_marcacao = 'MARCACAO_FIM'\n" +
                    "  )\n" +
                    "\n" +
                    "SELECT fonte_data_hora_inicio, fonte_data_hora_fim, justificativa_estouro, " +
                    "justificativa_tempo_recomendado, latitude_marcacao_inicio, " +
                    "longitude_marcacao_inicio,\n" +
                    "  latitude_marcacao_fim, longitude_marcacao_fim, cod_unidade, cpf_colaborador, " +
                    "cod_tipo_intervalo,\n" +
                    "  data_hora_inicio at time zone (select timezone from func_get_time_zone_unidade(cod_unidade)) " +
                    "as data_hora_inicio,\n" +
                    "  data_hora_fim at time zone (select timezone from func_get_time_zone_unidade(cod_unidade)) as " +
                    "data_hora_fim,\n" +
                    "  codigo_inicio, codigo_fim\n" +
                    "FROM (\n" +
                    "       SELECT *\n" +
                    "       FROM _inits\n" +
                    "       UNION ALL\n" +
                    "       SELECT *\n" +
                    "       FROM _ends\n" +
                    "     ) qry\n" +
                    "WHERE\n" +
                    "  COALESCE(data_hora_inicio, data_hora_fim) IS NOT NULL\n" +
                    "ORDER BY\n" +
                    "  cpf_colaborador,\n" +
                    "  cod_tipo_intervalo,\n" +
                    "  COALESCE(data_hora_inicio, data_hora_fim);";
}