-- Sobre:
-- A lógica aplicada nessa function é a seguinte:
-- Após as validações passadas nas précondições, o veículo vai ser deletado logicamente junto com suas dependências.
-- É deletado todos os checklist e aferições da placa, independentemente de unidades e empresas que ela passou.
-- Caso o veículo for da integração, também é deletado.
--
-- Precondições:
-- Veículo pertencer a unidade.
-- Veículo não ter pneus vinculados.
-- Veículo não estar deletado.
-- Remover TODAS aferições do veículo.
-- Remover TODOS checklist do veículo. (COS + COSI)
--
-- Précondições:
--
-- Histórico:
-- 2019-09-17 -> Adiciona SESSION_USER (natanrotta - PL-2229).
-- 2019-09-18 -> Adiciona no schema suporte (natanrotta - PL-2242).
-- 2020-03-04 -> Adiciona melhorias na deleção do veículo e suas dependências (natanrotta - PL-2330).
-- 2020-05-22 -> Adiciona estrutura para deletar veículos integrados (natanrotta - PLI-157).
-- 2020-07-07 -> Adiciona motivo de deleção. (thaisksf - PL-2801).
-- 2020-08-14 -> Adiciona chamada para logar execução da function (gustavocnp95 - PL-3066).
-- 2020-11-19 -> Adiciona impedimento para deleção de veículos que possuem acoplamento (thaisksf - PL-3289).
create or replace function suporte.func_veiculo_deleta_veiculo(f_cod_unidade bigint,
                                                               f_placa varchar(255),
                                                               f_motivo_delecao text,
                                                               out dependencias_deletadas text)
    returns text
    language plpgsql
    security definer
as
$$
declare
    v_codigo_loop                   bigint;
    v_lista_cod_afericao_placa      bigint[];
    v_lista_cod_check_placa         bigint[];
    v_lista_cod_prolog_deletado_cos bigint[];
    v_nome_empresa                  varchar(255) := (select e.nome
                                                     from empresa e
                                                     where e.codigo =
                                                           (select u.cod_empresa
                                                            from unidade u
                                                            where u.codigo = f_cod_unidade));
    v_nome_unidade                  varchar(255) := (select u.nome
                                                     from unidade u
                                                     where u.codigo = f_cod_unidade);
