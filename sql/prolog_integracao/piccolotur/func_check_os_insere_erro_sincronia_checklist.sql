-- Sobre:
--
-- Function utilizada na integração de ordem de serviço com a Praxio. Essa funtion é utilizada sempre que não é possível
-- sincronizar um checklist.
-- Nos cenários de erros, a function salva qual foi a error message e também o stacktrace que originou o erro. Também
-- incrementamos a quantidade de vezes que foi tentado sincronizar o checklist.
--
-- Histórico:
-- 2020-02-14 -> Function criada (diogenesvanzella - PLI-70).
-- 2020-02-25 -> Alteração do nome da function (diogenesvanzella - PLI-70).
create or replace function
    piccolotur.func_check_os_insere_erro_sincronia_checklist(f_cod_checklist bigint,
                                                             f_error_message text,
                                                             f_stacktrace text,
                                                             f_data_hora_atualizacao timestamp with time zone)
    returns void
    language plpgsql
as
$$
declare
    nova_quantidade_tentativas integer;
begin
    update piccolotur.checklist_pendente_para_sincronizar
    set mensagem_erro_ao_sincronizar = f_error_message,
        data_hora_ultima_atualizacao = f_data_hora_atualizacao,
        qtd_tentativas               = qtd_tentativas + 1
    where cod_checklist_para_sincronizar = f_cod_checklist
    returning qtd_tentativas into nova_quantidade_tentativas;

    insert into piccolotur.checklist_erros_sincronia(cod_checklist_para_sincronizar,
                                                     nova_qtd_tentativas,
                                                     error_stacktrace,
                                                     data_hora_erro)
    values (f_cod_checklist, nova_quantidade_tentativas, f_stacktrace, f_data_hora_atualizacao);

    if not found
    then
        -- Não queremos que esse erro seja mapeado para o usuário ou para a integração.
        raise exception '%', (format('Não foi possível salvar a error_message ao tentar sincronizar o checklist (%s)',
                                     f_cod_checklist));
    end if;
end;
$$;