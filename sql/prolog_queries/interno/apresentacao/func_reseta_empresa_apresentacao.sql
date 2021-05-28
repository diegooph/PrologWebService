create or replace function interno.func_reseta_empresa_apresentacao(f_cod_empresa_base bigint,
                                                                    f_cod_empresa_usuario bigint,
                                                                    out mensagem_sucesso text)
    returns text
    language plpgsql
as
$$
declare
    v_cod_unidades_base                     bigint[] := (select array_agg(u.codigo)
                                                         from unidade u
                                                         where u.cod_empresa = f_cod_empresa_base);
    v_cod_unidade_base                      bigint;
    v_cod_unidades_usuario                  bigint[] := (select array_agg(u.codigo)
                                                         from unidade u
                                                         where u.cod_empresa = f_cod_empresa_usuario);
    v_cod_unidade_usuario_nova              bigint;
    v_cod_colaboradores_usuario             bigint[] := (select array_agg(cd.codigo)
                                                         from colaborador_data cd
                                                         where cd.cod_unidade = any (v_cod_unidades_usuario));
    v_cod_afericoes                         bigint[] := (select array_agg(ad.codigo)
                                                         from afericao_data ad
                                                         where ad.cod_unidade = any (v_cod_unidades_usuario));
    v_cod_checklists                        bigint[] := (select array_agg(cd.codigo)
                                                         from checklist_data cd
                                                         where cd.cod_unidade = any (v_cod_unidades_usuario));
    v_cod_checklists_modelo                 bigint[] := (select distinct array_agg(cmd.codigo)
                                                         from checklist_modelo_data cmd
                                                         where cmd.cod_unidade = any (v_cod_unidades_usuario));
    v_tokens_checklists_off                 text     := (select array_agg(codu.token_sincronizacao_checklist)
                                                         from checklist_offline_dados_unidade codu
                                                         where codu.cod_unidade = any (v_cod_unidades_usuario));
    v_cod_movimentacoes                     bigint[] := (select array_agg(mo.codigo)
                                                         from movimentacao mo
                                                         where mo.cod_unidade = any (v_cod_unidades_usuario));
    v_cod_socorros                          bigint[] := (select array_agg(sr.codigo)
                                                         from socorro_rota sr
                                                         where sr.cod_unidade = any (v_cod_unidades_usuario));
    v_cod_veiculos_transferencias_processos bigint[] := (select array_agg(vtp.codigo)
                                                         from veiculo_transferencia_processo vtp
                                                         where (vtp.cod_unidade_destino = any (v_cod_unidades_usuario))
                                                            or (vtp.cod_unidade_origem = any (v_cod_unidades_usuario)));
    v_cod_pneu_transferencias_processos     bigint[] := (select array_agg(ptp.codigo)
                                                         from pneu_transferencia_processo ptp
                                                         where (ptp.cod_unidade_origem = any (v_cod_unidades_usuario))
                                                            or (ptp.cod_unidade_destino = any (v_cod_unidades_usuario)));
    v_cod_colaboradores_nps                 bigint[] := (select array_agg(colaboradores.cod_colaborador_nps)
                                                         from (select nbpc.cod_colaborador_bloqueio as cod_colaborador_nps
                                                               from cs.nps_bloqueio_pesquisa_colaborador nbpc
                                                               where nbpc.cod_colaborador_bloqueio = any (v_cod_colaboradores_usuario)
                                                               union
                                                               select nr.cod_colaborador_respostas as cod_colaborador_nps
                                                               from cs.nps_respostas nr
                                                               where nr.cod_colaborador_respostas = any (v_cod_colaboradores_usuario)) colaboradores);
    v_cod_treinamentos                      bigint[] := (select array_agg(t.codigo)
                                                         from treinamento t
                                                         where t.cod_unidade = any (v_cod_unidades_usuario));
    v_cod_servicos_realizados               bigint[] := (select array_agg(psr.codigo)
                                                         from pneu_servico_realizado_data psr
                                                         where psr.cod_unidade = any (v_cod_unidades_usuario));
    v_cod_intervalo                         bigint[] := (select array_agg(iu.cod_unidade)
                                                         from intervalo_unidade iu
                                                         where iu.cod_unidade = any (v_cod_unidades_usuario));
    v_cod_marcacoes                         bigint[] := (select array_agg(i.codigo)
                                                         from intervalo i
                                                         where i.cod_unidade = any (v_cod_unidades_usuario));
    v_cod_relatos                           bigint[] := (select array_agg(r.codigo)
                                                         from relato r
                                                         where r.cod_unidade = any (v_cod_unidades_usuario));
    v_cod_quiz                              bigint[] := (select array_agg(q.codigo)
                                                         from quiz q
                                                         where q.cod_unidade = any (v_cod_unidades_usuario));
    v_cod_fale_conosco                      bigint[] := (select array_agg(fc.codigo)
                                                         from fale_conosco fc
                                                         where fc.cod_unidade = any (v_cod_unidades_usuario));
    v_cod_testes_aferidor                   bigint[] := (select array_agg(pt.codigo)
                                                         from aferidor.procedimento_teste pt
                                                         where pt.cod_colaborador_execucao = any (v_cod_colaboradores_usuario));
    v_colaboradores_cadastrados             text[] ;
