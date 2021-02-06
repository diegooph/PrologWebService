-- Sobre:
--
-- Cria uma sessão para o usuário informado na tabela `usuario_prolog_sessao`.
--
-- Histórico:
-- 2020-10-14 -> Function criada (luizfp).
-- 2020-10-14 -> Realiza cast para UUID antes do insert (luizfp).
create or replace function interno.func_usuario_iniciar_sessao(f_cod_usuario bigint,
                                                               f_token_usuario text,
                                                               f_data_hora_atual timestamp with time zone)
    returns void
    language plpgsql
as
$$
begin
    insert into interno.usuario_prolog_sessao (token,
                                               cod_usuario,
                                               data_hora_criacao,
                                               data_hora_ultimo_uso)
    values (f_token_usuario::uuid,
            f_cod_usuario,
            f_data_hora_atual,
            f_data_hora_atual);

    if not found
    then
        perform throw_generic_error('Erro ao criar sessão do usuário Prolog, tente novamente.');
    end if;
end
$$;