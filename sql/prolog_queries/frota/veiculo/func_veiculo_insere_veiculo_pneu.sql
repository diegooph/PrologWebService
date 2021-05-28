create or replace function func_veiculo_insere_veiculo_pneu(f_cod_unidade bigint,
                                                            f_placa text,
                                                            f_cod_veiculo bigint,
                                                            f_cod_pneu bigint,
                                                            f_posicao bigint)
    returns boolean
    language plpgsql
as
$$
declare
    v_cod_empresa  bigint;
    v_cod_tipo     bigint;
    v_cod_diagrama bigint;
begin
    -- Busca o código da empresa de acordo com a unidade
    v_cod_empresa := (select u.cod_empresa
                      from unidade u
                      where u.codigo = f_cod_unidade);

    -- Busca o código do tipo de veículo pela placa
    v_cod_tipo := (select vd.cod_tipo
                   from veiculo_data vd
                   where vd.placa = f_placa);

    -- Busca o código do diagrama de acordo com o tipo de veículo
    v_cod_diagrama := (select vt.cod_diagrama
                       from public.veiculo_tipo vt
                       where vt.codigo = v_cod_tipo
                         and vt.cod_empresa = v_cod_empresa);

    if v_cod_diagrama is null or v_cod_diagrama <= 0
    then
        perform throw_generic_error('Não foi possível realizar o vínculo entre veículo e pneu.');
    end if;

    -- Aqui devemos apenas inserir o veículo no prolog.
    insert into veiculo_pneu(cod_unidade,
                             cod_pneu,
                             posicao,
                             cod_diagrama,
                             cod_veiculo)
    values (f_cod_unidade,
            f_cod_pneu,
            f_posicao,
            v_cod_diagrama,
            f_cod_veiculo);

    -- Validamos se houve alguma inserção ou atualização dos valores.
    if not found
    then
        perform throw_generic_error('Não foi possível realizar o vínculo entre veículo e pneu.');
    end if;

    return found;
end;
$$;