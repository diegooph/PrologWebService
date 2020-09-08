package br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.data;

import br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.cadastro.TipoVeiculoAvilan;
import com.google.common.collect.BiMap;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created on 10/11/17
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public interface AvaCorpAvilanDao {

    @Nonnull
    List<TipoVeiculoAvilanProLog> getTiposVeiculosAvilanProLog() throws SQLException;

    /**
     * Insere um tipo de veículo da Avilan no banco de dados e retorna o código gerado que será utilizado pelo ProLog.
     *
     * @param tipoVeiculoAvilan um {@link TipoVeiculoAvilan tipo de veículo}.
     * @return o código que será utilizado pelo ProLog equivalente ao tipo recém inserido.
     * @throws SQLException caso aconteça algo de errado no insert.
     */
    @Nonnull
    Long insertTipoVeiculoAvilan(@NotNull final TipoVeiculoAvilan tipoVeiculoAvilan) throws SQLException;

    @Nonnull
    String getCodTipoVeiculoAvilanByCodTipoVeiculoProLog(@NotNull final Long codigo) throws SQLException;

    @Nonnull
    FilialUnidadeAvilanProLog getFilialUnidadeAvilanByCodUnidadeProLog(@NotNull final Long codUnidadeProLog)
            throws SQLException;

    @Nonnull
    Short getCodDiagramaVeiculoProLogByCodTipoVeiculoAvilan(@Nonnull final String codTipoVeiculoAvilan)
            throws SQLException;

    /**
     * O {@link BiMap BiMap} retornado contém como chave a posição do pneu utlizada pela Avilan e como valor a posição
     * equivalente no ProLog. Com o BiMap temos garantia que os valores também não se repetem, pois não podem haver
     * posições diferentes na Avilan mapeadas para a mesma posição no ProLog.
     *
     * @return um {@link BiMap BiMap}.
     */
    @Nonnull
    BiMap<String, Integer> getPosicoesPneuAvilanProLogByCodTipoVeiculoAvilan(@Nonnull final String codTipoVeiculoAvilan)
            throws SQLException;

    Map<Long, String> getMapeamentoCodPerguntaUrlImagem(final Long codQuestionario) throws SQLException;

    /**
     * Busca a PLACA através do código do veículo.
     *
     * @param codVeiculo Código do veículo no Prolog.
     * @return a placa do veículo no Prolog.
     * @throws SQLException caso aconteça algo de errado na busca.
     */
    @Nonnull
    String getPlacaByCodVeiculo(@Nonnull final Long codVeiculo) throws SQLException;

}