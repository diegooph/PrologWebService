package br.com.zalf.prolog.webservice.entrega.indicador;

import br.com.zalf.prolog.webservice.entrega.indicador.acumulado.IndicadorAcumulado;
import br.com.zalf.prolog.webservice.entrega.indicador.item.Jornada;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by didi on 04/07/17.
 */
public interface IndicadorDao {

    /**
     * Cria o extrato acumulado INDIVIDUAL dos indicadores, serve apenas para a tela indicadores.
     *
     * @param dataInicial uma data em milisegundos
     * @param dataFinal   uma data em milisegundos
     * @param cpf         cpf do colaborador ao qual será realizada a busca
     * @return uma lista de {@link IndicadorAcumulado}
     * @throws SQLException caso não seja possível realizar a busca
     */
    List<IndicadorAcumulado> getAcumuladoIndicadoresIndividual(Long dataInicial,
                                                               Long dataFinal,
                                                               Long cpf) throws SQLException;

    /**
     * Busca o extrato por mapa de qualquer indicador, usa distinct para não repetir os mapas.
     *
     * @param dataInicial uma data em milisegundos
     * @param dataFinal   uma data em milisegundos
     * @param codRegional código da Regional ou '%'
     * @param codEmpresa  código da Empresa ou '%'
     * @param codUnidade  código da Unidade ou '%'
     * @param equipe      nome da equipe ou '%'
     * @param cpf         cpf ou '%'
     * @param indicador   constante provinda dos ITENS  ex: {@link Jornada#JORNADA}
     * @return uma lista de Indicador {@link Indicador}
     * @throws SQLException caso não seja possível realizar a busca no BD
     */
    List<Indicador> getExtratoIndicador(Long dataInicial,
                                        Long dataFinal,
                                        String codRegional,
                                        String codEmpresa,
                                        String codUnidade,
                                        String equipe,
                                        String cpf,
                                        String indicador) throws SQLException;
}
