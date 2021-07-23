create or replace function suporte.func_pneu_altera_pressao_ideal_by_numero_fogo(f_cod_empresa bigint,
                                                                                 f_cod_unidade bigint,
                                                                                 f_numero_fogo text,
                                                                                 f_nova_pressao_recomendada bigint,
                                                                                 out aviso_pressao_alterada text)
    returns text
    language plpgsql
    security definer
as
$$
declare
    qtd_linhas_atualizadas     bigint;
    pressao_minima_recomendada bigint := 0;
    pressao_maxima_recomendada bigint := 150;
begin
    perform suporte.func_historico_salva_execucao();
    perform func_garante_integridade_empresa_unidade(f_cod_empresa, f_cod_unidade);

    --Verifica se a pressao informada está dentro das recomendadas.
    if (f_nova_pressao_recomendada not between pressao_minima_recomendada and pressao_maxima_recomendada)
    then
        raise exception 'Pressão recomendada não está dentro dos valores pré-estabelecidos.
                        Mínima Recomendada: % ---- Máxima Recomendada: %', pressao_minima_recomendada,
            pressao_maxima_recomendada;
    end if;

    -- Verifica se existe o número de fogo informado.
    if not exists(select pd.codigo
                  from pneu pd
                  where pd.codigo_cliente = f_numero_fogo
                    and pd.cod_empresa = f_cod_empresa)
    then
        raise exception 'Número de fogo % não está cadastrado na empresa %!', f_numero_fogo, f_cod_empresa;
    end if;

    update pneu
    set pressao_recomendada = f_nova_pressao_recomendada
    where codigo_cliente = f_numero_fogo
      and cod_unidade = f_cod_unidade
      and cod_empresa = f_cod_empresa;

    get diagnostics qtd_linhas_atualizadas = row_count;

    if (qtd_linhas_atualizadas <= 0)
    then
        raise exception 'Erro ao atualizar a pressão recomendada com estes parâemtros:
                     Empresa %, Unidade %, Número de fogo %, Nova pressão %',
            f_cod_unidade,
            f_cod_empresa,
            f_numero_fogo,
            f_nova_pressao_recomendada;
    end if;

    select concat('Pressão recomendada do pneu com número de fogo ',
                  f_numero_fogo,
                  ' da empresa ',
                  f_cod_empresa,
                  ' da unidade ',
                  f_cod_unidade,
                  ' alterada para ',
                  f_nova_pressao_recomendada,
                  ' psi')
    into aviso_pressao_alterada;
end;
$$;