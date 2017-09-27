package br.com.zalf.prolog.webservice.imports.mapa;

import br.com.zalf.prolog.webservice.DatabaseConnection;
import br.com.zalf.prolog.webservice.commons.util.DateUtils;
import br.com.zalf.prolog.webservice.commons.util.Log;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.*;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static br.com.zalf.prolog.webservice.imports.ImportUtils.toTime;
import static br.com.zalf.prolog.webservice.imports.ImportUtils.toTimestamp;

public class MapaDaoImpl extends DatabaseConnection implements MapaDao {

    private static final String TAG = MapaDaoImpl.class.getSimpleName();

    public static final Time EMPTY_TIME = new Time(0L);

    //TODO: se um mapa tem sua equipe modificada, o verifyExists do mapa colaborador não é suficiente pra
    // mapear, teremos que implementar outra verificação mais eficiente, caso constrário ao realizar o update,
    // a equipe antiga continuará na tabela, recebendo por um mapa que não realizo

    public boolean insertOrUpdateMapa(String path, Long codUnidade) throws SQLException, IOException, ParseException {

        Connection conn = null;
        try {
            conn = getConnection();
            Reader in = new FileReader(path);
            List<CSVRecord> tabela = CSVFormat.DEFAULT.withDelimiter(';').parse(in).getRecords();
            //List<CSVRecord> tabela = CSVFormat.DEFAULT.parse(in).getRecords();
            for (int i = 1; i < tabela.size(); i++) {
                MapaImport mapa = createMapa(tabela.get(i));
                if (mapa != null) {
                    if (updateMapa(mapa, codUnidade, conn)) {
                        // Mapa ja existia e foi atualizado
                        Log.d(TAG, "update mapa: " + mapa.mapa);
                    } else {
                        Log.d(TAG, "insert mapa: " + mapa.mapa);
                        // Mapa não existia e foi inserido na base
                        insertMapa(mapa, codUnidade, conn);
                    }
                    insertOrUpdateMapaColaborador(mapa.mapa, codUnidade, mapa.matricMotorista, conn);
                    insertOrUpdateMapaColaborador(mapa.mapa, codUnidade, mapa.matricAjud1, conn);
                    insertOrUpdateMapaColaborador(mapa.mapa, codUnidade, mapa.matricAjud2, conn);
                }
            }
            return true;
        } finally {
            closeConnection(conn, null, null);
        }
    }

    private boolean insertOrUpdateMapaColaborador(int mapa, long codUnidade, int matricula, Connection conn) throws SQLException {
        if (matricula > 0) {
            if (verifyExistsMapaColaborador(mapa, codUnidade, matricula, conn)) {
                Log.d(TAG, "update mapa_colaborador: " + mapa);
            } else {
                Log.d(TAG, "insert mapa_colaborador: " + mapa);
                insertMapaColaborador(mapa, codUnidade, matricula, conn);
            }
        }
        return true;
    }

