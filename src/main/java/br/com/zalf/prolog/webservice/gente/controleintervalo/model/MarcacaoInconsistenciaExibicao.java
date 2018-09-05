package br.com.zalf.prolog.webservice.gente.controleintervalo.model;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 04/09/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class MarcacaoInconsistenciaExibicao {
    private Long codMarcacao;
    private Long codMarcacaoInconsistencia;
    private String descricaoInconsistencia;

    public MarcacaoInconsistenciaExibicao() {
        // TODO - Utilizar uma Factory para dado uma inconsistência, gerar a descrição correta.
    }

    @NotNull
    public static MarcacaoInconsistenciaExibicao createDummy() {
        final MarcacaoInconsistenciaExibicao marcacaoInconsistente = new MarcacaoInconsistenciaExibicao();
        marcacaoInconsistente.setCodMarcacao(101001L);
        marcacaoInconsistente.setCodMarcacaoInconsistencia(11111L);
        marcacaoInconsistente.setDescricaoInconsistencia("Marcações estão sobrepostas");
        return marcacaoInconsistente;
    }

    public Long getCodMarcacao() {
        return codMarcacao;
    }

    public void setCodMarcacao(final Long codMarcacao) {
        this.codMarcacao = codMarcacao;
    }

    public Long getCodMarcacaoInconsistencia() {
        return codMarcacaoInconsistencia;
    }

    public void setCodMarcacaoInconsistencia(final Long codMarcacaoInconsistencia) {
        this.codMarcacaoInconsistencia = codMarcacaoInconsistencia;
    }

    public String getDescricaoInconsistencia() {
        return descricaoInconsistencia;
    }

    public void setDescricaoInconsistencia(final String descricaoInconsistencia) {
        this.descricaoInconsistencia = descricaoInconsistencia;
    }

    @Override
    public String toString() {
        return "MarcacaoInconsistenciaExibicao{" +
                "codMarcacao=" + codMarcacao +
                ", codMarcacaoInconsistencia=" + codMarcacaoInconsistencia +
                ", descricaoInconsistencia='" + descricaoInconsistencia + '\'' +
                '}';
    }
}
