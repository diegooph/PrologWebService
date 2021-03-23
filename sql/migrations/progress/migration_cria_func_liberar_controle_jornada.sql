-- Essa function libera a funcionalidade de controle de jornada para uma unidade juntamente com
-- dois tipos de marcações: 'Jornada' e 'Refeição'.
--
-- Ela funciona da seguinte forma:
-- 1 - Libera o pilar gente para a unidade, caso ainda não tenha.
-- 2 - Libera a permissão de realizar marcação para todos os cargos da unidade.
-- 3 - Ao cadastrar o tipo 'Jornada':
--     a) Caso já exista um com mesmo nome, será vinculado à esse tipo todos os cargos da unidade.
--     b) Caso não exista um com mesmo nome, será cadastrado e vinculado à esse tipo todos os cargos da unidade.
-- 4 - Ao cadastrar o tipo 'Refeição':
--     a) Caso já exista um com mesmo nome, será vinculado à esse tipo todos os cargos da unidade.
--     b) Caso não exista um com mesmo nome, será cadastrado e vinculado à esse tipo todos os cargos da unidade.
--
-- Para executar essa function, existem duas formas:
-- Primeira (padrão):
--    select *
--    from suporte.func_unidade_libera_controle_jornada(f_cod_unidade => CODIGO_AQUI);
--
-- Segunda (personalizada, podendo informar se quer criar os tipos 'Jornada' e 'Refeição'):
--    select *
--    from suporte.func_unidade_libera_controle_jornada(f_cod_unidade => CODIGO_AQUI,
--                                                      f_liberar_tipo_jornada => false,
--                                                      f_liberar_tipo_refeicao => false);
--
-- Sobre os tipos criados:
--  Jornada:
--     Horário Sugerido:  06:00 AM
--     Tempo Recomendado: 08:00 (duração)
--     Tempo Limite:      09:30 (duração)
--
--  Refeição:
--     Horário Sugerido:  12:00 PM
--     Tempo Recomendado: 01:00 (duração)
--     Tempo Limite:      01:30 (duração)
--
create or replace function suporte.func_unidade_libera_controle_jornada(f_cod_unidade bigint,
                                                                        f_liberar_tipo_jornada boolean default true,
                                                                        f_liberar_tipo_refeicao boolean default true)
    returns text
    language plpgsql
    security definer
as
$$
declare
    v_cod_empresa constant                     bigint := (select u.cod_empresa
                                                          from unidade u
                                                          where u.codigo = f_cod_unidade);
    v_cod_pilar_gente constant                 bigint := 3;
    v_cod_permissao_realizar_marcacao constant bigint := 336;
    v_cod_tipo_jornada_existente               bigint;
    v_cod_tipo_refeicao_existente              bigint;
    v_cod_tipo_jornada_inserido                bigint;
    v_cod_tipo_refeicao_inserido               bigint;
    v_return_message text := 'Controle de Jornada liberado para unidade ' || f_cod_unidade || '. ';
begin
    perform func_garante_unidade_existe(f_cod_unidade);

    -- Libera pilar gente se ainda não tiver.
    insert into unidade_pilar_prolog (cod_unidade, cod_pilar)
    values (f_cod_unidade, v_cod_pilar_gente)
    on conflict on constraint pk_unidade_pilar_prolog do nothing;

    -- Libera realizar marcação para todos os cargos da unidade que ainda não tiverem.
    insert into cargo_funcao_prolog_v11 (cod_unidade, cod_funcao_colaborador, cod_funcao_prolog, cod_pilar_prolog)
    select f_cod_unidade,
           f.codigo,
           v_cod_permissao_realizar_marcacao,
           v_cod_pilar_gente
    from funcao f
    where f.cod_empresa = v_cod_empresa
    on conflict on constraint cargo_funcao_prolog_v11_pkey do nothing;

    if f_liberar_tipo_jornada
    then
        -- Verifica se já existe um tipo de nome similar a 'Jornada'.
        -- Talvez fosse melhor utilizar um contains 'Jornada' no nome ao invés de ilike!?
        select it.codigo
        from intervalo_tipo it
        where unaccent(trim(it.nome)) ilike 'Jornada'
          and it.cod_unidade = f_cod_unidade
          and it.ativo = true
        into v_cod_tipo_jornada_existente;

        -- Se não existe, então cadastra.
        if v_cod_tipo_jornada_existente is null or v_cod_tipo_jornada_existente <= 0
        then
            select v_return_message || ' Jornada criada. ' into v_return_message;

            insert into intervalo_tipo (cod_unidade,
                                        nome,
                                        icone,
                                        tempo_recomendado_minutos,
                                        tempo_estouro_minutos,
                                        horario_sugerido,
                                        ativo)
            values (f_cod_unidade, 'Jornada', 'JORNADA', 480, 570, '06:00:00', true)
            returning codigo into v_cod_tipo_jornada_inserido;
        else
            select v_return_message || ' Jornada já existia. ' into v_return_message;
        end if;

        -- Após cadastrar, libera esse tipo para todos os cargos da unidade.
        insert into intervalo_tipo_cargo (cod_unidade, cod_tipo_intervalo, cod_cargo)
        select f_cod_unidade,
               coalesce(v_cod_tipo_jornada_existente, v_cod_tipo_jornada_inserido),
               f.codigo
        from funcao f
        where f.cod_empresa = v_cod_empresa
        on conflict on constraint pk_intervalo_tipo_cargo do nothing;
    end if;

    -- Realiza a mesma lógica feita ao tipo 'Jornada', agora para o tipo refeição.
    if f_liberar_tipo_refeicao
    then
        select it.codigo
        from intervalo_tipo it
        where unaccent(trim(it.nome)) ilike unaccent('Refeição')
          and it.cod_unidade = f_cod_unidade
          and it.ativo = true
        into v_cod_tipo_refeicao_existente;

        if v_cod_tipo_refeicao_existente is null or v_cod_tipo_refeicao_existente <= 0
        then
            select v_return_message || ' Refeição criada. ' into v_return_message;

            insert into intervalo_tipo (cod_unidade,
                                        nome,
                                        icone,
                                        tempo_recomendado_minutos,
                                        tempo_estouro_minutos,
                                        horario_sugerido, ativo)
            values (f_cod_unidade, 'Refeição', 'ALIMENTACAO', 60, 90, '12:00:00', true)
            returning codigo into v_cod_tipo_refeicao_inserido;
        else
            select v_return_message || ' Refeição já existia. ' into v_return_message;
        end if;

        insert into intervalo_tipo_cargo (cod_unidade, cod_tipo_intervalo, cod_cargo)
        select f_cod_unidade,
               coalesce(v_cod_tipo_refeicao_existente, v_cod_tipo_refeicao_inserido),
               f.codigo
        from funcao f
        where f.cod_empresa = v_cod_empresa
        on conflict on constraint pk_intervalo_tipo_cargo do nothing;
    end if;

    -- Por fim, cria um token para a unidade.
    insert into intervalo_unidade (cod_unidade, versao_dados, token_sincronizacao_marcacao)
    values (f_cod_unidade, 1, upper(f_random_string(32)))
    on conflict on constraint pk_intervalo_unidade do nothing;

    return v_return_message;
end
$$;