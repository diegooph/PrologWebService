package br.com.zalf.prolog.webservice.entrega.mapa;

import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.StringUtils;
import br.com.zalf.prolog.webservice.commons.util.date.DateUtils;
import br.com.zalf.prolog.webservice.commons.util.date.Now;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.entrega.mapa._model.PlanilhaMapaColunaHolder;
import br.com.zalf.prolog.webservice.entrega.mapa._model.PlanilhaMapaImport;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.*;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static br.com.zalf.prolog.webservice.entrega.ImportUtils.toTime;
import static br.com.zalf.prolog.webservice.entrega.ImportUtils.toTimestamp;

public final class MapaDaoImpl extends DatabaseConnection implements MapaDao {
    private static final String TAG = MapaDaoImpl.class.getSimpleName();
    private static final Time EMPTY_TIME = new Time(0L);
    private static final int MATRICULA_DEFAULT = 0;

    public MapaDaoImpl() {

    }

    // TODO: Se um mapa tem sua equipe modificada, o verifyExists do mapa colaborador não é suficiente pra
    //  mapear, teremos que implementar outra verificação mais eficiente, caso constrário ao realizar o update,
    //  a equipe antiga continuará na tabela, recebendo por um mapa que não realizou.
    @Override
    public void insertOrUpdateMapa(@NotNull final Long codUnidade,
                                   @NotNull final List<String[]> planilhaMapa) throws Throwable {

        Connection conn = null;
        try {
            conn = getConnection();
            for (final String[] colunas : planilhaMapa) {
                final PlanilhaMapaImport mapa = createMapa(new PlanilhaMapaColunaHolder(colunas));
                if (mapa != null) {
                    if (updateMapa(mapa, codUnidade, conn)) {
                        // Mapa ja existia e foi atualizado.
                        Log.d(TAG, "update mapa: " + mapa.mapa);
                    } else {
                        Log.d(TAG, "insert mapa: " + mapa.mapa);
                        // Mapa não existia e foi inserido na base.
                        insertMapa(mapa, codUnidade, conn);
                    }
                    insertOrUpdateMapaColaborador(mapa.mapa, codUnidade, mapa.matricMotorista, conn);
                    insertOrUpdateMapaColaborador(mapa.mapa, codUnidade, mapa.matricAjud1, conn);
                    insertOrUpdateMapaColaborador(mapa.mapa, codUnidade, mapa.matricAjud2, conn);
                }
            }
        } finally {
            close(conn);
        }
    }

    private boolean insertOrUpdateMapaColaborador(final int mapa,
                                                  final long codUnidade,
                                                  final int matricula,
                                                  final Connection conn) throws SQLException {
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

    private boolean insertMapa(final PlanilhaMapaImport mapa,
                               final Long codUnidade,
                               final Connection conn) throws SQLException {
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
                    + " ?,  ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
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
            stmt.setTimestamp(86, Now.timestampUtc());
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
            stmt.setString(105, mapa.classificacaoRoadShow);
            if (mapa.dataEntrega != null) {
                stmt.setDate(106, DateUtils.toSqlDate(mapa.dataEntrega));
            } else {
                stmt.setDate(106, null);
            }
            stmt.setInt(107, mapa.qtdEntregasCarregRv);
            stmt.setInt(108, mapa.qtdEntregasEntregRv);
            stmt.setDouble(109, mapa.indiceDevEntregas);
            stmt.setObject(110, mapa.cpfMotorista);
            stmt.setObject(111, mapa.cpfAjudante1);
            stmt.setObject(112, mapa.cpfAjudante2);
            if (mapa.inicioRota != null) {
                stmt.setTimestamp(113, DateUtils.toTimestamp(mapa.inicioRota));
            } else {
                stmt.setTimestamp(113, null);
            }
            if (mapa.terminoRota != null) {
                stmt.setTimestamp(114, DateUtils.toTimestamp(mapa.terminoRota));
            } else {
                stmt.setTimestamp(114, null);
            }
            stmt.setString(115, mapa.motoristaJt12x36);
            stmt.setString(116, mapa.retira);
            final int count = stmt.executeUpdate();
            if (count == 0) {
                throw new SQLException("Erro ao inserir o mapa " + mapa + " na tabela");
            }
        } finally {
            close(stmt);
        }
        return true;
    }

