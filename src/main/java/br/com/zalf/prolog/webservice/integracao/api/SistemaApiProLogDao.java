package br.com.zalf.prolog.webservice.integracao.api;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 14/09/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public interface SistemaApiProLogDao {
    boolean isServicoMovimentacao(@NotNull final Long codServico) throws Throwable;
}
