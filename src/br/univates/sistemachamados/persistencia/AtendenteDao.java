package br.univates.sistemachamados.persistencia;

import br.univates.sistemachamados.negocio.Atendente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AtendenteDao implements GenericDao<Atendente> {
    private Connection conexao;

    public AtendenteDao(Connection conexao) {
        this.conexao = conexao;
    }

    @Override
    public void inserir(Atendente atendente) throws Exception {
        String sql = "INSERT INTO atendentes (nome, email, setor) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, atendente.getNome());
            stmt.setString(2, atendente.getEmail());
            stmt.setString(3, atendente.getSetor());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    atendente.setId(rs.getInt(1));
                }
            }
        }
    }

    @Override
    public void atualizar(Atendente atendente) throws Exception {
        String sql = "UPDATE atendentes SET nome = ?, email = ?, setor = ? WHERE id = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, atendente.getNome());
            stmt.setString(2, atendente.getEmail());
            stmt.setString(3, atendente.getSetor());
            stmt.setInt(4, atendente.getId());
            stmt.executeUpdate();
        }
    }

    @Override
    public void excluir(int id) throws Exception {
        String sql = "DELETE FROM atendentes WHERE id = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    @Override
    public Atendente buscarPorId(int id) throws Exception {
        Atendente atendente = null;
        String sql = "SELECT * FROM atendentes WHERE id = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    atendente = new Atendente();
                    atendente.setId(rs.getInt("id"));
                    atendente.setNome(rs.getString("nome"));
                    atendente.setEmail(rs.getString("email"));
                    atendente.setSetor(rs.getString("setor"));
                }
            }
        }
        return atendente;
    }

    @Override
    public List<Atendente> buscarTodos() throws Exception {
        List<Atendente> atendentes = new ArrayList<>();
        String sql = "SELECT * FROM atendentes";
        try (PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Atendente atendente = new Atendente();
                atendente.setId(rs.getInt("id"));
                atendente.setNome(rs.getString("nome"));
                atendente.setEmail(rs.getString("email"));
                atendente.setSetor(rs.getString("setor"));
                atendentes.add(atendente);
            }
        }
        return atendentes;
    }
}