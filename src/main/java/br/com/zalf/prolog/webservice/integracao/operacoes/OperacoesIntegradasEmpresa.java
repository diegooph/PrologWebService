package br.com.zalf.prolog.webservice.integracao.operacoes;

import br.com.zalf.prolog.webservice.gente.colaborador.model.Empresa;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface OperacoesIntegradasEmpresa {
    @NotNull
    List<Empresa> getFiltros(@NotNull final Long cpf) throws Throwable;
}
