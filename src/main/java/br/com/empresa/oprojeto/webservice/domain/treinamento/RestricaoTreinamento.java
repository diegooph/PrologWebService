package br.com.empresa.oprojeto.webservice.domain.treinamento;


public class RestricaoTreinamento {
    private int codTreinamento;
    private int codFuncao;

    public RestricaoTreinamento() {
    }

    public RestricaoTreinamento(int codTreinamento, int codFuncao) {
        this.codTreinamento = codTreinamento;
        this.codFuncao = codFuncao;
    }

    public int getCodTreinamento() {
        return codTreinamento;
    }

    public void setCodTreinamento(int codTreinamento) {
        this.codTreinamento = codTreinamento;
    }

    public int getCodFuncao() {
        return codFuncao;
    }

    public void setCodFuncao(int codFuncao) {
        this.codFuncao = codFuncao;
    }
}
