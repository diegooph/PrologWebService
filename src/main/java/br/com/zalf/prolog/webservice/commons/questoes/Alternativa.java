package br.com.zalf.prolog.webservice.commons.questoes;

/**
 * Classe interna que representa uma alternativa de resposta.
 * O atributo 'tipo' deve ser setado apenas no caso onde a AlternativaChecklist em questão se tratar da opção
 * Outros (especificar). Nesse caso, seta-se o tipo usando-se a constante TIPO_OUTROS dessa classe e a
 * resposta especificada pelo usuário irá se encontrar no atributo respostaOutros.
 */
public class Alternativa {
    public static final int TIPO_OUTROS = 0x1;
    public Long codigo;
    public String alternativa;
    public int tipo;
    public String respostaOutros;
    public int ordemExibicao;

    public Alternativa() {
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(final long codigo) {
        this.codigo = codigo;
    }

    public String getAlternativa() {
        return alternativa;
    }

    public void setAlternativa(final String alternativa) {
        this.alternativa = alternativa;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(final int tipo) {
        this.tipo = tipo;
    }

    public String getRespostaOutros() {
        return respostaOutros;
    }

    public void setRespostaOutros(final String respostaOutros) {
        this.respostaOutros = respostaOutros;
    }

    public int getOrdemExibicao() {
        return ordemExibicao;
    }

    public void setOrdemExibicao(final int ordemExibicao) {
        this.ordemExibicao = ordemExibicao;
    }

    public boolean isTipoOutros() {
        return this.tipo == TIPO_OUTROS;
    }

    @Override
    public String toString() {
        return "AlternativaChecklist{" +
                "codigo=" + codigo +
                ", alternativa='" + alternativa + '\'' +
                ", tipo=" + tipo +
                ", respostaOutros='" + respostaOutros + '\'' +
                ", ordemExibicao=" + ordemExibicao +
                '}';
    }
}
