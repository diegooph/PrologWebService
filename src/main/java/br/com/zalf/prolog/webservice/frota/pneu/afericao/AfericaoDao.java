package br.com.zalf.prolog.webservice.frota.pneu.afericao;

import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.Afericao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.CronogramaAfericao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.NovaAfericao;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.Restricao;

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
     * Atualiza uma aferição
     *
     * @param afericao      objeto {@link Afericao} para ser atualizado
     * @return              verdadeiro se operação for sucesso, falso caso contrário
     * @throws SQLException se ocorrer erro no banco
     */
    boolean update(Afericao afericao) throws SQLException;

    /**
     * adiciona uma aferição ao veículo da placa
     *
     * @param placa placa do veículo
     * @param tipoAfericao tipo da aferição que será realizada
     * @return retorna uma nova aferição
     * @throws SQLException se ocorrer erro no banco
     */
    NovaAfericao getNovaAfericao(String placa, String tipoAfericao) throws SQLException;

    /**
     * retorna as restrições de medidas da unidade
     *
     * @param codUnidade código da unidade
     * @return a restrição da unidade
     * @throws SQLException se ocorrer erro no banco
     */
    Restricao getRestricaoByCodUnidade(Long codUnidade) throws SQLException;

    /**
     * retorna as restrições de medidas da placa
     *
     * @param placa placa do veículo
     * @return a restrição da placa
     * @throws SQLException se ocorrer erro no banco
     */
    Restricao getRestricoesByPlaca(String placa) throws SQLException;

    /**
     * retorna a lista de placas da unidade e também a meta de
     * dias em que cada placa deve ser aferido
     *
     * @param codUnidade    código da unidade
     * @return              um {@link CronogramaAfericao} contendo as placas para ser aferidas
     * @throws SQLException para qualquer erro do banco
     */
    CronogramaAfericao getCronogramaAfericao(Long codUnidade) throws SQLException;

    List<Afericao> getAfericoes(Long codUnidade, String codTipoVeiculo, String placaVeiculo, long dataInicial,
                                long dataFinal, int limit, long offset) throws SQLException;

    /**
     * retorna uma aferição através do código dela
     *
     * @param codUnidade  código da unidade
     * @param codAfericao código da aferição
     * @return a aferição
     * @throws SQLException se ocorrer erro no banco
     */
    Afericao getByCod(Long codUnidade, Long codAfericao) throws SQLException;

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
    @Deprecated
    List<Afericao> getAfericoesByCodUnidadeByPlaca(List<String> codUnidades, List<String> placas, int limit, long offset) throws SQLException;
}
