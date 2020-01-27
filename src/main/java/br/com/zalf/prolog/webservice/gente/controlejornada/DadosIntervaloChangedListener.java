package br.com.zalf.prolog.webservice.gente.controlejornada;


import br.com.zalf.prolog.webservice.colaborador.ColaboradorDao;
import br.com.zalf.prolog.webservice.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.colaborador.model.ColaboradorEdicao;
import br.com.zalf.prolog.webservice.colaborador.model.ColaboradorInsercao;
import br.com.zalf.prolog.webservice.empresa.EmpresaDao;
import br.com.zalf.prolog.webservice.permissao.Visao;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;

public interface DadosIntervaloChangedListener {
    void onTiposMarcacaoChanged(@NotNull final Connection connection,
                                @NotNull final Long codUnidade) throws Throwable;

    void onCargoAtualizado(@NotNull final Connection connection,
                           @NotNull final EmpresaDao empresaDao,
                           @NotNull final Visao visaoNova,
                           @NotNull final Long codCargo,
                           @NotNull final Long codUnidade) throws Throwable;

    void onColaboradorInativado(@NotNull final Connection connection,
                                @NotNull final ColaboradorDao colaboradorDao,
                                @NotNull final Long cpf) throws Throwable;

    void onColaboradorInserido(@NotNull final Connection connection,
                               @NotNull final EmpresaDao empresaDao,
                               @NotNull final ColaboradorInsercao colaborador) throws Throwable;

    void onColaboradorAtualizado(@NotNull final Connection connection,
                                 @NotNull final EmpresaDao empresaDao,
                                 @NotNull final ColaboradorDao colaboradorDao,
                                 @NotNull final ColaboradorEdicao colaborador,
                                 @NotNull final Long cpfAntigo) throws Throwable;
}