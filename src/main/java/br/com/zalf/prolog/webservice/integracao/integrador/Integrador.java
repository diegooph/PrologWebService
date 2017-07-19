package br.com.zalf.prolog.webservice.integracao.integrador;

import br.com.zalf.prolog.webservice.integracao.operacoes.OperacoesIntegradasAfericao;
import br.com.zalf.prolog.webservice.integracao.operacoes.OperacoesIntegradasChecklist;
import br.com.zalf.prolog.webservice.integracao.operacoes.OperacoesIntegradasVeiculo;

/**
 * Created by luiz on 7/17/17.
 */
public interface Integrador extends
        OperacoesIntegradasVeiculo,
        OperacoesIntegradasAfericao,
        OperacoesIntegradasChecklist {

}