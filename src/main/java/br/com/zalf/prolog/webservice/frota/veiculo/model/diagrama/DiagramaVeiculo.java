package br.com.zalf.prolog.webservice.frota.veiculo.model.diagrama;

import java.util.Set;

/**
 * Created by luiz on 22/05/17.
 */
public class DiagramaVeiculo {
    private final Short codigo;
    private final String nome;
    private final Set<EixoVeiculo> eixos;
    private final String urlImagem;

    public DiagramaVeiculo(Short codigo, String nome, Set<EixoVeiculo> eixos, String urlImagem) {
        this.codigo = codigo;
        this.nome = nome;
        this.eixos = eixos;
        this.urlImagem = urlImagem;
    }

    public Short getCodigo() {
        return codigo;
    }

    public String getNome() {
        return nome;
    }

    public Set<EixoVeiculo> getEixos() {
        return eixos;
    }

    public String getUrlImagem() {
        return urlImagem;
    }
}