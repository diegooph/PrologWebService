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

create or replace function
    interno.func_clona_vinculo_veiculos_pneus(f_cod_unidade_base bigint, f_cod_unidade_usuario bigint)
    returns void
    language plpgsql
as
$$
declare
    v_cod_veiculos_com_vinculo text := (select array_agg(vp.cod_veiculo)
                                        from veiculo_pneu vp
                                        where vp.cod_unidade = f_cod_unidade_base);
begin
    -- COPIA VÍNCULOS, CASO EXISTAM.
    if (v_cod_veiculos_com_vinculo is not null)
    then
        with veiculos_base as (
            select row_number() over () as codigo_comparacao,
                   v.codigo             as cod_veiculo,
                   v.placa,
                   vdpp.posicao_prolog
            from veiculo_data v
                     join veiculo_tipo vt on v.cod_tipo = vt.codigo and v.cod_empresa = vt.cod_empresa
                     join veiculo_diagrama_posicao_prolog vdpp
                          on vt.cod_diagrama = vdpp.cod_diagrama
            where v.cod_unidade = f_cod_unidade_base
        ),
             veiculos_novos as (
                 select row_number() over () as codigo_comparacao,
                        v.placa,
                        v.cod_diagrama,
                        v.codigo,
                        vdpp.posicao_prolog
                 from veiculo_data v
                          join veiculo_tipo vt on v.cod_tipo = vt.codigo and v.cod_empresa = vt.cod_empresa
                          join veiculo_diagrama_posicao_prolog vdpp on vt.cod_diagrama = vdpp.cod_diagrama
                 where v.cod_unidade = f_cod_unidade_usuario),
             dados_de_para as (
                 select vn.codigo         as cod_veiculo_novo,
                        vn.placa          as placa_nova,
                        vn.posicao_prolog as posicao_prolog_novo,
                        vn.cod_diagrama   as cod_diagrama_novo,
                        pdn.codigo        as cod_pneu_novo
                 from veiculos_base vb
                          join veiculos_novos vn
                               on vb.codigo_comparacao = vn.codigo_comparacao and vb.posicao_prolog = vn.posicao_prolog
                          join veiculo_pneu vp on vb.cod_veiculo = vp.cod_veiculo and vb.posicao_prolog = vp.posicao
                          join pneu_data pdb
                               on vp.status_pneu = pdb.status and vp.cod_unidade = pdb.cod_unidade and
                                  vp.cod_pneu = pdb.codigo
                          join pneu_data pdn
                               on pdb.codigo_cliente = pdn.codigo_cliente and
                                  pdn.cod_unidade = f_cod_unidade_usuario and
                                  pdn.status = 'EM_USO')
        insert
        into veiculo_pneu (cod_pneu, cod_unidade, posicao, cod_diagrama, cod_veiculo)
        select ddp.cod_pneu_novo,
               f_cod_unidade_usuario,
               ddp.posicao_prolog_novo,
               ddp.cod_diagrama_novo,
               ddp.cod_veiculo_novo
        from dados_de_para ddp;
    end if;
end;
$$;

create or replace function implantacao.tg_func_vinculo_veiculo_pneu_confere_planilha_vinculo()
    returns trigger
    language plpgsql
    security definer
as
$$
declare
    v_qtd_erros            smallint := 0;
    v_msgs_erros           text;
    v_quebra_linha         text     := chr(10);
    v_cod_pneu             bigint;
    v_status_pneu          varchar(255);
    v_cod_unidade_pneu     bigint;
    v_cod_veiculo          bigint;
    v_placa                varchar(7);
    v_cod_tipo_veiculo     bigint;
    v_cod_diagrama_veiculo bigint;
    v_cod_unidade_placa    bigint;
    v_cod_empresa_placa    bigint;
    v_posicao_prolog       integer;
