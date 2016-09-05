package br.com.zalf.prolog.webservice.entrega.indicador;

import br.com.zalf.prolog.models.indicador.indicadores.item.*;
import br.com.zalf.prolog.models.util.MetaUtils;
import br.com.zalf.prolog.models.util.TimeUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jean on 02/09/16.
 * classe que converte os itens que vem em um ResultSet para os objetos
 */
public class Converter {

    private static final String TAG = Converter.class.getSimpleName();

    public static List<Indicador> createExtratoCaixaViagem(ResultSet rSet)throws SQLException{
        List<Indicador> itens = new ArrayList<>();
        while (rSet.next()){
            CaixaViagem item = new CaixaViagem();
            item.setData(rSet.getDate("DATA"))
                    .setMapa(rSet.getInt("MAPA"))
                    .setCxsCarregadas(rSet.getInt("CXCARREG"))
                    .setViagens(1)
                    .setMeta(rSet.getDouble("META_CAIXA_VIAGEM"))
                    .calculaResultado();
            itens.add(item);
        }
        return itens;
    }

    public static List<Indicador> createExtratoDevHl(ResultSet rSet) throws SQLException{
        List<Indicador> itens = new ArrayList<>();
        while(rSet.next()){
            DevHl item = new DevHl();
            item.setData(rSet.getDate("DATA"))
                    .setMapa(rSet.getInt("MAPA"))
                    .setOk(rSet.getDouble("QTHLENTREGUES"))
                    .setNok(rSet.getDouble("QTHLCARREGADOS") - item.getOk())
                    .setMeta(rSet.getDouble("META_DEV_HL"))
                    .calculaResultado();
            itens.add(item);
        }
        return itens;
    }

    public static List<Indicador> createExtratoDevPdv(ResultSet rSet) throws SQLException{
        List<Indicador> itens = new ArrayList<>();
        while(rSet.next()){
            DevPdv item = new DevPdv();
            item.setData(rSet.getDate("DATA"))
                    .setMapa(rSet.getInt("MAPA"))
                    .setOk(rSet.getInt("ENTREGASCOMPLETAS") + rSet.getInt("ENTREGASNAOREALIZADAS"))
                    .setNok(rSet.getInt("ENTREGASNAOREALIZADAS"))
                    .setMeta(rSet.getDouble("META_DEV_PDV"))
                    .calculaResultado();
            itens.add(item);
        }
        return itens;
    }

    public static List<Indicador> createExtratoDispersaoKm(ResultSet rSet) throws SQLException{
        List<Indicador> itens = new ArrayList<>();
        while(rSet.next()){
            DispersaoKm item = new DispersaoKm();
            item.setData(rSet.getDate("DATA"))
                    .setMapa(rSet.getInt("MAPA"))
                    .setMeta(rSet.getDouble("META_DISPERSAO_KM"))
                    .setKmPlanejado(rSet.getDouble("KMPREVISTOROAD"))
                    .setKmPercorrido(rSet.getDouble("KMENTR") - rSet.getDouble("KMSAI"))
                    .calculaResultado();
            itens.add(item);
        }
        return itens;
    }

    public static List<Indicador> createExtratoTracking(ResultSet rSet) throws SQLException{
        List<Indicador> itens = new ArrayList<>();
        while(rSet.next()){
            Tracking item = new Tracking();
            item.setData(rSet.getDate("DATA"))
                    .setMapa(rSet.getInt("MAPA"))
                    .setOk(rSet.getInt("APONTAMENTO_OK"))
                    .setNok(rSet.getInt("TOTAL_TRACKING") - item.getOk())
                    .setMeta(rSet.getDouble("META_TRACKING"))
                    .calculaResultado();
            itens.add(item);
        }
        return itens;
    }

    public static List<Indicador> createExtratoDispersaoTempo(ResultSet rSet) throws SQLException{
        List<Indicador> itens = new ArrayList<>();
        while(rSet.next()){
            DispersaoTempo item = new DispersaoTempo();
            item.setData(rSet.getDate("DATA"))
                    .setMapa(rSet.getInt("MAPA"))
                    .setMeta(rSet.getDouble("META_DISPERSAO_TEMPO"))
                    .setTempoPrevisto(rSet.getTime("TEMPOPREVISTOROAD"))
                    .setTempoRealizado(rSet.getTime("TEMPO_ROTA"))
                    .calculaResultado();
            itens.add(item);
        }
        return itens;
    }

    public static List<Indicador> createExtratoJornada(ResultSet rSet) throws SQLException{
        List<Indicador> itens = new ArrayList<>();
        while(rSet.next()){
            Jornada item = new Jornada();
            item.setData(rSet.getDate("DATA"))
                    .setMapa(rSet.getInt("MAPA"))
                    .setMeta(rSet.getTime("META_JORNADA_LIQUIDA_HORAS"))
                    .setTempoLargada(MetaUtils.calculaTempoLargada(rSet.getTime("HRSAI"), rSet.getTime("HRMATINAL")))
                    .setTempoInterno(rSet.getTime("TEMPOINTERNO"))
                    .setTempoRota(TimeUtils.differenceBetween(TimeUtils.toSqlTime(rSet.getTimestamp("HRENTR")),
					TimeUtils.toSqlTime(rSet.getTimestamp("HRSAI"))))
                    .calculaResultado();
            itens.add(item);
        }
        return itens;
    }

    public static List<Indicador> createExtratoTempoInterno(ResultSet rSet) throws SQLException{
        List<Indicador> itens = new ArrayList<>();
        while(rSet.next()){
            TempoInterno item = new TempoInterno();
            item.setData(rSet.getDate("DATA"))
                    .setMapa(rSet.getInt("MAPA"))
                    .setMeta(rSet.getTime("META_TEMPO_INTERNO_HORAS"))
                    .setHrEntrada(rSet.getTime("HRENTR"))
                    .setHrFechamento(TimeUtils.somaHoras(item.getHrEntrada(), rSet.getTime("TEMPOINTERNO")))
                    .calculaResultado();
            itens.add(item);
        }
        return itens;
    }

    public static List<Indicador> createExtratoTempoLargada(ResultSet rSet) throws SQLException{
        List<Indicador> itens = new ArrayList<>();
        while(rSet.next()){
            TempoLargada item = new TempoLargada();
            item.setData(rSet.getDate("DATA"))
                    .setMapa(rSet.getInt("MAPA"))
                    .setMeta(rSet.getTime("META_TEMPO_LARGADA_HORAS"))
                    .setHrMatinal(rSet.getTime("HRMATINAL"))
                    .setHrSaida(TimeUtils.toSqlTime(rSet.getTimestamp("HRSAI")))
                    .calculaResultado();
            itens.add(item);
        }
        return itens;
    }

    public static List<Indicador> createExtratoTempoRota(ResultSet rSet) throws SQLException{
        List<Indicador> itens = new ArrayList<>();
        while(rSet.next()){
            TempoRota item = new TempoRota();
            item.setData(rSet.getDate("DATA"))
                    .setMapa(rSet.getInt("MAPA"))
                    .setMeta(rSet.getTime("META_TEMPO_ROTA_HORAS"))
                    .setHrSaida(TimeUtils.toSqlTime(rSet.getTimestamp("HRSAI")))
                    .setHrEntrada(rSet.getTime("HRENTR"))
                    .calculaResultado();
            itens.add(item);
        }
        return itens;
    }
}
