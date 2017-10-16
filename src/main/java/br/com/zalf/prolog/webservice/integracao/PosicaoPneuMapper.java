package br.com.zalf.prolog.webservice.integracao;

import javax.annotation.Nonnull;

/**
 * Created on 16/10/17
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public interface PosicaoPneuMapper {

    int mapToProLog(@Nonnull final String posicaoCliente);
}
