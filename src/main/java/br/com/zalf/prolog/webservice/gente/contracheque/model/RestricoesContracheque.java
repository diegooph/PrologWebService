package br.com.zalf.prolog.webservice.gente.contracheque.model;

/**
 * Created by Zart on 19/05/2017.
 * Classe para auxiliar no calculo dos itens do pre contracheque
 */
public class RestricoesContracheque {

    public int codFuncaoMotorista;
    public int codFuncaoAjudante;
    public Double valorBonusMotorista;
    public Double valorBonusAjudante;
    public boolean recargaPartePremio;
    public String indicadorBonus;
    public Long codFuncaoSolicitante;
    public Long codUnidade;
    public short numeroViagensNecessariasParaReceberBonus;

    public RestricoesContracheque() {
    }
}