begin
    if (tg_op = 'UPDATE' and old.status_vinculo_realizado is true)
    then
        return old;
    else
        if (tg_op = 'UPDATE')
        then
            new.cod_unidade = old.cod_unidade;
            new.cod_empresa = old.cod_empresa;
        end if;
        new.usuario_update := session_user;
        new.placa_formatada_vinculo := remove_espacos_e_caracteres_especiais(new.placa_editavel);
        new.numero_fogo_pneu_formatado_vinculo := remove_all_spaces(new.numero_fogo_pneu_editavel);
        new.nomenclatura_posicao_formatada_vinculo := remove_all_spaces(new.nomenclatura_posicao_editavel);

        -- Verifica se empresa existe.
        if not exists(select e.codigo from empresa e where e.codigo = new.cod_empresa)
        then
            v_qtd_erros = v_qtd_erros + 1;
            v_msgs_erros =
                    concat(v_msgs_erros, v_qtd_erros, '- NÃO EXISTE EMPRESA COM CÓDIGO INFORMADO', v_quebra_linha);
        end if;

        -- Verifica se unidade existe.
        if not exists(select u.codigo from unidade u where u.codigo = new.cod_unidade)
        then
            v_qtd_erros = v_qtd_erros + 1;
            v_msgs_erros =
                    concat(v_msgs_erros, v_qtd_erros, '- NÃO EXISTE UNIDADE COM CÓDIGO INFORMADO', v_quebra_linha);
        end if;

        -- Verifica se unidade pertence a empresa.
        if not exists(
                select u.codigo from unidade u where u.codigo = new.cod_unidade and u.cod_empresa = new.cod_empresa)
        then
            v_qtd_erros = v_qtd_erros + 1;
            v_msgs_erros =
                    concat(v_msgs_erros, v_qtd_erros, '- A UNIDADE NÃO PERTENCE A EMPRESA', v_quebra_linha);
        end if;

        -- Verificações placas.
        -- Placa nula: Erro.
        -- Placa cadastrada em outra empresa: Erro.
        -- Placa cadastrada em outra unidade da mesma empresa: Erro.
        -- Posicao já ocupada por outro pneu: Erro.
        if ((new.placa_formatada_vinculo is not null) and
            (length(new.placa_formatada_vinculo) <> 0))
        then
            select v.codigo,
                   v.placa,
                   v.cod_tipo,
                   v.cod_diagrama,
                   v.cod_unidade,
                   v.cod_empresa
            into v_cod_veiculo,
                v_placa,
                v_cod_tipo_veiculo,
                v_cod_diagrama_veiculo,
                v_cod_unidade_placa,
                v_cod_empresa_placa
            from veiculo v
            where remove_all_spaces(v.placa) ilike
                  new.placa_formatada_vinculo;
            if (v_placa is null)
            then
                v_qtd_erros = v_qtd_erros + 1;
                v_msgs_erros = concat(v_msgs_erros, v_qtd_erros,
                                      '- A PLACA NÃO FOI ENCONTRADA',
                                      v_quebra_linha);
                new.status_vinculo_realizado = false;
            else
                if (v_cod_empresa_placa != new.cod_empresa)
                then
                    v_qtd_erros = v_qtd_erros + 1;
                    v_msgs_erros = concat(v_msgs_erros, v_qtd_erros,
                                          '- NÃO É POSSÍVEL REALIZAR O VÍNCULO POIS A PLACA PERTENCE A OUTRA EMPRESA',
                                          v_quebra_linha);
                    new.status_vinculo_realizado = false;
                else
                    if (v_cod_unidade_placa != new.cod_unidade)
                    then
                        v_qtd_erros = v_qtd_erros + 1;
                        v_msgs_erros = concat(v_msgs_erros, v_qtd_erros,
                                              '- NÃO É POSSÍVEL REALIZAR O VÍNCULO POIS A PLACA PERTENCE A OUTRA
                                              UNIDADE',
                                              v_quebra_linha);
                        new.status_vinculo_realizado = false;
                    else
                        -- Verificar se a posição existe nesse veículo e se está disponível.
                        if ((new.nomenclatura_posicao_formatada_vinculo is not null) and
                            (length(new.nomenclatura_posicao_formatada_vinculo) <> 0))
                        then
                            select ppne.posicao_prolog
                            into v_posicao_prolog
                            from pneu_posicao_nomenclatura_empresa ppne
                            where ppne.cod_diagrama = v_cod_diagrama_veiculo
                              and remove_all_spaces(ppne.nomenclatura)
                                ilike new.nomenclatura_posicao_formatada_vinculo
                              and ppne.cod_empresa = new.cod_empresa;
                            if (v_posicao_prolog is not null)
                            then
                                if exists(select vp.cod_veiculo
                                          from veiculo_pneu vp
                                          where vp.cod_veiculo = v_cod_veiculo
                                            and vp.posicao = v_posicao_prolog
                                            and vp.cod_unidade = new.cod_unidade)
                                then
                                    v_qtd_erros = v_qtd_erros + 1;
                                    v_msgs_erros =
                                            concat(v_msgs_erros, v_qtd_erros,
                                                   '- JÁ EXISTE PNEU VINCULADO À POSIÇÃO (NOMENCLATURA) INFORMADA',
                                                   v_quebra_linha);
                                end if;
                            else
                                v_qtd_erros = v_qtd_erros + 1;
                                v_msgs_erros =
                                        concat(v_msgs_erros, v_qtd_erros,
                                               '- NOMENCLATURA NÃO ENCONTRADA',
                                               v_quebra_linha);
                                new.status_vinculo_realizado = false;
                            end if;
                        else
                            v_qtd_erros = v_qtd_erros + 1;
                            v_msgs_erros =
                                    concat(v_msgs_erros, v_qtd_erros,
                                           '- NOMENCLATURA NÃO PODE SER NULA',
                                           v_quebra_linha);
                        end if;
                    end if;
                end if;
            end if;
        else
            v_qtd_erros = v_qtd_erros + 1;
            v_msgs_erros =
                    concat(v_msgs_erros, v_qtd_erros, '- A PLACA DE FOGO NÃO PODE SER NULA',
                           v_quebra_linha);
        end if;


        -- Verificações número de fogo.
        -- Número de fogo nulo: Erro.
        -- Número de fogo cadastrado em outra unidade da mesma empresa: Erro.
        -- Código do pneu não encontrado: Erro.
        -- Status do pneu diferente de 'ESTOQUE': Erro.
        if ((new.numero_fogo_pneu_formatado_vinculo is not null) and
            (length(new.numero_fogo_pneu_formatado_vinculo) <> 0))
        then
            select p.codigo,
                   p.status,
                   p.cod_unidade
            into v_cod_pneu, v_status_pneu, v_cod_unidade_pneu
            from pneu p
            where remove_all_spaces(p.codigo_cliente) ilike
                  new.numero_fogo_pneu_formatado_vinculo
              and p.cod_empresa = new.cod_empresa;
            if (v_cod_pneu is null)
            then
                v_qtd_erros = v_qtd_erros + 1;
                v_msgs_erros = concat(v_msgs_erros, v_qtd_erros,
                                      '- O PNEU NÃO FOI ENCONTRADO',
                                      v_quebra_linha);
                new.status_vinculo_realizado = false;
            else
                if (v_cod_unidade_pneu != new.cod_unidade)
                then
                    v_qtd_erros = v_qtd_erros + 1;
                    v_msgs_erros = concat(v_msgs_erros, v_qtd_erros,
                                          '- NÃO É POSSÍVEL REALIZAR O VÍNCULO POIS O PNEU PERTENCE A OUTRA UNIDADE',
                                          v_quebra_linha);
                    new.status_vinculo_realizado = false;
                else
                    if (v_status_pneu != 'ESTOQUE')
                    then
                        v_qtd_erros = v_qtd_erros + 1;
                        v_msgs_erros =
                                concat(v_msgs_erros, v_qtd_erros,
                                       '- PARA REALIZAR O VÍNCULO O PNEU DEVE ESTAR EM ESTOQUE, O STATUS ATUAL DO PNEU
                                       É: ',
                                       v_status_pneu, v_quebra_linha);
                        new.status_vinculo_realizado = false;
                    end if;
                end if;
            end if;
        else
            v_qtd_erros = v_qtd_erros + 1;
            v_msgs_erros =
                    concat(v_msgs_erros, v_qtd_erros, '- O NÚMERO DE FOGO NÃO PODE SER NULO',
                           v_quebra_linha);
        end if;

        if (v_qtd_erros > 0)
        then
            new.erros_encontrados = v_msgs_erros;
        else
            update pneu_data set status = 'EM_USO' where codigo = v_cod_pneu;
            insert into veiculo_pneu (cod_pneu,
                                      cod_unidade,
                                      posicao,
                                      cod_diagrama,
                                      cod_veiculo)
            values (v_cod_pneu,
                    new.cod_unidade,
                    v_posicao_prolog,
                    v_cod_diagrama_veiculo,
                    v_cod_veiculo);

            new.status_vinculo_realizado = true;
            new.erros_encontrados = '-';
        end if;
    end if;
    return new;
end;
$$;