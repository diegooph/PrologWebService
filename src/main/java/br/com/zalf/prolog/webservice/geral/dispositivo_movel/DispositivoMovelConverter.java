package br.com.zalf.prolog.webservice.geral.dispositivo_movel;

import br.com.zalf.prolog.webservice.geral.dispositivo_movel.model.DispositivoMovel;
import br.com.zalf.prolog.webservice.geral.dispositivo_movel.model.MarcaDispositivoMovelSelecao;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;

/**
 * Created on 16/07/19
 *
 * @author Wellington Moraes (https://github.com/wvinim)
 */
public final class DispositivoMovelConverter {

    private DispositivoMovelConverter() {
        throw new IllegalStateException(DispositivoMovelConverter.class.getSimpleName() + " cannot be instantiated!");
    }

    @NotNull
    static MarcaDispositivoMovelSelecao createMarcaCelularSelecao(@NotNull final ResultSet rSet) throws Throwable {
        return new MarcaDispositivoMovelSelecao(
                rSet.getLong("COD_MARCA"),
                rSet.getString("NOME_MARCA"));
    }

    @NotNull
    static DispositivoMovel createDispositivoMovel(@NotNull final ResultSet rSet) throws Throwable {
        return new DispositivoMovel(
                rSet.getLong("CODIGO"),
                rSet.getLong("COD_EMPRESA"),
                rSet.getString("IMEI"),
                rSet.getLong("COD_MARCA"),
                rSet.getString("MODELO"),
                rSet.getString("DESCRICAO")
        );
    }
}