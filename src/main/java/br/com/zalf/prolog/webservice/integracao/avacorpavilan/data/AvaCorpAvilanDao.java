package br.com.zalf.prolog.webservice.integracao.avacorpavilan.data;

import br.com.zalf.prolog.webservice.integracao.avacorpavilan.cadastro.TipoVeiculoAvilan;
import com.sun.istack.internal.NotNull;

import java.sql.SQLException;
import java.util.List;

/**
 * Created on 10/11/17
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public interface AvaCorpAvilanDao {

    @NotNull
    List<TipoVeiculoAvilanProLog> getTiposVeiculosAvilanProLog() throws SQLException;

    /**
     * Insere um tipo de veículo da Avilan no banco de dados e retorna o código gerado que será utilizado pelo ProLog.
     *
     * @param tipoVeiculoAvilan um {@link TipoVeiculoAvilan tipo de veículo}.
     * @return o código que será utilizado pelo ProLog equivalente ao tipo recém inserido.
     * @throws SQLException caso aconteça algo de errado no insert.
     */
    @NotNull
    Long insertTipoVeiculoAvilan(@NotNull final TipoVeiculoAvilan tipoVeiculoAvilan) throws SQLException;

    String getCodTipoVeiculoAvilanByCodTipoVeiculoProLog(@NotNull final Long codigo) throws Exception;
}