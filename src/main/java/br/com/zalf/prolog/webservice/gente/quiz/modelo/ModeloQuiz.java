package br.com.zalf.prolog.webservice.gente.quiz.modelo;

import br.com.zalf.prolog.webservice.gente.colaborador.model.Cargo;
import br.com.zalf.prolog.webservice.gente.quiz.quiz.model.PerguntaQuiz;
import br.com.zalf.prolog.webservice.gente.treinamento.model.Treinamento;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.List;

public final class ModeloQuiz {
    private Long codigo;
    private String nome;
    @Nullable
    private String descricao;
    private LocalDateTime dataHoraAbertura;
    private LocalDateTime dataHoraFechamento;
    /**
     * Funções (cargos) que têm acesso a este modelo de quiz.
     */
    private List<Cargo> funcoesLiberadas;

    private List<PerguntaQuiz> perguntas;

    /**
     * Um material de apoio para estudo prévio.
     * Pode ser null pois a unidade pode ou não disponibilizar esse material.
     */
    @Nullable
    private Treinamento materialApoio;

    /**
     * Contém a porcentagem do total de perguntas do quiz que devem ser acertadas para considerarmos que o colaborador
     * foi aprovado na realização do mesmo. Deve conter um valor entre 0.01 e 1.0.
     */
    private double porcentagemAprovacao;

    public double getPorcentagemAprovacao() {
        return porcentagemAprovacao;
    }

    public void setPorcentagemAprovacao(double porcentagemAprovacao) {
        this.porcentagemAprovacao = porcentagemAprovacao;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalDateTime getDataHoraAbertura() {
        return dataHoraAbertura;
    }

    public void setDataHoraAbertura(LocalDateTime dataHoraAbertura) {
        this.dataHoraAbertura = dataHoraAbertura;
    }

    public LocalDateTime getDataHoraFechamento() {
        return dataHoraFechamento;
    }

    public void setDataHoraFechamento(LocalDateTime dataHoraFechamento) {
        this.dataHoraFechamento = dataHoraFechamento;
    }

    public List<Cargo> getFuncoesLiberadas() {
        return funcoesLiberadas;
    }

    public void setFuncoesLiberadas(List<Cargo> funcoesLiberadas) {
        this.funcoesLiberadas = funcoesLiberadas;
    }

    public List<PerguntaQuiz> getPerguntas() {
        return perguntas;
    }

    public void setPerguntas(List<PerguntaQuiz> perguntas) {
        this.perguntas = perguntas;
    }

    public Treinamento getMaterialApoio() {
        return materialApoio;
    }

    public void setMaterialApoio(Treinamento materialApoio) {
        this.materialApoio = materialApoio;
    }
}