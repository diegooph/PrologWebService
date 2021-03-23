package br.com.zalf.prolog.webservice.frota.pneu.v3._model;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;

/**
 * Created on 2021-03-10
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Entity
@Table(name = "pneu_data", schema = "public")
@Builder
@NoArgsConstructor
@Getter
public class PneuEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigo", nullable = false)
    private Long id;

    @Column(name = "codigo_cliente", nullable = false)
    private String codCliente;

    @Column(name = "cod_modelo", nullable = false)
    private Long codModelo;

    @Column(name = "cod_dimensao", nullable = false)
    private Long codDimensao;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "recomendada", column = @Column(name = "pressao_recomendada", nullable = false)),
        @AttributeOverride(name = "atual", column = @Column(name = "pressao_atual"))
    })
    private Pressao pressao;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "interno", column = @Column(name = "altura_sulco_interno")),
        @AttributeOverride(name = "centralInterno", column = @Column(name = "altura_sulco_central_interno")),
        @AttributeOverride(name = "centralExterno", column = @Column(name = "altura_sulco_central_externo")),
        @AttributeOverride(name = "externo", column = @Column(name = "altura_sulco_externo"))
    })
    private AlturaSulco alturaSulco;


    @Column(name = "cod_unidade", nullable = false)
    private Long codUnidade;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "vida_atual")
    private Integer vidaAtual;

    @Column(name = "vida_total")
    private Integer vidaTotal;

    @Column(name = "cod_modelo_banda")
    private Long codModeloBanda;

    @Column(name = "dot", length = 20)
    private String dot;

    @Column(name = "valor", nullable = false)
    private BigDecimal valor;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "codUnidade", column = @Column(name = "data_hora_cadastro",
                                                                 columnDefinition = "timestamp with time zone default now()",
                                                                 nullable = false)),
        @AttributeOverride(name = "dataInclusao", column = @Column(name = "cod_unidade_cadastro", nullable = false))
    })
    private DadosCadastro dadosCadastro;

    @Column(name = "pneu_novo_nunca_rodado", columnDefinition = "boolean default false", nullable = false)
    private boolean usado;

    @Column(name = "cod_empresa", nullable = false)
    private Long codEmpresa;


    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "deletado", column = @Column(name = "deletado",
                                                           columnDefinition = "boolean default false",
                                                           nullable = false)),
        @AttributeOverride(name = "data", column = @Column(name = "data_hora_deletado")),
        @AttributeOverride(name = "username", column = @Column(name = "pg_username_delecao")),
        @AttributeOverride(name = "motivo", column = @Column(name = "motivo_delecao"))
    })
    private DadosDelecao dadosDelecao;

    public enum Status {
        EM_USO, ESTOQUE, ANALISE, DESCARTE
    }

    @Embeddable
    @Builder
    @NoArgsConstructor
    @Getter
    public static class AlturaSulco {

        private BigDecimal interno;

        private BigDecimal centralInterno;

        private BigDecimal centralExterno;

        private BigDecimal externo;
    }

    @Embeddable
    @Builder
    @NoArgsConstructor
    @Getter
    public static class Pressao {
        BigDecimal recomendada;
        BigDecimal atual;
    }

    @Embeddable
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class DadosCadastro {
        private Long codUnidade;
        private LocalDateTime dataInclusao;
    }
}
