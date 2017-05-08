package br.com.zalf.prolog.seguranca.gsd;

import br.com.zalf.prolog.commons.questoes.Pergunta;

import java.util.Date;
import java.util.List;

/**29/02/2016
 * Gabarito de segurança em distribuição.
 * Armazena todos os dados do formulário
 * Possui a classe interna PerguntaRespostasGsd para armazenar as respostas separadamente por colaborador
 *
 */
public class Gsd {
    public static final String PERGUNTA_ITENS_ROTA = "ITENS_ROTA";
    public static final String PERGUNTA_CONDICOES_EPIS = "CONDICOES_EPIS";
    public static final String PERGUNTA_CONDICOES_EPIS_OBSERVACOES = "CONDICOES_EPIS_OBSERVACOES";
    public static final String PERGUNTA_CONDICOES_PDVS = "CONDICOES_PDVS";
    public static final String PERGUNTA_CONDICOES_PDVS_BALDEIO = "CONDICOES_PDVS_BALDEIO";
    public static final String PERGUNTA_OUTROS_ESPECIFICAR = "OUTROS_ESPECIFICAR";
    public static final String PERGUNTA_CONDICOES_VEICULO = "CONDICOES_VEICULO";
    public static final String PERGUNTA_CONDICOES_VIA = "CONDICOES_VIA";
    private Long codigo;
    private Long cpfAvaliador;
    private Long cpfMotorista;
    private Long cpfAjudante1;
    private Long cpfAjudante2;
    private String nomeAvaliador;
    private String nomeMotorista;
    private String nomeAjudante1;
    private String nomeAjudante2;
    private Date dataHora;
    private String placaVeiculo;
    private List<Pdv> pdvs;
    /**
     * List da classe interna "PerguntaRespostasGsd" para armazenar as perguntas e respostas por cpf
     */
    private List<PerguntaRespostasGsd> perguntaRespostasList;
    private String urlFoto;
    private String latitude;
    private String longitude;

    public Gsd() {
    }

    public Gsd(Long codigo, Long cpfAvaliador, Long cpfMotorista, Long cpfAjudante1, Long cpfAjudante2,
               Date dataHora, String placaVeiculo, List<Pdv> pdvs,
               List<PerguntaRespostasGsd> perguntaRespostasList, String urlFoto, String latitude,
               String longitude) {
        this.codigo = codigo;
        this.cpfAvaliador = cpfAvaliador;
        this.cpfMotorista = cpfMotorista;
        this.cpfAjudante1 = cpfAjudante1;
        this.cpfAjudante2 = cpfAjudante2;
        this.dataHora = dataHora;
        this.placaVeiculo = placaVeiculo;
        this.pdvs = pdvs;
        this.perguntaRespostasList = perguntaRespostasList;
        this.urlFoto = urlFoto;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public Long getCpfAvaliador() {
        return cpfAvaliador;
    }

    public void setCpfAvaliador(Long cpfAvaliador) {
        this.cpfAvaliador = cpfAvaliador;
    }

    public Long getCpfMotorista() {
        return cpfMotorista;
    }

    public void setCpfMotorista(Long cpfMotorista) {
        this.cpfMotorista = cpfMotorista;
    }

    public Long getCpfAjudante1() {
        return cpfAjudante1;
    }

    public void setCpfAjudante1(Long cpfAjudante1) {
        this.cpfAjudante1 = cpfAjudante1;
    }

    public Long getCpfAjudante2() {
        return cpfAjudante2;
    }

    public void setCpfAjudante2(Long cpfAjudante2) {
        this.cpfAjudante2 = cpfAjudante2;
    }

    public Date getDataHora() {
        return dataHora;
    }

    public void setDataHora(Date dataHora) {
        this.dataHora = dataHora;
    }

    public String getPlacaVeiculo() {
        return placaVeiculo;
    }

    public void setPlacaVeiculo(String placaVeiculo) {
        this.placaVeiculo = placaVeiculo;
    }

    public List<Pdv> getPdvs() {
        return pdvs;
    }

    public void setPdvs(List<Pdv> pdvs) {
        this.pdvs = pdvs;
    }

    public String getUrlFoto() {
        return urlFoto;
    }

    public void setUrlFoto(String urlFoto) {
        this.urlFoto = urlFoto;
    }

    public List<PerguntaRespostasGsd> getPerguntaRespostasList() {
        return perguntaRespostasList;
    }

    public void setPerguntaRespostasList(List<PerguntaRespostasGsd> perguntaRespostasList) {
        this.perguntaRespostasList = perguntaRespostasList;
    }

    public String getNomeAvaliador() {
        return nomeAvaliador;
    }

    public void setNomeAvaliador(String nomeAvaliador) {
        this.nomeAvaliador = nomeAvaliador;
    }

    public String getNomeMotorista() {
        return nomeMotorista;
    }

    public void setNomeMotorista(String nomeMotorista) {
        this.nomeMotorista = nomeMotorista;
    }

    public String getNomeAjudante1() {
        return nomeAjudante1;
    }

    public void setNomeAjudante1(String nomeAjudante1) {
        this.nomeAjudante1 = nomeAjudante1;
    }

    public String getNomeAjudante2() {
        return nomeAjudante2;
    }

    public void setNomeAjudante2(String nomeAjudante2) {
        this.nomeAjudante2 = nomeAjudante2;
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

    /**
     * Classe interna PerguntaRespostasGsd
     */
    public static class PerguntaRespostasGsd {
        private Pergunta pergunta;
        private String respostaMotorista;
        private String respostaAjudante1;
        private String respostaAjudante2;
        private String respostaAvaliador;

        public PerguntaRespostasGsd(Pergunta pergunta, String respostaMotorista, String respostaAjudante1,
                                    String respostaAjudante2) {
            this.pergunta = pergunta;
            this.respostaMotorista = respostaMotorista;
            this.respostaAjudante1 = respostaAjudante1;
            this.respostaAjudante2 = respostaAjudante2;
        }

        public PerguntaRespostasGsd(Pergunta pergunta, String respostaAvaliador) {
            this.pergunta = pergunta;
            this.respostaAvaliador = respostaAvaliador;
        }

        public PerguntaRespostasGsd() {
        }

        public Pergunta getPergunta() {
            return pergunta;
        }

        public void setPergunta(Pergunta pergunta) {
            this.pergunta = pergunta;
        }

        public String getRespostaMotorista() {
            return respostaMotorista;
        }

        public void setRespostaMotorista(String respostaMotorista) {
            this.respostaMotorista = respostaMotorista;
        }

        public String getRespostaAjudante1() {
            return respostaAjudante1;
        }

        public void setRespostaAjudante1(String respostaAjudante1) {
            this.respostaAjudante1 = respostaAjudante1;
        }

        public String getRespostaAjudante2() {
            return respostaAjudante2;
        }

        public void setRespostaAjudante2(String respostaAjudante2) {
            this.respostaAjudante2 = respostaAjudante2;
        }

        public String getRespostaAvaliador() {
            return respostaAvaliador;
        }

        public void setRespostaAvaliador(String respostaAvaliador) {
            this.respostaAvaliador = respostaAvaliador;
        }

        @Override
        public String toString() {
            return "PerguntaRespostasGsd{" +
                    "respostaMotorista='" + respostaMotorista + '\'' +
                    ", respostaAjudante1='" + respostaAjudante1 + '\'' +
                    ", respostaAjudante2='" + respostaAjudante2 + '\'' +
                    ", respostaAvaliador='" + respostaAvaliador + '\'' +
                    '}';
        }
    }
}