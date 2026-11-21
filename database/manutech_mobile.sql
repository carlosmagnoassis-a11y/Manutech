-- phpMyAdmin SQL Dump
-- version 4.0.4.2
-- http://www.phpmyadmin.net
--
-- Máquina: localhost
-- Data de Criação: 21-Nov-2026 às 05:21
-- Versão do servidor: 5.6.13
-- versão do PHP: 5.4.17

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Base de Dados: `manutech_mobile`
--
CREATE DATABASE IF NOT EXISTS `manutech_mobile` DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci;
USE `manutech_mobile`;

-- --------------------------------------------------------

--
-- Estrutura da tabela `equipamentos`
--

CREATE TABLE IF NOT EXISTS `equipamentos` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `nome` varchar(100) COLLATE utf8_unicode_ci NOT NULL,
  `identificacao` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `tipo` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `localizacao` varchar(150) COLLATE utf8_unicode_ci DEFAULT NULL,
  `status` varchar(30) COLLATE utf8_unicode_ci DEFAULT 'Ativo',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_equipamentos_identificacao` (`identificacao`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=13 ;

--
-- Extraindo dados da tabela `equipamentos`
--

INSERT INTO `equipamentos` (`id`, `nome`, `identificacao`, `tipo`, `localizacao`, `status`) VALUES
(1, 'Servidor de Rede', 'SRV-001', 'Servidor', 'Datacenter', 'Ativo'),
(2, 'Switch de Rede', 'SW-001', 'Switch', 'Sala de Telecom', 'Ativo'),
(3, 'Roteador Principal', 'RT-001', 'Roteador', 'Sala de Telecom', 'Ativo'),
(4, 'Nobreak', 'NB-001', 'Energia', 'Datacenter', 'Ativo'),
(5, 'Access Point', 'AP-001', 'Wi-Fi', 'Administrativo', 'Ativo'),
(6, 'Servidor de Teste', 'SRV-TESTE-001', 'Servidor', 'Laboratorio', 'Ativo'),
(10, 'Servidor Facebook', 'SRV-005', 'Servidor', 'Datacenter', 'Ativo');

-- --------------------------------------------------------

--
-- Estrutura da tabela `manutencoes`
--

CREATE TABLE IF NOT EXISTS `manutencoes` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `id_equipamento` int(11) NOT NULL,
  `problema` varchar(200) COLLATE utf8_unicode_ci NOT NULL,
  `descricao` text COLLATE utf8_unicode_ci,
  `data` date NOT NULL,
  `status` varchar(30) COLLATE utf8_unicode_ci DEFAULT 'Aberta',
  PRIMARY KEY (`id`),
  KEY `id_equipamento` (`id_equipamento`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=7 ;

--
-- Extraindo dados da tabela `manutencoes`
--

INSERT INTO `manutencoes` (`id`, `id_equipamento`, `problema`, `descricao`, `data`, `status`) VALUES
(1, 1, 'Servidor sem conectividade', 'Equipamento apresenta falha de comunicacao com a rede.', '2026-11-20', 'Aberta'),
(2, 1, 'Servidor sem conectividade', 'Equipamento apresenta falha de comunicacao com a rede.', '2026-11-20', 'Concluída'),
(3, 1, 'Falha de comunicao na rede', 'Switch apresenta perda de conectividade nas portas de rede.', '2026-11-20', 'Em andamento'),
(4, 6, 'nao liga', 'com problema', '2026-11-20', 'Concluída'),
(5, 4, 'nao liga', 'baterias com problema', '2026-11-21', 'Concluída'),
(6, 5, 'nao liga', 'fonte queimada', '2026-11-21', 'Em andamento');

--
-- Constraints for dumped tables
--

--
-- Limitadores para a tabela `manutencoes`
--
ALTER TABLE `manutencoes`
  ADD CONSTRAINT `manutencoes_ibfk_1` FOREIGN KEY (`id_equipamento`) REFERENCES `equipamentos` (`id`);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
