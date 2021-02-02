package br.com.zalf.prolog.webservice.frota.pneu.nomenclatura;

import br.com.zalf.prolog.webservice.DummyData;
import br.com.zalf.prolog.webservice.commons.network.metadata.Platform;
import br.com.zalf.prolog.webservice.commons.network.metadata.UsedBy;
import br.com.zalf.prolog.webservice.frota.pneu.nomenclatura._model.PneuNomenclaturaCadastro;
import br.com.zalf.prolog.webservice.frota.pneu.nomenclatura._model.PneuNomenclaturaItemCadastro;
import br.com.zalf.prolog.webservice.frota.pneu.nomenclatura._model.PneuNomenclaturaItemVisualizacao;
import br.com.zalf.prolog.webservice.interceptors.debug.ConsoleDebugLog;
import br.com.zalf.prolog.webservice.interceptors.debug.ResourceDebugOnly;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 29/08/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */

@Path("/dummies")
@ConsoleDebugLog
@ResourceDebugOnly
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public final class DummyPneuNomenclaturaResource extends DummyData {

    @GET
    @UsedBy(platforms = Platform.WEBSITE)
    @Path("/pneus-nomenclaturas-visualizacao")
    public List<PneuNomenclaturaItemVisualizacao> getPneuNomenclaturaItemVisualizacao() {
        final List<PneuNomenclaturaItemVisualizacao> pneuNomenclaturaItem = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            pneuNomenclaturaItem.add(PneuNomenclaturaItemVisualizacao.createDummy(i));
        }
        return pneuNomenclaturaItem;
    }

    @GET
    @UsedBy(platforms = Platform.WEBSITE)
    @Path("/pneus-nomenclaturas-cadastro")
    public PneuNomenclaturaCadastro getPneuNomenclaturaCadastro() {
        final List<PneuNomenclaturaItemCadastro> itens = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            itens.add(PneuNomenclaturaItemCadastro.createDummy(i));
        }
       return PneuNomenclaturaCadastro.createDummy(itens);
    }
}

