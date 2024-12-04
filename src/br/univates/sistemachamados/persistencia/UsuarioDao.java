package br.univates.sistemachamados.persistencia;

import br.univates.sistemachamados.objetos.Usuario;

import java.sql.Connection;

public class UsuarioDao implements GenericDao<Usuario> {
    private Connection conexao;

    public UsuarioDao(Connection conexao) {
        this.conexao = conexao;
    }

    @Override
    public void inserir(Usuario usuario) throws Exception {
        // Validações
        if (usuario == null) {
            throw new IllegalArgumentException("Usuário não pode ser nulo");
        }
        if (usuario.getNome() == null || usuario.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do usuário é obrigatório");
        }

        String sql = "INSERT INTO usuarios (nome, email, telefone) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getEmail());
            stmt.setString(3, usuario.getTelefone());
            stmt.executeUpdate();

            // Obtém o ID gerado
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    usuario.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new Exception("Erro ao inserir usuário: " + e.getMessage(), e);
        }
    }

    @Override
    public void atualizar(Usuario usuario) throws Exception {
        // Validações
        if (usuario == null) {
            throw new IllegalArgumentException("Usuário não pode ser nulo");
        }
        if (usuario.getId() <= 0) {
            throw new IllegalArgumentException("ID do usuário inválido");
        }

        String sql = "UPDATE usuarios SET nome = ?, email = ?, telefone = ? WHERE id = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getEmail());
            stmt.setString(3, usuario.getTelefone());
            stmt.setInt(4, usuario.getId());

            int linhasAfetadas = stmt.executeUpdate();
            if (linhasAfetadas == 0) {
                throw new Exception("Usuário não encontrado para atualização");
            }
        } catch (SQLException e) {
            throw new Exception("Erro ao atualizar usuário: " + e.getMessage(), e);
        }
    }

    @Override
    public void excluir(int id) throws Exception {
        // Validações
        if (id <= 0) {
            throw new IllegalArgumentException("ID do usuário inválido");
        }

        String sql = "DELETE FROM usuarios WHERE id = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);

            int linhasAfetadas = stmt.executeUpdate();
            if (linhasAfetadas == 0) {
                throw new Exception("Usuário não encontrado para exclusão");
            }
        } catch (SQLException e) {
            throw new Exception("Erro ao excluir usuário: " + e.getMessage(), e);
        }
    }

    @Override
    public Usuario buscarPorId(int id) throws Exception {
        // Validações
        if (id <= 0) {
            throw new IllegalArgumentException("ID do usuário inválido");
        }

        String sql = "SELECT id, nome, email, telefone FROM usuarios WHERE id = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Usuario usuario = new Usuario();
                    usuario.setId(rs.getInt("id"));
                    usuario.setNome(rs.getString("nome"));
                    usuario.setEmail(rs.getString("email"));
                    usuario.setTelefone(rs.getString("telefone"));
                    return usuario;
                }
                throw new Exception("Usuário não encontrado");
            }
        } catch (SQLException e) {
            throw new Exception("Erro ao buscar usuário: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Usuario> buscarTodos() throws Exception {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT id, nome, email, telefone FROM usuarios ORDER BY nome";

        try (PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Usuario usuario = new Usuario();
                usuario.setId(rs.getInt("id"));
                usuario.setNome(rs.getString("nome"));
                usuario.setEmail(rs.getString("email"));
                usuario.setTelefone(rs.getString("telefone"));
                usuarios.add(usuario);
            }

            return usuarios;
        } catch (SQLException e) {
            throw new Exception("Erro ao buscar todos os usuários: " + e.getMessage(), e);
        }
    }

    // Método adicional para buscar usuário por email (útil para login)
    public Usuario buscarPorEmail(String email) throws Exception {
        // Validações
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email não pode ser vazio");
        }

        String sql = "SELECT id, nome, email, telefone FROM usuarios WHERE email = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Usuario usuario = new Usuario();
                    usuario.setId(rs.getInt("id"));
                    usuario.setNome(rs.getString("nome"));
                    usuario.setEmail(rs.getString("email"));
                    usuario.setTelefone(rs.getString("telefone"));
                    return usuario;
                }
                return null; // Retorna null se não encontrar
            }
        } catch (SQLException e) {
            throw new Exception("Erro ao buscar usuário por email: " + e.getMessage(), e);
        }
    }
}

