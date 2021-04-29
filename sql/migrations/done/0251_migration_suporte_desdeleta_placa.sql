create or replace function suporte.func_veiculo_desdeleta_veiculo(f_cod_unidade bigint,
                                                                  f_placa varchar(255),
                                                                  f_informacoes_extras_suporte text,
                                                                  out placa_desdeletada text)
    returns text
    language plpgsql
    security definer
as
$$
begin
    perform suporte.func_historico_salva_execucao(f_informacoes_extras_suporte);

    perform func_garante_unidade_existe(f_cod_unidade);

    perform func_garante_veiculo_existe(f_cod_unidade, f_placa);

    if not exists(select * from veiculo_data where cod_unidade = f_cod_unidade and placa = f_placa and deletado)
    then
        raise exception 'Erro! A Placa: % não está deletada.', f_placa;
    end if;

    update veiculo_data
    set deletado            = false,
        data_hora_deletado  = null,
        pg_username_delecao = null,
        motivo_delecao      = null
    where cod_unidade = f_cod_unidade
      and placa = f_placa
      and deletado = true;

    select 'A Placa ' || f_placa || ' está ativa novamente na unidade ' || f_cod_unidade
    into placa_desdeletada;
end
$$;