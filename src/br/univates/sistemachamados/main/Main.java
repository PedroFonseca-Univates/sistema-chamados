package br.univates.sistemachamados.main;


import br.univates.sistemachamados.apresentacao.TelaPrincipal;
import br.univates.sistemachamados.persistencia.ConexaoBancoDados;

import javax.swing.*;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) {
        try {
            ConexaoBancoDados.inicializar();
            System.out.println("Banco de dados inicializado com sucesso!");
        } catch (SQLException e) {
            System.err.println("Erro ao inicializar banco de dados: " + e.getMessage());
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            new TelaPrincipal().setVisible(true);
        });
    }

}