package br.com.zalf.prolog.webservice;

import br.com.zalf.prolog.webservice.commons.gson.GsonUtils;
import br.com.zalf.prolog.webservice.gente.controleintervalo.model.MarcacaoColaboradorAjuste;

public class Main {

    public static void main(String[] args) {
        System.out.println(GsonUtils.getGson().toJson(MarcacaoColaboradorAjuste.createDummy()));
    }
}