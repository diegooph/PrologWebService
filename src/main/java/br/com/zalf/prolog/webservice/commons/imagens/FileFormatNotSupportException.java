package br.com.zalf.prolog.webservice.commons.imagens;

/**
 * Created on 03/05/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public class FileFormatNotSupportException extends Throwable {
    public FileFormatNotSupportException(final String msg) {
        super(msg);
    }
}