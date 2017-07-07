package br.com.zalf.prolog.webservice.gente.prontuarioCondutor;

import br.com.zalf.prolog.webservice.DatabaseConnection;
import br.com.zalf.prolog.webservice.colaborador.Colaborador;
import br.com.zalf.prolog.webservice.commons.util.DateUtils;
import br.com.zalf.prolog.webservice.commons.util.L;
import br.com.zalf.prolog.webservice.gente.prontuarioCondutor.model.ProntuarioCondutor;
import br.com.zalf.prolog.webservice.gente.prontuarioCondutor.model.Situacao;
import br.com.zalf.prolog.webservice.gente.prontuarioCondutor.model.ocorrencia.*;
import br.com.zalf.prolog.webservice.imports.ImportUtils;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Zart on 03/07/2017.
 */
public class ProntuarioCondutorDaoImpl extends DatabaseConnection implements ProntuarioCondutorDao {

    public static final String TAG = ProntuarioCondutorDaoImpl.class.getSimpleName();

    private static final int LINHA_INICIAL = 5;
    private static final int COLUMN_CPF = 3;
    private static final int COLUMN_STATUS = 7;
    private static final int COLUMN_MOTIVO = 8;
    private static final int COLUMN_PONTUACAO_CNH = 11;
    private static final int COLUMN_VENCIMENTO_CNH = 12;
    private static final int COLUMN_DOCUMENTOS_RS = 13;
    private static final int COLUMN_DOCUMENTOS_EC = 14;
    private static final int COLUMN_DOCUMENTOS_IT = 15;
    private static final int COLUMN_PONTUACAO_PONDERADA = 16;
    private static final int COLUMN_ACIDENTES_TRABALHO_FAI = 18;
    private static final int COLUMN_ACIDENTES_TRABALHO_LTI = 19;
    private static final int COLUMN_ACIDENTES_TRABALHO_MDI = 20;
    private static final int COLUMN_ACIDENTES_TRABALHO_MTI = 21;
    private static final int COLUMN_ACIDENTES_TRANSITO_CAPOTAMENTOS = 22;
    private static final int COLUMN_ACIDENTES_TRANSITO_COLISOES = 23;
    private static final int COLUMN_ACIDENTES_TRANSITO_TOMBAMENTOS = 24;
    private static final int COLUMN_MULTAS_GRAVE = 25;
    private static final int COLUMN_MULTAS_GRAVISSIMA = 26;
    private static final int COLUMN_MULTAS_LEVE = 27;
    private static final int COLUMN_MULTAS_MEDIA = 28;
    private static final int COLUMN_SAC_IMPERICIA = 29;
    private static final int COLUMN_SAC_IMPRUDENCIA = 30;
    private static final int COLUMN_INDISCIPLINA_ADVERTENCIAS = 31;
    private static final int COLUMN_INDISCIPLINA_SUSPENSOES = 32;
    private static final int COLUMN_SAV_IMPERICIA = 33;
    private static final int COLUMN_SAV_IMPRUDENCIA = 34;
    private static final int COLUMN_TELEMETRIA_EXCESSO_VELOCIDADE_1 = 35;
    private static final int COLUMN_TELEMETRIA_EXCESSO_VELOCIDADE_2 = 36;
    private static final int COLUMN_TELEMETRIA_EXCESSO_VELOCIDADE_3 = 37;
    private static final int COLUMN_TELEMETRIA_FORCA_G = 38;
    private static final int COLUMN_TELEMETRIA_FRENAGEM_BRUSCA = 39;

