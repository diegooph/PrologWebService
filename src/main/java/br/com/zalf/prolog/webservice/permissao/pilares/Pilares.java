package br.com.zalf.prolog.webservice.permissao.pilares;

public final class Pilares {

    public static final int FROTA = 1;
    public static final int SEGURANCA = 2;
    public static final int GENTE = 3;
    public static final int ENTREGA = 4;
    public static final int GERAL = 5;

    private Pilares() {
        // prevent instatiation
    }

    public static final class Frota extends Pilar {

        /**
         * Essa permissão libera tanto a visualização das transferências de veículos/pneus quanto a realização das
         * mesmas.
         */
        public static final int TRANSFERENCIA_PNEUS_VEICULOS = 141;

        public final class FarolStatusPlacas {

            public static final int VISUALIZAR = 10;

            private FarolStatusPlacas() {
            }

        }

        public final class Checklist {

            public static final int REALIZAR = 11;
            public static final int VISUALIZAR_TODOS = 118;

            private Checklist() {
            }

            public final class Modelo {

                public static final int VISUALIZAR = 112;
                public static final int CADASTRAR = 113;
                public static final int ALTERAR = 114;

                private Modelo() {
                }

            }

        }

        public final class Veiculo {

            public static final int CADASTRAR = 14;
            public static final int ALTERAR = 16;
            public static final int VISUALIZAR = 115;
            public static final int VISUALIZAR_RELATORIOS = 122;

            private Veiculo() {
            }

        }

        public final class Pneu {

            public static final int CADASTRAR = 15;
            public static final int ALTERAR = 17;
            public static final int VINCULAR_VEICULO = 111;
            public static final int VISUALIZAR = 116;

            private Pneu() {
            }

            public final class Movimentacao {

                //-- ‘Movimentação - Veículo / Estoque (Veículo -> Estoque • Estoque -> Veículo • Veículo -> Veículo)’
                public static final int MOVIMENTAR_VEICULO_ESTOQUE = 142;
                //-- ‘Movimentação - Análise (Estoque ou Veículo -> Análise • Análise -> Estoque)’
                public static final int MOVIMENTAR_ANALISE = 143;
                //-- ‘Movimentação - Descarte (Estoque ou Veículo ou Análise -> Descarte)’
                public static final int MOVIMENTAR_DESCARTE = 144;
                public static final int CADASTRAR_MOTIVOS_MOVIMENTACAO = 123;
                public static final int EDITAR_MOTIVOS_MOVIMENTACAO = 124;

                private Movimentacao() {
                }

            }
        }

        public final class Afericao {

            public static final int REALIZAR_AFERICAO_PLACA = 18;
            public static final int REALIZAR_AFERICAO_PNEU_AVULSO = 140;
            public static final int VISUALIZAR_TODAS_AFERICOES = 117;

            private Afericao() {
            }

            public final class ConfiguracaoAfericao {

                public static final int CONFIGURAR = 100;

            }

        }

        public final class OrdemServico {
            private OrdemServico() {
            }

            public final class Checklist {

                public static final int VISUALIZAR = 12;
                public static final int RESOLVER_ITEM = 13;

                private Checklist() {
                }

            }

            public final class Pneu {

                public static final int VISUALIZAR = 119;
                public static final int CONSERTAR_ITEM = 19;

                private Pneu() {
                }

            }
        }

        public final class Relatorios {

            public static final int PNEU = 110;
            public static final int CHECKLIST = 121;

            private Relatorios() {
            }

        }

        public final class Recapadora {

            public static final int CADASTRO = 130;
            public static final int VISUALIZACAO = 131;
            public static final int EDICAO = 132;

            private Recapadora() {
            }

            public final class TipoServico {

                public static final int CADASTRO = 133;
                public static final int VISUALIZACAO = 134;
                public static final int EDICAO = 135;

                private TipoServico() {
                }

            }

        }

        public final class SocorroRota {

            public static final int SOLICITAR_SOCORRO = 145;
            // Assumir um socorro. Invalidar um socorro. Finalizar um socorro.
            public static final int TRATAR_SOCORRO = 146;
            public static final int VISUALIZAR_SOCORROS_E_RELATORIOS = 147;
            // Listar, criar, editar, ativar e inativar opções de problemas
            public static final int GERENCIAR_OPCOES_PROBLEMAS = 148;

            private SocorroRota() {
            }

        }

    }

    public static final class Seguranca extends Pilar {

        public static final int GSD = 20;

        public final class Relato {

            public static final int REALIZAR = 21;
            public static final int CLASSIFICAR = 23;
            public static final int FECHAR = 24;
            public static final int VISUALIZAR = 25;
            public static final int RELATORIOS = 26;

            private Relato() {
            }

        }

    }

    public static final class Gente extends Pilar {

