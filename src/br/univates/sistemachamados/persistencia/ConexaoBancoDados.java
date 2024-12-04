package br.univates.sistemachamados.persistencia;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoBancoDados {
    private static final String URL = "jdbc:postgresql://localhost:5432/helpdesk";
    private static final String USUARIO = "seu_usuario";
    private static final String SENHA = "sua_senha";

    // Método para obter conexão com o banco de dados
    public static Connection obterConexao() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, SENHA);
    }

    // Método para fechar conexão
    public static void fecharConexao(Connection conexao) {
        if (conexao != null) {
            try {
                conexao.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Método para iniciar transação
    public static void iniciarTransacao(Connection conexao) throws SQLException {
        conexao.setAutoCommit(false);
    }

    // Método para confirmar transação
    public static void confirmarTransacao(Connection conexao) throws SQLException {
        conexao.commit();
    }

    // Método para reverter transação em caso de erro
    public static void reverterTransacao(Connection conexao) throws SQLException {
        conexao.rollback();
    }
}
