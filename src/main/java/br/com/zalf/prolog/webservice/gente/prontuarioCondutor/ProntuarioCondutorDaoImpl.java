package br.com.zalf.prolog.webservice.gente.prontuarioCondutor;

import br.com.zalf.prolog.webservice.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.commons.util.StringUtils;
import br.com.zalf.prolog.webservice.commons.util.date.DateUtils;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.entrega.ImportUtils;
import br.com.zalf.prolog.webservice.gente.prontuarioCondutor.model.ProntuarioCondutor;
import br.com.zalf.prolog.webservice.gente.prontuarioCondutor.model.Situacao;
import br.com.zalf.prolog.webservice.gente.prontuarioCondutor.model.ocorrencia.*;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.*;

/**
 * Created by Zart on 03/07/2017.
 */
public class ProntuarioCondutorDaoImpl extends DatabaseConnection implements ProntuarioCondutorDao {
    private static final int PRIMEIRA_LINHA_HEADER = 3;
    private static final int LINHA_INICIAL = 5;

    private static final int COLUMN_CPF = 2;
    private static final int COLUMN_STATUS = 4;
    private static final int COLUMN_MOTIVO = 5;
    private static final int COLUMN_PONTUACAO_CNH = 7;
    private static final int COLUMN_VENCIMENTO_CNH = 8;
    private static final int COLUMN_DOCUMENTOS_RS = 9;
    private static final int COLUMN_DOCUMENTOS_EC = 10;
    private static final int COLUMN_DOCUMENTOS_IT = 11;
    private static final int COLUMN_PONTUACAO_PONDERADA = 12;
    private static final int COLUMN_ACIDENTES_TRABALHO_FAI = 14;
    private static final int COLUMN_ACIDENTES_TRABALHO_LTI = 15;
    private static final int COLUMN_ACIDENTES_TRABALHO_MDI = 16;
    private static final int COLUMN_ACIDENTES_TRABALHO_MTI = 17;
    private static final int COLUMN_ACIDENTES_TRANSITO_CAPOTAMENTOS = 18;
    private static final int COLUMN_ACIDENTES_TRANSITO_COLISOES = 19;
    private static final int COLUMN_ACIDENTES_TRANSITO_TOMBAMENTOS = 20;
    private static final int COLUMN_MULTAS_GRAVE = 21;
    private static final int COLUMN_MULTAS_GRAVISSIMA = 22;
    private static final int COLUMN_MULTAS_LEVE = 23;
    private static final int COLUMN_MULTAS_MEDIA = 24;
    private static final int COLUMN_SAC_IMPERICIA = 25;
    private static final int COLUMN_SAC_IMPRUDENCIA = 26;
    private static final int COLUMN_INDISCIPLINA_ADVERTENCIAS = 27;
    private static final int COLUMN_INDISCIPLINA_SUSPENSOES = 28;
    private static final int COLUMN_SAV_IMPERICIA = 29;
    private static final int COLUMN_SAV_IMPRUDENCIA = 30;
    private static final int COLUMN_TELEMETRIA_EXCESSO_VELOCIDADE_1 = 31;
    private static final int COLUMN_TELEMETRIA_EXCESSO_VELOCIDADE_2 = 32;
    private static final int COLUMN_TELEMETRIA_EXCESSO_VELOCIDADE_3 = 33;
    private static final int COLUMN_TELEMETRIA_FORCA_G = 34;
    private static final int COLUMN_TELEMETRIA_FRENAGEM_BRUSCA = 35;
    private static final int COLUMN_TELEMETRIA_POWER_ON = 36;

    public ProntuarioCondutorDaoImpl() {

    }

    @Override
    public boolean insertOrUpdate(String path) throws SQLException, IOException {

        Connection conn = null;
        try {
            conn = getConnection();
            final Reader in = new FileReader(path);
            final List<CSVRecord> tabela = CSVFormat.DEFAULT.withDelimiter(';').parse(in).getRecords();
            final List<List<String>> dados = criarDadosSemColunasVazias(tabela);
            for (int i = 0; i < dados.size(); i++) {
                final ProntuarioCondutor prontuario = createProntuarioFromCsv(dados.get(i));
                if (prontuario != null) {
                    if (updateProntuario(conn, prontuario)) {
                        // Prontuário ja existia e foi atualizado.
                    } else {
                        // Prontuário não existia e foi inserido na base.
                        insertProntuario(conn, prontuario);
                    }
                }
            }
            return true;
        } finally {
            close(conn);
        }
    }