        public final class Intervalo {
            /**
             * Quem tiver essa permissão, automaticamente está liberado para visualizar suas pŕoprias marcações.
             */
            public static final int MARCAR_INTERVALO = 336;
            public static final int VISUALIZAR_TODAS_MARCACOES = 337;
            /**
             * Permite ao usuário criar, editar, ativar ou inativar marcações.
             */
            public static final int AJUSTE_MARCACOES = 338;
            public static final int CRIAR_TIPO_INTERVALO = 340;
            public static final int ALTERAR_TIPO_INTERVALO = 344;
            public static final int ATIVAR_INATIVAR_TIPO_INTERVALO = 341;

            private Intervalo() {
            }

        }

        public final class ProntuarioCondutor {

            public static final int UPLOAD = 333;
            public static final int VISUALIZAR_PROPRIO = 334;
            public static final int VISUALIZAR_TODOS = 335;

            private ProntuarioCondutor() {
            }

        }

        public final class Relatorios {

            public static final int QUIZ = 330;
            public static final int FALE_CONOSCO = 331;
            public static final int SOLICITACAO_FOLGA = 332;
            public static final int INTERVALOS = 342;
            public static final int TREINAMENTOS = 343;

            private Relatorios() {
            }

        }

        public final class Treinamentos {

            public static final int VISUALIZAR_PROPRIOS = 30;
            public static final int CRIAR = 323;
            public static final int ALTERAR = 318;

            private Treinamentos() {
            }

        }

        public final class Calendario {

            public static final int VISUALIZAR_PROPRIOS = 32;
            public static final int CRIAR_EVENTO = 324;
            public static final int ALTERAR_EVENTO = 319;

            private Calendario() {
            }

        }

        public final class PreContracheque {

            public static final int UPLOAD_E_EDICAO = 34;
            public static final int VISUALIZAR = 35;

            private PreContracheque() {
            }

        }

        public final class Quiz {

            public static final int REALIZAR = 36;
            public static final int VISUALIZAR = 326;

            private Quiz() {
            }

            public final class Modelo {

                public static final int VISUALIZAR = 320;
                public static final int CRIAR = 37;
                public static final int ALTERAR = 321;

                private Modelo() {
                }

            }

        }

        public final class SolicitacaoFolga {

            public static final int REALIZAR = 38;
            public static final int FEEDBACK_SOLICITACAO = 39;
            public static final int VISUALIZAR = 327;

            private SolicitacaoFolga() {
            }

        }

        public final class Colaborador {

            public static final int CADASTRAR = 310;
            public static final int EDITAR = 325;
            public static final int VISUALIZAR = 316;

            private Colaborador() {
            }

        }

        public final class Permissao {

            public static final int VISUALIZAR = 328;
            public static final int VINCULAR_CARGO = 329;

            private Permissao() {
            }

        }

        public final class Equipe {

            public static final int CADASTRAR = 311;
            public static final int EDITAR = 313;
            public static final int VISUALIZAR = 317;

            private Equipe() {
            }

        }

        public final class FaleConosco {

            public static final int VISUALIZAR_TODOS = 322;
            public static final int REALIZAR = 314;
            public static final int FEEDBACK = 315;

            private FaleConosco() {
            }

        }

    }

    public static final class Entrega extends Pilar {

        public final class Indicadores {

            public static final int INDICADORES = 40;

            private Indicadores() {
            }

        }

        public final class Relatorios {

            public static final int INDICADORES = 41;
            public static final int PRODUTIVIDADE = 48;

            private Relatorios() {
            }

        }

        public final class Upload {

            public static final int MAPA_TRACKING = 42;
            public static final int VERIFICACAO_DADOS = 43;

            private Upload() {
            }

        }

        public final class EscalaDiaria {

            public static final int DELETAR = 410;
            public static final int INSERIR_REGISTRO = 411;
            public static final int VISUALIZAR = 412;
            public static final int EDITAR = 413;

            private EscalaDiaria() {
            }

        }

        public final class Meta {

            public static final int EDITAR = 44;
            public static final int VISUALIZAR = 47;

            private Meta() {
            }

        }

        public final class Produtividade {

            public static final int INDIVIDUAL = 45;
            public static final int CONSOLIDADO = 46;

            private Produtividade() {
            }

        }

        public final class RaizenProdutividade {

            public static final int INSERIR_REGISTROS = 417;
            public static final int VISUALIZAR_TODOS = 414;
            public static final int VISUALIZAR_PROPRIOS = 415;
            public static final int EDITAR = 416;
            public static final int DELETAR = 418;
            public static final int VISUALIZAR_RELATORIOS = 419;

            private RaizenProdutividade() {
            }

        }

    }

    public static final class Geral extends Pilar {

        public static final class Empresa {

            /**
             * Permite ao usuário acessar a listagem de regionais e unidades.
             */
            public static final int VISUALIZAR_ESTRUTURA = 503;
            /**
             * Permite ao usuário alterar o nome, as regionais e unidades da empresa.
             */
            public static final int EDITAR_ESTRUTURA = 502;

            private Empresa() {
            }

        }

        public final class DispositivosMoveis {

            public static final int GESTAO = 501;

            private DispositivosMoveis() {
            }

        }

    }
}