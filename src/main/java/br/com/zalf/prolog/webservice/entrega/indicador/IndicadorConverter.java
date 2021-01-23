package br.com.zalf.prolog.webservice.entrega.indicador;

import br.com.zalf.prolog.webservice.commons.util.datetime.TimeUtils;
import br.com.zalf.prolog.webservice.entrega.indicador.acumulado.*;
import br.com.zalf.prolog.webservice.entrega.indicador.item.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jean on 02/09/16.
 * classe que converte os itens que vem em um ResultSet para os objetos
 */
public class IndicadorConverter {

    private static final String TAG = IndicadorConverter.class.getSimpleName();

    static List<Indicador> createExtratoCaixaViagem(ResultSet rSet)throws SQLException {
        List<Indicador> itens = new ArrayList<>();
        while (rSet.next()){
            itens.add(createCaixaViagem(rSet));
        }
        return itens;
    }

    static CaixaViagem createCaixaViagem(ResultSet rSet)throws SQLException{
        CaixaViagem item = new CaixaViagem();
        item.setData(rSet.getDate("DATA"))
                .setMapa(rSet.getInt("MAPA"))
                .setCxsCarregadas(rSet.getInt("CXCARREG"))
                .setViagens(1)
                .setMeta(rSet.getDouble("META_CAIXA_VIAGEM"))
                .calculaResultado();
        return item;
    }

    static List<Indicador> createExtratoDevHl(ResultSet rSet) throws SQLException{
        List<Indicador> itens = new ArrayList<>();
        while(rSet.next()){
            itens.add(createDevHl(rSet));
        }
        return itens;
    }

    static DevHl createDevHl(ResultSet rSet)throws SQLException{
        DevHl item = new DevHl();
        item.setData(rSet.getDate("DATA"))
                .setMapa(rSet.getInt("MAPA"))
                .setOk(rSet.getDouble("QTHLENTREGUES"))
                .setNok(rSet.getDouble("QTHLCARREGADOS") - item.getOk())
                .setMeta(rSet.getDouble("META_DEV_HL"))
                .calculaResultado();
        return item;
    }

    static List<Indicador> createExtratoDevNf(ResultSet rSet) throws SQLException{
        List<Indicador> itens = new ArrayList<>();
        while(rSet.next()){
            itens.add(createDevNf(rSet));
        }
        return itens;
    }

    static DevNf createDevNf(ResultSet rSet)throws SQLException{
        DevNf item = new DevNf();
        item.setData(rSet.getDate("DATA"))
                .setMapa(rSet.getInt("MAPA"))
                .setOk(rSet.getDouble("QTNFENTREGUES"))
                .setNok(rSet.getDouble("QTNFCARREGADAS") - item.getOk())
                .setMeta(rSet.getDouble("META_DEV_NF"))
                .calculaResultado();
        return item;
    }

    static List<Indicador> createExtratoDevPdv(ResultSet rSet) throws SQLException{
        List<Indicador> itens = new ArrayList<>();
        while(rSet.next()){
            itens.add(createDevPdv(rSet));
        }
        return itens;
    }

    static DevPdv createDevPdv(ResultSet rSet) throws SQLException{
        DevPdv item = new DevPdv();
        item.setData(rSet.getDate("DATA"))
                .setMapa(rSet.getInt("MAPA"))
                .setOk(rSet.getDouble("ENTREGASCOMPLETAS") + rSet.getDouble("ENTREGASPARCIAIS"))
                .setNok(rSet.getDouble("ENTREGASNAOREALIZADAS"))
                .setMeta(rSet.getDouble("META_DEV_PDV"))
                .calculaResultado();
        return item;
    }

    static List<Indicador> createExtratoDispersaoKm(ResultSet rSet) throws SQLException{
        List<Indicador> itens = new ArrayList<>();
        while(rSet.next()){
            itens.add(createDispersaoKm(rSet));
        }
        return itens;
    }