begin
    perform suporte.func_historico_salva_execucao();
    -- Verifica se unidade existe.
    perform func_garante_unidade_existe(f_cod_unidade);

    -- VERIFICA SE VEÍCULO EXISTE.
    perform func_garante_veiculo_existe(f_cod_unidade, f_placa);

    -- Verifica se veiculo possui pneus aplicados.
    if exists(select vp.cod_pneu from veiculo_pneu vp where vp.placa = f_placa and vp.cod_unidade = f_cod_unidade)
    then
        raise exception 'Erro! A Placa: % possui pneus aplicados. Favor removê-los', f_placa;
    end if;

    -- Verifica se possui acoplamento.
    if EXISTS(select vd.codigo
              from veiculo_data vd
              where vd.placa = f_placa
                and vd.cod_unidade = f_cod_unidade
                and vd.acoplado is true)
    then
        raise exception 'Erro! A Placa: % possui acoplamentos. Favor removê-los', f_placa;
    end if;

    -- Verifica se a placa possui aferição.
    if exists(select a.codigo from afericao_data a where a.placa_veiculo = f_placa)
    then
        -- Coletamos todos os cod_afericao que a placa possui.
        select array_agg(a.codigo)
        from afericao_data a
        where a.placa_veiculo = f_placa
        into v_lista_cod_afericao_placa;

        -- DELETAMOS AFERIÇÃO EM AFERICAO_MANUTENCAO_DATA.
        update afericao_manutencao_data
        set deletado            = true,
            data_hora_deletado  = now(),
            pg_username_delecao = session_user,
            motivo_delecao      = f_motivo_delecao
        where deletado = false
          and cod_afericao = any (v_lista_cod_afericao_placa);

        -- DELETAMOS AFERIÇÃO EM AFERICAO_VALORES_DATA.
        update afericao_valores_data
        set deletado            = true,
            data_hora_deletado  = now(),
            pg_username_delecao = session_user,
            motivo_delecao      = f_motivo_delecao
        where deletado = false
          and cod_afericao = any (v_lista_cod_afericao_placa);

        -- DELETAMOS AFERIÇÃO.
        update afericao_data
        set deletado            = true,
            data_hora_deletado  = now(),
            pg_username_delecao = session_user,
            motivo_delecao      = f_motivo_delecao
        where deletado = false
          and codigo = any (v_lista_cod_afericao_placa);
    end if;

    -- VERIFICA SE PLACA POSSUI CHECKLIST.
    if exists(select c.placa_veiculo from checklist_data c where c.placa_veiculo = f_placa)
    then
        -- BUSCA TODOS OS CÓDIGO DO CHECKLIST DA PLACA.
        select ARRAY_AGG(c.codigo)
        from checklist_data c
        where c.placa_veiculo = f_placa
        into v_lista_cod_check_placa;

        -- DELETA COD_CHECK EM COS.
        update checklist_ordem_servico_data
        set deletado            = true,
            data_hora_deletado  = now(),
            pg_username_delecao = session_user,
            motivo_delecao      = f_motivo_delecao
        where deletado = false
          and cod_checklist = any (v_lista_cod_check_placa);

        -- Busco os códigos prolog deletados em cos.
        select ARRAY_AGG(codigo_prolog)
        from checklist_ordem_servico_data
        where cod_checklist = any (v_lista_cod_check_placa)
          and cod_unidade = f_cod_unidade
          and deletado is true
        into v_lista_cod_prolog_deletado_cos;

        -- PARA CADA CÓDIGO PROLOG DELETADO EM COS, DELETAMOS O REFERENTE NA COSI.
        foreach v_codigo_loop in array v_lista_cod_prolog_deletado_cos
            loop
                -- DELETA EM COSI AQUELES QUE FORAM DELETADOS NA COS.
                update checklist_ordem_servico_itens_data
                set deletado            = true,
                    data_hora_deletado  = NOW(),
                    pg_username_delecao = SESSION_USER,
                    motivo_delecao      = f_motivo_delecao
                where deletado = false
                  and (cod_os, cod_unidade) = (select cos.codigo, cos.cod_unidade
                                               from checklist_ordem_servico_data cos
                                               where cos.codigo_prolog = v_codigo_loop);
            end loop;

        -- DELETA TODOS CHECKLIST DA PLACA.
        update checklist_data
        set deletado            = true,
            data_hora_deletado  = NOW(),
            pg_username_delecao = SESSION_USER,
            motivo_delecao      = f_motivo_delecao
        where placa_veiculo = f_placa
          and deletado = false
          and codigo = any (v_lista_cod_check_placa);
    end if;

    -- Verifica se a placa é integrada.
    if EXISTS(select ivc.placa_veiculo_cadastro
              from integracao.veiculo_cadastrado ivc
              where ivc.cod_unidade_cadastro = f_cod_unidade
                and ivc.placa_veiculo_cadastro = f_placa)
    then
        -- Realiza a deleção da placa. (Não possuímos deleção lógica)
        delete
        from integracao.veiculo_cadastrado
        where cod_unidade_cadastro = f_cod_unidade
          and placa_veiculo_cadastro = f_placa;
    end if;

    -- REALIZA DELEÇÃO DA PLACA.
    update veiculo_data
    set deletado            = true,
        data_hora_deletado  = NOW(),
        pg_username_delecao = SESSION_USER,
        motivo_delecao      = f_motivo_delecao
    where cod_unidade = f_cod_unidade
      and placa = f_placa
      and deletado = false;

    -- MENSAGEM DE SUCESSO.
    select 'Veículo deletado junto com suas dependências. Veículo: '
               || f_placa
               || ', Empresa: '
               || v_nome_empresa
               || ', Unidade: '
               || v_nome_unidade
    into dependencias_deletadas;
end;
$$;
