-- Sobre:
--
-- Function utilizada na integração de ordem de serviço com a Praxio. Essa funtion marca um código de checklist como
-- não precisa ser sincronizado.
--
-- Histórico:
-- 2020-02-14 -> Function criada (diogenesvanzella - PLI-70).
-- 2020-02-25 -> Alteração do nome da function (diogenesvanzella - PLI-70).
create or replace function
    piccolotur.func_check_os_marca_checklist_nao_precisa_sincronizar(f_cod_checklist bigint,
                                                                     f_data_hora_atualizacao timestamp with time zone)
    returns void
    language plpgsql
as
$$
begin
    update piccolotur.checklist_pendente_para_sincronizar
    set precisa_ser_sincronizado     = false,
        data_hora_ultima_atualizacao = f_data_hora_atualizacao
    where cod_checklist_para_sincronizar = f_cod_checklist;

    if not found
    then
        -- Não queremos que esse erro seja mapeado para o usuário ou para a integração.
        raise exception '%', (format('Não foi possível marcar o checklist (%s) para não ser sincronizado',
                                     f_cod_checklist));
    end if;
end;
$$;