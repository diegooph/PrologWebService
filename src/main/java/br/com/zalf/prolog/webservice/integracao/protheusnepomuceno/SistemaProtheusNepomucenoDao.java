package br.com.zalf.prolog.webservice.integracao.protheusnepomuceno;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

/**
 * Created on 12/03/20
 *
 * @author Wellington Moraes (https://github.com/wvinim)
 */
public interface SistemaProtheusNepomucenoDao {

    @NotNull
    List<Long> getApenasUnidadesMapeadas(@NotNull final Connection conn,
                                         @NotNull final List<Long> codUnidades) throws Throwable;

    @NotNull
    Map<Long, String> getCodFiliais(@NotNull final Connection conn,
                                    @NotNull final List<Long> codUnidades) throws Throwable;

    @NotNull
    List<String> verificaCodAuxiliarTipoVeiculoValido(@Nullable final Long codEmpresaTipoVeiculo,
                                                      @Nullable final Long codTipoVeiculo) throws Throwable;
}