    @Override
    public Double getPontuacaoProntuario(Long cpf) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT pontuacao_ponderada FROM prontuario_condutor_consolidado" +
                    " WHERE cpf_colaborador = ?");
            stmt.setLong(1, cpf);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return rSet.getDouble("PONTUACAO_PONDERADA");
            }
            return null;
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    public List<ProntuarioCondutor> getResumoProntuarios(Long codUnidade, String codEquipe) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT c.cpf, initcap(c.nome) as nome, pc.PONTUACAO_PONDERADA, " +
                    "pc.STATUS, pc.MOTIVO, pc.PONTUACAO, pc.VENCIMENTO_CNH " +
                    "FROM prontuario_condutor_consolidado pc JOIN colaborador c on c.cpf = pc.CPF_COLABORADOR " +
                    "JOIN equipe e ON e.codigo = c.cod_equipe AND e.cod_unidade = c.cod_unidade " +
                    "WHERE c.cod_unidade = ? and e.codigo::text like ? and c.status_ativo = true " +
                    "ORDER BY pc.PONTUACAO_PONDERADA desc, initcap(c.nome) asc");
            stmt.setLong(1, codUnidade);
            stmt.setString(2, codEquipe);
            rSet = stmt.executeQuery();
            final List<ProntuarioCondutor> prontuarios = new ArrayList<>();
            while (rSet.next()) {
                final ProntuarioCondutor prontuario = new ProntuarioCondutor();
                final Colaborador colaborador = new Colaborador();
                colaborador.setCpf(rSet.getLong("CPF"));
                colaborador.setNome(rSet.getString("NOME"));
                prontuario.setColaborador(colaborador);
                prontuario.setPontuacaoTotalPonderada(rSet.getDouble("PONTUACAO_PONDERADA"));
                prontuario.setCnh(new Cnh(
                        rSet.getInt("PONTUACAO"),
                        rSet.getDate("VENCIMENTO_CNH")));
                prontuario.setSituacao(new Situacao(
                        rSet.getString("STATUS"),
                        rSet.getString("MOTIVO")));
                prontuarios.add(prontuario);
            }
            return prontuarios;
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    public ProntuarioCondutor getProntuario(Long cpf) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        final ProntuarioCondutor prontuario = new ProntuarioCondutor();
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM PRONTUARIO_CONDUTOR_CONSOLIDADO WHERE CPF_COLABORADOR = ?;");
            stmt.setLong(1, cpf);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                final Situacao situacao = new Situacao();
                situacao.setStatus(rSet.getString("STATUS"));
                situacao.setMotivo(rSet.getString("MOTIVO"));

                final Cnh cnh = new Cnh();
                cnh.setPontuacao(rSet.getInt("PONTUACAO"));
                cnh.setVencimento(rSet.getDate("VENCIMENTO_CNH"));

                final Documento documento = new Documento();
                documento.setRs(rSet.getString("DOCUMENTOS_RS"));
                documento.setEc(rSet.getString("DOCUMENTOS_EC"));
                documento.setIt(rSet.getString("DOCUMENTOS_IT"));

                prontuario.setPontuacaoTotalPonderada(rSet.getDouble("PONTUACAO_PONDERADA"));

                final AcidentesTrabalho acidentesTrabalho = new AcidentesTrabalho();
                acidentesTrabalho.setFai(rSet.getInt("ACIDENTES_FAI"));
                acidentesTrabalho.setLti(rSet.getInt("ACIDENTES_LTI"));
                acidentesTrabalho.setMdi(rSet.getInt("ACIDENTES_MDI"));
                acidentesTrabalho.setMti(rSet.getInt("ACIDENTES_MTI"));

                final AcidentesTransito acidentesTransito = new AcidentesTransito();
                acidentesTransito.setCapotamentos(rSet.getInt("CAPOTAMENTOS"));
                acidentesTransito.setColisoes(rSet.getInt("COLISOES"));
                acidentesTransito.setTombamentos(rSet.getInt("TOMBAMENTOS"));

                final Multas multas = new Multas();
                multas.setGrave(rSet.getInt("MULTAS_GRAVE"));
                multas.setGravissima(rSet.getInt("MULTAS_GRAVISSIMA"));
                multas.setLeve(rSet.getInt("MULTAS_LEVE"));
                multas.setMedia(rSet.getInt("MULTAS_MEDIA"));

                final Sac sac = new Sac();
                sac.setImpericia(rSet.getInt("SAC_IMPERICIA"));
                sac.setImprudencia(rSet.getInt("SAC_IMPRUDENCIA"));

                final Indisciplina indisciplina = new Indisciplina();
                indisciplina.setAdvertencias(rSet.getInt("ADVERTENCIAS"));
                indisciplina.setSuspensoes(rSet.getInt("SUSPENSOES"));

                final Sav sav = new Sav();
                sav.setImpericia(rSet.getInt("SAV_IMPERICIA"));
                sav.setImprudencia(rSet.getInt("SAV_IMPRUDENCIA"));

                final Telemetria telemetria = new Telemetria();
                telemetria.setExcessoVelocidade1(rSet.getInt("EXCESSO_VELOCIDADE_1"));
                telemetria.setExcessoVelocidade2(rSet.getInt("EXCESSO_VELOCIDADE_2"));
                telemetria.setExcessoVelocidade3(rSet.getInt("EXCESSO_VELOCIDADE_3"));
                telemetria.setForcaG(rSet.getInt("FORCA_G"));
                telemetria.setFrenagemBrusca(rSet.getInt("FRENAGEM_BRUSCA"));
                telemetria.setPowerOn(rSet.getInt("POWER_ON"));

                prontuario.setSituacao(situacao);
                prontuario.setCnh(cnh);
                prontuario.setDocumento(documento);
                prontuario.setAcidentesTrabalho(acidentesTrabalho);
                prontuario.setAcidentesTransito(acidentesTransito);
                prontuario.setMultas(multas);
                prontuario.setSac(sac);
                prontuario.setIndisciplina(indisciplina);
                prontuario.setSav(sav);
                prontuario.setTelemetria(telemetria);
                return prontuario;
            }
        } finally {
            close(conn, stmt, rSet);
        }
        return prontuario;
    }

    @NotNull
    private List<List<String>> criarDadosSemColunasVazias(@NotNull final List<CSVRecord> tabela) {
        if (tabela.isEmpty())
            return Collections.emptyList();

        final Map<Integer, String> colunasVazias = new HashMap<>();
        final int totalColunas = tabela.get(0).size();
        for (int colunaAtual = 0; colunaAtual < totalColunas; colunaAtual++) {
            for (int j = PRIMEIRA_LINHA_HEADER; j < tabela.size(); j++) {
                final CSVRecord linha = tabela.get(j);
                colunasVazias.put(colunaAtual, "");
                if (!StringUtils.isNullOrEmpty(linha.get(colunaAtual))) {
                    colunasVazias.remove(colunaAtual);
                    break;
                }
            }
        }

        final List<List<String>> dados = new ArrayList<>();
        for (int i = LINHA_INICIAL; i < tabela.size(); i++) {
            final List<String> values = new ArrayList<>(totalColunas - colunasVazias.size());
            final CSVRecord linha = tabela.get(i);
            for (int colunaAtual = 0; colunaAtual < totalColunas; colunaAtual++) {
                if (!colunasVazias.containsKey(colunaAtual)) {
                    values.add(linha.get(colunaAtual));
                }
            }
            dados.add(values);
        }

        return dados;
    }

    @Nullable
    private ProntuarioCondutor createProntuarioFromCsv(@Nullable final List<String> linha) {
        final ProntuarioCondutor prontuario = new ProntuarioCondutor();
        if (linha == null || linha.isEmpty() || linha.get(0).isEmpty()) {
            return null;
        } else {
            final Colaborador colaborador = new Colaborador();
            colaborador.setCpf(Long.parseLong(getValue(COLUMN_CPF, linha).replace(".", "").replace("-", "")));

            final Situacao situacao = new Situacao();
            situacao.setStatus(getValue(COLUMN_STATUS, linha));
            situacao.setMotivo(getValue(COLUMN_MOTIVO, linha));

            final Cnh cnh = new Cnh();
            if (getValue(COLUMN_PONTUACAO_CNH, linha).isEmpty()) {
                cnh.setPontuacao(0);
            } else {
                cnh.setPontuacao((int) parseDouble(getValue(COLUMN_PONTUACAO_CNH, linha)));
            }
            // Podemos ignorar o uso de toTimestamp aqui. No banco, a columa é um DATE (que é o que importa para o
            // vencimento de CNH) então não teremos problema com time zone nesse caso.
            cnh.setVencimento(ImportUtils.toTimestamp(getValue(COLUMN_VENCIMENTO_CNH, linha)));

            final Documento documento = new Documento();
            documento.setRs(getValue(COLUMN_DOCUMENTOS_RS, linha));
            documento.setEc(getValue(COLUMN_DOCUMENTOS_EC, linha));
            documento.setIt(getValue(COLUMN_DOCUMENTOS_IT, linha));

            prontuario.setPontuacaoTotalPonderada(parseDouble(getValue(COLUMN_PONTUACAO_PONDERADA, linha)));

            final AcidentesTrabalho acidentesTrabalho = new AcidentesTrabalho();
            acidentesTrabalho.setFai((int) parseDouble(getValue(COLUMN_ACIDENTES_TRABALHO_FAI, linha)));
            acidentesTrabalho.setLti((int) parseDouble(getValue(COLUMN_ACIDENTES_TRABALHO_LTI, linha)));
            acidentesTrabalho.setMdi((int) parseDouble(getValue(COLUMN_ACIDENTES_TRABALHO_MDI, linha)));
            acidentesTrabalho.setMti((int) parseDouble(getValue(COLUMN_ACIDENTES_TRABALHO_MTI, linha)));

            final AcidentesTransito acidentesTransito = new AcidentesTransito();
            acidentesTransito.setCapotamentos((int) parseDouble(getValue(
                    COLUMN_ACIDENTES_TRANSITO_CAPOTAMENTOS, linha)));
            acidentesTransito.setColisoes((int) parseDouble(getValue(COLUMN_ACIDENTES_TRANSITO_COLISOES, linha)));
            acidentesTransito.setTombamentos((int) parseDouble(getValue(
                    COLUMN_ACIDENTES_TRANSITO_TOMBAMENTOS, linha)));

            final Multas multas = new Multas();
            multas.setGrave((int) parseDouble(getValue(COLUMN_MULTAS_GRAVE, linha)));
            multas.setGravissima((int) parseDouble(getValue(COLUMN_MULTAS_GRAVISSIMA, linha)));
            multas.setLeve((int) parseDouble(getValue(COLUMN_MULTAS_LEVE, linha)));
            multas.setMedia((int) parseDouble(getValue(COLUMN_MULTAS_MEDIA, linha)));

            final Sac sac = new Sac();
            sac.setImpericia((int) parseDouble(getValue(COLUMN_SAC_IMPERICIA, linha)));
            sac.setImprudencia((int) parseDouble(getValue(COLUMN_SAC_IMPRUDENCIA, linha)));

            final Indisciplina indisciplina = new Indisciplina();
            indisciplina.setAdvertencias((int) parseDouble(getValue(COLUMN_INDISCIPLINA_ADVERTENCIAS, linha)));
            indisciplina.setSuspensoes((int) parseDouble(getValue(COLUMN_INDISCIPLINA_SUSPENSOES, linha)));

            final Sav sav = new Sav();
            sav.setImpericia((int) parseDouble(getValue(COLUMN_SAV_IMPERICIA, linha)));
            sav.setImprudencia((int) parseDouble(getValue(COLUMN_SAV_IMPRUDENCIA, linha)));

            final Telemetria telemetria = new Telemetria();
            telemetria.setExcessoVelocidade1((int) parseDouble(getValue(
                    COLUMN_TELEMETRIA_EXCESSO_VELOCIDADE_1, linha)));
            telemetria.setExcessoVelocidade2((int) parseDouble(getValue(
                    COLUMN_TELEMETRIA_EXCESSO_VELOCIDADE_2, linha)));
            telemetria.setExcessoVelocidade3((int) parseDouble(getValue(
                    COLUMN_TELEMETRIA_EXCESSO_VELOCIDADE_3, linha)));
            telemetria.setForcaG((int) parseDouble(getValue(COLUMN_TELEMETRIA_FORCA_G, linha)));
            telemetria.setFrenagemBrusca((int) parseDouble(getValue(COLUMN_TELEMETRIA_FRENAGEM_BRUSCA, linha)));
            telemetria.setPowerOn((int) parseDouble(getValue(COLUMN_TELEMETRIA_POWER_ON, linha)));

            prontuario.setColaborador(colaborador);
            prontuario.setSituacao(situacao);
            prontuario.setCnh(cnh);
            prontuario.setDocumento(documento);
            prontuario.setAcidentesTrabalho(acidentesTrabalho);
            prontuario.setAcidentesTransito(acidentesTransito);
            prontuario.setMultas(multas);
            prontuario.setSac(sac);
            prontuario.setIndisciplina(indisciplina);
            prontuario.setSav(sav);
            prontuario.setTelemetria(telemetria);
        }
        return prontuario;
    }

    private String getValue(final int columnIndex, @NotNull final List<String> linha) {
        return linha.get(columnIndex);
    }

    private double parseDouble(@NotNull final String value) {
        return Double.parseDouble(value.replace(",", "."));
    }

    private boolean insertProntuario(@NotNull final Connection conn, @NotNull final ProntuarioCondutor prontuario)
            throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("INSERT INTO PRONTUARIO_CONDUTOR_CONSOLIDADO VALUES (?,?,?,?,?,?,?,?,?,?,?," +
                    "?,?,?,?,?," +
                    "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            stmt.setLong(1, prontuario.getColaborador().getCpf());
            stmt.setString(2, prontuario.getSituacao().getStatus());
            stmt.setString(3, prontuario.getSituacao().getMotivo());
            stmt.setDouble(4, prontuario.getCnh().getPontuacao());
            stmt.setDate(5, DateUtils.toSqlDate(prontuario.getCnh().getVencimento()));
            stmt.setString(6, prontuario.getDocumento().getRs());
            stmt.setString(7, prontuario.getDocumento().getEc());
            stmt.setString(8, prontuario.getDocumento().getIt());
            stmt.setDouble(9, prontuario.getPontuacaoTotalPonderada());
            stmt.setInt(10, prontuario.getAcidentesTrabalho().getFai());
            stmt.setInt(11, prontuario.getAcidentesTrabalho().getLti());
            stmt.setInt(12, prontuario.getAcidentesTrabalho().getMdi());
            stmt.setInt(13, prontuario.getAcidentesTrabalho().getMti());
            stmt.setInt(14, prontuario.getAcidentesTransito().getCapotamentos());
            stmt.setInt(15, prontuario.getAcidentesTransito().getColisoes());
            stmt.setInt(16, prontuario.getAcidentesTransito().getTombamentos());
            stmt.setInt(17, prontuario.getMultas().getLeve());
            stmt.setInt(18, prontuario.getMultas().getMedia());
            stmt.setInt(19, prontuario.getMultas().getGrave());
            stmt.setInt(20, prontuario.getMultas().getGravissima());
            stmt.setInt(21, prontuario.getSac().getImpericia());
            stmt.setInt(22, prontuario.getSac().getImprudencia());
            stmt.setInt(23, prontuario.getSav().getImpericia());
            stmt.setInt(24, prontuario.getSav().getImprudencia());
            stmt.setInt(25, prontuario.getIndisciplina().getAdvertencias());
            stmt.setInt(26, prontuario.getIndisciplina().getSuspensoes());
            stmt.setInt(27, prontuario.getTelemetria().getExcessoVelocidade1());
            stmt.setInt(28, prontuario.getTelemetria().getExcessoVelocidade2());
            stmt.setInt(29, prontuario.getTelemetria().getExcessoVelocidade3());
            stmt.setInt(30, prontuario.getTelemetria().getForcaG());
            stmt.setInt(31, prontuario.getTelemetria().getFrenagemBrusca());
            stmt.setObject(32, OffsetDateTime.now(Clock.systemUTC()));
            stmt.setObject(33, prontuario.getTelemetria().getPowerOn());
            int count = stmt.executeUpdate();
            if (count == 0) {
                throw new SQLException("Erro ao inserir o prontuário do colaborador: " + prontuario.getColaborador()
                        .getCpf());
            }
        } finally {
            close(stmt);
        }
        return true;
    }

    private boolean updateProntuario(@NotNull final Connection conn, @NotNull final ProntuarioCondutor prontuario)
            throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("UPDATE PRONTUARIO_CONDUTOR_CONSOLIDADO SET " +
                    "CPF_COLABORADOR= ? ," +
                    "STATUS = ? ," +
                    "MOTIVO = ? ," +
                    "PONTUACAO = ? ," +
                    "VENCIMENTO_CNH = ? ," +
                    "DOCUMENTOS_RS = ? ," +
                    "DOCUMENTOS_EC = ? ," +
                    "DOCUMENTOS_IT = ? ," +
                    "PONTUACAO_PONDERADA= ? ," +
                    "ACIDENTES_FAI = ? ," +
                    "ACIDENTES_LTI = ? ," +
                    "ACIDENTES_MDI = ? ," +
                    "ACIDENTES_MTI = ? ," +
                    "CAPOTAMENTOS= ? ," +
                    "COLISOES= ? ," +
                    "TOMBAMENTOS = ? ," +
                    "MULTAS_LEVE = ? ," +
                    "MULTAS_MEDIA= ? ," +
                    "MULTAS_GRAVE= ? ," +
                    "MULTAS_GRAVISSIMA = ? ," +
                    "SAC_IMPERICIA = ? ," +
                    "SAC_IMPRUDENCIA = ? ," +
                    "SAV_IMPERICIA = ? ," +
                    "SAV_IMPRUDENCIA = ? ," +
                    "ADVERTENCIAS= ? ," +
                    "SUSPENSOES= ? ," +
                    "EXCESSO_VELOCIDADE_1= ? ," +
                    "EXCESSO_VELOCIDADE_2= ? ," +
                    "EXCESSO_VELOCIDADE_3= ? ," +
                    "FORCA_G = ? ," +
                    "FRENAGEM_BRUSCA = ? ," +
                    "POWER_ON = ? ," +
                    "DATA_ATUALIZACAO= ? " +
                    "WHERE cpf_colaborador = ?;");
            stmt.setLong(1, prontuario.getColaborador().getCpf());
            stmt.setString(2, prontuario.getSituacao().getStatus());
            stmt.setString(3, prontuario.getSituacao().getMotivo());
            stmt.setDouble(4, prontuario.getCnh().getPontuacao());
            stmt.setDate(5, DateUtils.toSqlDate(prontuario.getCnh().getVencimento()));
            stmt.setString(6, prontuario.getDocumento().getRs());
            stmt.setString(7, prontuario.getDocumento().getEc());
            stmt.setString(8, prontuario.getDocumento().getIt());
            stmt.setDouble(9, prontuario.getPontuacaoTotalPonderada());
            stmt.setInt(10, prontuario.getAcidentesTrabalho().getFai());
            stmt.setInt(11, prontuario.getAcidentesTrabalho().getLti());
            stmt.setInt(12, prontuario.getAcidentesTrabalho().getMdi());
            stmt.setInt(13, prontuario.getAcidentesTrabalho().getMti());
            stmt.setInt(14, prontuario.getAcidentesTransito().getCapotamentos());
            stmt.setInt(15, prontuario.getAcidentesTransito().getColisoes());
            stmt.setInt(16, prontuario.getAcidentesTransito().getTombamentos());
            stmt.setInt(17, prontuario.getMultas().getLeve());
            stmt.setInt(18, prontuario.getMultas().getMedia());
            stmt.setInt(19, prontuario.getMultas().getGrave());
            stmt.setInt(20, prontuario.getMultas().getGravissima());
            stmt.setInt(21, prontuario.getSac().getImpericia());
            stmt.setInt(22, prontuario.getSac().getImprudencia());
            stmt.setInt(23, prontuario.getSav().getImpericia());
            stmt.setInt(24, prontuario.getSav().getImprudencia());
            stmt.setInt(25, prontuario.getIndisciplina().getAdvertencias());
            stmt.setInt(26, prontuario.getIndisciplina().getSuspensoes());
            stmt.setInt(27, prontuario.getTelemetria().getExcessoVelocidade1());
            stmt.setInt(28, prontuario.getTelemetria().getExcessoVelocidade2());
            stmt.setInt(29, prontuario.getTelemetria().getExcessoVelocidade3());
            stmt.setInt(30, prontuario.getTelemetria().getForcaG());
            stmt.setInt(31, prontuario.getTelemetria().getFrenagemBrusca());
            stmt.setInt(32, prontuario.getTelemetria().getPowerOn());
            stmt.setObject(33, OffsetDateTime.now(Clock.systemUTC()));
            stmt.setLong(34, prontuario.getColaborador().getCpf());
            final int count = stmt.executeUpdate();
            if (count == 0) {
                return false;
            }
        } finally {
            close(stmt);
        }
        return true;
    }
}