-- Habilita extensão para geração de UUID nativa (PostgreSQL)
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- =============================================================================
-- MÓDULO: GESTÃO & RESTAURANTE
-- =============================================================================

CREATE TABLE IF NOT EXISTS restaurante (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nome VARCHAR(150) NOT NULL,
    ativo BOOLEAN NOT NULL,
    descricao TEXT,
    telefone VARCHAR(20) NOT NULL,
    cep VARCHAR(9) NOT NULL,
    logradouro VARCHAR(150) NOT NULL,
    valor_minimo_pedido NUMERIC(10, 2) NOT NULL DEFAULT 0.00,
    complemento VARCHAR(150),
    override_funcionamento_tipo VARCHAR(30) NULL,
    override_funcionamento_valido_ate TIMESTAMP WITH TIME ZONE NULL,
    numero VARCHAR(20) NOT NULL,
    bairro VARCHAR(80) NOT NULL,
    data_criacao TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE  IF NOT EXISTS horario_funcionamento(
    id UUID PRIMARY KEY,
    restaurante_id UUID NOT NULL,
    dia_semana VARCHAR(9) NOT NULL,
    hora_abertura TIME NOT NULL,
    hora_fechamento TIME NOT NULL,

    CONSTRAINT fk_horario_restaurante
        FOREIGN KEY (restaurante_id)
        REFERENCES restaurante (id),

    CONSTRAINT ck_horario_dia_semana
        CHECK (dia_semana IN (
            'MONDAY',
            'TUESDAY',
            'WEDNESDAY',
            'THURSDAY',
            'FRIDAY',
            'SATURDAY',
            'SUNDAY'
        )),

    CONSTRAINT ck_horario_intervalo
        CHECK (hora_abertura < hora_fechamento)
);

-- =============================================================================
-- MÓDULO: USUÁRIO
-- =============================================================================

CREATE TABLE IF NOT EXISTS usuario (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nome VARCHAR(120) NOT NULL,
    senha TEXT NOT NULL,
    ativo BOOLEAN NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    cpf VARCHAR(14) NOT NULL UNIQUE

);

CREATE TABLE IF NOT EXISTS usuario_restaurante (
    id UUID PRIMARY KEY,
    usuario_id UUID NOT NULL,
    restaurante_id UUID NOT NULL,
    usuario_role VARCHAR(50) NOT NULL,
    CONSTRAINT uk_usuario_restaurante UNIQUE (usuario_id, restaurante_id),
    CONSTRAINT fk_usuario_restaurante_usuario
        FOREIGN KEY (usuario_id) REFERENCES usuario(id),
    CONSTRAINT fk_usuario_restaurante_restaurante
        FOREIGN KEY (restaurante_id) REFERENCES restaurante(id)
);

-- =============================================================================
-- MÓDULO: ESTOQUE & PRODUÇÃO
-- =============================================================================

CREATE TABLE IF NOT EXISTS ingrediente (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    restaurante_id UUID NOT NULL,
    nome VARCHAR(100) NOT NULL,
    unidade VARCHAR(20) NOT NULL,
    custo_unidade NUMERIC(10, 4) NOT NULL,
    quantidade_total NUMERIC(12, 3) NOT NULL,
    CONSTRAINT fk_ingrediente_restaurante FOREIGN KEY (restaurante_id) REFERENCES restaurante(id),
    CONSTRAINT uk_ingrediente_restaurante_nome UNIQUE (restaurante_id, nome),
    CONSTRAINT uk_ingrediente_restaurante UNIQUE (id, restaurante_id),
    CONSTRAINT ck_ingrediente_unidade CHECK (unidade IN ('G', 'KG', 'ML', 'L', 'UN'))
);

CREATE TABLE IF NOT EXISTS produto (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    restaurante_id UUID NOT NULL,
    nome VARCHAR(120) NOT NULL,
    custo_producao NUMERIC(10, 2) NOT NULL DEFAULT 0.00,
    data_criacao TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_produto_restaurante FOREIGN KEY (restaurante_id) REFERENCES restaurante(id),
    CONSTRAINT uk_produto_restaurante UNIQUE (id, restaurante_id)
);

CREATE TABLE IF NOT EXISTS produto_ingrediente (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    produto_id UUID NOT NULL,
    ingrediente_id UUID NOT NULL,
    restaurante_id UUID NOT NULL,
    qtd_ingrediente NUMERIC(10, 3) NOT NULL,
    CONSTRAINT fk_prod_ing_produto FOREIGN KEY (produto_id, restaurante_id) REFERENCES produto(id, restaurante_id) ON DELETE CASCADE,
    CONSTRAINT fk_prod_ing_ingrediente FOREIGN KEY (ingrediente_id, restaurante_id) REFERENCES ingrediente(id, restaurante_id),
    CONSTRAINT uk_produto_ingrediente UNIQUE (produto_id, ingrediente_id)
);

-- =============================================================================
-- MÓDULO: CARDÁPIO / CATÁLOGO COMERCIAL
-- =============================================================================

CREATE TABLE IF NOT EXISTS cardapio (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    restaurante_id UUID NOT NULL,
    nome VARCHAR(100) NOT NULL,
    descricao TEXT,
    ativo BOOLEAN NOT NULL DEFAULT true,
    data_criacao TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_cardapio_restaurante FOREIGN KEY (restaurante_id) REFERENCES restaurante(id)
);

CREATE TABLE IF NOT EXISTS categoria (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    restaurante_id UUID NOT NULL,
    nome VARCHAR(80) NOT NULL,
    descricao TEXT,
    preco_a_partir NUMERIC(10, 2),
    data_criacao TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_categoria_restaurante FOREIGN KEY (restaurante_id) REFERENCES restaurante(id)
);

CREATE TABLE IF NOT EXISTS produto_cardapio (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    cardapio_id UUID NOT NULL,
    categoria_id UUID NOT NULL,
    produto_id UUID NOT NULL,
    nome_comercial VARCHAR(120) NOT NULL,
    descricao TEXT,
    imagem_url VARCHAR(500),
    preco NUMERIC(10, 2) NOT NULL,
    tempo_preparo INTEGER,
    CONSTRAINT fk_prod_cardapio_cardapio FOREIGN KEY (cardapio_id) REFERENCES cardapio(id) ON DELETE CASCADE,
    CONSTRAINT fk_prod_cardapio_categoria FOREIGN KEY (categoria_id) REFERENCES categoria(id),
    CONSTRAINT fk_prod_cardapio_produto FOREIGN KEY (produto_id) REFERENCES produto(id)
);

CREATE TABLE IF NOT EXISTS grupo_opcao (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    produto_cardapio_id UUID NOT NULL,
    nome VARCHAR(100) NOT NULL,
    obrigatorio BOOLEAN NOT NULL DEFAULT false,
    minimo_escolhas INTEGER NOT NULL DEFAULT 0,
    maximo_escolhas INTEGER NOT NULL DEFAULT 1,
    CONSTRAINT fk_grupo_opcao_prod_cardapio
        FOREIGN KEY (produto_cardapio_id) REFERENCES produto_cardapio(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS opcao_produto (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    grupo_opcao_id UUID NOT NULL,
    nome VARCHAR(100) NOT NULL,
    preco_adicional NUMERIC(10, 2) NOT NULL DEFAULT 0.00,
    quantidade_maxima INTEGER NOT NULL DEFAULT 1,
    disponivel BOOLEAN NOT NULL DEFAULT true,
    CONSTRAINT fk_opcao_produto_grupo FOREIGN KEY (grupo_opcao_id) REFERENCES grupo_opcao(id) ON DELETE CASCADE
);

-- Ligação entre Opção do Catálogo e Baixa de Estoque
CREATE TABLE IF NOT EXISTS opcao_ingrediente (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    opcao_produto_id UUID NOT NULL,
    ingrediente_id UUID NOT NULL,
    qtd_ingrediente NUMERIC(10, 3) NOT NULL,
    CONSTRAINT fk_opcao_ing_opcao FOREIGN KEY (opcao_produto_id) REFERENCES opcao_produto(id) ON DELETE CASCADE,
    CONSTRAINT fk_opcao_ing_ingrediente FOREIGN KEY (ingrediente_id) REFERENCES ingrediente(id),
    CONSTRAINT uk_opcao_ingrediente UNIQUE (opcao_produto_id, ingrediente_id)
);

-- =============================================================================
-- MÓDULO: CLIENTE
-- =============================================================================

CREATE TABLE IF NOT EXISTS cliente (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nome VARCHAR(120) NOT NULL,
    telefone VARCHAR(11) NOT NULL,
    cpf VARCHAR(14) NOT NULL UNIQUE,
    ativo BOOLEAN NOT NULL DEFAULT true,
    criado_em TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    excluido_em TIMESTAMP WITH TIME ZONE
);

CREATE TABLE IF NOT EXISTS cliente_endereco (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    cliente_id UUID NOT NULL,
    cep VARCHAR(9) NOT NULL,
    logradouro VARCHAR(150) NOT NULL,
    numero VARCHAR(20) NOT NULL,
    bairro VARCHAR(80) NOT NULL,
    complemento VARCHAR(100),
    apelido VARCHAR(50),
    padrao BOOLEAN NOT NULL DEFAULT false,
    CONSTRAINT fk_endereco_cliente FOREIGN KEY (cliente_id) REFERENCES cliente(id) ON DELETE CASCADE
);

-- =============================================================================
-- MÓDULO: PEDIDO & AUDITORIA FINANCEIRA
-- =============================================================================

CREATE TABLE IF NOT EXISTS pedido (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    restaurante_id UUID NOT NULL,
    cliente_id UUID NOT NULL,
    entrega_cep VARCHAR(9) NOT NULL,
    entrega_logradouro VARCHAR(150) NOT NULL,
    entrega_numero VARCHAR(20) NOT NULL,
    entrega_bairro VARCHAR(80) NOT NULL,
    entrega_complemento VARCHAR(100),
    valor_itens NUMERIC(10, 2) NOT NULL,
    valor_entrega NUMERIC(10, 2) NOT NULL DEFAULT 0.00,
    valor_desconto NUMERIC(10, 2) NOT NULL DEFAULT 0.00,
    valor_total NUMERIC(10, 2) NOT NULL,
    status VARCHAR(30) NOT NULL,
    motivo_cancelamento TEXT NOT NULL,
    criado_em TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    confirmado_em TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    saiu_entrega_em TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    entregue_em TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    cancelado_em TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_pedido_restaurante FOREIGN KEY (restaurante_id) REFERENCES restaurante(id),
    CONSTRAINT fk_pedido_cliente FOREIGN KEY (cliente_id) REFERENCES cliente(id)
);

CREATE TABLE IF NOT EXISTS item_pedido (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    pedido_id UUID NOT NULL,
    produto_cardapio_id UUID NOT NULL,
    nome_item VARCHAR(120) NOT NULL,
    preco_item NUMERIC(10, 2) NOT NULL,
    quantidade_item INTEGER NOT NULL,
    subtotal_item NUMERIC(10, 2) NOT NULL,
    observacao VARCHAR(255),
    CONSTRAINT fk_item_pedido_pedido FOREIGN KEY (pedido_id) REFERENCES pedido(id) ON DELETE CASCADE,
    CONSTRAINT fk_item_pedido_produto_cardapio FOREIGN KEY (produto_cardapio_id) REFERENCES produto_cardapio(id)
);

CREATE TABLE IF NOT EXISTS opcao_item_pedido (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    item_pedido_id UUID NOT NULL,
    opcao_produto_id UUID NOT NULL,
    nome_opcao VARCHAR(100) NOT NULL,
    preco_opcao NUMERIC(10, 2) NOT NULL,
    quantidade_opcao INTEGER NOT NULL,
    CONSTRAINT fk_opcao_item_item_pedido FOREIGN KEY (item_pedido_id) REFERENCES item_pedido(id) ON DELETE CASCADE,
    CONSTRAINT fk_opcao_item_opcao_produto FOREIGN KEY (opcao_produto_id) REFERENCES opcao_produto(id)
);

-- SPRING MODULITH
CREATE TABLE IF NOT EXISTS event_publication
(
  id                     UUID NOT NULL,
  listener_id            TEXT NOT NULL,
  event_type             TEXT NOT NULL,
  serialized_event       TEXT NOT NULL,
  publication_date       TIMESTAMP WITH TIME ZONE NOT NULL,
  completion_date        TIMESTAMP WITH TIME ZONE,
  status                 TEXT,
  completion_attempts    INT,
  last_resubmission_date TIMESTAMP WITH TIME ZONE,
  PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS event_publication_serialized_event_hash_idx ON event_publication USING hash(serialized_event);
CREATE INDEX IF NOT EXISTS event_publication_by_completion_date_idx ON event_publication (completion_date);