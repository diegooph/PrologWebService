package br.com.zalf.prolog.webservice.v3.frota.kmprocessos.visitor;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 2021-03-26
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public interface Visitable {
    void accept(@NotNull final AlteracaoKmProcessoVisitor visitor);
}