    private boolean insertMapa(MapaImport mapa, Long codUnidade, Connection conn) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("INSERT INTO MAPA VALUES(?, ?,	?,	?,"
                    + " ?,	?,	?,	?,	?,	?,	?,	?,	?,	?,	?,	?,	?,	?,"
                    + "	?,	?,	?,	?,	?,	?,	?,	?,	?,	?,	?,	?,	?,	?,"
                    + "	?,	?,	?,	?,	?,	?,	?,	?,	?,	?,	?,	?,	?,	?,"
                    + "	?,	?,	?,	?,	?,	?,	?,	?,	?,	?,	?,	?,	?,	?,"
                    + "	?,	?,	?,	?,	?,	?,	?,	?,	?,	?,	?,	?,	?,	?,"
                    + "	?,	?,	?,	?,	?,	?,	?,	?,	?,	?,  ?,  ?,  ?,  ?,"
                    + " ?,  ?,  ?,  ?,  ?,  ?,  ?,  ?,  ?,  ?,  ?,  ?,  ?,  ?,"
                    + " ?,  ?)");
            stmt.setDate(1, DateUtils.toSqlDate(mapa.data));
            stmt.setInt(2, mapa.transp);
            stmt.setString(3, mapa.entrega);
            stmt.setString(4, mapa.cargaAtual);
            stmt.setString(5, mapa.frota);
            stmt.setDouble(6, mapa.custoSpot);
            stmt.setInt(7, mapa.regiao);
            stmt.setInt(8, mapa.veiculo);
            stmt.setString(9, mapa.placa);
            stmt.setDouble(10, mapa.veiculoIndisp);
            stmt.setDouble(11, mapa.placaIndisp);
            stmt.setDouble(12, mapa.frotaIndisp);
            stmt.setInt(13, mapa.tipoIndisp);
            stmt.setInt(14, mapa.mapa);
            stmt.setInt(15, mapa.entregas);
            stmt.setDouble(16, mapa.cxCarreg);
            stmt.setDouble(17, mapa.cxEntreg);
            stmt.setDouble(18, mapa.ocupacao);
            stmt.setDouble(19, mapa.cxRota);
            stmt.setDouble(20, mapa.cxAs);
            stmt.setDouble(21, mapa.veicBM);
            stmt.setInt(22, mapa.rShow);
            stmt.setString(23, mapa.entrVol);
            stmt.setTimestamp(24, DateUtils.toTimestamp(mapa.hrSai));
            stmt.setTimestamp(25, DateUtils.toTimestamp(mapa.hrEntr));
            stmt.setInt(26, mapa.kmSai);
            stmt.setInt(27, mapa.kmEntr);
            stmt.setDouble(28, mapa.custoVariavel);
            stmt.setDouble(29, mapa.lucro);
            stmt.setDouble(30, mapa.lucroUnit);
            stmt.setDouble(31, mapa.valorFrete);
            stmt.setString(32, mapa.tipoImposto);
            stmt.setDouble(33, mapa.percImposto);
            stmt.setDouble(34, mapa.valorImposto);
            stmt.setDouble(35, mapa.valorFaturado);
            stmt.setDouble(36, mapa.valorUnitCxEntregue);
            stmt.setDouble(37, mapa.valorPgCxEntregSemImp);
            stmt.setDouble(38, mapa.valorPgCxEntregComImp);
            stmt.setTime(39, mapa.tempoPrevistoRoad);
            stmt.setDouble(40, mapa.kmPrevistoRoad);
            stmt.setDouble(41, mapa.valorUnitPontoMot);
            stmt.setDouble(42, mapa.valorUnitPontoAjd);
            stmt.setDouble(43, mapa.valorEquipeEntrMot);
            stmt.setDouble(44, mapa.valorEquipeEntrAjd);
            stmt.setDouble(45, mapa.custoVLC);
            stmt.setDouble(46, mapa.lucroUnitCEDBZ);
            stmt.setDouble(47, mapa.CustoVlcCxEntr);
            stmt.setTime(48, mapa.tempoInterno);
            stmt.setDouble(49, mapa.valorDropDown);
            stmt.setString(50, mapa.veicCadDD);
            stmt.setDouble(51, mapa.kmLaco);
            stmt.setDouble(52, mapa.kmDeslocamento);
            stmt.setTime(53, mapa.tempoLaco);
            stmt.setTime(54, mapa.tempoDeslocamento);
            stmt.setDouble(55, mapa.sitMultiCDD);
            stmt.setInt(56, mapa.unbOrigem);
            stmt.setInt(57, mapa.matricMotorista);
            stmt.setInt(58, mapa.matricAjud1);
            stmt.setInt(59, mapa.matricAjud2);
            stmt.setString(60, mapa.valorCTEDifere);
            stmt.setInt(61, mapa.qtNfCarregadas);
            stmt.setInt(62, mapa.qtNfEntregues);
            stmt.setDouble(63, mapa.indDevCx);
            stmt.setDouble(64, mapa.indDevNf);
            stmt.setDouble(65, mapa.fator);
            stmt.setString(66, mapa.recarga);
            stmt.setTime(67, mapa.hrMatinal);
            stmt.setTime(68, mapa.hrJornadaLiq);
            stmt.setTime(69, mapa.hrMetaJornada);
            stmt.setDouble(70, mapa.vlBateuJornMot);
            stmt.setDouble(71, mapa.vlNaoBateuJornMot);
            stmt.setDouble(72, mapa.vlRecargaMot);
            stmt.setDouble(73, mapa.vlBateuJornAju);
            stmt.setDouble(74, mapa.vlNaoBateuJornAju);
            stmt.setDouble(75, mapa.vlRecargaAju);
            stmt.setDouble(76, mapa.vlTotalMapa);
            stmt.setDouble(77, mapa.qtHlCarregados);
            stmt.setDouble(78, mapa.qtHlEntregues);
            stmt.setDouble(79, mapa.indiceDevHl);
            stmt.setString(80, mapa.regiao2);
            stmt.setInt(81, mapa.qtNfCarregGeral);
            stmt.setInt(82, mapa.qtNfEntregGeral);
            stmt.setDouble(83, mapa.capacidadeVeiculoKg);
            stmt.setDouble(84, mapa.pesoCargaKg);
            stmt.setLong(85, codUnidade);
            stmt.setTimestamp(86, DateUtils.toTimestamp(new Date(System.currentTimeMillis())));
            stmt.setInt(87, mapa.capacVeiculoCx);
            stmt.setInt(88, mapa.entregasCompletas);
            stmt.setInt(89, mapa.entregasParciais);
            stmt.setInt(90, mapa.entregasNaoRealizadas);
            stmt.setInt(91, mapa.codFilial);
            stmt.setString(92, mapa.nomeFilial);
            stmt.setInt(93, mapa.codSupervTrs);
            stmt.setString(94, mapa.nomeSupervTrs);
            stmt.setInt(95, mapa.codSpot);
            stmt.setString(96, mapa.nomeSpot);
            stmt.setInt(97, mapa.equipCarregados);
            stmt.setInt(98, mapa.equipDevolvidos);
            stmt.setInt(99, mapa.equipRecolhidos);
            stmt.setDouble(100, mapa.cxEntregTracking);
            stmt.setTimestamp(101, DateUtils.toTimestamp(mapa.hrCarreg));
            stmt.setTimestamp(102, DateUtils.toTimestamp(mapa.hrPCFisica));
            stmt.setTimestamp(103, DateUtils.toTimestamp(mapa.hrPCFinanceira));
            stmt.setString(104, mapa.stMapa);
            int count = stmt.executeUpdate();
            if (count == 0) {
                throw new SQLException("Erro ao inserir o mapa " + mapa + " na tabela");
            }
        } finally {
            closeConnection(null, stmt, null);
        }
        return true;
    }

    private boolean insertMapaColaborador(int mapa, long codUnidade, int matricula, Connection conn) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("INSERT INTO MAPA_COLABORADOR VALUES(?, ?,	?);");
            stmt.setInt(1, mapa);
            stmt.setLong(2, codUnidade);
            stmt.setInt(3, matricula);
            int count = stmt.executeUpdate();
            if (count == 0) {
                throw new SQLException("Erro ao inserir o mapa_colaborador: " + mapa + " matricula: " + matricula);
            }
        } finally {
            closeConnection(null, stmt, null);
        }
        return true;
    }

    private boolean verifyExistsMapaColaborador(int mapa, long codUnidade, int matricula, Connection conn) throws SQLException {
        ResultSet rSet = null;
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("SELECT EXISTS(SELECT MC.MAPA FROM "
                    + "MAPA_COLABORADOR MC WHERE MC.MAPA = ? AND MC.COD_AMBEV = ? AND MC.COD_UNIDADE = ?);");
            stmt.setInt(1, mapa);
            stmt.setInt(2, matricula);
            stmt.setLong(3, codUnidade);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return rSet.getBoolean("EXISTS");
            }
        } finally {
            closeConnection(null, stmt, rSet);
        }
        return true;
    }

    private boolean updateMapa(MapaImport mapa, Long codUnidade, Connection conn) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("UPDATE MAPA "
                    + "SET "
                    + "Data= ?, "
                    + "Transp= ?, "
                    + "Entrega= ?, "
                    + "CargaAtual= ?, "
                    + "Frota= ?, "
                    + "CustoSpot= ?, "
                    + "Regiao= ?, "
                    + "Veiculo= ?, "
                    + "Placa= ?, "
                    + "VeiculoIndisp= ?, "
                    + "PlacaIndisp= ?, "
                    + "FrotaIndisp= ?, "
                    + "TipoIndisp= ?, "
                    + "Entregas= ?, "
                    + "CxCarreg= ?, "
                    + "CxEntreg= ?, "
                    + "Ocupacao= ?, "
                    + "CxRota= ?, "
                    + "CxAS= ?, "
                    + "VeicBM= ?, "
                    + "RShow= ?, "
                    + "EntrVol= ?, "
                    + "HrSai= ?, "
                    + "HrEntr= ?, "
                    + "KmSai= ?, "
                    + "KmEntr= ?, "
                    + "CustoVariavel= ?, "
                    + "Lucro= ?, "
                    + "LucroUnit= ?, "
                    + "ValorFrete= ?, "
                    + "TipoImposto= ?, "
                    + "PercImposto= ?, "
                    + "ValorImposto= ?, "
                    + "ValorFaturado= ?, "
                    + "ValorUnitCxEntregue= ?, "
                    + "ValorPgCxEntregSemImp= ?, "
                    + "ValorPgCxEntregComImp= ?, "
                    + "TempoPrevistoRoad= ?, "
                    + "KmPrevistoRoad= ?, "
                    + "ValorUnitPontoMot= ?, "
                    + "ValorUnitPontoAjd= ?, "
                    + "ValorEquipeEntrMot= ?, "
                    + "ValorEquipeEntrAjd= ?, "
                    + "CustoVariavelCEDBZ= ?, "
                    + "LucroUnitCEDBZ= ?, "
                    + "LucroVariavelCxEntregueFFCEDBZ= ?, "
                    + "TempoInterno= ?, "
                    + "ValorDropDown= ?, "
                    + "VeicCadDD= ?, "
                    + "KmLaco= ?, "
                    + "KmDeslocamento= ?, "
                    + "TempoLaco= ?, "
                    + "TempoDeslocamento= ?, "
                    + "SitMultiCDD= ?, "
                    + "UnbOrigem= ?, "
                    + "MatricMotorista= ?, "
                    + "MatricAjud1= ?, "
                    + "MatricAjud2= ?, "
                    + "ValorCTEDifere= ?, "
                    + "QtNfCarregadas= ?, "
                    + "QtNfEntregues= ?, "
                    + "IndDevCx= ?, "
                    + "IndDevNf= ?, "
                    + "Fator= ?, "
                    + "Recarga= ?, "
                    + "HrMatinal= ?, "
                    + "HrJornadaLiq= ?, "
                    + "HrMetaJornada= ?, "
                    + "VlBateuJornMot= ?, "
                    + "VlNaoBateuJornMot= ?, "
                    + "VlRecargaMot= ?, "
                    + "VlBateuJornAju= ?, "
                    + "VlNaoBateuJornAju= ?, "
                    + "VlRecargaAju= ?, "
                    + "VlTotalMapa= ?, "
                    + "QtHlCarregados= ?, "
                    + "QtHlEntregues= ?, "
                    + "IndiceDevHl= ?, "
                    + "Regiao2= ?, "
                    + "QtNfCarregGeral= ?, "
                    + "QtNfEntregGeral= ?, "
                    + "CapacidadeVeiculoKG= ?, "
                    + "PesoCargaKG= ?, "
                    + "CapacVeiculoCx= ?, "
                    + "EntregasCompletas= ?, "
                    + "EntregasParciais= ?, "
                    + "EntregasNaoRealizadas= ?, "
                    + "CodFilial= ?, "
                    + "NomeFilial= ?, "
                    + "CodSupervTrs= ?, "
                    + "NomeSupervTrs= ?, "
                    + "CodSpot= ?, "
                    + "NomeSpot= ?, "
                    + "EquipCarregados= ?, "
                    + "EquipDevolvidos= ?, "
                    + "EquipRecolhidos= ?, "
                    + "CxEntregTracking= ?, "
                    + "HrCarreg= ?, "
                    + "HrPCFisica= ?, "
                    + "HrPCFinanceira= ?, "
                    + "StMapa= ?, "
                    + "cod_unidade= ?, "
                    + "data_hora_import= ? "
                    + " WHERE Mapa = ? AND cod_unidade = ?;");

            stmt.setDate(1, DateUtils.toSqlDate(mapa.data));
            stmt.setInt(2, mapa.transp);
            stmt.setString(3, mapa.entrega);
            stmt.setString(4, mapa.cargaAtual);
            stmt.setString(5, mapa.frota);
            stmt.setDouble(6, mapa.custoSpot);
            stmt.setInt(7, mapa.regiao);
            stmt.setInt(8, mapa.veiculo);
            stmt.setString(9, mapa.placa);
            stmt.setDouble(10, mapa.veiculoIndisp);
            stmt.setDouble(11, mapa.placaIndisp);
            stmt.setDouble(12, mapa.frotaIndisp);
            stmt.setInt(13, mapa.tipoIndisp);
            stmt.setInt(14, mapa.entregas);
            stmt.setDouble(15, mapa.cxCarreg);
            stmt.setDouble(16, mapa.cxEntreg);
            stmt.setDouble(17, mapa.ocupacao);
            stmt.setDouble(18, mapa.cxRota);
            stmt.setDouble(19, mapa.cxAs);
            stmt.setDouble(20, mapa.veicBM);
            stmt.setInt(21, mapa.rShow);
            stmt.setString(22, mapa.entrVol);
            stmt.setTimestamp(23, DateUtils.toTimestamp(mapa.hrSai));
            stmt.setTimestamp(24, DateUtils.toTimestamp(mapa.hrEntr));
            stmt.setInt(25, mapa.kmSai);
            stmt.setInt(26, mapa.kmEntr);
            stmt.setDouble(27, mapa.custoVariavel);
            stmt.setDouble(28, mapa.lucro);
            stmt.setDouble(29, mapa.lucroUnit);
            stmt.setDouble(30, mapa.valorFrete);
            stmt.setString(31, mapa.tipoImposto);
            stmt.setDouble(32, mapa.percImposto);
            stmt.setDouble(33, mapa.valorImposto);
            stmt.setDouble(34, mapa.valorFaturado);
            stmt.setDouble(35, mapa.valorUnitCxEntregue);
            stmt.setDouble(36, mapa.valorPgCxEntregSemImp);
            stmt.setDouble(37, mapa.valorPgCxEntregComImp);
            stmt.setTime(38, mapa.tempoPrevistoRoad);
            stmt.setDouble(39, mapa.kmPrevistoRoad);
            stmt.setDouble(40, mapa.valorUnitPontoMot);
            stmt.setDouble(41, mapa.valorUnitPontoAjd);
            stmt.setDouble(42, mapa.valorEquipeEntrMot);
            stmt.setDouble(43, mapa.valorEquipeEntrAjd);
            stmt.setDouble(44, mapa.custoVLC);
            stmt.setDouble(45, mapa.lucroUnitCEDBZ);
            stmt.setDouble(46, mapa.CustoVlcCxEntr);
            stmt.setTime(47, mapa.tempoInterno);
            stmt.setDouble(48, mapa.valorDropDown);
            stmt.setString(49, mapa.veicCadDD);
            stmt.setDouble(50, mapa.kmLaco);
            stmt.setDouble(51, mapa.kmDeslocamento);
            stmt.setTime(52, mapa.tempoLaco);
            stmt.setTime(53, mapa.tempoDeslocamento);
            stmt.setDouble(54, mapa.sitMultiCDD);
            stmt.setInt(55, mapa.unbOrigem);
            stmt.setInt(56, mapa.matricMotorista);
            stmt.setInt(57, mapa.matricAjud1);
            stmt.setInt(58, mapa.matricAjud2);
            stmt.setString(59, mapa.valorCTEDifere);
            stmt.setInt(60, mapa.qtNfCarregadas);
            stmt.setInt(61, mapa.qtNfEntregues);
            stmt.setDouble(62, mapa.indDevCx);
            stmt.setDouble(63, mapa.indDevNf);
            stmt.setDouble(64, mapa.fator);
            stmt.setString(65, mapa.recarga);
            stmt.setTime(66, mapa.hrMatinal);
            stmt.setTime(67, mapa.hrJornadaLiq);
            stmt.setTime(68, mapa.hrMetaJornada);
            stmt.setDouble(69, mapa.vlBateuJornMot);
            stmt.setDouble(70, mapa.vlNaoBateuJornMot);
            stmt.setDouble(71, mapa.vlRecargaMot);
            stmt.setDouble(72, mapa.vlBateuJornAju);
            stmt.setDouble(73, mapa.vlNaoBateuJornAju);
            stmt.setDouble(74, mapa.vlRecargaAju);
            stmt.setDouble(75, mapa.vlTotalMapa);
            stmt.setDouble(76, mapa.qtHlCarregados);
            stmt.setDouble(77, mapa.qtHlEntregues);
            stmt.setDouble(78, mapa.indiceDevHl);
            stmt.setString(79, mapa.regiao2);
            stmt.setInt(80, mapa.qtNfCarregGeral);
            stmt.setInt(81, mapa.qtNfEntregGeral);
            stmt.setDouble(82, mapa.capacidadeVeiculoKg);
            stmt.setDouble(83, mapa.pesoCargaKg);
            stmt.setInt(84, mapa.capacVeiculoCx);
            stmt.setInt(85, mapa.entregasCompletas);
            stmt.setInt(86, mapa.entregasParciais);
            stmt.setInt(87, mapa.entregasNaoRealizadas);
            stmt.setInt(88, mapa.codFilial);
            stmt.setString(89, mapa.nomeFilial);
            stmt.setInt(90, mapa.codSupervTrs);
            stmt.setString(91, mapa.nomeSupervTrs);
            stmt.setInt(92, mapa.codSpot);
            stmt.setString(93, mapa.nomeSpot);
            stmt.setInt(94, mapa.equipCarregados);
            stmt.setInt(95, mapa.equipDevolvidos);
            stmt.setInt(96, mapa.equipRecolhidos);
            stmt.setDouble(97, mapa.cxEntregTracking);
            stmt.setTimestamp(98, DateUtils.toTimestamp(mapa.hrCarreg));
            stmt.setTimestamp(99, DateUtils.toTimestamp(mapa.hrPCFisica));
            stmt.setTimestamp(100, DateUtils.toTimestamp(mapa.hrPCFinanceira));
            stmt.setString(101, mapa.stMapa);
            stmt.setLong(102, codUnidade);
            stmt.setTimestamp(103, DateUtils.toTimestamp(new Date(System.currentTimeMillis())));
            // condição do where:
            stmt.setInt(104, mapa.mapa);
            stmt.setLong(105, codUnidade);
            int count = stmt.executeUpdate();
            if (count == 0) {
                return false;
            }
        } finally {
            closeConnection(null, stmt, null);
        }
        return true;
    }

    private MapaImport createMapa(CSVRecord linha) throws ParseException {
        MapaImport mapa = new MapaImport();
//        caso a data esteja vazia, retorna null para essa linha inteira, evitando erros nos inserts/update
        if(linha.get(0).isEmpty()){
            return null;
        }
        mapa.data = toDate(linha.get(0));
        mapa.transp = Integer.parseInt(linha.get(1));
        mapa.entrega = linha.get(2).replace(" ", "");
        mapa.cargaAtual = linha.get(3).replace(" ", "");
        mapa.frota = linha.get(4).replace(" ", "");
        mapa.custoSpot = Double.parseDouble(linha.get(5).replace(",", "."));
        mapa.regiao = Integer.parseInt(linha.get(6));
        mapa.veiculo = Integer.parseInt(linha.get(7));
        mapa.placa = linha.get(8).replace(" ", "");
        mapa.veiculoIndisp = Double.parseDouble(linha.get(9).replace(",", "."));
        // inserir 0 caso venha em branco
        if (linha.get(10).trim().isEmpty()) {
            mapa.placaIndisp = 0;
        } else {
            mapa.placaIndisp = Double.parseDouble(linha.get(10).replace(",", "."));
        }
        // inserir 0 caso venha em branco
        if (linha.get(11).trim().isEmpty()) {
            mapa.frotaIndisp = 0;
        } else {
            mapa.frotaIndisp = Double.parseDouble(linha.get(11).replace(",", "."));
        }
        mapa.tipoIndisp = Integer.parseInt(linha.get(12));
        mapa.mapa = Integer.parseInt(linha.get(13));
        mapa.entregas = Integer.parseInt(linha.get(14));
        mapa.cxCarreg = Double.parseDouble(linha.get(15).replace(",", "."));
        mapa.cxEntreg = Double.parseDouble(linha.get(16).replace(",", "."));
        mapa.ocupacao = Double.parseDouble(linha.get(17).replace(",", "."));
        mapa.cxRota = Double.parseDouble(linha.get(18).replace(",", "."));
        mapa.cxAs = Double.parseDouble(linha.get(19).replace(",", "."));
        mapa.veicBM = Double.parseDouble(linha.get(20).replace(",", "."));
        mapa.rShow = Integer.parseInt(linha.get(21));
        mapa.entrVol = linha.get(22).replace(" ", "");
        Date hrSaida = toTimestamp(linha.get(23));
        Date hrEntrada = toTimestamp(linha.get(24));
        if (hrSaida == null && hrEntrada == null) {
            mapa.hrSai = new Date(0);
            mapa.hrEntr = new Date(0);
        } else if (hrSaida == null) {
            mapa.hrSai = hrEntrada;
            mapa.hrEntr = hrEntrada;
        } else if (hrEntrada == null) {
            mapa.hrSai = hrSaida;
            mapa.hrEntr = hrSaida;
        } else {
            mapa.hrSai = hrSaida;
            mapa.hrEntr = hrEntrada;
        }
        mapa.kmSai = Integer.parseInt(linha.get(25));
        mapa.kmEntr = Integer.parseInt(linha.get(26));
        mapa.custoVariavel = Double.parseDouble(linha.get(27).replace(",", "."));
        mapa.lucro = Double.parseDouble(linha.get(28).replace(",", "."));
        mapa.lucroUnit = Double.parseDouble(linha.get(29).replace(",", "."));
        mapa.valorFrete = Double.parseDouble(linha.get(30).replace(",", "."));
        mapa.tipoImposto = linha.get(31).replace(" ", "");
        mapa.percImposto = Double.parseDouble(linha.get(32).replace(",", "."));
        mapa.valorImposto = Double.parseDouble(linha.get(33).replace(",", "."));
        mapa.valorFaturado = Double.parseDouble(linha.get(34).replace(",", "."));
        mapa.valorUnitCxEntregue = Double.parseDouble(linha.get(35).replace(",", "."));
        mapa.valorPgCxEntregSemImp = Double.parseDouble(linha.get(36).replace(",", "."));
        mapa.valorPgCxEntregComImp = Double.parseDouble(linha.get(37).replace(",", "."));
        // realizar replace de " " e " ' " por vazio
        mapa.tempoPrevistoRoad = toTime(linha.get(38));
        mapa.kmPrevistoRoad = Double.parseDouble(linha.get(39).replace(",", "."));
        mapa.valorUnitPontoMot = Double.parseDouble(linha.get(40).replace(",", "."));
        mapa.valorUnitPontoAjd = Double.parseDouble(linha.get(41).replace(",", "."));
        mapa.valorEquipeEntrMot = Double.parseDouble(linha.get(42).replace(",", "."));
        mapa.valorEquipeEntrAjd = Double.parseDouble(linha.get(43).replace(",", "."));
        mapa.custoVLC = Double.parseDouble(linha.get(44).replace(",", "."));
        mapa.lucroUnitCEDBZ = Double.parseDouble(linha.get(45).replace(",", "."));
        mapa.CustoVlcCxEntr = Double.parseDouble(linha.get(46).replace(",", "."));
        if (linha.get(47).trim().isEmpty()) {
            mapa.tempoInterno = EMPTY_TIME;
        } else {
            mapa.tempoInterno = toTime(linha.get(47));
        }
        mapa.valorDropDown = Double.parseDouble(linha.get(48).replace(",", "."));
        mapa.veicCadDD = linha.get(49).replace(" ", "");
        mapa.kmLaco = Double.parseDouble(linha.get(50).replace(",", "."));
        mapa.kmDeslocamento = Double.parseDouble(linha.get(51).replace(",", "."));
        //fazer replace
        mapa.tempoLaco = toTime(linha.get(52));
        //fazer replace
        mapa.tempoDeslocamento = toTime(linha.get(53));
        mapa.sitMultiCDD = Double.parseDouble(linha.get(54).replace(",", "."));
        mapa.unbOrigem = Integer.parseInt(linha.get(55));
        mapa.matricMotorista = Integer.parseInt(linha.get(56));
        mapa.matricAjud1 = Integer.parseInt(linha.get(57));
        mapa.matricAjud2 = Integer.parseInt(linha.get(58));
        mapa.valorCTEDifere = linha.get(59).replace(" ", "");
        mapa.qtNfCarregadas = Integer.parseInt(linha.get(60));
        mapa.qtNfEntregues = Integer.parseInt(linha.get(61));
        mapa.indDevCx = Double.parseDouble(linha.get(62).replace(",", "."));
        mapa.indDevNf = Double.parseDouble(linha.get(63).replace(",", "."));
        ;
        mapa.fator = Double.parseDouble(linha.get(64).replace(",", "."));
        mapa.recarga = linha.get(65).replace(" ", "");
        mapa.hrMatinal = toTime(linha.get(66));
        mapa.hrJornadaLiq = toTime(linha.get(67));
        mapa.hrMetaJornada = toTime(linha.get(68));
        mapa.vlBateuJornMot = Double.parseDouble(linha.get(69).replace(",", "."));
        mapa.vlNaoBateuJornMot = Double.parseDouble(linha.get(70).replace(",", "."));
        mapa.vlRecargaMot = Double.parseDouble(linha.get(71).replace(",", "."));
        mapa.vlBateuJornAju = Double.parseDouble(linha.get(72).replace(",", "."));
        mapa.vlNaoBateuJornAju = Double.parseDouble(linha.get(73).replace(",", "."));
        if (linha.get(74).trim().isEmpty()) {
            mapa.vlRecargaAju = 0;
        } else {
            mapa.vlRecargaAju = Double.parseDouble(linha.get(74).replace(",", "."));
        }
        mapa.vlTotalMapa = Double.parseDouble(linha.get(75).replace(",", "."));
        mapa.qtHlCarregados = Double.parseDouble(linha.get(76).replace(",", "."));
        mapa.qtHlEntregues = Double.parseDouble(linha.get(77).replace(",", "."));
        mapa.indiceDevHl = Double.parseDouble(linha.get(78).replace(",", "."));
        mapa.regiao2 = linha.get(79).replace(" ", "");
        mapa.qtNfCarregGeral = Integer.parseInt(linha.get(80));
        mapa.qtNfEntregGeral = Integer.parseInt(linha.get(81));
        mapa.capacidadeVeiculoKg = Double.parseDouble(linha.get(82).replace(",", "."));
        mapa.pesoCargaKg = Double.parseDouble(linha.get(83).replace(",", "."));
        mapa.capacVeiculoCx = Integer.parseInt(linha.get(84));
        mapa.entregasCompletas = Integer.parseInt(linha.get(85));
        mapa.entregasParciais = Integer.parseInt(linha.get(86));
        mapa.entregasNaoRealizadas = Integer.parseInt(linha.get(87));
        mapa.codFilial = Integer.parseInt(linha.get(88));
        mapa.nomeFilial = linha.get(89);
        mapa.codSupervTrs = Integer.parseInt(linha.get(90));
        mapa.nomeSupervTrs = linha.get(91);
        mapa.codSpot = Integer.parseInt(linha.get(92));
        mapa.nomeSpot = linha.get(93);
        mapa.equipCarregados = Integer.parseInt(linha.get(94));
        mapa.equipDevolvidos = Integer.parseInt(linha.get(95));
        mapa.equipRecolhidos = Integer.parseInt(linha.get(96));
        mapa.cxEntregTracking = Double.parseDouble(linha.get(97));
        mapa.hrCarreg = toTimestamp(linha.get(98));
        mapa.hrPCFisica = toTimestamp(linha.get(99));
        mapa.hrPCFinanceira = toTimestamp(linha.get(100));
        mapa.stMapa = linha.get(101);
        return mapa;
    }

    /**
     * Converte uma string para Date
     *
     * @param data uma String contendo uma data
     * @return um Date
     */
    public static Date toDate(String data) {

        String date = String.valueOf(data);
        int ano;
        int mes;
        int dia;
        Calendar calendar = Calendar.getInstance();

        if (date.length() == 7) {
            ano = Integer.parseInt(date.substring(3, 7));
            mes = Integer.parseInt(date.substring(1, 3));
            dia = Integer.parseInt(date.substring(0, 1));

        } else {
            ano = Integer.parseInt(date.substring(4, 8));
            mes = Integer.parseInt(date.substring(2, 4));
            dia = Integer.parseInt(date.substring(0, 2));
        }
        calendar.set(Calendar.YEAR, ano);
        // calendario no java começa em 0, no 2art o mês começa em 1
        calendar.set(Calendar.MONTH, mes - 1);
        calendar.set(Calendar.DAY_OF_MONTH, dia);

        return calendar.getTime();
    }
}