    static DispersaoKm createDispersaoKm(ResultSet rSet) throws SQLException{
        DispersaoKm item = new DispersaoKm();
        item.setData(rSet.getDate("DATA"))
                .setMapa(rSet.getInt("MAPA"))
                .setMeta(rSet.getDouble("META_DISPERSAO_KM"))
                .setKmPlanejado(rSet.getDouble("KMPREVISTOROAD"))
                .setKmPercorrido(rSet.getDouble("KMENTR") - rSet.getDouble("KMSAI"))
                .calculaResultado();
        return item;
    }

    static List<Indicador> createExtratoTracking(ResultSet rSet) throws SQLException{
        List<Indicador> itens = new ArrayList<>();
        while(rSet.next()){
            itens.add(createTracking(rSet));
        }
        return itens;
    }

    static Tracking createTracking(ResultSet rSet)throws SQLException{
        Tracking item = new Tracking();
        item.setData(rSet.getDate("DATA"))
                .setMapa(rSet.getInt("MAPA"))
                .setOk(rSet.getInt("APONTAMENTOS_OK"))
                .setNok(rSet.getInt("TOTAL_TRACKING") - item.getOk())
                .setMeta(rSet.getDouble("META_TRACKING"))
                .calculaResultado();
        return item;
    }

    static List<Indicador> createExtratoDispersaoTempo(ResultSet rSet) throws SQLException{
        List<Indicador> itens = new ArrayList<>();
        while(rSet.next()){
            itens.add(createDispersaoTempo(rSet));
        }
        return itens;
    }

    static DispersaoTempo createDispersaoTempo(ResultSet rSet)throws SQLException{
        DispersaoTempo item = new DispersaoTempo();
        item.setData(rSet.getDate("DATA"))
                .setMapa(rSet.getInt("MAPA"))
                .setMeta(rSet.getDouble("META_DISPERSAO_TEMPO"))
                .setPrevisto(Duration.ofSeconds(rSet.getInt("TEMPOPREVISTOROAD")))
                .setRealizado(Duration.ofSeconds(rSet.getInt("TEMPO_ROTA")))
                .calculaResultado();
        return item;
    }

    static List<Indicador> createExtratoJornada(ResultSet rSet) throws SQLException{
        List<Indicador> itens = new ArrayList<>();
        while(rSet.next()){
            itens.add(createJornada(rSet));
        }
        return itens;
    }

    static Jornada createJornada(ResultSet rSet)throws SQLException{
        Jornada item = new Jornada();
        item.setData(rSet.getDate("DATA"))
                .setMapa(rSet.getInt("MAPA"))
                .setMeta(Duration.ofSeconds(rSet.getInt("META_JORNADA_LIQUIDA_HORAS")))
                .setTempoLargada(Duration.ofSeconds(rSet.getInt("TEMPO_LARGADA")))
                .setTempoInterno(Duration.ofSeconds(rSet.getInt("TEMPOINTERNO")))
                .setTempoRota(Duration.ofSeconds(rSet.getInt("TEMPO_ROTA")))
                .calculaResultado();
        return item;
    }

    static List<Indicador> createExtratoTempoInterno(ResultSet rSet) throws SQLException{
        List<Indicador> itens = new ArrayList<>();
        while(rSet.next()){
            itens.add(createTempoInterno(rSet));
        }
        return itens;
    }

    static TempoInterno createTempoInterno(ResultSet rSet)throws SQLException{
        TempoInterno item = new TempoInterno();
        item.setData(rSet.getDate("DATA"))
                .setMapa(rSet.getInt("MAPA"))
                .setMeta(Duration.ofSeconds(rSet.getInt("META_TEMPO_INTERNO_HORAS")))
                .setHrEntrada(rSet.getTime("HRENTR"))
                .setHrFechamento(new Time(item.getHrEntrada().getTime() + Duration.ofSeconds(rSet.getInt("TEMPOINTERNO")).toMillis()))
//                    .setHrFechamento(TimeUtils.somaHoras(item.getHrEntrada(), rSet.getTime("TEMPOINTERNO")))
                .calculaResultado();
        return item;
    }

