package br.com.zalf.prolog.webservice.seguranca.relato.model;


import br.com.zalf.prolog.webservice.gente.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.commons.questoes.Alternativa;
import br.com.zalf.prolog.webservice.seguranca.pdv.Pdv;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;

/**
 * Relato de incidente, dados do apontador, local e fotos do ocorrido.
 */
public class Relato {

    public enum Status {
        INVALIDO("INVALIDO"),
        FECHADO("FECHADO"),
        PENDENTE_CLASSIFICACAO("PENDENTE_CLASSIFICACAO"),
        PENDENTE_FECHAMENTO("PENDENTE_FECHAMENTO");

        private final String s;

        Status(String s) {
            this.s = s;
        }

        public String asString() {
            return s;
        }

        public static Status fromString(String text) throws IllegalArgumentException {
            if (text != null) {
                for (Status b : Status.values()) {
                    if (text.equalsIgnoreCase(b.s)) {
                        return b;
                    }
                }
            }
            throw new IllegalArgumentException("Nenhum enum com esse valor encontrado: " + text);
        }
    }

    /**
     * Status que um relato pode assumir
     */
    public static final String INVALIDO = "INVALIDO";

    /**
     * Essa constante é a generalização dos dois tipos de pendentes. Usando ela para fazer uma
     * requisição por relatos pendentes, obtemos tanto os PENDENTE_CLASSIFICACAO como os
     * PENDENTE_FECHAMENTO.
     */
    public static final String PENDENTE = "PENDENTE%";

    public static final String PENDENTE_FECHAMENTO = "PENDENTE_FECHAMENTO";
    public static final String PENDENTE_CLASSIFICACAO = "PENDENTE_CLASSIFICACAO";
    /**
     * Aqui faz mais sentido usar FECHADO ao invés de RESOLVIDO, pois o TST
     * pode fechar um relato sem necessariamente resolvê-lo.
     */
    public static final String FECHADO = "FECHADO";

    /**
     * O status atual desse relato
     */
    private String status;

    /**
     * Feedback fornecido pela pessoa responsável por fechar esse relato.
     * O feedback é apenas exigido em caso de fechamento, não quando for marcado como inválido
     */
    private String feedbackFechamento;

    /**
     * Data em que esse relato foi fechado ou marcado como inválido
     */
    private LocalDateTime dataFechamento;
    /**
     * Pessoa responsável por fechar ou marcar como inválido esse relato
     */
    private Colaborador colaboradorFechamento;

    /**
     * Data em que esse relato foi fechado ou marcado como inválido
     */
    private LocalDateTime dataClassificacao;
    /**
     * Pessoa responsável por fechar ou marcar como inválido esse relato
     */
    private Colaborador colaboradorClassificacao;

    /**
     * Pessoa responsável por realizar este relato
     */
    private Colaborador colaboradorRelato;

    /**
     * A descrição de um relato, esse atributo pode ser nulo, ja que o colaborador não é obrigado a descrever
     * o ocorrido
     */
    @Nullable
    private String descricao;

    private Long codigo;
    private LocalDateTime dataLocal;
    private LocalDateTime dataDatabase;
    private String latitude;
    private String longitude;
    private String urlFoto1;
    private String urlFoto2;
    private String urlFoto3;
    private Pdv pdv;
    private double distanciaColaborador;
    private Alternativa alternativa;

    public Relato() {

    }

    public Colaborador getColaboradorFechamento() {
        return colaboradorFechamento;
    }

    public void setColaboradorFechamento(Colaborador colaboradorFechamento) {
        this.colaboradorFechamento = colaboradorFechamento;
    }

    public Colaborador getColaboradorClassificacao() {
        return colaboradorClassificacao;
    }

    public void setColaboradorClassificacao(Colaborador colaboradorClassificacao) {
        this.colaboradorClassificacao = colaboradorClassificacao;
    }

    public Pdv getPdv() {
        return pdv;
    }

    public void setPdv(Pdv pdv) {
        this.pdv = pdv;
    }

    public Colaborador getColaboradorRelato() {
        return colaboradorRelato;
    }

    public void setColaboradorRelato(Colaborador colaboradorRelato) {
        this.colaboradorRelato = colaboradorRelato;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFeedbackFechamento() {
        return feedbackFechamento;
    }

    public void setFeedbackFechamento(String feedbackFechamento) {
        this.feedbackFechamento = feedbackFechamento;
    }

    public LocalDateTime getDataFechamento() {
        return dataFechamento;
    }

    public void setDataFechamento(LocalDateTime dataFechamento) {
        this.dataFechamento = dataFechamento;
    }

    public LocalDateTime getDataClassificacao() {
        return dataClassificacao;
    }

    public void setDataClassificacao(LocalDateTime dataClassificacao) {
        this.dataClassificacao = dataClassificacao;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public LocalDateTime getDataLocal() {
        return dataLocal;
    }

    public void setDataLocal(LocalDateTime dataLocal) {
        this.dataLocal = dataLocal;
    }

    public LocalDateTime getDataDatabase() {
        return dataDatabase;
    }

    public void setDataDatabase(LocalDateTime dataDatabase) {
        this.dataDatabase = dataDatabase;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getUrlFoto1() {
        return urlFoto1;
    }

    public void setUrlFoto1(String urlFoto1) {
        this.urlFoto1 = urlFoto1;
    }

    public String getUrlFoto2() {
        return urlFoto2;
    }

    public void setUrlFoto2(String urlFoto2) {
        this.urlFoto2 = urlFoto2;
    }

    public String getUrlFoto3() {
        return urlFoto3;
    }

    public void setUrlFoto3(String urlFoto3) {
        this.urlFoto3 = urlFoto3;
    }

    public double getDistanciaColaborador() {
        return distanciaColaborador;
    }

    public void setDistanciaColaborador(double distanciaColaborador) {
        this.distanciaColaborador = distanciaColaborador;
    }

    public Alternativa getAlternativa() {
        return alternativa;
    }

    public void setAlternativa(Alternativa alternativa) {
        this.alternativa = alternativa;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void corrigeUrls() {
        // Garanti que a URL 1 sempre vai ter uma URL diferente de null.
        if (urlFoto1 == null) {
            if (urlFoto2 != null) {
                urlFoto1 = urlFoto2;
                urlFoto2 = null;
            } else if (urlFoto3 != null) {
                urlFoto1 = urlFoto3;
                urlFoto3 = null;
            }
        }
    }

    @Override
    public String toString() {
        return "Relato{" +
                "status='" + status + '\'' +
                ", feedbackFechamento='" + feedbackFechamento + '\'' +
                ", dataFechamento=" + dataFechamento +
                ", colaboradorFechamento=" + colaboradorFechamento +
                ", dataClassificacao=" + dataClassificacao +
                ", colaboradorClassificacao=" + colaboradorClassificacao +
                ", colaboradorRelato=" + colaboradorRelato +
                ", descricao='" + descricao + '\'' +
                ", codigo=" + codigo +
                ", dataLocal=" + dataLocal +
                ", dataDatabase=" + dataDatabase +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                ", urlFoto1='" + urlFoto1 + '\'' +
                ", urlFoto2='" + urlFoto2 + '\'' +
                ", urlFoto3='" + urlFoto3 + '\'' +
                ", pdv=" + pdv +
                ", distanciaColaborador=" + distanciaColaborador +
                ", alternativa=" + alternativa +
                '}';
    }
}