begin
    -- verifica se empresas existem.
    perform func_garante_empresa_existe(f_cod_empresa_base);
    perform func_garante_empresa_existe(f_cod_empresa_usuario);

    -- busca e deleta vínculos que possam existir de colaborador, veículos e pneus.
    --- aferiçao.
    if (v_cod_afericoes is not null)
    then
        perform interno.func_deleta_afericoes_dependencias(v_cod_unidades_usuario, v_cod_afericoes);
    end if;

    --- checklist.
    if ((v_cod_checklists is not null) or (v_cod_checklists_modelo is not null))
    then
        perform interno.func_deleta_checklists_dependencias(v_cod_unidades_usuario,
                                                            v_cod_checklists,
                                                            v_cod_checklists_modelo);
    end if;

    --- deleta token ckecklist offline
    -- (mesmo sem ter checklist - pode haver o token - pois ele é criado assim que uma unidade é cadastrada)
    if (v_tokens_checklists_off is not null)
    then
        perform interno.func_deleta_tokens_checklists_offlines(v_cod_unidades_usuario);
    end if;

    -- movimentação.
    if (v_cod_movimentacoes is not null)
    then
        perform interno.func_deleta_movimentacoes_dependencias(v_cod_unidades_usuario, v_cod_movimentacoes);
    end if;

    --- socorro em rota.
    if (v_cod_socorros is not null)
    then
        perform interno.func_deleta_socorros_dependencias(f_cod_empresa_usuario, v_cod_socorros);
    end if;

    --- transferencia de veículos.
    if (v_cod_veiculos_transferencias_processos is not null)
    then
        perform interno.func_deleta_transferencias_veiculos_dependencias(v_cod_veiculos_transferencias_processos);
    end if;

    -- transferencia de pneu
    if (v_cod_pneu_transferencias_processos is not null)
    then
        perform interno.func_deleta_transferencias_pneus_dependencias(v_cod_pneu_transferencias_processos,
                                                                      v_cod_unidades_usuario);
    end if;

    -- intervalo
    if (v_cod_intervalo is not null)
    then
        perform interno.func_deleta_intervalo_dependencias(v_cod_unidades_usuario, v_cod_marcacoes);
    end if;

    -- nps
    if (v_cod_colaboradores_nps is not null)
    then
        perform interno.func_deleta_nps(v_cod_colaboradores_nps);
    end if;

    -- produtividade
    if exists(select ap.cod_unidade from acessos_produtividade ap where ap.cod_unidade = any (v_cod_unidades_usuario))
    then
        perform interno.func_deleta_produtividades_dependencias(v_cod_unidades_usuario);
    end if;

    -- relato
    if (v_cod_relatos is not null)
    then
        perform interno.func_deleta_relatos_dependencias(v_cod_unidades_usuario);
    end if;

    -- quiz
    if (v_cod_quiz is not null)
    then
        perform interno.func_deleta_quiz_dependencias(v_cod_unidades_usuario, v_cod_quiz);
    end if;

    -- treinamento
    if (v_cod_treinamentos is not null)
    then
        perform interno.func_deleta_treinamentos_dependencias(v_cod_treinamentos);
    end if;

    -- servico pneu
    if (v_cod_servicos_realizados is not null)
    then
        perform interno.func_deleta_servicos_pneu_dependencias(f_cod_empresa_usuario, v_cod_servicos_realizados);
    end if;

    -- fale conosco
    if (v_cod_fale_conosco is not null)
    then
        perform interno.func_deleta_fale_conosco(v_cod_fale_conosco);
    end if;

    -- testes aferidor
    if (v_cod_testes_aferidor is not null)
    then
        perform interno.func_deleta_testes_aferidor(v_cod_testes_aferidor);
    end if;

    -- deleta veículos
    perform interno.func_deleta_veiculos(f_cod_empresa_usuario, v_cod_unidades_usuario);

    -- deleta pneus
    perform interno.func_deleta_pneus(f_cod_empresa_usuario, v_cod_unidades_usuario);

    -- deleta colaboradores
    perform interno.func_deleta_colaboradores(f_cod_empresa_usuario, v_cod_unidades_usuario);

    -- deleta unidades
    perform interno.func_deleta_unidades(f_cod_empresa_usuario, v_cod_unidades_usuario);

    -- clonagens
    --- clona unidades
    perform interno.func_clona_unidades(f_cod_empresa_base, f_cod_empresa_usuario);

    --- clona nomenclaturas
    perform interno.func_clona_nomenclaturas(f_cod_empresa_base, f_cod_empresa_usuario);

    foreach v_cod_unidade_base in array v_cod_unidades_base
        loop
            v_cod_unidade_usuario_nova := (select unova.codigo
                                           from unidade ubase
                                                    join unidade unova on ubase.nome = unova.nome
                                           where ubase.codigo = v_cod_unidade_base
                                             and unova.cod_empresa = f_cod_empresa_usuario);

            --- clona veículos
            if exists(select vd.codigo from veiculo_data vd where vd.cod_unidade = v_cod_unidade_base)
            then
                perform interno.func_clona_veiculos(f_cod_empresa_base, v_cod_unidade_base, f_cod_empresa_usuario,
                                                    v_cod_unidade_usuario_nova);

            end if;

            --- clona pneus
            if exists(select pd.codigo from pneu_data pd where pd.cod_unidade = v_cod_unidade_base)
            then
                perform interno.func_clona_pneus(f_cod_empresa_base, v_cod_unidade_base, f_cod_empresa_usuario,
                                                 v_cod_unidade_usuario_nova);
            end if;

            --- clona vinculos
            if exists(select vp.cod_veiculo from veiculo_pneu vp where vp.cod_unidade = v_cod_unidade_base)
            then
                perform interno.func_clona_vinculo_veiculos_pneus(v_cod_unidade_base, v_cod_unidade_usuario_nova);
            end if;

            --- clona colaboradores
            if exists(select cd.codigo from colaborador_data cd where cd.cod_unidade = v_cod_unidade_base)
            then
                perform interno.func_clona_colaboradores(f_cod_empresa_base, v_cod_unidade_base,
                                                         f_cod_empresa_usuario,
                                                         v_cod_unidade_usuario_nova);
            end if;
        end loop;

    v_colaboradores_cadastrados = (select array_agg(concat('CPF: ', c.cpf,
                                                           ' | DATA NASCIMENTO: ', c.data_nascimento,
                                                           ' | NÍVEL DE PERMISSAO: ', c.cod_permissao,
                                                           ' | CARGO: ', f.nome))
                                   from colaborador c
                                            join funcao f on f.cod_empresa = c.cod_empresa and f.codigo = c.cod_funcao
                                   where c.cod_empresa = f_cod_empresa_usuario);

    select 'A EMPRESA FOI RESETADA E OS DADOS FORAM CLONADOS COM SUCESSO. OS COLABORADORES CADASTRADOS SÃO: ' ||
           concat(v_colaboradores_cadastrados)
    into mensagem_sucesso;
end ;
$$;