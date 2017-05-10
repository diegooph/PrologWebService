package br.com.zalf.prolog.webservice.frota.pneu.afericao;

import br.com.zalf.prolog.webservice.frota.pneu.pneu.Restricao;

import java.sql.SQLException;
import java.util.List;

public interface AfericaoDao {

    /**
     * insere uma aferição lincada com o código da unidade
     *
     * @param afericao   uma aferição
     * @param codUnidade código da unidade
     * @return valor da operação
     * @throws SQLException se ocorrer erro no banco
     */
    boolean insert(Afericao afericao, Long codUnidade) throws SQLException;

    /**
     * adiciona uma aferição ao veículo da placa
     *
     * @param placa placa do veículo
     * @return retorna uma nova aferição
     * @throws SQLException se ocorrer erro no banco
     */
    NovaAfericao getNovaAfericao(String placa) throws SQLException;

    /**
     * retorna as restrições de medidas da unidade
     *
     * @param codUnidade código da unidade
     * @return a restrição da unidade
     * @throws SQLException se ocorrer erro no banco
     */
    Restricao getRestricoesByCodUnidade(Long codUnidade) throws SQLException;

    /**
     * retorna as restrições de medidas da placa
     *
     * @param placa placa do veículo
     * @return a restrição da placa
     * @throws SQLException se ocorrer erro no banco
     */
    Restricao getRestricoesByPlaca(String placa) throws SQLException;

    //TODO - comentar o javadoc
    SelecaoPlacaAfericao getSelecaoPlacaAfericao(Long codUnidade) throws SQLException;

    /**
     * pega a lista de aferições executadas
     *
     * @param codUnidades código da unidade
     * @param placas      placa do veículo
     * @param limit       limite de busca de dados no banco
     * @param offset      offset de busca no banco de dados
     * @return uma lista de aferições
     * @throws SQLException se ocorrer erro no banco
     */
    List<Afericao> getAfericoesByCodUnidadeByPlaca(List<String> codUnidades, List<String> placas, long limit, long offset) throws SQLException;

    /**
     * retorna uma aferição através do código dela
     *
     * @param codAfericao código da aferição
     * @param codUnidade  código da unidade
     * @return a aferição
     * @throws SQLException se ocorrer erro no banco
     */
    Afericao getByCod(Long codAfericao, Long codUnidade) throws SQLException;

    /**
     * Atualiza uma aferição
     *
     * @param afericao
     * @return
     * @throws SQLException
     */
    boolean update(Afericao afericao) throws SQLException;
}
