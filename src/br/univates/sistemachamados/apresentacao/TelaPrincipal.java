package br.univates.sistemachamados.apresentacao;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;

public class TelaPrincipal extends JFrame {
    private Connection conexao;

    public TelaPrincipal(Connection conexao) {
        this.conexao = conexao;

        // Configurações básicas da janela
        setTitle("Sistema de Gerenciamento de Chamados");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Criar barra de menu
        JMenuBar menuBar = new JMenuBar();

        // Menus
        JMenu menuCadastros = new JMenu("Cadastros");
        JMenu menuChamados = new JMenu("Chamados");

        // Itens de menu Cadastros
        JMenuItem itemCadastroUsuario = new JMenuItem("Usuários");
        itemCadastroUsuario.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new TelaCadastroUsuario(conexao).setVisible(true);
            }
        });

        JMenuItem itemCadastroAtendente = new JMenuItem("Atendentes");
        itemCadastroAtendente.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new TelaCadastroAtendente(conexao).setVisible(true);
            }
        });

        JMenuItem itemCadastroTipoChamado = new JMenuItem("Tipos de Chamado");
        itemCadastroTipoChamado.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new TelaCadastroTipoChamado(conexao).setVisible(true);
            }
        });

        // Itens de menu Chamados
        JMenuItem itemNovoChamado = new JMenuItem("Novo Chamado");
        itemNovoChamado.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new TelaCadastroChamado(conexao).setVisible(true);
            }
        });

        JMenuItem itemConsultaChamados = new JMenuItem("Consultar Chamados");
        itemConsultaChamados.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new TelaConsultaChamados(conexao).setVisible(true);
            }
        });

        JMenuItem itemResolverChamados = new JMenuItem("Resolver Chamados");
        itemResolverChamados.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new TelaResolverChamado(conexao).setVisible(true);
            }
        });

        // Adicionar itens aos menus
        menuCadastros.add(itemCadastroUsuario);
        menuCadastros.add(itemCadastroAtendente);
        menuCadastros.add(itemCadastroTipoChamado);

        menuChamados.add(itemNovoChamado);
        menuChamados.add(itemConsultaChamados);
        menuChamados.add(itemResolverChamados);

        // Adicionar menus à barra de menu
        menuBar.add(menuCadastros);
        menuBar.add(menuChamados);

        // Definir barra de menu
        setJMenuBar(menuBar);

        // Painel de boas-vindas
        JPanel painelPrincipal = new JPanel(new BorderLayout());
        JLabel labelBoasVindas = new JLabel("Bem-vindo ao Sistema de Gerenciamento de Chamados", SwingConstants.CENTER);
        labelBoasVindas.setFont(new Font("Arial", Font.BOLD, 20));
        painelPrincipal.add(labelBoasVindas, BorderLayout.CENTER);

        add(painelPrincipal);
    }
}