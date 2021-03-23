-- Fiz um select distinct pra ver quais status existiam:
-- 200: logando errorBody e isError true, corrigido no primeiro select;
-- 201: logando corretamente, tudo certo, então não estava no primeiro select;
-- 401: logando body e isError false. corrigido no segundo select;
-- 404: logando body e isError false. corrigido no segundo select;
-- 500: logando body e isError false. corrigido no segundo select;

-- Corrige os logs de status 200 que tinha isError como true
update integracao.log_request_response
set response_json = response_json - 'errorBody' - 'isError'
    || jsonb_build_object('body', response_json -> 'errorBody', 'isError', false)
where response_status = 200
  and response_json -> 'isError' = 'true'
  and data_hora_request::date >= '2020-09-07';

-- Corrige os logs de status 401, 404 e 500, que tinham isError como false.
update integracao.log_request_response
set response_json = response_json - 'body' - 'isError'
    || jsonb_build_object('errorBody', response_json -> 'body', 'isError', true)
where response_status in (401, 404, 500)
  and response_json -> 'isError' = 'false'
  and data_hora_request::date >= '2020-09-07';