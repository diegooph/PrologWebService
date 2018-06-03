package br.com.zalf.prolog.webservice;

import br.com.zalf.prolog.webservice.commons.gson.GsonUtils;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.Pneu;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.PneuComum;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.PneuAnalise;

public class Main {

    public static void main(String[] args) {
        final PneuComum pneu = new PneuComum();
        final PneuAnalise pneuAnalise = new PneuAnalise();
        ((PneuAnalise)pneuAnalise).setCodigoColeta("suhUHSUAHS");

        final String sPneu = GsonUtils.getGson().toJson(pneu);
        final String sPneuAanalise = GsonUtils.getGson().toJson(pneuAnalise);
        System.out.println(sPneu);
        System.out.println(sPneuAanalise);

        System.out.println(GsonUtils.getGson().fromJson(sPneu, Pneu.class));
        System.out.println(GsonUtils.getGson().fromJson(sPneuAanalise, Pneu.class));
    }
}