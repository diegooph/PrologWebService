create or replace function integracao.func_pneu_vincula_pneu_posicao_placa(f_cod_veiculo_prolog bigint,
                                                                           f_placa_veiculo_pneu_aplicado text,
                                                                           f_cod_pneu_prolog bigint,
                                                                           f_codigo_pneu_cliente text,
                                                                           f_cod_unidade_pneu bigint,
                                                                           f_posicao_veiculo_pneu_aplicado integer,
                                                                           f_is_posicao_estepe boolean)
    returns boolean
    language plpgsql
as
$$
declare
    f_qtd_rows_alteradas bigint;
begin
    -- Validamos se a placa existe no ProLog.
    if (f_cod_veiculo_prolog is null or f_cod_veiculo_prolog <= 0)
    then
        perform public.throw_generic_error(format('A placa informada %s não está presente no Sistema ProLog',
                                                  f_placa_veiculo_pneu_aplicado));
    end if;

    -- Validamos se o placa e o pneu pertencem a mesma unidade.
    if ((select v.cod_unidade from public.veiculo v where v.codigo = f_cod_veiculo_prolog) <> f_cod_unidade_pneu)
    then
        perform public.throw_generic_error(
                format('A placa informada %s está em uma Unidade diferente do pneu informado %s,
               unidade da placa %s, unidade do pneu %s',
                       f_placa_veiculo_pneu_aplicado,
                       f_codigo_pneu_cliente,
                       (select v.cod_unidade from public.veiculo v where v.codigo = f_cod_veiculo_prolog),
                       f_cod_unidade_pneu));
    end if;

    -- Validamos se a posição repassada é uma posição válida no ProLog.
    if (not is_placa_posicao_pneu_valida(f_cod_veiculo_prolog, f_posicao_veiculo_pneu_aplicado, f_is_posicao_estepe))
    then
        perform public.throw_generic_error(
                format('A posição informada %s para o pneu, não é uma posição válida para a placa %s',
                       f_posicao_veiculo_pneu_aplicado,
                       f_placa_veiculo_pneu_aplicado));
    end if;

    -- Validamos se a placa possui algum outro pneu aplicado na posição.
    if (select exists(select *
                      from public.veiculo_pneu vp
                      where vp.cod_veiculo = f_cod_veiculo_prolog
                        and vp.cod_unidade = f_cod_unidade_pneu
                        and vp.posicao = f_posicao_veiculo_pneu_aplicado))
    then
        perform public.throw_generic_error(format('Já existe um pneu na placa %s, posição %s',
                                                  f_placa_veiculo_pneu_aplicado,
                                                  f_posicao_veiculo_pneu_aplicado));
    end if;

    -- Vincula pneu a placa.
    insert into public.veiculo_pneu(cod_pneu,
                                    cod_unidade,
                                    posicao,
                                    cod_diagrama,
                                    cod_veiculo)
    values (f_cod_pneu_prolog,
            f_cod_unidade_pneu,
            f_posicao_veiculo_pneu_aplicado,
            (select vt.cod_diagrama
             from veiculo_tipo vt
             where vt.codigo = (select v.cod_tipo from veiculo v where v.codigo = f_cod_veiculo_prolog)),
            f_cod_veiculo_prolog);

    get diagnostics f_qtd_rows_alteradas = row_count;

    -- Verificamos se o update ocorreu como deveria
    if (f_qtd_rows_alteradas <= 0)
    then
        perform public.throw_generic_error(format('Não foi possível aplicar o pneu %s na placa %s',
                                                  f_codigo_pneu_cliente,
                                                  f_placa_veiculo_pneu_aplicado));
    end if;

    -- Retornamos sucesso se o pneu estiver aplicado na placa e posição que deveria estar.
    if (select exists(select vp.posicao
                      from public.veiculo_pneu vp
                      where vp.cod_veiculo = f_cod_veiculo_prolog
                        and vp.cod_pneu = f_cod_pneu_prolog
                        and vp.posicao = f_posicao_veiculo_pneu_aplicado
                        and vp.cod_unidade = f_cod_unidade_pneu))
    then
        return true;
    else
        perform public.throw_generic_error(format('Não foi possível aplicar o pneu %s na placa %s',
                                                  f_codigo_pneu_cliente,
                                                  f_placa_veiculo_pneu_aplicado));
    end if;
end ;
$$;