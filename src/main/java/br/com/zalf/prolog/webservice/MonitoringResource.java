package br.com.zalf.prolog.webservice;

import br.com.zalf.prolog.webservice.interceptors.debug.ConsoleDebugLog;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Created on 2019-09-15
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Path("/v2/monitor")
@ConsoleDebugLog
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public final class MonitoringResource {

    @GET
    public boolean isUp() {
        return true;
    }
}