    static List<Indicador> createExtratoTempoLargada(ResultSet rSet) throws SQLException{
        List<Indicador> itens = new ArrayList<>();
        while(rSet.next()){
            itens.add(createTempoLargada(rSet));
        }
        return itens;
    }

    static TempoLargada createTempoLargada(ResultSet rSet)throws SQLException{
        TempoLargada item = new TempoLargada();
        item.setData(rSet.getDate("DATA"))
                .setMapa(rSet.getInt("MAPA"))
                .setMeta(Duration.ofSeconds(rSet.getInt("META_TEMPO_LARGADA_HORAS")))
                .setHrMatinal(rSet.getTime("HRMATINAL"))
                .setHrSaida(TimeUtils.toSqlTime(rSet.getTimestamp("HRSAI")))
                .calculaResultado();
        return item;
    }

    static List<Indicador> createExtratoTempoRota(ResultSet rSet) throws SQLException{
        List<Indicador> itens = new ArrayList<>();
        while(rSet.next()){
            itens.add(createTempoRota(rSet));
        }
        return itens;
    }

    static TempoRota createTempoRota(ResultSet rSet)throws SQLException{
        TempoRota item = new TempoRota();
        item.setData(rSet.getDate("DATA"))
                .setMapa(rSet.getInt("MAPA"))
                .setMeta(Duration.ofSeconds(rSet.getInt("META_TEMPO_ROTA_HORAS")))
                .setHrSaida(TimeUtils.toSqlTime(rSet.getTimestamp("HRSAI")))
                .setHrEntrada(rSet.getTime("HRENTR"))
                .calculaResultado();
        return item;
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
        item.setTotalOk(rSet.getInt("HL_CARREGADOS_TOTAL") - rSet.getInt("HL_DEVOLVIDOS_TOTAL"))
                .setTotalNok(rSet.getInt("HL_DEVOLVIDOS_TOTAL"))
                .setMeta(rSet.getDouble("META_DEV_HL"))
                .calculaResultado();
        return item;
    }

    static IndicadorAcumulado createAcumuladoDevNf(ResultSet rSet) throws SQLException{
        DevNfAcumulado item = new DevNfAcumulado();
        item.setTotalOk(rSet.getInt("NF_CARREGADAS_TOTAL") - rSet.getInt("NF_DEVOLVIDAS_TOTAL"))
                .setTotalNok(rSet.getInt("NF_DEVOLVIDAS_TOTAL"))
                .setMeta(rSet.getDouble("META_DEV_NF"))
                .calculaResultado();
        return item;
    }

    static IndicadorAcumulado createAcumuladoDevPdv(ResultSet rSet) throws SQLException{
        DevPdvAcumulado item = new DevPdvAcumulado();
        item.setTotalOk(rSet.getInt("PDV_CARREGADOS_TOTAL") - rSet.getInt("PDV_DEVOLVIDOS_TOTAL"))
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
                .calculaResultado();
        return item;
    }

    static IndicadorAcumulado createAcumuladoDispersaoTempoMedia(ResultSet rSet) throws SQLException{
        DispersaoTempoAcumuladoMedia item = new DispersaoTempoAcumuladoMedia();
        item.setPrevisto(Duration.ofSeconds(rSet.getInt("MEDIA_DISPERSAO_TEMPO_PLANEJADO")))
                .setRealizado(Duration.ofSeconds(rSet.getInt("MEDIA_DISPERSAO_TEMPO_REALIZADO")))
                .setMeta(rSet.getDouble("META_DISPERSAO_TEMPO"))
                .calculaResultado();
        return item;
    }

    static IndicadorAcumulado createAcumuladoJornadaMapas(ResultSet rSet) throws SQLException{
        JornadaAcumuladoMapas item = new JornadaAcumuladoMapas();
        item.setMapasOk(rSet.getInt("TOTAL_MAPAS_BATERAM_JORNADA"))
                .setMapasNok(rSet.getInt("VIAGENS_TOTAL") - rSet.getInt("TOTAL_MAPAS_BATERAM_JORNADA"))
                .setMeta(rSet.getDouble("META_JORNADA_LIQUIDA_MAPAS"))
                .calculaResultado();
        return item;
    }

