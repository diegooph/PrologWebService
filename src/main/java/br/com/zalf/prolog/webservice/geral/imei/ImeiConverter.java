package br.com.zalf.prolog.webservice.geral.imei;

import br.com.zalf.prolog.webservice.geral.imei.model.*;
import br.com.zalf.prolog.webservice.permissao.pilares.ImpactoPermissaoProLog;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 16/07/19
 *
 * @author Wellington Moraes (https://github.com/wvinim)
 */
public final class ImeiConverter {

    private ImeiConverter() {
        throw new IllegalStateException(ImeiConverter.class.getSimpleName() + " cannot be instantiated!");
    }

    @NotNull
    static MarcaCelularSelecao createMarcaCelularSelecao(@NotNull final ResultSet rSet) throws Throwable {
        return new MarcaCelularSelecao(
                rSet.getLong("COD_MARCA"),
                rSet.getString("NOME_MARCA"));
    }

}