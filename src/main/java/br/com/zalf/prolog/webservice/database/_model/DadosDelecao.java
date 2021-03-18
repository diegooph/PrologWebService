package br.com.zalf.prolog.webservice.database._model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.time.LocalDateTime;

/**
 * Created on 2021-03-18
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Embeddable
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class DadosDelecao {
    private boolean deletado;
    private LocalDateTime data;
    private String username;
    private String motivo;
}
