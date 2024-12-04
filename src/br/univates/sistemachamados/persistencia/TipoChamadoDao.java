package br.univates.sistemachamados.persistencia;

import br.univates.sistemachamados.objetos.TipoChamado;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TipoChamadoDao implements GenericDao<TipoChamado> {
    private Connection conexao;

    public TipoChamadoDao(Connection conexao) {
        this.conexao = conexao;
    }

    @Override
    public void inserir(TipoChamado tipoChamado) throws Exception {
        String sql = "INSERT INTO tipos_chamado (descricao) VALUES (?)";
        try (PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, tipoChamado.getDescricao());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    tipoChamado.setId(rs.getInt(1));
                }
            }
        }
    }

    @Override
    public void atualizar(TipoChamado tipoChamado) throws Exception {
        String sql = "UPDATE tipos_chamado SET descricao = ? WHERE id = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, tipoChamado.getDescricao());
            stmt.setInt(2, tipoChamado.getId());
            stmt.executeUpdate();
        }
    }

    @Override
    public void excluir(int id) throws Exception {
        String sql = "DELETE FROM tipos_chamado WHERE id = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    @Override
    public TipoChamado buscarPorId(int id) throws Exception {
        TipoChamado tipoChamado = null;
        String sql = "SELECT * FROM tipos_chamado WHERE id = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    tipoChamado = new TipoChamado();
                    tipoChamado.setId(rs.getInt("id"));
                    tipoChamado.setDescricao(rs.getString("descricao"));
                }
            }
        }
        return tipoChamado;
    }

    @Override
    public List<TipoChamado> buscarTodos() throws Exception {
        List<TipoChamado> tiposChamado = new ArrayList<>();
        String sql = "SELECT * FROM tipos_chamado";
        try (PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                TipoChamado tipoChamado = new TipoChamado();
                tipoChamado.setId(rs.getInt("id"));
                tipoChamado.setDescricao(rs.getString("descricao"));
                tiposChamado.add(tipoChamado);
            }
        }
        return tiposChamado;
    }
}
