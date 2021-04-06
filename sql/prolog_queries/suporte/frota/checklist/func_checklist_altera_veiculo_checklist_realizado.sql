create or replace function suporte.func_checklist_altera_veiculo_checklist_realizado(f_cod_checklist bigint,
                                                                                     f_nova_placa_veiculo text)
    returns text
    language plpgsql
    security definer
as
$$
declare
    v_cod_empresa_checklist bigint;
    v_cod_veiculo           bigint;
begin
    if f_cod_checklist is null or f_cod_checklist <= 0
    then
        raise exception 'Forneça um "f_cod_checklist" válido!';
    end if;

    if f_nova_placa_veiculo is null or f_nova_placa_veiculo = ''
    then
        raise exception 'Forneça uma "f_nova_placa_veiculo" válida!';
    end if;

    if not exists(select codigo from checklist c where c.codigo = f_cod_checklist)
    then
        raise exception
            'O checklist de código % não foi encontrado. Verifique se ele está deletado.
            Checklists deletados não podem ser alterados.',
            f_cod_checklist;
    end if;

    select u.cod_empresa
    from checklist c
             join unidade u on c.cod_unidade = u.codigo
    where c.codigo = f_cod_checklist
    into v_cod_empresa_checklist;
    if v_cod_empresa_checklist is null or v_cod_empresa_checklist <= 0
    then
        raise exception 'Erro ao buscar o código da empresa do checklist informado.';
    end if;

    select v.codigo
    from veiculo v
    where v.placa = f_nova_placa_veiculo
      and v.cod_empresa = v_cod_empresa_checklist
    into v_cod_veiculo;
    if v_cod_veiculo is null or v_cod_veiculo <= 0
    then
        raise exception
            'Erro ao buscar o código do veículo com base na placa "%" e código de empresa "%".
            Verifique se o veículo está deletado. Veículos deletados não podem ser atribuídos a checklists já realizados.',
            f_nova_placa_veiculo,
            v_cod_empresa_checklist;
    end if;

    update checklist
    set placa_veiculo = f_nova_placa_veiculo,
        cod_veiculo   = v_cod_veiculo
    where codigo = f_cod_checklist
      and placa_veiculo <> f_nova_placa_veiculo;

    return format('Veículo do checklist %s alterado com sucesso.', f_cod_checklist);
end
$$;