package br.com.zalf.prolog.webservice.v3.fleet.acoplamento._model;

import br.com.zalf.prolog.webservice.v3.LocalDateTimeUtcAttributeConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * Created on 2021-06-11
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
@Builder(toBuilder = true, setterPrefix = "with")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@Table(schema = "public", name = "veiculo_acoplamento_processo")
public class AcoplamentoProcessoEntity {
    @Id
    @Column(name = "codigo", nullable = false)
    private Long codigo;
    @Column(name = "cod_unidade", nullable = false)
    private Long codUnidade;
    @Column(name = "cod_colaborador", nullable = false)
    private Long codColaborador;
    @Convert(converter = LocalDateTimeUtcAttributeConverter.class)
    @Column(name = "data_hora")
    private LocalDateTime dataHoraRealizacao;
    @Column(name = "observacao")
    private String observacao;
    @OneToMany(mappedBy = "acoplamentoProcessoEntity",
               fetch = FetchType.LAZY,
               targetEntity = AcoplamentoAtualEntity.class)
    private Set<AcoplamentoAtualEntity> acoplamentoAtualEntities;
}