    static IndicadorAcumulado createAcumuladoJornadaMedia(ResultSet rSet) throws SQLException{
        JornadaAcumuladoMedia item = new JornadaAcumuladoMedia();
        item.setResultado(Duration.ofSeconds(rSet.getInt("MEDIA_JORNADA")))
                .setMeta(Duration.ofSeconds(rSet.getInt("META_JORNADA_LIQUIDA_HORAS")))
                .calculaResultado();
        return item;
    }

    static IndicadorAcumulado createAcumuladoTempoInternoMapas(ResultSet rSet) throws SQLException{
        TempoInternoAcumuladoMapas item = new TempoInternoAcumuladoMapas();
        item.setMapasOk(rSet.getInt("TOTAL_MAPAS_BATERAM_TEMPO_INTERNO"))
                .setMapasNok(rSet.getInt("TOTAL_MAPAS_VALIDOS_TEMPO_INTERNO") - rSet.getInt("TOTAL_MAPAS_BATERAM_TEMPO_INTERNO"))
                .setMeta(rSet.getDouble("META_TEMPO_INTERNO_MAPAS"))
                .calculaResultado();
        return item;
    }

    static IndicadorAcumulado createAcumuladoTempoInternoMedia(ResultSet rSet) throws SQLException{
        TempoInternoAcumuladoMedia item = new TempoInternoAcumuladoMedia();
        item.setResultado(Duration.ofSeconds(rSet.getInt("MEDIA_TEMPO_INTERNO")))
                .setMeta(Duration.ofSeconds(rSet.getInt("META_TEMPO_INTERNO_HORAS")))
                .calculaResultado();
        return item;
    }

    static IndicadorAcumulado createAcumuladoTempoLargadaMapas(ResultSet rSet) throws SQLException{
        TempoLargadaAcumuladoMapas item = new TempoLargadaAcumuladoMapas();
        item.setMapasOk(rSet.getInt("TOTAL_MAPAS_BATERAM_TEMPO_LARGADA"))
                .setMapasNok(rSet.getInt("TOTAL_MAPAS_VALIDOS_TEMPO_LARGADA") - rSet.getInt("TOTAL_MAPAS_BATERAM_TEMPO_LARGADA"))
                .setMeta(rSet.getDouble("META_TEMPO_LARGADA_MAPAS"))
                .calculaResultado();
        return item;
    }

    static IndicadorAcumulado createAcumuladoTempoLargadaMedia(ResultSet rSet) throws SQLException{
        TempoLargadaAcumuladoMedia item = new TempoLargadaAcumuladoMedia();
        item.setResultado(Duration.ofSeconds(rSet.getInt("MEDIA_TEMPO_LARGADA")))
                .setMeta(Duration.ofSeconds(rSet.getInt("META_TEMPO_LARGADA_HORAS")))
                .calculaResultado();
        return item;
    }

    static IndicadorAcumulado createAcumuladoTempoRotaMapas(ResultSet rSet) throws SQLException{
        TempoRotaAcumuladoMapas item = new TempoRotaAcumuladoMapas();
        item.setMapasOk(rSet.getInt("TOTAL_MAPAS_BATERAM_TEMPO_ROTA"))
                .setMapasNok(rSet.getInt("VIAGENS_TOTAL") - rSet.getInt("TOTAL_MAPAS_BATERAM_TEMPO_ROTA"))
                .setMeta(rSet.getDouble("META_TEMPO_ROTA_MAPAS"))
                .calculaResultado();
        return item;
    }

    static IndicadorAcumulado createAcumuladoTempoRotaMedia(ResultSet rSet) throws SQLException{
        TempoRotaAcumuladoMedia item = new TempoRotaAcumuladoMedia();
        item.setResultado(Duration.ofSeconds(rSet.getInt("MEDIA_TEMPO_ROTA")))
                .setMeta(Duration.ofSeconds(rSet.getInt("META_TEMPO_ROTA_HORAS")))
                .calculaResultado();
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

