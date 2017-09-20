package br.com.zalf.prolog.webservice.gente.controleintervalo;


import br.com.zalf.prolog.webservice.empresa.EmpresaDao;
import br.com.zalf.prolog.webservice.permissao.Visao;
import com.sun.istack.internal.NotNull;

import java.sql.Connection;

public interface DadosIntervaloChangedListener {
    void onTiposIntervaloChanged(@NotNull final Connection connection,
                                 @NotNull final Long codUnidade) throws Throwable;

    void onPreUpdateCargo(@NotNull final Connection connection,
                          @NotNull final EmpresaDao empresaDao,
                          @NotNull final Visao visaoNova,
                          @NotNull final Long codCargo,
                          @NotNull final Long codUnidade) throws Throwable;
}