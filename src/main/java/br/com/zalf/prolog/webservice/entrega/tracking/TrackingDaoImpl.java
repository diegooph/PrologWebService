package br.com.zalf.prolog.webservice.entrega.tracking;

import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.datetime.DateUtils;
import br.com.zalf.prolog.webservice.commons.util.datetime.Now;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

import static br.com.zalf.prolog.webservice.entrega.ImportUtils.toTime;
import static br.com.zalf.prolog.webservice.entrega.ImportUtils.toTimestamp;

public class TrackingDaoImpl extends DatabaseConnection implements TrackingDao {
    private static final String TAG = TrackingDaoImpl.class.getSimpleName();

    public TrackingDaoImpl() {

    }

    /**
     * Método usado para verificar se uma string contém algum número
     *
     * @param str uma String
     * @return um boolean
     */
    private static boolean containsNumber(final String str) {
        return str.matches(".*\\d+.*");
    }

    @Override
    public boolean insertOrUpdateTracking(final String path, final Long codUnidade) throws SQLException, IOException, ParseException {
        Connection conn = null;
        try {
            conn = getConnection();
            final Reader in = new FileReader(path);
            final List<CSVRecord> tabela = CSVFormat.DEFAULT.withDelimiter(';').parse(in).getRecords();
            //List<CSVRecord> tabela = CSVFormat.DEFAULT.parse(in).getRecords();
            for (int i = 1; i < tabela.size(); i++) {
                final TrackingImport tracking = createTracking(tabela.get(i));
                if (tracking != null) {
                    Log.d(TAG, "Entrou no insertOrUpdateTracking, mapa/entrega: " + tracking.mapa + "/" + tracking.codCliente);
                    if (updateTracking(tracking, codUnidade, conn)) {
                        // Linha já existe e será atualizada
                        Log.d(TAG, "Update Tracking, mapa/entrega: " + tracking.mapa + "/" + tracking.codCliente);
                    } else {
                        Log.d(TAG, "insert Tracking, mapa/entrega: " + tracking.mapa + "/" + tracking.codCliente);
                        // Linha não existe e será inserida
                        insertTracking(tracking, codUnidade, conn);
                    }
                }
            }
        } finally {
            closeConnection(conn, null, null);
        }
        return true;
    }

