CREATE DATABASE IF NOT EXISTS petshop
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE petshop;

CREATE TABLE IF NOT EXISTS categoria (
  id_categoria BIGINT NOT NULL AUTO_INCREMENT,
  nome VARCHAR(255) NOT NULL,
  descricao TEXT,
  ativo BOOLEAN DEFAULT TRUE,
  PRIMARY KEY (id_categoria)
);

CREATE TABLE IF NOT EXISTS produtos (
  id_produtos BIGINT NOT NULL AUTO_INCREMENT,
  nome VARCHAR(255) NOT NULL,
  descricao VARCHAR(255),
  preco DECIMAL(10,2) NOT NULL,
  preco_desconto DECIMAL(10,2) NOT NULL,
  imagem LONGTEXT,
  qtd_estoque INT NOT NULL DEFAULT 0,
  ativo BOOLEAN NOT NULL DEFAULT TRUE,
  id_categoria BIGINT NOT NULL,
  PRIMARY KEY (id_produtos),
  CONSTRAINT fk_produtos_categoria
    FOREIGN KEY (id_categoria)
    REFERENCES categoria(id_categoria)
);

CREATE TABLE IF NOT EXISTS pedidos (
  id_pedido BIGINT NOT NULL AUTO_INCREMENT,
  id_usuario_fk BIGINT NOT NULL,
  data_pedido DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  status VARCHAR(255) NOT NULL,
  valor_total DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  endereco_entrega VARCHAR(255),
  PRIMARY KEY (id_pedido)
);

CREATE TABLE IF NOT EXISTS itens_pedido (
  id_item_pedido BIGINT NOT NULL AUTO_INCREMENT,
  id_pedido_fk BIGINT NOT NULL,
  id_produto_fk BIGINT NOT NULL,
  nome_produto VARCHAR(255) NOT NULL,
  quantidade INT NOT NULL,
  preco_unitario DECIMAL(10,2) NOT NULL,
  PRIMARY KEY (id_item_pedido),
  CONSTRAINT fk_itens_pedido_pedido
    FOREIGN KEY (id_pedido_fk)
    REFERENCES pedidos(id_pedido)
    ON DELETE CASCADE,
  CONSTRAINT fk_itens_pedido_produto
    FOREIGN KEY (id_produto_fk)
    REFERENCES produtos(id_produtos)
);
