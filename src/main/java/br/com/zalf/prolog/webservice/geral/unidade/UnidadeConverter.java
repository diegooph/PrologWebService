package br.com.zalf.prolog.webservice.geral.unidade;

import br.com.zalf.prolog.webservice.geral.unidade._model.UnidadeVisualizacaoListagem;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.time.LocalDateTime;

/**
 * Created on 2020-03-12
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
public final class UnidadeConverter {

    private UnidadeConverter() {
        throw new IllegalStateException(UnidadeConverter.class.getSimpleName() + " cannot be instantiated!");
    }

    @NotNull
    static UnidadeVisualizacaoListagem createUnidadeVisualizacaoListagem(
            @NotNull final ResultSet rSet) throws Throwable {
        return new UnidadeVisualizacaoListagem(rSet.getLong("CODIGO_UNIDADE"),
                rSet.getString("NOME_UNIDADE"),
                rSet.getInt("TOTAL_COLABORADORES_UNIDADE"),
                rSet.getLong("CODIGO_REGIONAL_UNIDADE"),
                rSet.getString("NOME_REGIAO_REGIONAL_UNIDADE"),
                rSet.getString("TIMEZONE_UNIDADE"),
                rSet.getObject("DATA_HORA_CADASTRO_UNIDADE", LocalDateTime.class),
                rSet.getBoolean("STATUS_ATIVO_UNIDADE"),
                rSet.getString("CODIGO_AUXILIAR_UNIDADE"),
                rSet.getString("LATITUDE_UNIDADE"),
                rSet.getString("LONGITUDE_UNIDADE"));
    }

}
