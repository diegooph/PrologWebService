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
import java.util.Date;
import java.util.List;

/**
 * Created by Zart on 03/07/2017.
 */
public class ProntuarioCondutorDaoImpl extends DatabaseConnection implements ProntuarioCondutorDao {

    public static final String TAG = ProntuarioCondutorDaoImpl.class.getSimpleName();

    public boolean insertOrUpdate(String path) throws SQLException, IOException, ParseException {

        Connection conn = null;
        try {
            conn = getConnection();
            Reader in = new FileReader(path);
            List<CSVRecord> tabela = CSVFormat.DEFAULT.withDelimiter(';').parse(in).getRecords();
            for (int i = 1; i < tabela.size(); i++) {
                ProntuarioCondutor prontuario = createProntuarioFromCsv(tabela.get(i));
                if (prontuario != null) {
                    if (updateProntuario(prontuario, conn)) {
                        // Prontuário ja existia e foi atualizado
                        L.d(TAG, "update prontuarioCondutor: " + prontuario.getColaborador().getNome());
                    } else {
                        L.d(TAG, "insert prontuarioCondutor: " + prontuario.getColaborador().getNome());
                        // Mapa não existia e foi inserido na base
                        insertProntuario(prontuario, conn);
                    }
                }
            }
            return true;
        } finally {
            closeConnection(conn, null, null);
        }
    }

    private ProntuarioCondutor createProntuarioFromCsv(CSVRecord linha) throws ParseException {
        ProntuarioCondutor prontuario = new ProntuarioCondutor();
        if (linha.get(0).isEmpty()) {
            return null;
        } else {
            Colaborador colaborador = new Colaborador();
            colaborador.setCpf(Long.parseLong(linha.get(1).replace(".", "").replace("-", "")));

            Situacao situacao = new Situacao();
            situacao.setStatus(linha.get(3));
            situacao.setMotivo(linha.get(4));

            Cnh cnh = new Cnh();
            cnh.setPontuacao(Integer.parseInt(linha.get(11)));
            cnh.setVencimento(ImportUtils.toTimestamp(linha.get(11)));

            Documento documento = new Documento();
            documento.setRs(linha.get(13));
            documento.setEc(linha.get(14));
            documento.setIt(linha.get(15));

            prontuario.setPontuacaoTotalPonderada(Double.parseDouble(linha.get(16).replace(",",".")));

            AcidentesTrabalho acidentesTrabalho = new AcidentesTrabalho();
            acidentesTrabalho.setFai(Integer.parseInt(linha.get(18)));
            acidentesTrabalho.setLti(Integer.parseInt(linha.get(19)));
            acidentesTrabalho.setMdi(Integer.parseInt(linha.get(20)));
            acidentesTrabalho.setMti(Integer.parseInt(linha.get(21)));

            AcidentesTransito acidentesTransito = new AcidentesTransito();
            acidentesTransito.setCapotamentos(Integer.parseInt(linha.get(22)));
            acidentesTransito.setColisoes(Integer.parseInt(linha.get(23)));
            acidentesTransito.setTombamentos(Integer.parseInt(linha.get(24)));

            Multas multas = new Multas();
            multas.setGrave(Integer.parseInt(linha.get(25)));
            multas.setGravissima(Integer.parseInt(linha.get(26)));
            multas.setLeve(Integer.parseInt(linha.get(27)));
            multas.setMedia(Integer.parseInt(linha.get(28)));

            Sac sac = new Sac();
            sac.setImpericia(Integer.parseInt(linha.get(29)));
            sac.setImprudencia(Integer.parseInt(linha.get(30)));

            Indisciplina indisciplina = new Indisciplina();
            indisciplina.setAdvertencias(Integer.parseInt(linha.get(31)));
            indisciplina.setSuspensoes(Integer.parseInt(linha.get(32)));

            Sav sav = new Sav();
            sav.setImpericia(Integer.parseInt(linha.get(33)));
            sav.setImprudencia(Integer.parseInt(linha.get(34)));

            Telemetria telemetria = new Telemetria();
            telemetria.setExcessoVelocidade1(Integer.parseInt(linha.get(35)));
            telemetria.setExcessoVelocidade2(Integer.parseInt(linha.get(36)));
            telemetria.setExcessoVelocidade3(Integer.parseInt(linha.get(37)));
            telemetria.setForcaG(Integer.parseInt(linha.get(38)));
            telemetria.setFrenagemBrusca(Integer.parseInt(linha.get(39)));

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
                    "MOTIVO TEXT = ? ,\n" +
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
                    "WHERE cpf_colaborador = ?);");
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
            if(rSet.next()) {
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
        }finally {
            closeConnection(conn, stmt, rSet);
        }
        return prontuario;
    }
}
