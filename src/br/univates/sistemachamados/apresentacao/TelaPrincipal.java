package br.univates.sistemachamados.apresentacao;

import javax.swing.*;
import java.awt.*;

public class TelaPrincipal extends JFrame {
    public TelaPrincipal() {
        setTitle("Sistema de Chamados - Help Desk");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create main menu bar
        JMenuBar menuBar = new JMenuBar();

        // Menus
        JMenu cadastrosMenu = new JMenu("Cadastros");
        JMenu chamadosMenu = new JMenu("Chamados");

        // Cadastros Menu Items
        JMenuItem usuariosItem = new JMenuItem("Usuários");
        //TODO: usuariosItem.addActionListener(e -> new UsuarioFrame().setVisible(true));
        JMenuItem atendentesItem = new JMenuItem("Atendentes");
        //TODO: atendentesItem.addActionListener(e -> new AtendenteFrame().setVisible(true));
        JMenuItem tiposChamadoItem = new JMenuItem("Tipos de Chamado");
        //TODO: tiposChamadoItem.addActionListener(e -> new TipoChamadoFrame().setVisible(true));

        // Chamados Menu Items
        JMenuItem abrirChamadoItem = new JMenuItem("Abrir Chamado");
        //TODO: abrirChamadoItem.addActionListener(e -> new ChamadoFrame(null).setVisible(true));
        JMenuItem consultarChamadosItem = new JMenuItem("Consultar Chamados");
        //TODO: consultarChamadosItem.addActionListener(e -> new ConsultaChamadosFrame().setVisible(true));

        // Add items to menus
        cadastrosMenu.add(usuariosItem);
        cadastrosMenu.add(atendentesItem);
        cadastrosMenu.add(tiposChamadoItem);

        chamadosMenu.add(abrirChamadoItem);
        chamadosMenu.add(consultarChamadosItem);

        // Add menus to menu bar
        menuBar.add(cadastrosMenu);
        menuBar.add(chamadosMenu);

        setJMenuBar(menuBar);

        // Optional: Add a welcome panel
        JPanel welcomePanel = new JPanel();
        JLabel welcomeLabel = new JLabel("Bem-vindo ao Sistema de Chamados");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomePanel.add(welcomeLabel);

        add(welcomePanel);
    }

    public static void main(String[] args) {

        // Run the application
        SwingUtilities.invokeLater(() -> {
            new TelaPrincipal().setVisible(true);
        });
    }
}
