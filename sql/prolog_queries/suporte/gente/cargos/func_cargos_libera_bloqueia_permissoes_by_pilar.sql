create or replace function
    suporte.func_cargos_libera_bloqueia_permissoes_by_pilar(f_liberar_bloquear text,
                                                            f_cod_pilares bigint[],
                                                            f_cod_empresas bigint[],
                                                            f_cod_motivo_bloqueio bigint,
                                                            f_observacao_bloqueio text default null,
                                                            f_cod_unidades bigint[] default null)
    returns text
    language plpgsql
    security definer
as
$$
declare
    v_cod_permissoes bigint[] := (select array_agg(fp.codigo)
                                  from funcao_prolog_v11 fp
                                  where fp.cod_pilar = any (f_cod_pilares));
begin
    perform suporte.func_historico_salva_execucao();
    -- Validamos apenas os pilares não mapeados, demais validações são feitas pela function interna.
    perform func_garante_pilares_validos(f_cod_pilares::integer[]);

    perform suporte.func_cargos_libera_bloqueia_permissoes_by_codigo(f_liberar_bloquear,
                                                                     v_cod_permissoes,
                                                                     f_cod_empresas,
                                                                     f_cod_motivo_bloqueio,
                                                                     f_observacao_bloqueio,
                                                                     f_cod_unidades);

    return (select format('A operação de %s foi realizada com sucesso para as permissões dos pilares (%s)',
                          f_liberar_bloquear, f_cod_pilares));
end;
$$;