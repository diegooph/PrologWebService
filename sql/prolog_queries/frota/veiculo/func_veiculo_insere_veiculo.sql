create or replace function func_veiculo_insere_veiculo(f_cod_unidade bigint,
                                                       f_placa text,
                                                       f_identificador_frota text,
                                                       f_km_atual bigint,
                                                       f_cod_modelo bigint,
                                                       f_cod_tipo bigint,
                                                       f_possui_hubodometro boolean,
                                                       f_origem_cadastro text)
    returns bigint
    language plpgsql
as
$$
declare
    v_cod_empresa           bigint;
    v_status_ativo constant boolean := true;
    v_cod_diagrama          bigint;
    v_cod_veiculo_prolog    bigint;
    v_motorizado            boolean;
begin
    v_cod_empresa := (select u.cod_empresa
                      from unidade u
                      where u.codigo = f_cod_unidade);

    v_cod_diagrama := (select vt.cod_diagrama
                       from veiculo_tipo vt
                       where vt.codigo = f_cod_tipo
                         and vt.cod_empresa = v_cod_empresa);

    v_motorizado := (select vd.motorizado
                     from veiculo_diagrama vd
                     where vd.codigo = v_cod_diagrama);

    if (v_motorizado and f_possui_hubodometro)
    then
        perform throw_generic_error(
            'Não é possivel cadastrar um veiculo motorizado com hubodometro, favor verificar.');
    end if;

    insert into veiculo(cod_empresa,
                        cod_unidade,
                        placa,
                        km,
                        status_ativo,
                        cod_tipo,
                        cod_modelo,
                        cod_diagrama,
                        motorizado,
                        cod_unidade_cadastro,
                        identificador_frota,
                        possui_hubodometro,
                        origem_cadastro)
    values (v_cod_empresa,
            f_cod_unidade,
            f_placa,
            f_km_atual,
            v_status_ativo,
            f_cod_tipo,
            f_cod_modelo,
            v_cod_diagrama,
            v_motorizado,
            f_cod_unidade,
            f_identificador_frota,
            f_possui_hubodometro,
            f_origem_cadastro)
    returning codigo into v_cod_veiculo_prolog;

    -- Verificamos se o insert funcionou.
    if v_cod_veiculo_prolog is null or v_cod_veiculo_prolog <= 0
    then
        perform throw_generic_error('Não foi possível inserir o veículo, tente novamente');
    end if;

    return v_cod_veiculo_prolog;
end;
$$;