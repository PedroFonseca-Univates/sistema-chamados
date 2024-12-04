package br.univates.sistemachamados.persistencia;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConexaoBancoDados {
    private static final String DATABASE_PATH = "helpdesk.db";
    private static Connection conexao = null;

    // Método para obter conexão com o banco de dados SQLite
    public static Connection obterConexao() throws SQLException {
        if (conexao == null || conexao.isClosed()) {
            // Carregar o driver JDBC do SQLite
            try {
                Class.forName("org.sqlite.JDBC");
            } catch (ClassNotFoundException e) {
                throw new SQLException("Driver SQLite não encontrado", e);
            }

            // Criar conexão
            String url = "jdbc:sqlite:" + DATABASE_PATH;
            conexao = DriverManager.getConnection(url);
        }
        return conexao;
    }

    // Método para criar as tabelas necessárias
    public static void criarTabelas() throws SQLException {
        try (Connection conn = obterConexao();
             Statement stmt = conn.createStatement()) {

            // Criar tabela de usuários
            stmt.execute("CREATE TABLE IF NOT EXISTS usuarios (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "nome TEXT NOT NULL, " +
                    "email TEXT, " +
                    "telefone TEXT" +
                    ")");

            // Criar tabela de tipos de chamado
            stmt.execute("CREATE TABLE IF NOT EXISTS tipos_chamado (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "descricao TEXT NOT NULL" +
                    ")");

            // Criar tabela de atendentes
            stmt.execute("CREATE TABLE IF NOT EXISTS atendentes (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "nome TEXT NOT NULL, " +
                    "email TEXT, " +
                    "setor TEXT" +
                    ")");

            // Criar tabela de chamados
            stmt.execute("CREATE TABLE IF NOT EXISTS chamados (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "usuario_id INTEGER NOT NULL, " +
                    "tipo_chamado_id INTEGER NOT NULL, " +
                    "atendente_id INTEGER, " +
                    "descricao TEXT, " +
                    "status TEXT, " +
                    "data_criacao DATE, " +
                    "data_resolucao DATE, " +
                    "solucao TEXT, " +
                    "FOREIGN KEY(usuario_id) REFERENCES usuarios(id), " +
                    "FOREIGN KEY(tipo_chamado_id) REFERENCES tipos_chamado(id), " +
                    "FOREIGN KEY(atendente_id) REFERENCES atendentes(id)" +
                    ")");
        }
    }

    // Método para inicializar o banco de dados
    public static void inicializar() throws SQLException {
        // Verificar se o banco de dados já existe
        File dbFile = new File(DATABASE_PATH);

        // Se o banco de dados não existir, criar as tabelas
        if (!dbFile.exists()) {
            criarTabelas();

            // Opcional: Adicionar dados iniciais
            adicionarDadosIniciais();
        }
    }

    // Método para adicionar dados iniciais (opcional)
    private static void adicionarDadosIniciais() throws SQLException {
        try (Connection conn = obterConexao();
             Statement stmt = conn.createStatement()) {

            // Inserir tipos de chamado iniciais
            stmt.execute("INSERT INTO tipos_chamado (descricao) VALUES " +
                    "('Suporte Técnico'), " +
                    "('Dúvida'), " +
                    "('Problema de Sistema')");

            // Inserir um atendente inicial
            stmt.execute("INSERT INTO atendentes (nome, email, setor) VALUES " +
                    "('Atendente Padrão', 'atendente@empresa.com', 'Suporte')");
        }
    }

    // Método para fechar a conexão
    public static void fecharConexao() {
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

    // Método main para testar a inicialização
    public static void main(String[] args) {
        try {
            inicializar();
            System.out.println("Banco de dados inicializado com sucesso!");
        } catch (SQLException e) {
            System.err.println("Erro ao inicializar banco de dados: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
