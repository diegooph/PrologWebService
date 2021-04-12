create or replace function func_veiculo_transferencia_deleta_servicos_pneu(f_cod_veiculo bigint,
                                                                           f_cod_pneu bigint,
                                                                           f_cod_transferencia_veiculo_informacoes bigint,
                                                                           f_data_hora_realizacao_transferencia timestamp with time zone)
    returns void
    language plpgsql
as
$$
declare
    v_qtd_inserts bigint;
    v_qtd_updates bigint;
begin
    insert into afericao_manutencao_servico_deletado_transferencia (cod_servico,
                                                                    cod_veiculo_transferencia_informacoes)
    select am.codigo,
           f_cod_transferencia_veiculo_informacoes
           -- Utilizamos propositalmente a view e não a tabela _DATA, para não copiar serviços deletados e não fechados.
    from afericao_manutencao am
             join afericao a on a.codigo = am.cod_afericao
    where a.cod_veiculo = f_cod_veiculo
      and am.cod_pneu = f_cod_pneu
      and am.data_hora_resolucao is null
      and (am.fechado_automaticamente_movimentacao = false or am.fechado_automaticamente_movimentacao is null);

    get diagnostics v_qtd_inserts = row_count;

    update afericao_manutencao_data
    set deletado            = true,
        pg_username_delecao = SESSION_USER,
        data_hora_deletado  = f_data_hora_realizacao_transferencia
    where cod_pneu = f_cod_pneu
      and deletado = false
      and data_hora_resolucao is null
      and (fechado_automaticamente_movimentacao = false or fechado_automaticamente_movimentacao is null);

    get diagnostics v_qtd_updates = row_count;

    -- O SELECT do INSERT e o UPDATE são propositalmente diferentes nas condições do WHERE. No INSERT fazemos o JOIN
    -- com AFERICAO para buscar apenas os serviços em aberto do pneu no veículo em que ele está sendo transferido.
    -- Isso é importante, pois como fazemos o vínculo com a transferência do veículo, não podemos vincular que o veículo
    -- fechou serviços em aberto do veículo B. Ainda que seja o mesmo pneu em jogo.
    -- Em teoria, não deveriam existir serviços em aberto em outra placa que não a atual em que o pneu está aplicado.
    -- Porém, podemos ter uma inconsistência no BD.
    -- Utilizando essas condições diferentes no WHERE do INSERT e UPDATE, nós garantimos que o ROW_COUNT será diferente
    -- em ambos e vamos lançar uma exception, mapeando esse problema para termos visibilidade.
    if v_qtd_inserts <> v_qtd_updates
    then
        raise exception 'Erro ao deletar os serviços de pneus na transferência de veículos. Rollback necessário!';
    end if;
end;
$$;