    private boolean insertMapaColaborador(final int mapa,
                                          final long codUnidade,
                                          final int matricula,
                                          final Connection conn) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("INSERT INTO MAPA_COLABORADOR VALUES(?, ?,	?);");
            stmt.setInt(1, mapa);
            stmt.setLong(2, codUnidade);
            stmt.setInt(3, matricula);
            final int count = stmt.executeUpdate();
            if (count == 0) {
                throw new SQLException("Erro ao inserir o mapa_colaborador: " + mapa + " matricula: " + matricula);
            }
        } finally {
            close(stmt);
        }
        return true;
    }

    private boolean verifyExistsMapaColaborador(final int mapa,
                                                final long codUnidade,
                                                final int matricula,
                                                final Connection conn) throws SQLException {
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
            close(stmt, rSet);
        }
        return true;
    }

    private boolean updateMapa(final PlanilhaMapaImport mapa,
                               final Long codUnidade,
                               final Connection conn) throws SQLException {
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
                    + "data_hora_import= ?, "
                    + "classificacao_roadshow= ?,"
                    + "data_entrega= ?,"
                    + "qt_entregas_carreg_rv= ?,"
                    + "qt_entregas_entreg_rv= ?,"
                    + "indice_dev_entregas= ?,"
                    + "cpf_motorista= ?,"
                    + "cpf_ajudante_1= ?,"
                    + "cpf_ajudante_2= ?,"
                    + "inicio_rota= ?,"
                    + "termino_rota= ?,"
                    + "motorista_jt_12x36= ?,"
                    + "retira= ?"
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
            stmt.setTimestamp(103, Now.timestampUtc());
            stmt.setString(104, mapa.classificacaoRoadShow);
            if (mapa.dataEntrega != null) {
                stmt.setDate(105, DateUtils.toSqlDate(mapa.dataEntrega));
            } else {
                stmt.setDate(105, null);
            }
            stmt.setInt(106, mapa.qtdEntregasCarregRv);
            stmt.setInt(107, mapa.qtdEntregasEntregRv);
            stmt.setDouble(108, mapa.indiceDevEntregas);
            stmt.setObject(109, mapa.cpfMotorista);
            stmt.setObject(110, mapa.cpfAjudante1);
            stmt.setObject(111, mapa.cpfAjudante2);
            if (mapa.inicioRota != null) {
                stmt.setTimestamp(112, DateUtils.toTimestamp(mapa.inicioRota));
            } else {
                stmt.setTimestamp(112, null);
            }
            if (mapa.terminoRota != null) {
                stmt.setTimestamp(113, DateUtils.toTimestamp(mapa.terminoRota));
            } else {
                stmt.setTimestamp(113, null);
            }
            stmt.setString(114, mapa.motoristaJt12x36);
            stmt.setString(115, mapa.retira);
            // condição do where:
            stmt.setInt(116, mapa.mapa);
            stmt.setLong(117, codUnidade);
            final int count = stmt.executeUpdate();
            if (count == 0) {
                return false;
            }
        } finally {
            close(stmt);
        }
        return true;
    }

    @Nullable
    private PlanilhaMapaImport createMapa(@NotNull final PlanilhaMapaColunaHolder colunas) throws ParseException {
        // Caso a data esteja vazia, retorna null, evitando erros nos inserts/updates.
        if (StringUtils.isNullOrEmpty(colunas.get(0))) {
            return null;
        }
        final PlanilhaMapaImport mapa = new PlanilhaMapaImport();
        mapa.data = toDate(colunas.get(0));
        mapa.transp = Integer.parseInt(colunas.get(1));
        mapa.entrega = colunas.get(2).replace(" ", "");
        mapa.cargaAtual = colunas.get(3).replace(" ", "");
        mapa.frota = colunas.get(4).replace(" ", "");
        mapa.custoSpot = Double.parseDouble(colunas.get(5).replace(",", "."));
        mapa.regiao = Integer.parseInt(colunas.get(6));
        mapa.veiculo = Integer.parseInt(colunas.get(7));
        mapa.placa = colunas.get(8).replace(" ", "");
        mapa.veiculoIndisp = Double.parseDouble(colunas.get(9).replace(",", "."));
        // inserir 0 caso venha em branco
        if (colunas.get(10).trim().isEmpty()) {
            mapa.placaIndisp = 0;
        } else {
            mapa.placaIndisp = Double.parseDouble(colunas.get(10).replace(",", "."));
        }
        // inserir 0 caso venha em branco
        if (colunas.get(11).trim().isEmpty()) {
            mapa.frotaIndisp = 0;
        } else {
            mapa.frotaIndisp = Double.parseDouble(colunas.get(11).replace(",", "."));
        }
        mapa.tipoIndisp = Integer.parseInt(colunas.get(12));
        mapa.mapa = Integer.parseInt(colunas.get(13));
        mapa.entregas = Integer.parseInt(colunas.get(14));
        mapa.cxCarreg = Double.parseDouble(colunas.get(15).replace(",", "."));
        mapa.cxEntreg = Double.parseDouble(colunas.get(16).replace(",", "."));
        mapa.ocupacao = Double.parseDouble(colunas.get(17).replace(",", "."));
        mapa.cxRota = Double.parseDouble(colunas.get(18).replace(",", "."));
        mapa.cxAs = Double.parseDouble(colunas.get(19).replace(",", "."));
        mapa.veicBM = Double.parseDouble(colunas.get(20).replace(",", "."));
        mapa.rShow = Integer.parseInt(colunas.get(21));
        mapa.entrVol = colunas.get(22).replace(" ", "");
        final Date hrSaida = toTimestamp(colunas.get(23));
        final Date hrEntrada = toTimestamp(colunas.get(24));
        if (hrSaida == null && hrEntrada == null) {
            throw new IllegalStateException("Não é possível inserir um mapa que não tenha hrSaida e nem hrEntrada");
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
        mapa.kmSai = Integer.parseInt(colunas.get(25));
        mapa.kmEntr = Integer.parseInt(colunas.get(26));
        mapa.custoVariavel = Double.parseDouble(colunas.get(27).replace(",", "."));
        mapa.lucro = Double.parseDouble(colunas.get(28).replace(",", "."));
        mapa.lucroUnit = Double.parseDouble(colunas.get(29).replace(",", "."));
        mapa.valorFrete = Double.parseDouble(colunas.get(30).replace(",", "."));
        mapa.tipoImposto = colunas.get(31).replace(" ", "");
        mapa.percImposto = Double.parseDouble(colunas.get(32).replace(",", "."));
        mapa.valorImposto = Double.parseDouble(colunas.get(33).replace(",", "."));
        mapa.valorFaturado = Double.parseDouble(colunas.get(34).replace(",", "."));
        mapa.valorUnitCxEntregue = Double.parseDouble(colunas.get(35).replace(",", "."));
        mapa.valorPgCxEntregSemImp = Double.parseDouble(colunas.get(36).replace(",", "."));
        mapa.valorPgCxEntregComImp = Double.parseDouble(colunas.get(37).replace(",", "."));
        // realizar replace de " " e " ' " por vazio
        mapa.tempoPrevistoRoad = toTime(colunas.get(38));
        mapa.kmPrevistoRoad = Double.parseDouble(colunas.get(39).replace(",", "."));
        mapa.valorUnitPontoMot = Double.parseDouble(colunas.get(40).replace(",", "."));
        mapa.valorUnitPontoAjd = Double.parseDouble(colunas.get(41).replace(",", "."));
        mapa.valorEquipeEntrMot = Double.parseDouble(colunas.get(42).replace(",", "."));
        mapa.valorEquipeEntrAjd = Double.parseDouble(colunas.get(43).replace(",", "."));
        mapa.custoVLC = Double.parseDouble(colunas.get(44).replace(",", "."));
        mapa.lucroUnitCEDBZ = Double.parseDouble(colunas.get(45).replace(",", "."));
        mapa.CustoVlcCxEntr = Double.parseDouble(colunas.get(46).replace(",", "."));
        if (colunas.get(47).trim().isEmpty()) {
            mapa.tempoInterno = EMPTY_TIME;
        } else {
            mapa.tempoInterno = toTime(colunas.get(47));
        }
        mapa.valorDropDown = Double.parseDouble(colunas.get(48).replace(",", "."));
        mapa.veicCadDD = colunas.get(49).replace(" ", "");
        mapa.kmLaco = Double.parseDouble(colunas.get(50).replace(",", "."));
        mapa.kmDeslocamento = Double.parseDouble(colunas.get(51).replace(",", "."));
        // Fazer replace.
        mapa.tempoLaco = toTime(colunas.get(52));
        // Fazer replace.
        mapa.tempoDeslocamento = toTime(colunas.get(53));
        mapa.sitMultiCDD = Double.parseDouble(colunas.get(54).replace(",", "."));
        mapa.unbOrigem = Integer.parseInt(colunas.get(55));
        mapa.matricMotorista = StringUtils.isNullOrEmpty(colunas.get(56)) ? MATRICULA_DEFAULT : Integer.parseInt(colunas.get(56));
        mapa.matricAjud1 = StringUtils.isNullOrEmpty(colunas.get(57)) ? MATRICULA_DEFAULT : Integer.parseInt(colunas.get(57));
        mapa.matricAjud2 = StringUtils.isNullOrEmpty(colunas.get(58)) ? MATRICULA_DEFAULT : Integer.parseInt(colunas.get(58));
        mapa.valorCTEDifere = colunas.get(59).replace(" ", "");
        mapa.qtNfCarregadas = Integer.parseInt(colunas.get(60));
        mapa.qtNfEntregues = Integer.parseInt(colunas.get(61));
        mapa.indDevCx = Double.parseDouble(colunas.get(62).replace(",", "."));
        mapa.indDevNf = Double.parseDouble(colunas.get(63).replace(",", "."));
        mapa.fator = parseOrDefaultIfEmpty(colunas.get(64), 1);
        // Se o fator existir e for 0, setamos para 1 para evitar que alguns cálculos que dividem por fator acabem
        // quebrando.
        if (mapa.fator == 0) {
            mapa.fator = 1;
        }
        mapa.recarga = colunas.get(65).replace(" ", "");
        mapa.hrMatinal = toTime(colunas.get(66));
        mapa.hrJornadaLiq = toTime(colunas.get(67));
        if (colunas.get(68).equals("0")) {
            mapa.hrMetaJornada = new Time(0);
        } else {
            mapa.hrMetaJornada = toTime(colunas.get(68));
        }
        mapa.vlBateuJornMot = parseOrDefaultIfEmpty(colunas.get(69), 0);
        mapa.vlNaoBateuJornMot = parseOrDefaultIfEmpty(colunas.get(70), 0);
        mapa.vlRecargaMot = parseOrDefaultIfEmpty(colunas.get(71), 0);
        mapa.vlBateuJornAju = parseOrDefaultIfEmpty(colunas.get(72), 0);
        mapa.vlNaoBateuJornAju = parseOrDefaultIfEmpty(colunas.get(73), 0);
        mapa.vlRecargaAju = parseOrDefaultIfEmpty(colunas.get(74), 0);
        mapa.vlTotalMapa = parseOrDefaultIfEmpty(colunas.get(75), 0);
        mapa.qtHlCarregados = Double.parseDouble(colunas.get(76).replace(",", "."));
        mapa.qtHlEntregues = Double.parseDouble(colunas.get(77).replace(",", "."));
        mapa.indiceDevHl = Double.parseDouble(colunas.get(78).replace(",", "."));
        mapa.regiao2 = colunas.get(79).replace(" ", "");
        mapa.qtNfCarregGeral = Integer.parseInt(colunas.get(80));
        mapa.qtNfEntregGeral = Integer.parseInt(colunas.get(81));
        mapa.capacidadeVeiculoKg = Double.parseDouble(colunas.get(82).replace(",", "."));
        mapa.pesoCargaKg = Double.parseDouble(colunas.get(83).replace(",", "."));
        mapa.capacVeiculoCx = Integer.parseInt(colunas.get(84));
        mapa.entregasCompletas = Integer.parseInt(colunas.get(85));
        mapa.entregasParciais = Integer.parseInt(colunas.get(86));
        mapa.entregasNaoRealizadas = Integer.parseInt(colunas.get(87));
        mapa.codFilial = Integer.parseInt(colunas.get(88));
        mapa.nomeFilial = colunas.get(89);
        mapa.codSupervTrs = Integer.parseInt(colunas.get(90));
        mapa.nomeSupervTrs = colunas.get(91);
        mapa.codSpot = Integer.parseInt(colunas.get(92));
        mapa.nomeSpot = colunas.get(93);
        mapa.equipCarregados = Integer.parseInt(colunas.get(94));
        mapa.equipDevolvidos = Integer.parseInt(colunas.get(95));
        mapa.equipRecolhidos = Integer.parseInt(colunas.get(96));
        mapa.cxEntregTracking = Double.parseDouble(colunas.get(97).replace(",", "."));
        mapa.hrCarreg = toTimestamp(colunas.get(98));
        mapa.hrPCFisica = toTimestamp(colunas.get(99));
        mapa.hrPCFinanceira = toTimestamp(colunas.get(100));
        mapa.stMapa = colunas.get(101);
        if (!colunas.get(102).isEmpty()) {
            mapa.dataEntrega = toDate(colunas.get(102));
        } else {
            mapa.dataEntrega = null;
        }
        mapa.qtdEntregasCarregRv = Integer.parseInt(colunas.get(103));
        mapa.qtdEntregasEntregRv = Integer.parseInt(colunas.get(104));
        mapa.indiceDevEntregas = Double.parseDouble(colunas.get(105).replace(",", "."));
        if (!colunas.get(106).trim().isEmpty()) {
            mapa.cpfMotorista = Long.valueOf(colunas.get(106).trim());
        } else {
            mapa.cpfMotorista = null;
        }
        if (!colunas.get(107).trim().isEmpty()) {
            mapa.cpfAjudante1 = Long.valueOf(colunas.get(107).trim());
        } else {
            mapa.cpfAjudante1 = null;
        }
        if (!colunas.get(108).trim().isEmpty()) {
            mapa.cpfAjudante2 = Long.valueOf(colunas.get(108));
        } else {
            mapa.cpfAjudante2 = null;
        }
        if (!colunas.get(109).isEmpty()) {
            mapa.inicioRota = toTimestamp(colunas.get(109));
        } else {
            mapa.inicioRota = null;
        }
        if (!colunas.get(110).isEmpty()) {
            mapa.terminoRota = toTimestamp(colunas.get(110));
        } else {
            mapa.terminoRota = null;
        }
        mapa.motoristaJt12x36 = colunas.get(111);
        mapa.retira = colunas.get(112);
        mapa.classificacaoRoadShow = colunas.get(113);
        return mapa;
    }

    private double parseOrDefaultIfEmpty(@NotNull final String value, final double defaultValue) {
        if (StringUtils.isNullOrEmpty(value.trim())) {
            return defaultValue;
        }

        return Double.parseDouble(value.replace(",", "."));
    }

    /**
     * Converte uma string para Date
     *
     * @param data uma String contendo uma data
     * @return um Date
     */
    private Date toDate(final String data) {
        final int ano;
        final int mes;
        final int dia;
        final Calendar calendar = Calendar.getInstance();

        if (data.length() == 7) {
            ano = Integer.parseInt(data.substring(3, 7));
            mes = Integer.parseInt(data.substring(1, 3));
            dia = Integer.parseInt(data.substring(0, 1));
        } else {
            ano = Integer.parseInt(data.substring(4, 8));
            mes = Integer.parseInt(data.substring(2, 4));
            dia = Integer.parseInt(data.substring(0, 2));
        }
        calendar.set(Calendar.YEAR, ano);
        // calendario no java começa em 0, no 2art o mês começa em 1
        calendar.set(Calendar.MONTH, mes - 1);
        calendar.set(Calendar.DAY_OF_MONTH, dia);

        return calendar.getTime();
    }
}