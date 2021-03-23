-- SQL para buscar LOG de pneu na integração com a Praxio
select codigo                           as                                                         codigo,
       cod_empresa                      as                                                         cod_empresa,
       response_status                  as                                                         status,
       (request_json ->> 'body')::jsonb as                                                         request,
       f_if(response_status = 200, response_json ->> 'body', response_json ->> 'errorBody')::jsonb response,
       data_hora_request                as                                                         data_hora
from log.log_request_response
where cod_empresa = 45
  and data_hora_request::date >= '20200806'
  and request_json ->> 'body' like '%codigoCliente\":\"1609176%'::text
order by codigo desc;

-- SQL para buscar LOG de placa na integração com a Praxio
select codigo                           as                                                         codigo,
       cod_empresa                      as                                                         cod_empresa,
       response_status                  as                                                         status,
       (request_json ->> 'body')::jsonb as                                                         request,
       f_if(response_status = 200, response_json ->> 'body', response_json ->> 'errorBody')::jsonb response,
       data_hora_request                as                                                         data_hora
from log.log_request_response
where cod_empresa = 45
  and data_hora_request::date >= '20200806'
  and request_json ->> 'body'::text like '%LOZ 5D93%'::text
order by codigo desc;

-- SQL para buscar LOG de Ordem de Serviço da Piccolotur
select codigo,
       cod_empresa,
       response_status,
       (request_json ->> 'body')::jsonb,
       f_if(response_status = 200, response_json ->> 'body', response_json ->> 'errorBody')::jsonb,
       data_hora_request
from log.log_request_response
where (cod_empresa = 11 or cod_empresa = 28)
  and request_json ->> 'body'::text like '%codOsGlobus\":117373%'::text
order by codigo desc;

-- SQL para buscar LOG de Ordem de Serviço da Translecchi
select response_status,
       (request_json ->> 'body')::jsonb,
       response_json,
       data_hora_request at time zone tz_unidade(149)
from log.log_request_response
where cod_empresa = 4
  and (request_json ->> 'body')::text like '%codOrdemServico": 4930%';

-- SQL para buscar LOG de envio de dados do Prolog para outro sistema
-- NOVA AFERIÇÃO NEPOMUCENO
select data_hora_request,
       (response_json ->> 'body')::jsonb,
       *
from log.log_request_response
where request_json ->> 'url' ilike '%http://wsapp.expressonepomuceno.com.br:8191/rest/NOVA_AFERICAO%'::text
  and response_json ->> 'body' ilike '%codPneu\": \"149019%'
order by codigo;

-- ENVIO DE AFERIÇÃO NEPOMUCENO
select (request_json ->> 'body')::jsonb,
       *
from log.log_request_response
where request_json ->> 'url' ilike '%http://wsapp.expressonepomuceno.com.br:8191/rest/inspneus%'::text
  and request_json ->> 'body' ilike '%codigoCliente\":\"3440%';

-- ENVIO DE ORDENS DE SERVIÇO AVILAN
select (request_json ->> 'body')::jsonb,
       *
from log.log_request_response
where request_json ->> 'url' ilike '%http://prolog.avaconcloud.com/Avilan/IntegracaoMobile/api/OrdemServicoIn%'::text
  and data_hora_request::date >= now()::date
order by data_hora_request desc;