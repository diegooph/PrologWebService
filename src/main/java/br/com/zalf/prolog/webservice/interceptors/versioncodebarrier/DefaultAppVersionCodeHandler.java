package br.com.zalf.prolog.webservice.interceptors.versioncodebarrier;

import br.com.zalf.prolog.webservice.errorhandling.error.VersaoAppBloqueadaException;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 14/05/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class DefaultAppVersionCodeHandler implements AppVersionCodeBarrier {

    @Override
    public void stopIfNeeded(final long versionCodeReceived,
                             final long targetVersionCode,
                             @NotNull final VersionCodeHandlerMode mode,
                             @NotNull final String appBlockedMessage) throws VersaoAppBloqueadaException {
        switch (mode) {
            case BLOCK_THIS_SPECIFIC_VERSION:
                if (versionCodeReceived == targetVersionCode) {
                    throw new VersaoAppBloqueadaException(appBlockedMessage);
                }
                break;
            case BLOCK_THIS_VERSION_AND_BELOW:
                if (versionCodeReceived <= targetVersionCode) {
                    throw new VersaoAppBloqueadaException(appBlockedMessage);
                }
                break;
            default:
                throw new IllegalArgumentException("Nenhum modo implementado para: " + mode);
        }
    }
}