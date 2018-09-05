package br.com.zalf.prolog.webservice;

import br.com.zalf.prolog.webservice.commons.gson.GsonUtils;
import br.com.zalf.prolog.webservice.gente.controleintervalo.model.MarcacaoAjusteAdicao;
import br.com.zalf.prolog.webservice.gente.controleintervalo.model.MarcacaoAjusteAdicaoInicioFim;
import br.com.zalf.prolog.webservice.gente.controleintervalo.model.MarcacaoAjusteAtivacaoInativacao;
import br.com.zalf.prolog.webservice.gente.controleintervalo.model.MarcacaoAjusteEdicao;

public class Main {

    public static void main(String[] args) {
        System.out.println(GsonUtils.getGson().toJson(MarcacaoAjusteAdicao.createDummy()));
        System.out.println("\n\n\n");
        System.out.println(GsonUtils.getGson().toJson(MarcacaoAjusteAdicaoInicioFim.createDummy()));
        System.out.println("\n\n\n");
        System.out.println(GsonUtils.getGson().toJson(MarcacaoAjusteAtivacaoInativacao.createDummy()));
        System.out.println("\n\n\n");
        System.out.println(GsonUtils.getGson().toJson(MarcacaoAjusteEdicao.createDummy()));
    }
}