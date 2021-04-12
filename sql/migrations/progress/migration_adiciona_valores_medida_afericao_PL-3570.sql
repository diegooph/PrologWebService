drop function func_afericao_get_afericoes_avulsas_paginada(f_cod_unidades bigint[],
                                                           f_data_inicial date,
                                                           f_data_final date,
                                                           f_limit bigint,
                                                           f_offset bigint);

drop function func_afericao_get_afericoes_placas_paginada(f_cod_unidades bigint[],
                                                          f_cod_tipo_veiculo bigint,
                                                          f_placa_veiculo text,
                                                          f_data_inicial date,
                                                          f_data_final date,
                                                          f_limit bigint,
                                                          f_offset bigint);