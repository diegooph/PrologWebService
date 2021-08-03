package br.com.zalf.prolog.webservice.v3.fleet.vehicle.vehiclelayout._model;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class AxleLayoutPk implements Serializable {
    private short id;
    private short position;
}
