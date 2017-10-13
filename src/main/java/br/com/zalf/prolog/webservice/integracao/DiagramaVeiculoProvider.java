package br.com.zalf.prolog.webservice.integracao;

import javax.annotation.Nonnull;

/**
 * Created on 10/10/17.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public interface DiagramaVeiculoProvider {

    Long getCodDiagramaBy(@Nonnull final String tipoVeiculo);
}