    private boolean insertTracking(final TrackingImport tracking, final Long codUnidade, final Connection conn) throws SQLException {

        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("INSERT INTO TRACKING VALUES("
                    + " ?,  ?,  ?,	?,	?,	?,	?,	?,	?,	?,	?,	?,	?,	?,"
                    + "	?,	?,	?,	?,	?,	?,	?,	?,	?,	?,	?,	?,	?,	?,"
                    + "	?,	?,	?,	?,	?,	?,	?,	?,	?,	?,	?,	?,	?,	?,"
                    + "	?,  ?)");

            stmt.setInt(1, tracking.classe);
            stmt.setDate(2, DateUtils.toSqlDate(tracking.data));
            stmt.setInt(3, tracking.mapa);
            stmt.setString(4, tracking.placa);
            stmt.setInt(5, tracking.codCliente);
            stmt.setInt(6, tracking.seqReal);
            stmt.setInt(7, tracking.seqPlan);
            stmt.setTime(8, tracking.inicioRota);
            stmt.setTime(9, tracking.horarioMatinal);
            stmt.setTime(10, tracking.saidaCDD);
            stmt.setTime(11, tracking.chegadaPDV);
            stmt.setTime(12, tracking.tempoPrevRetorno);
            stmt.setTime(13, tracking.tempoRetorno);
            stmt.setDouble(14, tracking.distPrevRetorno);
            stmt.setDouble(15, tracking.distPercRetorno);
            stmt.setTime(16, tracking.inicioEntrega);
            stmt.setTime(17, tracking.fimEntrega);
            stmt.setTime(18, tracking.fimRota);
            stmt.setTime(19, tracking.entradaCDD);
            stmt.setDouble(20, tracking.caixasCarregadas);
            stmt.setDouble(21, tracking.caixasDevolvidas);
            stmt.setDouble(22, tracking.repasse);
            stmt.setTime(23, tracking.tempoEntrega);
            stmt.setTime(24, tracking.tempoDescarga);
            stmt.setTime(25, tracking.tempoEspera);
            stmt.setTime(26, tracking.tempoAlmoco);
            stmt.setTime(27, tracking.tempoTotalRota);
            stmt.setDouble(28, tracking.dispApontCadastrado);
            stmt.setString(29, tracking.latEntrega);
            stmt.setString(30, tracking.lonEntrega);
            stmt.setInt(31, tracking.unidadeNegocio);
            stmt.setString(32, tracking.transportadora);
            stmt.setString(33, tracking.latClienteApontamento);
            stmt.setString(34, tracking.lonClienteApontamento);
            stmt.setString(35, tracking.latAtualCliente);
            stmt.setString(36, tracking.lonAtualCliente);
            stmt.setDouble(37, tracking.distanciaPrev);
            stmt.setTime(38, tracking.tempoDeslocamento);
            stmt.setDouble(39, tracking.velMedia);
            stmt.setDouble(40, tracking.distanciaPercApontamento);
            stmt.setString(41, tracking.aderenciaSequenciaEntrega);
            stmt.setString(42, tracking.aderenciaJanelaEntrega);
            stmt.setString(43, tracking.pdvLacrado);
            stmt.setLong(44, codUnidade);
            final int count = stmt.executeUpdate();
            if (count == 0) {
                throw new SQLException("Erro ao inserir a tabela");
            }
        } finally {
            closeStatement(stmt);
        }
        return true;
    }

    private boolean updateTracking(final TrackingImport tracking, final Long codUnidade, final Connection conn) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("UPDATE TRACKING "
                    + "SET "
                    + " Classe = ?, "
                    + " Data = ?, "
                    + " Mapa = ?, "
                    + " Placa = ?, "
                    + " Cod_Cliente = ?, "
                    + " Seq_Real = ?, "
                    + " Seq_Plan = ?, "
                    + " Inicio_Rota = ?, "
                    + " Horario_Matinal = ?, "
                    + " Saida_CDD = ?, "
                    + " Chegada_ao_PDV = ?, "
                    + " Tempo_Prev_Retorno = ?, "
                    + " Tempo_Retorno = ?, "
                    + " Dist_Prev_Retorno = ?, "
                    + " Dist_Perc_Retorno = ?, "
                    + " Inicio_Entrega = ?, "
                    + " Fim_Entrega = ?, "
                    + " Fim_Rota = ?, "
                    + " Entrada_CDD = ?, "
                    + " Caixas_carregadas = ?, "
                    + " Caixas_devolvidas = ?, "
                    + " Repasse = ?, "
                    + " Tempo_de_entrega = ?, "
                    + " Tempo_Descarga = ?, "
                    + " Tempo_Espera = ?, "
                    + " Tempo_Almoco = ?, "
                    + " Tempo_total_de_rota = ?, "
                    + " Disp_Apont_Cadastrado = ?, "
                    + " Lat_Entrega = ?, "
                    + " Lon_Entrega = ?, "
                    + " Unidade_Negocio = ?, "
                    + " Transportadora = ?, "
                    + " Lat_Cliente_Apontamento = ?, "
                    + " Lon_Cliente_Apontamento = ?, "
                    + " Lat_Atual_Cliente = ?, "
                    + " Lon_Atual_Cliente = ?, "
                    + " Distancia_Prev = ?, "
                    + " Tempo_Deslocamento = ?, "
                    + " Vel_Media_km_h = ?, "
                    + " Distancia_Perc_Apontamento = ?, "
                    + " Aderencia_Sequencia_Entrega = ?, "
                    + " Aderencia_Janela_Entrega = ?, "
                    + " PDV_Lacrado = ?, "
                    + " cod_unidade = ?, "
                    + "data_hora_import = ? "
                    + " WHERE Mapa = ? AND data = ? AND placa = ? AND cod_cliente =?;");

            stmt.setInt(1, tracking.classe);
            stmt.setDate(2, DateUtils.toSqlDate(tracking.data));
            stmt.setInt(3, tracking.mapa);
            stmt.setString(4, tracking.placa);
            stmt.setInt(5, tracking.codCliente);
            stmt.setInt(6, tracking.seqReal);
            stmt.setInt(7, tracking.seqPlan);
            stmt.setTime(8, tracking.inicioRota);
            stmt.setTime(9, tracking.horarioMatinal);
            stmt.setTime(10, tracking.saidaCDD);
            stmt.setTime(11, tracking.chegadaPDV);
            stmt.setTime(12, tracking.tempoPrevRetorno);
            stmt.setTime(13, tracking.tempoRetorno);
            stmt.setDouble(14, tracking.distPrevRetorno);
            stmt.setDouble(15, tracking.distPercRetorno);
            stmt.setTime(16, tracking.inicioEntrega);
            stmt.setTime(17, tracking.fimEntrega);
            stmt.setTime(18, tracking.fimRota);
            stmt.setTime(19, tracking.entradaCDD);
            stmt.setDouble(20, tracking.caixasCarregadas);
            stmt.setDouble(21, tracking.caixasDevolvidas);
            stmt.setDouble(22, tracking.repasse);
            stmt.setTime(23, tracking.tempoEntrega);
            stmt.setTime(24, tracking.tempoDescarga);
            stmt.setTime(25, tracking.tempoEspera);
            stmt.setTime(26, tracking.tempoAlmoco);
            stmt.setTime(27, tracking.tempoTotalRota);
            stmt.setDouble(28, tracking.dispApontCadastrado);
            stmt.setString(29, tracking.latEntrega);
            stmt.setString(30, tracking.lonEntrega);
            stmt.setInt(31, tracking.unidadeNegocio);
            stmt.setString(32, tracking.transportadora);
            stmt.setString(33, tracking.latClienteApontamento);
            stmt.setString(34, tracking.lonClienteApontamento);
            stmt.setString(35, tracking.latAtualCliente);
            stmt.setString(36, tracking.lonAtualCliente);
            stmt.setDouble(37, tracking.distanciaPrev);
            stmt.setTime(38, tracking.tempoDeslocamento);
            stmt.setDouble(39, tracking.velMedia);
            stmt.setDouble(40, tracking.distanciaPercApontamento);
            stmt.setString(41, tracking.aderenciaSequenciaEntrega);
            stmt.setString(42, tracking.aderenciaJanelaEntrega);
            stmt.setString(43, tracking.pdvLacrado);
            stmt.setLong(44, codUnidade);
            stmt.setTimestamp(45, Now.getTimestampUtc());
            stmt.setInt(46, tracking.mapa);
            stmt.setDate(47, DateUtils.toSqlDate(tracking.data));
            stmt.setString(48, tracking.placa);
            stmt.setInt(49, tracking.codCliente);
            final int count = stmt.executeUpdate();
            if (count == 0) {
                return false;
            }
        } finally {
            closeStatement(stmt);
        }
        return true;
    }

    private TrackingImport createTracking(final CSVRecord linha) throws ParseException {
        if (linha.get(1).isEmpty()) {
            return null;
        }
        final TrackingImport tracking = new TrackingImport();
        if (!String.valueOf(linha.get(0)).trim().isEmpty()) {
            //tracking.classe = Integer.parseInt(linha.get(0));
        }
        if (!String.valueOf(linha.get(1)).trim().isEmpty()) {
            tracking.data = toTimestamp(linha.get(1));
        }
        if (!String.valueOf(linha.get(2)).trim().isEmpty()) {
            tracking.mapa = Integer.parseInt(linha.get(2));
        }
        if (!String.valueOf(linha.get(3)).trim().isEmpty()) {
            tracking.placa = String.valueOf(linha.get(3));
        }
        if (!String.valueOf(linha.get(4)).trim().isEmpty()) {
            tracking.codCliente = Integer.parseInt(linha.get(4));
        }
        if (!String.valueOf(linha.get(5)).trim().isEmpty()) {
            tracking.seqReal = Integer.parseInt(linha.get(5));
        }
        if (!String.valueOf(linha.get(6)).trim().isEmpty()) {
            tracking.seqPlan = Integer.parseInt(linha.get(6));
        }
//				if(!String.valueOf(linha.get(7)).trim().equals(NAO_RELATADO)){
//					tracking.inicioRota = toTime(linha.get(7));
//				}
        if (containsNumber(linha.get(7))) {
            tracking.inicioRota = toTime(linha.get(7));
        }
        if (!String.valueOf(linha.get(8)).trim().isEmpty()) {
            tracking.horarioMatinal = toTime(linha.get(8));
        }
        if (!String.valueOf(linha.get(9)).trim().isEmpty()) {
            tracking.saidaCDD = toTime(linha.get(9));
        }
//				if(!String.valueOf(linha.get(10)).trim().equals(NAO_RELATADO)){
//					tracking.chegadaPDV = toTime(linha.get(10));
//				}
        if (containsNumber(linha.get(10))) {
            tracking.chegadaPDV = toTime(linha.get(10));
        }
//				if(!String.valueOf(linha.get(11)).trim().equals(NAO_RELATADO)){
//					tracking.tempoPrevRetorno = toTime(linha.get(11));
//				}
        if (containsNumber(linha.get(11))) {
            tracking.tempoPrevRetorno = toTime(linha.get(11));
        }
//				if(!String.valueOf(linha.get(12)).trim().equals(NAO_RELATADO)){
//					tracking.tempoRetorno = toTime(linha.get(12));
//				}
        if (containsNumber(linha.get(12))) {
            tracking.tempoRetorno = toTime(linha.get(12));
        }
//				if(!String.valueOf(linha.get(13)).trim().equals(NAO_RELATADO)){
//					tracking.distPrevRetorno = Double.parseDouble(linha.get(13).replace(",", "."));
//				}
        if (containsNumber(linha.get(13))) {
            tracking.distPrevRetorno = Double.parseDouble(linha.get(13).replace(",", "."));
        }
//				if(!String.valueOf(linha.get(14)).trim().equals(NAO_RELATADO)){
//					tracking.distPercRetorno = Double.parseDouble(linha.get(14).replace(",", "."));
//				}
        if (containsNumber(linha.get(14))) {
            tracking.distPercRetorno = Double.parseDouble(linha.get(14).replace(",", "."));
        }
//				if(!String.valueOf(linha.get(15)).trim().equals(NAO_RELATADO)){
//					tracking.inicioEntrega = toTime(linha.get(15));
//				}
        if (containsNumber(linha.get(15))) {
            tracking.inicioEntrega = toTime(linha.get(15));
        }
//				if(!String.valueOf(linha.get(16)).trim().equals(NAO_RELATADO)){
//					tracking.fimEntrega = toTime(linha.get(16));
//				}
        if (containsNumber(linha.get(16))) {
            tracking.fimEntrega = toTime(linha.get(16));
        }
//				if(!String.valueOf(linha.get(17)).trim().equals(NAO_RELATADO)){
//					tracking.fimRota = toTime(linha.get(17));
//				}
        if (containsNumber(linha.get(17))) {
            tracking.fimRota = toTime(linha.get(17));
        }
//				if(!String.valueOf(linha.get(18)).trim().equals(NAO_RELATADO)){
//					tracking.entradaCDD = toTime(linha.get(18));
//				}
        if (containsNumber(linha.get(18))) {
            tracking.entradaCDD = toTime(linha.get(18));
        }
        if (!String.valueOf(linha.get(19)).trim().isEmpty()) {
            tracking.caixasCarregadas = Double.parseDouble(linha.get(19).replace(",", "."));
        }
        if (!String.valueOf(linha.get(20)).trim().isEmpty()) {
            tracking.caixasDevolvidas = Double.parseDouble(linha.get(20).replace(",", "."));
        }
        if (!String.valueOf(linha.get(21)).trim().isEmpty()) {
            tracking.repasse = Double.parseDouble(linha.get(21).replace(",", "."));
        }
        if (!String.valueOf(linha.get(22)).trim().isEmpty()) {
            tracking.tempoEntrega = toTime(linha.get(22));
        }
        if (!String.valueOf(linha.get(23)).trim().isEmpty()) {
            tracking.tempoDescarga = toTime(linha.get(23));
        }
        if (!String.valueOf(linha.get(24)).trim().isEmpty()) {
            tracking.tempoEspera = toTime(linha.get(24));
        }
        if (!String.valueOf(linha.get(25)).trim().isEmpty()) {
            tracking.tempoAlmoco = toTime(linha.get(25));
        }
        if (!String.valueOf(linha.get(26)).trim().isEmpty()) {
            tracking.tempoTotalRota = toTime(linha.get(26));
        }
//				if(!String.valueOf(linha.get(27)).trim().equals(NAO_RELATADO)){
//					tracking.dispApontCadastrado = Double.parseDouble(linha.get(27).replace(",", "."));
//				}
        if (containsNumber(linha.get(27))) {
            tracking.dispApontCadastrado = Double.parseDouble(linha.get(27).replace(".", "").replace(",", "."));
        }
        if (!String.valueOf(linha.get(28)).trim().isEmpty()) {
            tracking.latEntrega = linha.get(28).replace(",", ".");
        }
        if (!String.valueOf(linha.get(29)).trim().isEmpty()) {
            tracking.lonEntrega = linha.get(29).replace(",", ".");
        }
        if (!String.valueOf(linha.get(30)).trim().isEmpty()) {
            tracking.unidadeNegocio = Integer.parseInt(linha.get(30));
        }
        if (!String.valueOf(linha.get(31)).trim().isEmpty()) {
            tracking.transportadora = linha.get(31).trim();
        }
        if (!String.valueOf(linha.get(32)).trim().isEmpty()) {
            tracking.latClienteApontamento = linha.get(32).replace(",", ".");
        }
        if (!String.valueOf(linha.get(33)).trim().isEmpty()) {
            tracking.lonClienteApontamento = linha.get(33).replace(",", ".");
        }
        if (!String.valueOf(linha.get(34)).trim().isEmpty()) {
            tracking.latAtualCliente = linha.get(34).replace(",", ".");
        }
        if (!String.valueOf(linha.get(35)).trim().isEmpty()) {
            tracking.lonAtualCliente = linha.get(35).replace(",", ".");
        }
        if (!String.valueOf(linha.get(36)).trim().isEmpty()) {
            tracking.distanciaPrev = Double.parseDouble(linha.get(36).replace(",", "."));
        }
//				if(!String.valueOf(linha.get(37)).trim().equals(NAO_RELATADO)){
//					tracking.tempoDeslocamento = toTime(linha.get(37));
//				}
        if (containsNumber(linha.get(37))) {
            tracking.tempoDeslocamento = toTime(linha.get(37));
        }
        if (!String.valueOf(linha.get(38)).trim().isEmpty()) {
            tracking.velMedia = Double.parseDouble(linha.get(38).replace(",", "."));
        }
        if (!String.valueOf(linha.get(39)).trim().isEmpty()) {
            tracking.distanciaPercApontamento = Double.parseDouble(linha.get(39).replace(",", "."));
        }
        if (!String.valueOf(linha.get(40)).trim().isEmpty()) {
            tracking.aderenciaSequenciaEntrega = linha.get(40).trim();
        }
        if (!String.valueOf(linha.get(41)).trim().isEmpty()) {
            tracking.aderenciaJanelaEntrega = linha.get(41).trim();
        }
        if (!String.valueOf(linha.get(42)).trim().isEmpty()) {
            tracking.pdvLacrado = linha.get(42).trim();
        }
        return tracking;
    }
}