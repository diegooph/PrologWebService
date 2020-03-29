package br.com.zalf.prolog.webservice.integracao.operacoes;

/**
 * As operações que possuem integração são separadas cada qual em sua interface. As operações do checklist que possuem
 * integração com qualquer empresa, por exemplo, estão declaradas em {@link OperacoesIntegradasChecklist}. Porém, essa
 * separação só serve para podermos ter uma visão melhor de quais operações de cada recurso estão integradas. Desse
 * modo, elas possuem visibilidade package-private e deixamos acessível apenas esta interface, que extende todas as
 * outras. Isso também facilita para quem implementa essas operações não ter que implementar diversas interfaces.
 */
public interface OperacoesIntegradas extends
        OperacoesIntegradasAfericao,
        OperacoesIntegradasChecklist,
        OperacoesIntegradasChecklistOrdemServico,
        OperacoesIntegradasVeiculo,
        OperacoesIntegradasVeiculoTransferencia,
        OperacoesIntegradasPneu,
        OperacoesIntegradasPneuTransferencia,
        OperacoesIntegradasMovimentacao,
        OperacoesIntegradasAfericaoServico,
        OperacoesIntegradasTipoVeiculo {
}