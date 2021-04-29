create or replace function integracao.func_fix_servicos_cadastro_pneus(cod_pneus bigint[],
                                                                       out execution_message text)
    language plpgsql
as
$$
declare
    cod_pneu_prolog       bigint;
    cod_unidade_pneu      bigint;
    cod_modelo_banda_pneu bigint;
    vida_atual_pneu       integer;
    valor_banda           real;
begin
    foreach cod_pneu_prolog in array cod_pneus
        loop
            select p.cod_unidade,
                   p.cod_modelo_banda,
                   p.vida_atual,
                   psrd.custo
            from pneu p
                     join pneu_servico_realizado_data psrd
                          on p.codigo = psrd.cod_pneu
                     join pneu_servico_realizado_incrementa_vida_data psrivd
                          on psrd.codigo = psrivd.cod_servico_realizado and
                             psrd.fonte_servico_realizado = psrivd.fonte_servico_realizado
            where p.codigo = cod_pneu_prolog
            order by psrd.codigo desc
            limit 1
            into cod_unidade_pneu, cod_modelo_banda_pneu, vida_atual_pneu, valor_banda;

            perform integracao.func_pneu_realiza_incremento_vida_cadastro(cod_unidade_pneu,
                                                                          cod_pneu_prolog,
                                                                          cod_modelo_banda_pneu,
                                                                          valor_banda,
                                                                          vida_atual_pneu);
        end loop;
    select 'Pneus corrigidos, testar as listagens, aferições e movimentações para validar' into execution_message;
end;
$$;