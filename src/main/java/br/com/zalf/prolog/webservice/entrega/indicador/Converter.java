package br.com.zalf.prolog.webservice.entrega.indicador;

import br.com.zalf.prolog.commons.util.MetaUtils;
import br.com.zalf.prolog.commons.util.TimeUtils;
import br.com.zalf.prolog.entrega.indicador.indicadores.acumulado.*;
import br.com.zalf.prolog.entrega.indicador.indicadores.item.*;
import br.com.zalf.prolog.webservice.util.GsonUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jean on 02/09/16.
 * classe que converte os itens que vem em um ResultSet para os objetos
 */
public class Converter {

    private static final String TAG = Converter.class.getSimpleName();

    static List<Indicador> createExtratoCaixaViagem(ResultSet rSet)throws SQLException {
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

    static List<Indicador> createExtratoDevHl(ResultSet rSet) throws SQLException{
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

    static List<Indicador> createExtratoDevPdv(ResultSet rSet) throws SQLException{
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

    static List<Indicador> createExtratoDispersaoKm(ResultSet rSet) throws SQLException{
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

    static List<Indicador> createExtratoTracking(ResultSet rSet) throws SQLException{
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

    static List<Indicador> createExtratoDispersaoTempo(ResultSet rSet) throws SQLException{
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

    static List<Indicador> createExtratoJornada(ResultSet rSet) throws SQLException{
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

    static List<Indicador> createExtratoTempoInterno(ResultSet rSet) throws SQLException{
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

    static List<Indicador> createExtratoTempoLargada(ResultSet rSet) throws SQLException{
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

    static List<Indicador> createExtratoTempoRota(ResultSet rSet) throws SQLException{
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

    /*
    Criação dos objetos acumulados
     */

    static IndicadorAcumulado createAcumuladoCaixaViagem(ResultSet rSet) throws SQLException {
        CaixaViagemAcumulado item = new CaixaViagemAcumulado();
        item.setCxsCarregadasTotal(rSet.getInt("CARREGADAS_TOTAL"))
                .setViagensTotal(rSet.getInt("VIAGENS_TOTAL"))
                .setMeta(rSet.getDouble("META_CAIXA_VIAGEM"))
                .calculaResultado();
        return item;
    }

    static IndicadorAcumulado createAcumuladoDevHl(ResultSet rSet) throws SQLException{
        DevHlAcumulado item = new DevHlAcumulado();
        item.setTotalOk(rSet.getInt("HL_CARREGADOS_TOTAL"))
                .setTotalNok(rSet.getInt("HL_DEVOLVIDOS_TOTAL"))
                .setMeta(rSet.getDouble("META_DEV_HL"))
                .calculaResultado();
        return item;
    }

    static IndicadorAcumulado createAcumuladoDevPdv(ResultSet rSet) throws SQLException{
        DevPdvAcumulado item = new DevPdvAcumulado();
        item.setTotalOk(rSet.getInt("PDV_CARREGADOS_TOTAL"))
                .setTotalNok(rSet.getInt("PDV_DEVOLVIDOS_TOTAL"))
                .setMeta(rSet.getDouble("META_DEV_PDV"))
                .calculaResultado();
        return item;
    }

    static IndicadorAcumulado createAcumuladoDispersaoKm(ResultSet rSet) throws SQLException{
        DispersaoKmAcumulado item = new DispersaoKmAcumulado();
        item.setKmPercorridoTotal(rSet.getInt("KM_PERCORRIDO_TOTAL"))
                .setKmPlanejadoTotal(rSet.getInt("KM_PLANEJADO_TOTAL"))
                .setMeta(rSet.getDouble("META_DISPERSAO_KM"))
                .calculaResultado();
        return  item;
    }

    static IndicadorAcumulado createAcumuladoDispersaoTempoMapas(ResultSet rSet) throws SQLException{
        DispersaoTempoAcumuladoMapas item = new DispersaoTempoAcumuladoMapas();
        item.setMapasOk(rSet.getInt("TOTAL_MAPAS_BATERAM_DISPERSAO_TEMPO"))
                .setMapasNok(rSet.getInt("VIAGENS_TOTAL") - rSet.getInt("TOTAL_MAPAS_BATERAM_DISPERSAO_TEMPO"))
                .setMeta(rSet.getDouble("META_DISPERSAO_TEMPO"))
                .calculaResultadoMapas();
        return item;
    }

    static IndicadorAcumulado createAcumuladoDispersaoTempoMedia(ResultSet rSet) throws SQLException{
        DispersaoTempoAcumuladoMedia item = new DispersaoTempoAcumuladoMedia();
        item.setPlanejado(rSet.getTime("MEDIA_DISPERSAO_TEMPO_PLANEJADO"))
                .setRealizado(rSet.getTime("MEDIA_DISPERSAO_TEMPO_REALIZADO"))
                .setMeta(rSet.getDouble("META_DISPERSAO_TEMPO"))
                .calculaResultado();
        return item;
    }

    static IndicadorAcumulado createAcumuladoJornadaMapas(ResultSet rSet) throws SQLException{
        JornadaAcumuladoMapas item = new JornadaAcumuladoMapas();
        item.setMapasOk(rSet.getInt("TOTAL_MAPAS_BATERAM_JORNADA"))
                .setMapasNok(rSet.getInt("VIAGENS_TOTAL") - rSet.getInt("TOTAL_MAPAS_BATERAM_JORNADA"))
                .setMeta(rSet.getDouble("META_JORNADA_LIQUIDA_MAPAS"))
                .calculaResultadoMapas();
        return item;
    }

    static IndicadorAcumulado createAcumuladoJornadaMedia(ResultSet rSet) throws SQLException{
        JornadaAcumuladoMedia item = new JornadaAcumuladoMedia();
        item.setResultado(rSet.getTime("MEDIA_JORNADA"))
                .setMeta(rSet.getTime("META_JORNADA_LIQUIDA_HORAS"))
                .calculaResultado();
        return item;
    }

    static IndicadorAcumulado createAcumuladoTempoInternoMapas(ResultSet rSet) throws SQLException{
        TempoInternoAcumuladoMapas item = new TempoInternoAcumuladoMapas();
        item.setMapasOk(rSet.getInt("TOTAL_MAPAS_BATERAM_TEMPO_INTERNO"))
                .setMapasNok(rSet.getInt("TOTAL_MAPAS_VALIDOS_TEMPO_INTERNO") - rSet.getInt("TOTAL_MAPAS_BATERAM_TEMPO_INTERNO"))
                .setMeta(rSet.getDouble("META_TEMPO_INTERNO_MAPAS"))
                .calculaResultadoMapas();
        return item;
    }

    static IndicadorAcumulado createAcumuladoTempoInternoMedia(ResultSet rSet) throws SQLException{
        TempoInternoAcumuladoMedia item = new TempoInternoAcumuladoMedia();
        item.setResultado(rSet.getTime("MEDIA_TEMPO_INTERNO"))
                .setMeta(rSet.getTime("META_TEMPO_INTERNO_HORAS"));
        return item;
    }

    static IndicadorAcumulado createAcumuladoTempoLargadaMapas(ResultSet rSet) throws SQLException{
        TempoLargadaAcumuladoMapas item = new TempoLargadaAcumuladoMapas();
        item.setMapasOk(rSet.getInt("TOTAL_MAPAS_BATERAM_TEMPO_LARGADA"))
                .setMapasNok(rSet.getInt("TOTAL_MAPAS_VALIDOS_TEMPO_LARGADA") - rSet.getInt("TOTAL_MAPAS_BATERAM_TEMPO_LARGADA"))
                .setMeta(rSet.getDouble("META_TEMPO_LARGADA_MAPAS"))
                .calculaResultadoMapas();
        return item;
    }

    static IndicadorAcumulado createAcumuladoTempoLargadaMedia(ResultSet rSet) throws SQLException{
        TempoLargadaAcumuladoMedia item = new TempoLargadaAcumuladoMedia();
        item.setResultado(rSet.getTime("MEDIA_TEMPO_LARGADA"))
                .setMeta(rSet.getTime("META_TEMPO_LARGADA_HORAS"))
                .calculaResultado();
        return item;
    }

    static IndicadorAcumulado createAcumuladoTempoRotaMapas(ResultSet rSet) throws SQLException{
        TempoRotaAcumuladoMapas item = new TempoRotaAcumuladoMapas();
        item.setMapasOk(rSet.getInt("TOTAL_MAPAS_BATERAM_TEMPO_ROTA"))
                .setMapasNok(rSet.getInt("VIAGENS_TOTAL") - rSet.getInt("TOTAL_MAPAS_BATERAM_TEMPO_ROTA"))
                .setMeta(rSet.getDouble("META_TEMPO_ROTA_MAPAS"))
                .calculaResultadoMapas();
        return item;
    }

    static IndicadorAcumulado createAcumuladoTempoRotaMedia(ResultSet rSet) throws SQLException{
        TempoRotaAcumuladoMedia item = new TempoRotaAcumuladoMedia();
        item.setResultado(rSet.getTime("MEDIA_TEMPO_ROTA"))
                .setMeta(rSet.getTime("META_TEMPO_ROTA_HORAS"))
                .calculaResultado();
        Duration dur = Duration.ofMillis(item.getResultado().getTime());
        System.out.println(GsonUtils.getGson().toJson(dur.toHours()));
        System.out.println(GsonUtils.getGson().toJson(dur.toMinutes()));

        return item;
    }

    static IndicadorAcumulado createAcumuladoTracking(ResultSet rSet) throws SQLException{
        TrackingAcumulado item = new TrackingAcumulado();
        item.setTotalOk(rSet.getInt("TOTAL_APONTAMENTOS_OK"))
                .setTotalNok(rSet.getInt("TOTAL_APONTAMENTOS") - rSet.getInt("TOTAL_APONTAMENTOS_OK"))
                .setMeta(rSet.getDouble("META_TRACKING"))
                .calculaResultado();
        return item;
    }
}

