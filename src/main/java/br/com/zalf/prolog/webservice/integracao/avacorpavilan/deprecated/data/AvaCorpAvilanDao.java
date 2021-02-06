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

    @Nonnull
    BiMap<String, Integer> getPosicoesPneuAvilanProLogByCodTipoVeiculoAvilan(@Nonnull final String codTipoVeiculoAvilan)
            throws SQLException;

    Map<Long, String> getMapeamentoCodPerguntaUrlImagem(final Long codQuestionario) throws SQLException;

    @Nonnull
    String getPlacaByCodVeiculo(@Nonnull final Long codVeiculo) throws SQLException;

}