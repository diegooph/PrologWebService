package br.com.zalf.prolog.webservice.interceptors.versioncodebarrier;

import org.jetbrains.annotations.NotNull;

/**
 * Classe utilizada para barrar determinadas versões de App de executarem alguma requisição. Sempre que precisarmos
 * barrar alguma requisição por qualquer motivo, devemos criar uma implementação dessa classe que contenha essa lógica.
 *
 * Created on 14/05/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public interface AppVersionCodeBarrier {

   void stopIfNeeded(final long versionCodeReceived,
                     final long targetVersionCode,
                     @NotNull final VersionCodeHandlerMode mode);
}