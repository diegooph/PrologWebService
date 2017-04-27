package br.com.zalf.prolog.seguranca.dashboard;
import br.com.zalf.prolog.commons.colaborador.Colaborador;
import br.com.zalf.prolog.seguranca.ocorrencia.Local;
import br.com.zalf.prolog.seguranca.relato.Relato;

import java.sql.Time;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by jean on 25/03/16.
 */
public class DashSeguranca {


    public Date dataInicial;
    public Date dataFinal;
    public int qtRelatosHoje; //total de relatos recebidos hoje
    public int qtRelatosMes; // total de relatos recebidos no mês
    public int qtRelatosTotal;//total de relatos da unidade
    public int qtRelatosMesAnterior; // total de relatos recebidos M-1
    public int qtRelatosMesmoPeriodoMesAnterior; // total de relatos recebidos no M-1 até o mesmo dia atual
    public int metaRelatos; // meta de relatos no mês
    public int qtRelatosResolvidos; // relatos resolvidos no mês
    public int qtRelatosEmAberto; // relatos em aberto no mês
    public double qtPorcentagemRelatosResolvidos; // % de relatos resolvidos no mes
    public double qtPorcentagemRelatosAbertos; // % de relatos em aberto no mês
    public Map<String,Integer > mapRelatosByCategoria; // Escorregão 3
    public Map<String,Integer> mapRelatosByFuncao; // Motorista 2
    public Map<String,Integer> mapRelatosByEquipe; // Sala 1 22
    public Map<Date,Integer> mapRelatosByMes; // Janeiro 4
    public List<Local> listLocalRelatos; //Map com latitude e longitude
    public Map<Colaborador, Integer> mapRelatosByColaborador; // total de relatos realizados por colaborador no mês atual
    public Time tempoMedioResolucaoRelato; // tempo médio para resolução de um relato;
    public List<Relato> listRelatosEmAberto; // relatos em aberto, ordenar por mais antigo primeiro
    public List<Relato> listRelatosRecentementeFechados; // relatos fechados ordenados por data de fechamento,
    // de forma que o mais recentemente fechado seja exibido primeiro

    public int qtGsdHoje; // total de gsd realizadas hoje
    public int qtGsdMes; // total de gsd realizadas no mês

    @Override
    public String toString() {
        return "DashSeguranca{" +
                "dataInicial=" + dataInicial +
                ", dataFinal=" + dataFinal +
                ", qtRelatosHoje=" + qtRelatosHoje +
                ", qtRelatosMes=" + qtRelatosMes +
                ", qtRelatosTotal=" + qtRelatosTotal +
                ", qtRelatosMesAnterior=" + qtRelatosMesAnterior +
                ", qtRelatosMesmoPeriodoMesAnterior=" + qtRelatosMesmoPeriodoMesAnterior +
                ", metaRelatos=" + metaRelatos +
                ", qtRelatosResolvidos=" + qtRelatosResolvidos +
                ", qtRelatosEmAberto=" + qtRelatosEmAberto +
                ", qtPorcentagemRelatosResolvidos=" + qtPorcentagemRelatosResolvidos +
                ", qtPorcentagemRelatosAbertos=" + qtPorcentagemRelatosAbertos +
                ", mapRelatosByCategoria=" + mapRelatosByCategoria +
                ", mapRelatosByFuncao=" + mapRelatosByFuncao +
                ", mapRelatosByEquipe=" + mapRelatosByEquipe +
                ", mapRelatosByMes=" + mapRelatosByMes +
                ", listLocalRelatos=" + listLocalRelatos +
                ", mapRelatosByColaborador=" + mapRelatosByColaborador +
                ", tempoMedioResolucaoRelato=" + tempoMedioResolucaoRelato +
                ", listRelatosEmAberto=" + listRelatosEmAberto +
                ", listRelatosRecentementeFechados=" + listRelatosRecentementeFechados +
                ", qtGsdHoje=" + qtGsdHoje +
                ", qtGsdMes=" + qtGsdMes +
                '}';
    }
}
