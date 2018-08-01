package br.com.zalf.prolog.webservice.gente.prontuarioCondutor;

import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.commons.util.date.DateUtils;
import br.com.zalf.prolog.webservice.gente.prontuarioCondutor.model.ProntuarioCondutor;
import br.com.zalf.prolog.webservice.gente.prontuarioCondutor.model.Situacao;
import br.com.zalf.prolog.webservice.gente.prontuarioCondutor.model.ocorrencia.*;
import br.com.zalf.prolog.webservice.entrega.ImportUtils;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zart on 03/07/2017.
 */
public class ProntuarioCondutorDaoImpl extends DatabaseConnection implements ProntuarioCondutorDao {
    private static final int LINHA_INICIAL = 5;
    private static final int LINHA_VALIDACAO_1 = 3;
    private static final int LINHA_VALIDACAO_2 = 4;

    private static final int COLUMN_CPF = 1;
    private static final int COLUMN_STATUS = 3;
    private static final int COLUMN_MOTIVO = 4;
    private static final int COLUMN_PONTUACAO_CNH = 6;
    private static final int COLUMN_VENCIMENTO_CNH = 7;
    private static final int COLUMN_DOCUMENTOS_RS = 8;
    private static final int COLUMN_DOCUMENTOS_EC = 9;
    private static final int COLUMN_DOCUMENTOS_IT = 10;
    private static final int COLUMN_PONTUACAO_PONDERADA = 11;
    private static final int COLUMN_ACIDENTES_TRABALHO_FAI = 13;
    private static final int COLUMN_ACIDENTES_TRABALHO_LTI = 14;
    private static final int COLUMN_ACIDENTES_TRABALHO_MDI = 15;
    private static final int COLUMN_ACIDENTES_TRABALHO_MTI = 16;
    private static final int COLUMN_ACIDENTES_TRANSITO_CAPOTAMENTOS = 17;
    private static final int COLUMN_ACIDENTES_TRANSITO_COLISOES = 18;
    private static final int COLUMN_ACIDENTES_TRANSITO_TOMBAMENTOS = 19;
    private static final int COLUMN_MULTAS_GRAVE = 20;
    private static final int COLUMN_MULTAS_GRAVISSIMA = 21;
    private static final int COLUMN_MULTAS_LEVE = 22;
    private static final int COLUMN_MULTAS_MEDIA = 23;
    private static final int COLUMN_SAC_IMPERICIA = 24;
    private static final int COLUMN_SAC_IMPRUDENCIA = 25;
    private static final int COLUMN_INDISCIPLINA_ADVERTENCIAS = 26;
    private static final int COLUMN_INDISCIPLINA_SUSPENSOES = 27;
    private static final int COLUMN_SAV_IMPERICIA = 28;
    private static final int COLUMN_SAV_IMPRUDENCIA = 29;
    private static final int COLUMN_TELEMETRIA_EXCESSO_VELOCIDADE_1 = 30;
    private static final int COLUMN_TELEMETRIA_EXCESSO_VELOCIDADE_2 = 31;
    private static final int COLUMN_TELEMETRIA_EXCESSO_VELOCIDADE_3 = 32;
    private static final int COLUMN_TELEMETRIA_FORCA_G = 33;
    private static final int COLUMN_TELEMETRIA_FRENAGEM_BRUSCA = 34;
    private List<Integer> indices;

    public ProntuarioCondutorDaoImpl() {

    }

    @Override
    public boolean insertOrUpdate(String path) throws SQLException, IOException, ParseException {

        Connection conn = null;
        try {
            conn = getConnection();
            final Reader in = new FileReader(path);
            final List<CSVRecord> tabela = CSVFormat.DEFAULT.withDelimiter(';').parse(in).getRecords();
            createIndices(tabela);
            for (int i = LINHA_INICIAL; i < tabela.size(); i++) {
                final ProntuarioCondutor prontuario = createProntuarioFromCsv(tabela.get(i));
                if (prontuario != null) {
                    if (updateProntuario(prontuario, conn)) {
                        // Prontuário ja existia e foi atualizado
//                        Log.d(TAG, "update prontuarioCondutor: " + prontuario.getColaborador().getCpf());
                    } else {
                        // Mapa não existia e foi inserido na base
                        insertProntuario(prontuario, conn);
//                        Log.d(TAG, "insert prontuarioCondutor: " + prontuario.getColaborador().getCpf());
                    }
                }
            }
            return true;
        } finally {
            closeConnection(conn, null, null);
        }
    }

