drop function integracao.func_pneu_valida_posicoes_sistema_parceiro(bigint, text[], bigint[]);
create or replace function
    integracao.func_pneu_valida_posicoes_sistema_parceiro(f_cod_tipo_veiculo bigint,
                                                          f_posicoes_parceiro text[],
                                                          f_posicoes_prolog bigint[]) returns bigint
    language plpgsql
as
$$
declare
    v_posicoes_diagrama_prolog    bigint[];
    v_posicoes_invalidas          bigint[];
    v_posicoes_repetidas_parceiro text[];
    v_posicoes_repetidas_prolog   text[];
    v_mensagem_retorno            text := '';
    v_tem_erro                    boolean;
begin
    -- Valida se código tipo do veículo recebido não é null.
    if (f_cod_tipo_veiculo is null or f_cod_tipo_veiculo <= 0)
    then
        perform throw_generic_error('O código tipo do veículo não pode ser nulo');
    end if;

    -- Valida código tipo existe no Sistema Prolog.
    if not exists(select vd.codigo from veiculo_tipo vd where vd.codigo = f_cod_tipo_veiculo)
    then
        perform throw_generic_error(
                format('O código tipo (%s) informado não existe no Sistema Prolog', f_cod_tipo_veiculo));
    end if;

    -- Valida se a lista de posições não está vazia.
    if not (f_size_array(f_posicoes_prolog) > 0)
    then
        perform throw_generic_error('A lista com as posições do diagrama está vazia');
    end if;

    -- Busca todas as posições do diagrama.
    select array_agg(vdpp.posicao_prolog)
    from veiculo_diagrama_posicao_prolog vdpp
    where vdpp.cod_diagrama = (select vt.cod_diagrama from veiculo_tipo vt where vt.codigo = f_cod_tipo_veiculo)
    into v_posicoes_diagrama_prolog;

    -- Valida se as posições do diagrama foram encontradas.
    if not (f_size_array(v_posicoes_diagrama_prolog) > 0)
    then
        perform throw_generic_error(
                format('Não foi possível buscar as posições do diagrama do veículo de código tipo (%s)',
                       f_cod_tipo_veiculo));
    end if;

    -- Valida se as posições recebidas não contém nas posições do diagrama.
    if not (f_posicoes_prolog <@ v_posicoes_diagrama_prolog)
    then
        -- Busca as posições que foram enviadas e não fazem parte das posições do diagrama.
        select array_agg(posicao.posicao_cliente)
        from (select unnest(f_posicoes_prolog) as posicao_cliente) as posicao
        where posicao.posicao_cliente not in (select unnest(v_posicoes_diagrama_prolog))
        into v_posicoes_invalidas;
    end if;

    -- Filtra as posições repetidas do cliente.
    with posicoes_duplicadas as (
        select posicao_duplicada as posicao_duplicada
        from unnest(f_posicoes_prolog) as posicao_duplicada
        group by posicao_duplicada
        having count(*) > 1
    )

    select array_agg(pd.posicao_duplicada)
    from posicoes_duplicadas pd
    into v_posicoes_repetidas_prolog;

    -- Filtra as posições repetidas do cliente.
    with posicoes_duplicadas as (
        select posicao_duplicada as posicao_duplicada
        from unnest(f_posicoes_parceiro) as posicao_duplicada
        group by posicao_duplicada
        having count(*) > 1
    )

    select array_agg(pd.posicao_duplicada)
    from posicoes_duplicadas pd
    into v_posicoes_repetidas_parceiro;

    -- Faz direcionamento para o retorno da exception.
    if (f_size_array(v_posicoes_invalidas) > 0)
    then
        select v_mensagem_retorno || 'As posições do Sistema Prolog ('
                   || array_to_string(v_posicoes_invalidas, ', ') || ') não pertencem ao tipo de veículo de código ('
                   || f_cod_tipo_veiculo || E')\n'
        into v_mensagem_retorno;
        v_tem_erro = true;
    end if;
    if (f_size_array(v_posicoes_repetidas_prolog) > 0)
    then
        select v_mensagem_retorno || 'As posições do Sistema Prolog ('
                   || array_to_string(v_posicoes_repetidas_prolog, ', ') ||
               ') estão repetidas no tipo de veículo de código ('
                   || f_cod_tipo_veiculo || E')\n'
        into v_mensagem_retorno;
        v_tem_erro = true;
    end if;
    if (f_size_array(v_posicoes_repetidas_parceiro) > 0)
    then
        select v_mensagem_retorno || 'As posições do Globus ('
                   || array_to_string(v_posicoes_repetidas_parceiro, ', ') ||
               ') estão repetidas no tipo de veículo de código (' || f_cod_tipo_veiculo || ')'
        into v_mensagem_retorno;
        v_tem_erro = true;
    end if;

    if (v_tem_erro) then
        perform throw_generic_error(v_mensagem_retorno);
    end if;

    -- Caso nenhuma exception for lançada, retornamos sucesso.
    return f_cod_tipo_veiculo;
end;
$$;