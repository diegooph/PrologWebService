create or replace function suporte.func_pneu_remove_vinculo_pneu(f_cpf_solicitante bigint,
                                                                 f_cod_unidade bigint,
                                                                 f_cod_veiculo bigint,
                                                                 f_lista_cod_pneus bigint[],
                                                                 out aviso_pneus_desvinculados text)
    returns text
    language plpgsql
    security definer
as
$$
declare
    status_pneu_estoque              text                     := 'ESTOQUE';
    status_pneu_em_uso               text                     := 'EM_USO';
    data_hora_atual                  timestamp with time zone := now();
    cod_pneu_da_vez                  bigint;
    cod_movimentacao_criada          bigint;
    cod_processo_movimentacao_criado bigint;
    vida_atual_pneu                  bigint;
    posicao_pneu                     integer;
    km_atual_veiculo                 bigint                   := (select v.km
                                                                  from veiculo v
                                                                  where v.cod_unidade = f_cod_unidade
                                                                    and v.codigo = f_cod_veiculo);
    nome_colaborador                 text                     := (select c.nome
                                                                  from colaborador c
                                                                  where c.cpf = f_cpf_solicitante);
begin
    perform suporte.func_historico_salva_execucao();
    -- verifica se colaborador possui integridade com unidade;
    perform func_garante_integridade_unidade_colaborador(f_cod_unidade, f_cpf_solicitante);

    -- verifica se unidade existe;
    perform func_garante_unidade_existe(f_cod_unidade);

    -- verifica se veículo existe;
    perform func_garante_veiculo_existe_by_codigo(f_cod_unidade, f_cod_veiculo);

    -- verifica quantiade de pneus recebida;
    if (array_length(f_lista_cod_pneus, 1) > 0)
    then
        -- cria processo para movimentação
        insert into movimentacao_processo(cod_unidade, data_hora, cpf_responsavel, observacao)
        values (f_cod_unidade,
                data_hora_atual,
                f_cpf_solicitante,
                'Processo para desvincular o pneu de uma placa')
        returning codigo into cod_processo_movimentacao_criado;

        foreach cod_pneu_da_vez in array f_lista_cod_pneus
            loop
                -- verifica se pneu não está vinculado a placa informada;
                if not exists(select vp.cod_veiculo
                              from veiculo_pneu vp
                              where vp.cod_veiculo = f_cod_veiculo
                                and vp.cod_pneu = cod_pneu_da_vez)
                then
                    raise exception 'Erro! O pneu com código: % não está vinculado ao veículo de código %',
                        cod_pneu_da_vez, f_cod_veiculo;
                end if;

                -- busca vida atual e posicao do pneu;
                select p.vida_atual, vp.posicao
                from pneu p
                         join veiculo_pneu vp on p.codigo = vp.cod_pneu
                where p.codigo = cod_pneu_da_vez
                into vida_atual_pneu, posicao_pneu;

                if (cod_processo_movimentacao_criado > 0)
                then
                    -- insere movimentação retornando o código da mesma;
                    insert into movimentacao(cod_movimentacao_processo,
                                             cod_unidade,
                                             cod_pneu,
                                             sulco_interno,
                                             sulco_central_interno,
                                             sulco_externo,
                                             vida,
                                             observacao,
                                             sulco_central_externo)
                    select cod_processo_movimentacao_criado,
                           f_cod_unidade,
                           cod_pneu_da_vez,
                           p.altura_sulco_interno,
                           p.altura_sulco_central_interno,
                           p.altura_sulco_externo,
                           vida_atual_pneu,
                           null,
                           p.altura_sulco_central_externo
                    from pneu p
                    where p.codigo = cod_pneu_da_vez
                    returning codigo into cod_movimentacao_criada;

                    -- insere destino da movimentação;
                    insert into movimentacao_destino(cod_movimentacao, tipo_destino)
                    values (cod_movimentacao_criada, status_pneu_estoque);

                    -- insere origem da movimentação;
                    perform func_movimentacao_insert_movimentacao_veiculo_origem(cod_pneu_da_vez,
                                                                                 f_cod_unidade,
                                                                                 status_pneu_em_uso,
                                                                                 cod_movimentacao_criada,
                                                                                 f_cod_veiculo,
                                                                                 km_atual_veiculo,
                                                                                 posicao_pneu);

                    -- remove pneu do vinculo;
                    delete from veiculo_pneu where cod_pneu = cod_pneu_da_vez and cod_veiculo = f_cod_veiculo;

                    -- atualiza status do pneu
                    update pneu
                    set status = status_pneu_estoque
                    where codigo = cod_pneu_da_vez
                      and cod_unidade = f_cod_unidade;

                    -- verifica se o pneu possui serviços em aberto;
                    if exists(select am.cod_pneu
                              from afericao_manutencao am
                              where am.cod_unidade = f_cod_unidade
                                and am.cod_pneu = cod_pneu_da_vez
                                and am.data_hora_resolucao is null
                                and am.cpf_mecanico is null
                                and am.fechado_automaticamente_movimentacao is false
                                and am.fechado_automaticamente_integracao is false)
                    then
                        -- remove serviços em aberto;
                        update afericao_manutencao
                        set fechado_automaticamente_movimentacao = true,
                            cod_processo_movimentacao            = cod_processo_movimentacao_criado,
                            data_hora_resolucao                  = data_hora_atual
                        where cod_unidade = f_cod_unidade
                          and cod_pneu = cod_pneu_da_vez
                          and data_hora_resolucao is null
                          and cpf_mecanico is null
                          and fechado_automaticamente_movimentacao is false
                          and fechado_automaticamente_integracao is false;
                    end if;
                else
                    raise exception 'Erro! Não foi possível realizar o processo de movimentação para o pneu código: %',
                        cod_pneu_da_vez;
                end if;
            end loop;
    else
        raise exception 'Erro! Precisa-se de pelo menos um (1) pneu para realizar a operação!';
    end if;

    -- mensagem de sucesso;
    select 'Movimentação realizada com sucesso!! Autorizada por ' || nome_colaborador ||
           ' com CPF: ' || f_cpf_solicitante || '. Os pneus que estavam na placa de código ' || f_cod_veiculo ||
           ' foram movidos para estoque.'
    into aviso_pneus_desvinculados;
end
$$;