    private void createIndices(List<CSVRecord> tabela) {
        indices = new ArrayList<>();
        final CSVRecord linhaValidacao1 = tabela.get(LINHA_VALIDACAO_1);
        final CSVRecord linhaValidacao2 = tabela.get(LINHA_VALIDACAO_2);
        for (int i = 0; i < linhaValidacao1.size(); i++) {
            if (!linhaValidacao1.get(i).isEmpty() || !linhaValidacao2.get(i).isEmpty()) {
                indices.add(i);
            }
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
            closeConnection(conn, stmt, rSet);
        }
    }

    @Override
    public List<ProntuarioCondutor> getResumoProntuarios(Long codUnidade, String codEquipe) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        final List<ProntuarioCondutor> prontuarios = new ArrayList<>();
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
            closeConnection(conn, stmt, rSet);
        }
    }

    private ProntuarioCondutor createProntuarioFromCsv(CSVRecord linha) throws ParseException {
        final ProntuarioCondutor prontuario = new ProntuarioCondutor();
        if (linha.get(0).isEmpty()) {
            return null;
        } else {
            final Colaborador colaborador = new Colaborador();
            colaborador.setCpf(Long.parseLong(linha.get(indices.get(COLUMN_CPF)).replace(".", "").replace("-", "")));

            final Situacao situacao = new Situacao();
            situacao.setStatus(linha.get(indices.get(COLUMN_STATUS)));
            situacao.setMotivo(linha.get(indices.get(COLUMN_MOTIVO)));

            final Cnh cnh = new Cnh();
            if (linha.get(indices.get(COLUMN_PONTUACAO_CNH)).isEmpty()) {
                cnh.setPontuacao(0);
            } else {
                cnh.setPontuacao(Integer.parseInt(linha.get(indices.get(COLUMN_PONTUACAO_CNH))));
            }
            // Podemos ignorar o uso de toTimestamp aqui. No banco, a columa é um DATE (que é o que importa para o
            // vencimento de CNH) então não teremos problema com time zone nesse caso.
            cnh.setVencimento(ImportUtils.toTimestamp(linha.get(indices.get(COLUMN_VENCIMENTO_CNH))));

            final Documento documento = new Documento();
            documento.setRs(linha.get(indices.get(COLUMN_DOCUMENTOS_RS)));
            documento.setEc(linha.get(indices.get(COLUMN_DOCUMENTOS_EC)));
            documento.setIt(linha.get(indices.get(COLUMN_DOCUMENTOS_IT)));

            prontuario.setPontuacaoTotalPonderada(Double.parseDouble(linha.get(indices.get
                    (COLUMN_PONTUACAO_PONDERADA)).replace(",", ".")));

            final AcidentesTrabalho acidentesTrabalho = new AcidentesTrabalho();
            acidentesTrabalho.setFai(Integer.parseInt(linha.get(indices.get(COLUMN_ACIDENTES_TRABALHO_FAI))));
            acidentesTrabalho.setLti(Integer.parseInt(linha.get(indices.get(COLUMN_ACIDENTES_TRABALHO_LTI))));
            acidentesTrabalho.setMdi(Integer.parseInt(linha.get(indices.get(COLUMN_ACIDENTES_TRABALHO_MDI))));
            acidentesTrabalho.setMti(Integer.parseInt(linha.get(indices.get(COLUMN_ACIDENTES_TRABALHO_MTI))));

            final AcidentesTransito acidentesTransito = new AcidentesTransito();
            acidentesTransito.setCapotamentos(Integer.parseInt(linha.get(indices.get
                    (COLUMN_ACIDENTES_TRANSITO_CAPOTAMENTOS))));
            acidentesTransito.setColisoes(Integer.parseInt(linha.get(indices.get(COLUMN_ACIDENTES_TRANSITO_COLISOES))));
            acidentesTransito.setTombamentos(Integer.parseInt(linha.get(indices.get
                    (COLUMN_ACIDENTES_TRANSITO_TOMBAMENTOS))));

            final Multas multas = new Multas();
            multas.setGrave(Integer.parseInt(linha.get(indices.get(COLUMN_MULTAS_GRAVE))));
            multas.setGravissima(Integer.parseInt(linha.get(indices.get(COLUMN_MULTAS_GRAVISSIMA))));
            multas.setLeve(Integer.parseInt(linha.get(indices.get(COLUMN_MULTAS_LEVE))));
            multas.setMedia(Integer.parseInt(linha.get(indices.get(COLUMN_MULTAS_MEDIA))));

            final Sac sac = new Sac();
            sac.setImpericia(Integer.parseInt(linha.get(indices.get(COLUMN_SAC_IMPERICIA))));
            sac.setImprudencia(Integer.parseInt(linha.get(indices.get(COLUMN_SAC_IMPRUDENCIA))));

            final Indisciplina indisciplina = new Indisciplina();
            indisciplina.setAdvertencias(Integer.parseInt(linha.get(indices.get(COLUMN_INDISCIPLINA_ADVERTENCIAS))));
            indisciplina.setSuspensoes(Integer.parseInt(linha.get(indices.get(COLUMN_INDISCIPLINA_SUSPENSOES))));

            final Sav sav = new Sav();
            sav.setImpericia(Integer.parseInt(linha.get(indices.get(COLUMN_SAV_IMPERICIA))));
            sav.setImprudencia(Integer.parseInt(linha.get(indices.get(COLUMN_SAV_IMPRUDENCIA))));

            final Telemetria telemetria = new Telemetria();
            telemetria.setExcessoVelocidade1(Integer.parseInt(linha.get(indices.get
                    (COLUMN_TELEMETRIA_EXCESSO_VELOCIDADE_1))));
            telemetria.setExcessoVelocidade2(Integer.parseInt(linha.get(indices.get
                    (COLUMN_TELEMETRIA_EXCESSO_VELOCIDADE_2))));
            telemetria.setExcessoVelocidade3(Integer.parseInt(linha.get(indices.get
                    (COLUMN_TELEMETRIA_EXCESSO_VELOCIDADE_3))));
            telemetria.setForcaG(Integer.parseInt(linha.get(indices.get(COLUMN_TELEMETRIA_FORCA_G))));
            telemetria.setFrenagemBrusca(Integer.parseInt(linha.get(indices.get(COLUMN_TELEMETRIA_FRENAGEM_BRUSCA))));

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

    private boolean insertProntuario(ProntuarioCondutor prontuario, Connection conn) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("INSERT INTO PRONTUARIO_CONDUTOR_CONSOLIDADO VALUES (?,?,?,?,?,?,?,?,?,?,?," +
                    "?,?,?,?,?," +
                    "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
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
            int count = stmt.executeUpdate();
            if (count == 0) {
                throw new SQLException("Erro ao inserir o prontuário do colaborador: " + prontuario.getColaborador()
                        .getCpf());
            }
        } finally {
            closeStatement(stmt);
        }
        return true;
    }

    private boolean updateProntuario(ProntuarioCondutor prontuario, Connection connection) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = connection.prepareStatement("UPDATE PRONTUARIO_CONDUTOR_CONSOLIDADO SET " +
                    "CPF_COLABORADOR= ? ,\n" +
                    "STATUS = ? ,\n" +
                    "MOTIVO = ? ,\n" +
                    "PONTUACAO = ? ,\n" +
                    "VENCIMENTO_CNH = ? ,\n" +
                    "DOCUMENTOS_RS = ? ,\n" +
                    "DOCUMENTOS_EC = ? ,\n" +
                    "DOCUMENTOS_IT = ? ,\n" +
                    "PONTUACAO_PONDERADA= ? ,\n" +
                    "ACIDENTES_FAI = ? ,\n" +
                    "ACIDENTES_LTI = ? ,\n" +
                    "ACIDENTES_MDI = ? ,\n" +
                    "ACIDENTES_MTI = ? ,\n" +
                    "CAPOTAMENTOS= ? ,\n" +
                    "COLISOES= ? ,\n" +
                    "TOMBAMENTOS = ? ,\n" +
                    "MULTAS_LEVE = ? ,\n" +
                    "MULTAS_MEDIA= ? ,\n" +
                    "MULTAS_GRAVE= ? ,\n" +
                    "MULTAS_GRAVISSIMA = ? ,\n" +
                    "SAC_IMPERICIA = ? ,\n" +
                    "SAC_IMPRUDENCIA = ? ,\n" +
                    "SAV_IMPERICIA = ? ,\n" +
                    "SAV_IMPRUDENCIA = ? ,\n" +
                    "ADVERTENCIAS= ? ,\n" +
                    "SUSPENSOES= ? ,\n" +
                    "EXCESSO_VELOCIDADE_1= ? ,\n" +
                    "EXCESSO_VELOCIDADE_2= ? ,\n" +
                    "EXCESSO_VELOCIDADE_3= ? ,\n" +
                    "FORCA_G = ? ,\n" +
                    "FRENAGEM_BRUSCA = ? ,\n" +
                    "DATA_ATUALIZACAO= ?\n" +
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
            stmt.setObject(32, OffsetDateTime.now(Clock.systemUTC()));
            stmt.setLong(33, prontuario.getColaborador().getCpf());
            int count = stmt.executeUpdate();
            if (count == 0) {
                return false;
            }
        } finally {
            closeStatement(stmt);
        }
        return true;
    }

    public ProntuarioCondutor getProntuario(Long cpf) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        final ProntuarioCondutor prontuario = new ProntuarioCondutor();
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM PRONTUARIO_CONDUTOR_CONSOLIDADO WHERE CPF_COLABORADOR = ?");
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
            closeConnection(conn, stmt, rSet);
        }
        return prontuario;
    }
}