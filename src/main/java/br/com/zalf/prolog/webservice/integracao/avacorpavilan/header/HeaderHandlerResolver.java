package br.com.zalf.prolog.webservice.integracao.avacorpavilan.header;

import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.HandlerResolver;
import javax.xml.ws.handler.PortInfo;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by luiz on 24/07/17.
 */
public class HeaderHandlerResolver implements HandlerResolver {

    @Override
    public List<Handler> getHandlerChain(PortInfo portInfo) {
        List<Handler> handlerChain = new ArrayList<>();
        handlerChain.add(new HeaderHandler());
        return handlerChain;
    }
}