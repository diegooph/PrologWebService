create or replace function suporte.func_pneu_altera_pressao_ideal_by_dimensao(f_cod_empresa bigint,
                                                                              f_cod_unidade bigint,
                                                                              f_cod_dimensao bigint,
                                                                              f_nova_pressao_recomendada bigint,
                                                                              f_qtd_pneus_impactados bigint,
                                                                              out aviso_pressao_alterada text)
    returns text
    language plpgsql
    security definer
as
$$
declare
    qtd_real_pneus_impactados  bigint;
    pressao_minima_recomendada bigint := 0;
    pressao_maxima_recomendada bigint := 150;
begin
    perform suporte.func_historico_salva_execucao();
    -- Verifica se a pressao informada está dentro das recomendadas.
    if (f_nova_pressao_recomendada not between pressao_minima_recomendada and pressao_maxima_recomendada)
    then
        raise exception 'Pressão recomendada não está dentro dos valores pré-estabelecidos.
                        Mínima Recomendada: % ---- Máxima Recomendada: %', pressao_minima_recomendada,
            pressao_maxima_recomendada;
    end if;

    -- Verifica se a empresa existe.
    if not exists(select e.codigo
                  from empresa e
                  where e.codigo = f_cod_empresa)
    then
        raise exception 'Empresa de código % não existe!', f_cod_empresa;
    end if;

    -- Verifica se a unidade existe.
    if not exists(select u.codigo
                  from unidade u
                  where u.codigo = f_cod_unidade)
    then
        raise exception 'Unidade de código % não existe!', f_cod_unidade;
    end if;

    -- Verifica se existe a dimensão informada.
    if not exists(select dm.codigo
                  from dimensao_pneu dm
                  where dm.codigo = f_cod_dimensao)
    then
        raise exception 'Dimensao de código % não existe!', f_cod_dimensao;
    end if;

    -- Verifica se a unidade é da empresa informada.
    if not exists(select u.codigo
                  from unidade u
                  where u.codigo = f_cod_unidade
                    and u.cod_empresa = f_cod_empresa)
    then
        raise exception 'A unidade % não pertence a empresa %!', f_cod_unidade, f_cod_empresa;
    end if;

    -- Verifica se algum pneu possui dimensão informada.
    if not exists(select p.cod_dimensao
                  from pneu p
                  where p.cod_dimensao = f_cod_dimensao
                    and p.cod_unidade = f_cod_unidade
                    and p.cod_empresa = f_cod_empresa)
    then
        raise exception 'Não existem pneus com a dimensão % na unidade %', f_cod_dimensao, f_cod_unidade;
    end if;

    -- Verifica quantidade de pneus impactados.
    select count(p.codigo)
    from pneu p
    where p.cod_dimensao = f_cod_dimensao
      and p.cod_unidade = f_cod_unidade
      and p.cod_empresa = f_cod_empresa
    into qtd_real_pneus_impactados;
    if (qtd_real_pneus_impactados <> f_qtd_pneus_impactados)
    then
        raise exception 'A quantidade de pneus informados como impactados pela mudança de pressão (%) não condiz com a
                       quantidade real de pneus que serão afetados!', f_qtd_pneus_impactados;
    end if;

    update pneu
    set pressao_recomendada = f_nova_pressao_recomendada
    where cod_dimensao = f_cod_dimensao
      and cod_unidade = f_cod_unidade
      and cod_empresa = f_cod_empresa;

    select concat('Pressão recomendada dos pneus com dimensão ',
                  f_cod_dimensao,
                  ' da unidade ',
                  f_cod_unidade,
                  ' alterada para ',
                  f_nova_pressao_recomendada,
                  ' psi')
    into aviso_pressao_alterada;
end;
$$;