    public boolean insertOrUpdate(String path) throws SQLException, IOException, ParseException {

        Connection conn = null;
        try {
            conn = getConnection();
            Reader in = new FileReader(path);
            List<CSVRecord> tabela = CSVFormat.DEFAULT.withDelimiter(';').parse(in).getRecords();
            for (int i = LINHA_INICIAL; i < tabela.size(); i++) {
                ProntuarioCondutor prontuario = createProntuarioFromCsv(tabela.get(i));
                if (prontuario != null) {
                    if (updateProntuario(prontuario, conn)) {
                        // Prontuário ja existia e foi atualizado
                        L.d(TAG, "update prontuarioCondutor: " + prontuario.getColaborador().getNome());
                    } else {
                        // Mapa não existia e foi inserido na base
                        insertProntuario(prontuario, conn);
                        L.d(TAG, "insert prontuarioCondutor: " + prontuario.getColaborador().getNome());
                    }
                }
            }
            return true;
        } finally {
            closeConnection(conn, null, null);
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
        }finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    public List<ProntuarioCondutor> getResumoProntuarios(Long codUnidade, String codEquipe) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        List<ProntuarioCondutor> prontuarios = new ArrayList<>();
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT c.cpf, initcap(c.nome) as nome, pc.PONTUACAO_PONDERADA\n" +
                    "FROM prontuario_condutor_consolidado pc JOIN colaborador c on c.cpf = pc.CPF_COLABORADOR\n" +
                    "JOIN equipe e ON e.codigo = c.cod_equipe AND e.cod_unidade = c.cod_unidade\n" +
                    "WHERE c.cod_unidade = ? and e.codigo::text like ?\n" +
                    "ORDER BY pc.PONTUACAO_PONDERADA desc, initcap(c.nome) asc");
            stmt.setLong(1, codUnidade);
            stmt.setString(2, codEquipe);
            rSet = stmt.executeQuery();
            while(rSet.next()) {
                ProntuarioCondutor prontuario = new ProntuarioCondutor();
                Colaborador colaborador = new Colaborador();
                colaborador.setCpf(rSet.getLong("CPF"));
                colaborador.setNome(rSet.getString("NOME"));
                prontuario.setColaborador(colaborador);
                prontuario.setPontuacaoTotalPonderada(rSet.getDouble("PONTUACAO_PONDERADA"));
                prontuarios.add(prontuario);
            }
            return prontuarios;
        }finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    private ProntuarioCondutor createProntuarioFromCsv(CSVRecord linha) throws ParseException {
        ProntuarioCondutor prontuario = new ProntuarioCondutor();
        if (linha.get(0).isEmpty()) {
            return null;
        } else {
            Colaborador colaborador = new Colaborador();
            colaborador.setCpf(Long.parseLong(linha.get(COLUMN_CPF).replace(".", "").replace("-", "")));

            Situacao situacao = new Situacao();
            situacao.setStatus(linha.get(COLUMN_STATUS));
            situacao.setMotivo(linha.get(COLUMN_MOTIVO));

            Cnh cnh = new Cnh();
            cnh.setPontuacao(Integer.parseInt(linha.get(COLUMN_PONTUACAO_CNH)));
            cnh.setVencimento(ImportUtils.toTimestamp(linha.get(COLUMN_VENCIMENTO_CNH)));

            Documento documento = new Documento();
            documento.setRs(linha.get(COLUMN_DOCUMENTOS_RS));
            documento.setEc(linha.get(COLUMN_DOCUMENTOS_EC));
            documento.setIt(linha.get(COLUMN_DOCUMENTOS_IT));

            prontuario.setPontuacaoTotalPonderada(Double.parseDouble(linha.get(COLUMN_PONTUACAO_PONDERADA).replace(",", ".")));

            AcidentesTrabalho acidentesTrabalho = new AcidentesTrabalho();
            acidentesTrabalho.setFai(Integer.parseInt(linha.get(COLUMN_ACIDENTES_TRABALHO_FAI)));
            acidentesTrabalho.setLti(Integer.parseInt(linha.get(COLUMN_ACIDENTES_TRABALHO_LTI)));
            acidentesTrabalho.setMdi(Integer.parseInt(linha.get(COLUMN_ACIDENTES_TRABALHO_MDI)));
            acidentesTrabalho.setMti(Integer.parseInt(linha.get(COLUMN_ACIDENTES_TRABALHO_MTI)));

            AcidentesTransito acidentesTransito = new AcidentesTransito();
            acidentesTransito.setCapotamentos(Integer.parseInt(linha.get(COLUMN_ACIDENTES_TRANSITO_CAPOTAMENTOS)));
            acidentesTransito.setColisoes(Integer.parseInt(linha.get(COLUMN_ACIDENTES_TRANSITO_COLISOES)));
            acidentesTransito.setTombamentos(Integer.parseInt(linha.get(COLUMN_ACIDENTES_TRANSITO_TOMBAMENTOS)));

            Multas multas = new Multas();
            multas.setGrave(Integer.parseInt(linha.get(COLUMN_MULTAS_GRAVE)));
            multas.setGravissima(Integer.parseInt(linha.get(COLUMN_MULTAS_GRAVISSIMA)));
            multas.setLeve(Integer.parseInt(linha.get(COLUMN_MULTAS_LEVE)));
            multas.setMedia(Integer.parseInt(linha.get(COLUMN_MULTAS_MEDIA)));

            Sac sac = new Sac();
            sac.setImpericia(Integer.parseInt(linha.get(COLUMN_SAC_IMPERICIA)));
            sac.setImprudencia(Integer.parseInt(linha.get(COLUMN_SAC_IMPRUDENCIA)));

            Indisciplina indisciplina = new Indisciplina();
            indisciplina.setAdvertencias(Integer.parseInt(linha.get(COLUMN_INDISCIPLINA_ADVERTENCIAS)));
            indisciplina.setSuspensoes(Integer.parseInt(linha.get(COLUMN_INDISCIPLINA_SUSPENSOES)));

            Sav sav = new Sav();
            sav.setImpericia(Integer.parseInt(linha.get(COLUMN_SAV_IMPERICIA)));
            sav.setImprudencia(Integer.parseInt(linha.get(COLUMN_SAV_IMPRUDENCIA)));

            Telemetria telemetria = new Telemetria();
            telemetria.setExcessoVelocidade1(Integer.parseInt(linha.get(COLUMN_TELEMETRIA_EXCESSO_VELOCIDADE_1)));
            telemetria.setExcessoVelocidade2(Integer.parseInt(linha.get(COLUMN_TELEMETRIA_EXCESSO_VELOCIDADE_2)));
            telemetria.setExcessoVelocidade3(Integer.parseInt(linha.get(COLUMN_TELEMETRIA_EXCESSO_VELOCIDADE_3)));
            telemetria.setForcaG(Integer.parseInt(linha.get(COLUMN_TELEMETRIA_FORCA_G)));
            telemetria.setFrenagemBrusca(Integer.parseInt(linha.get(COLUMN_TELEMETRIA_FRENAGEM_BRUSCA)));

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
            stmt = conn.prepareStatement("INSERT INTO PRONTUARIO_CONDUTOR_CONSOLIDADO VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?," +
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
            stmt.setTimestamp(32, DateUtils.toTimestamp(new Date(System.currentTimeMillis())));
            int count = stmt.executeUpdate();
            if (count == 0) {
                throw new SQLException("Erro ao inserir o prontuário do colaborador: " + prontuario.getColaborador().getCpf());
            }
        } finally {
            closeConnection(null, stmt, null);
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
            stmt.setTimestamp(32, DateUtils.toTimestamp(new Date(System.currentTimeMillis())));
            stmt.setLong(33, prontuario.getColaborador().getCpf());
            int count = stmt.executeUpdate();
            if (count == 0) {
                return false;
            }
        } finally {
            closeConnection(null, stmt, null);
        }
        return true;
    }

    public ProntuarioCondutor getProntuario(Long cpf) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        ProntuarioCondutor prontuario = new ProntuarioCondutor();
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM PRONTUARIO_CONDUTOR_CONSOLIDADO WHERE CPF_COLABORADOR = ?");
            stmt.setLong(1, cpf);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                Situacao situacao = new Situacao();
                situacao.setStatus(rSet.getString("STATUS"));
                situacao.setMotivo(rSet.getString("MOTIVO"));

                Cnh cnh = new Cnh();
                cnh.setPontuacao(rSet.getInt("PONTUACAO"));
                cnh.setVencimento(rSet.getDate("VENCIMENTO_CNH"));

                Documento documento = new Documento();
                documento.setRs(rSet.getString("DOCUMENTOS_RS"));
                documento.setEc(rSet.getString("DOCUMENTOS_EC"));
                documento.setIt(rSet.getString("DOCUMENTOS_IT"));

                prontuario.setPontuacaoTotalPonderada(rSet.getDouble("PONTUACAO_PONDERADA"));

                AcidentesTrabalho acidentesTrabalho = new AcidentesTrabalho();
                acidentesTrabalho.setFai(rSet.getInt("ACIDENTES_FAI"));
                acidentesTrabalho.setLti(rSet.getInt("ACIDENTES_LTI"));
                acidentesTrabalho.setMdi(rSet.getInt("ACIDENTES_MDI"));
                acidentesTrabalho.setMti(rSet.getInt("ACIDENTES_MTI"));

                AcidentesTransito acidentesTransito = new AcidentesTransito();
                acidentesTransito.setCapotamentos(rSet.getInt("CAPOTAMENTOS"));
                acidentesTransito.setColisoes(rSet.getInt("COLISOES"));
                acidentesTransito.setTombamentos(rSet.getInt("TOMBAMENTOS"));

                Multas multas = new Multas();
                multas.setGrave(rSet.getInt("MULTAS_GRAVE"));
                multas.setGravissima(rSet.getInt("MULTAS_GRAVISSIMA"));
                multas.setLeve(rSet.getInt("MULTAS_LEVE"));
                multas.setMedia(rSet.getInt("MULTAS_MEDIA"));

                Sac sac = new Sac();
                sac.setImpericia(rSet.getInt("SAC_IMPERICIA"));
                sac.setImprudencia(rSet.getInt("SAC_IMPRUDENCIA"));

                Indisciplina indisciplina = new Indisciplina();
                indisciplina.setAdvertencias(rSet.getInt("ADVERTENCIAS"));
                indisciplina.setSuspensoes(rSet.getInt("SUSPENSOES"));

                Sav sav = new Sav();
                sav.setImpericia(rSet.getInt("SAV_IMPERICIA"));
                sav.setImprudencia(rSet.getInt("SAV_IMPRUDENCIA"));

                Telemetria telemetria = new Telemetria();
                telemetria.setExcessoVelocidade1(rSet.getInt("EXCESSO_VELOCIDADE_1"));
                telemetria.setExcessoVelocidade2(rSet.getInt("EXCESSO_VELOCIDADE_1"));
                telemetria.setExcessoVelocidade3(rSet.getInt("EXCESSO_VELOCIDADE_1"));
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
