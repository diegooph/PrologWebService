package br.com.zalf.prolog.webservice.frota.pneu.afericao.v3._model;

import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.TipoMedicaoColetadaAfericao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.TipoProcessoColetaAfericao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.configuracao._model.FormaColetaDadosAfericaoEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.br.CPF;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Created on 2021-02-09
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@Table(schema = "public", name = "afericao_data")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_processo_coleta", length = 11, discriminatorType = DiscriminatorType.STRING)
public abstract class AfericaoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigo", nullable = false)
    private Long codigo;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    @Column(name = "cpf_aferidor", nullable = false)
    @CPF
    private String cpfAferidor;

    @Column(name = "tempo_realizacao")
    private Integer tempoRealizacao;

    @Column(name = "tipo_medicao_coletada", length = 13, nullable = false)
    private TipoMedicaoColetadaAfericao tipoMedicaoColetada;

    @Column(name = "cod_unidade", nullable = false)
    private Long codUnidade;

    @Column(name = "tipo_processo_coleta", length = 11, nullable = false)
    private TipoProcessoColetaAfericao tipoProcessoColeta;

    @Column(columnDefinition = "default false", nullable = false)
    private boolean deletado;

    @Column(name = "data_hora_deletado")
    private LocalDateTime dataHoraDelecao;

    @Column(name = "pg_username_delecao")
    private String usernameDelecao;

    @Column(name = "forma_coleta_dados", nullable = false)
    private FormaColetaDadosAfericaoEnum formaColetaDados;

    @Column(name = "motivo_delecao")
    private String motivoDelecao;

}
