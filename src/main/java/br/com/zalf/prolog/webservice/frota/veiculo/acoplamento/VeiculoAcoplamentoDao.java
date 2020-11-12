package br.com.zalf.prolog.webservice.frota.veiculo.acoplamento;

import br.com.zalf.prolog.webservice.frota.veiculo.acoplamento._model.realizacao.VeiculoAcopladoMantido;
import br.com.zalf.prolog.webservice.frota.veiculo.acoplamento._model.realizacao.VeiculoAcoplamentoAcaoRealizada;
import br.com.zalf.prolog.webservice.frota.veiculo.acoplamento._model.realizacao.VeiculoAcoplamentoProcessoInsert;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.util.List;

/**
 * Created on 2020-11-03
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public interface VeiculoAcoplamentoDao {

    /**
     * Desfaz o acomplamento atual com o código de processo {@code codProcessoAcoplamento}. Isso significa que todos
     * os veículos que compõem o acoplamento com o código de processo informado ficarão desacoplados e livres para
     * outro engate após a execução deste método.
     * <p>
     * Como a {@link Connection connection} é recebida por parâmetro, nenhuma mudança é de fato efetivada em banco.
     * É preciso que quem chamou este método faça um commit.
     *
     * @param conn                   Uma conexão já estabelecida com o banco de dados.
     * @param codProcessoAcoplamento Código do processo de acoplamento que será desfeito.
     */
    void removeAcoplamentoAtual(@NotNull final Long codProcessoAcoplamento);

    /**
     * Insere um processo de acoplamento retornando seu código em caso de sucesso. Este método não realiza nenhum
     * acoplamento em si e nem salva histórico, apenas cria o processo inicial.
     * <p>
     * Como a {@link Connection connection} é recebida por parâmetro, nenhuma mudança é de fato efetivada em banco.
     * É preciso que quem chamou este método faça um commit.
     *
     * @param conn                Uma conexão já estabelecida com o banco de dados.
     * @param processoAcoplamento Objeto contendo informações do processo de acoplamento que será inserido.
     * @return O código do processo de acoplamento inserido.
     */
    @NotNull
    Long insertProcessoAcoplamento(@NotNull final VeiculoAcoplamentoProcessoInsert processoAcoplamento);

    /**
     * Insere os históricos do processo de acoplamento de código {@code codProcessoAcoplamento}. Em um mesmo processo,
     * veículos podem ser acoplados, desacoplados ou permanecerem na composição.
     * <p>
     * Como a {@link Connection connection} é recebida por parâmetro, nenhuma mudança é de fato efetivada em banco.
     * É preciso que quem chamou este método faça um commit.
     *
     * @param conn                   Uma conexão já estabelecida com o banco de dados.
     * @param codProcessoAcoplamento O código do processo de acoplamento para o qual os históricos serão inseridos.
     * @param acoesRealizadas        As ações que foram realizadas neste processo de acoplamento.
     */
    void insertHistoricoAcoesRealizadas(@NotNull final Long codProcessoAcoplamento,
                                        @NotNull final List<VeiculoAcoplamentoAcaoRealizada> acoesRealizadas);

    /**
     * Insere o estado atual dos acoplamentos no banco de dados. Isso quer dizer que: os veículos presentes na lista
     * {@code veiculosAcopladosMantidos} serão de fato acoplados no banco de dados.
     * <p>
     * Como a {@link Connection connection} é recebida por parâmetro, nenhuma mudança é de fato efetivada em banco.
     * É preciso que quem chamou este método faça um commit.
     *
     * @param conn                      Uma conexão já estabelecida com o banco de dados.
     * @param veiculosAcopladosMantidos Os acoplamentos realizados ou mantidos nesse processo de acoplamento.
     */
    void insertEstadoAtualAcoplamentos(@NotNull final List<VeiculoAcopladoMantido> veiculosAcopladosMantidos);
}
