-- Script de inicialização do banco de dados para o Payment Service
-- Este script é executado quando o container MySQL é iniciado pela primeira vez

-- Criar o banco de dados se não existir
CREATE DATABASE IF NOT EXISTS payment_db;

-- Usar o banco de dados
USE payment_db;

-- Criar tabela para armazenar logs de pagamentos (opcional)
CREATE TABLE IF NOT EXISTS payment_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    payment_id VARCHAR(255) NOT NULL,
    order_id VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_payment_id (payment_id),
    INDEX idx_order_id (order_id)
);

-- Inserir dados de exemplo (opcional)
-- INSERT INTO payment_logs (payment_id, order_id, status, amount) VALUES 
-- ('MP123456789', 'ORDER001', 'PENDING', 25.50);

-- Mostrar informações do banco
SELECT 'Payment Service Database initialized successfully' as message;


