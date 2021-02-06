-- 2020-10-09 -> Remove parâmetro de placa (thaisksf).
drop function suporte.func_afericao_deleta_afericao(f_cod_unidade bigint,
                                                    f_placa text,
                                                    f_codigo_afericao bigint,
                                                    f_motivo_delecao text);
create or replace function suporte.func_afericao_deleta_afericao(f_cod_unidade bigint,
                                                                 f_codigo_afericao bigint,
                                                                 f_motivo_delecao text,
                                                                 out aviso_afericao_deletada text)
    returns text
    language plpgsql
    security definer
as
$$
declare
    qtd_linhas_atualizadas bigint;
begin
    perform suporte.func_historico_salva_execucao();
    if ((select COUNT(codigo)
         from afericao_data
         where codigo = f_codigo_afericao
           and cod_unidade = f_cod_unidade) <= 0)
    then
        raise exception 'Nenhuma aferição encontrada com estes parâmetros: Unidade % e Código %',
            f_cod_unidade, f_codigo_afericao;
    end if;

    -- DELETA AFERIÇÃO.
    update afericao_data
    set deletado            = true,
        data_hora_deletado  = NOW(),
        pg_username_delecao = SESSION_USER,
        motivo_delecao      = f_motivo_delecao
    where cod_unidade = f_cod_unidade
      and codigo = f_codigo_afericao
      and deletado = false;

    get diagnostics qtd_linhas_atualizadas = row_count;

    if (qtd_linhas_atualizadas <= 0)
    then
        raise exception 'Erro ao deletar o aferição de unidade: % e código: %',
            f_cod_unidade, f_codigo_afericao;
    end if;

    -- DELETA AFERIÇÃO VALORES.
    update afericao_valores_data
    set deletado            = true,
        data_hora_deletado  = NOW(),
        pg_username_delecao = SESSION_USER,
        motivo_delecao      = f_motivo_delecao
    where cod_unidade = f_cod_unidade
      and cod_afericao = f_codigo_afericao
      and deletado = false;

    get diagnostics qtd_linhas_atualizadas = row_count;

    -- SE TEM AFERIÇÃO, TAMBÉM DEVERÁ CONTER VALORES DE AFERIÇÃO, ENTÃO DEVE-SE VERIFICAR.
    if ((qtd_linhas_atualizadas <= 0) and ((select COUNT(*)
                                            from afericao_valores_data avd
                                            where avd.cod_unidade = f_cod_unidade
                                              and avd.cod_afericao = f_codigo_afericao) > 0))
    then
        raise exception 'Erro ao deletar os valores de  aferição de unidade: % e código: %',
            f_cod_unidade, f_codigo_afericao;
    end if;

    -- DELETA AFERIÇÃO MANUTENÇÃO.
    -- Não verificamos quantas linhas atualizadas pois aferição pode não ter manutenções.
    update afericao_manutencao_data
    set deletado            = true,
        data_hora_deletado  = NOW(),
        pg_username_delecao = SESSION_USER,
        motivo_delecao      = f_motivo_delecao
    where cod_unidade = f_cod_unidade
      and cod_afericao = f_codigo_afericao;

    select 'AFERIÇÃO DELETADA: '
               || f_codigo_afericao
               || ', CÓDIGO DA UNIDADE: '
               || f_cod_unidade
    into aviso_afericao_deletada;
end;
$$;