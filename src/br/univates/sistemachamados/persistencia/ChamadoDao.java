package br.univates.sistemachamados.persistencia;

import br.univates.sistemachamados.objetos.Chamado;

import java.sql.*;

import java.util.ArrayList;
import java.util.List;

public class ChamadoDao implements GenericDao<Chamado> {
    private Connection conexao;
    private UsuarioDao usuarioDao;
    private TipoChamadoDao tipoChamadoDao;
    private AtendenteDao atendenteDao;

    public ChamadoDao(Connection conexao) {
        this.conexao = conexao;
        this.usuarioDao = new UsuarioDao(conexao);
        this.tipoChamadoDao = new TipoChamadoDao(conexao);
        this.atendenteDao = new AtendenteDao(conexao);
    }

    @Override
    public void inserir(Chamado chamado) throws Exception {
        String sql = "INSERT INTO chamados " +
                "(usuario_id, tipo_chamado_id, atendente_id, descricao, status, data_criacao, data_resolucao, solucao) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, chamado.getUsuario().getId());
            stmt.setInt(2, chamado.getTipoChamado().getId());

            // Lidar com atendente que pode ser nulo
            if (chamado.getAtendente() != null) {
                stmt.setInt(3, chamado.getAtendente().getId());
            } else {
                stmt.setNull(3, Types.INTEGER);
            }

            stmt.setString(4, chamado.getDescricao());
            stmt.setString(5, chamado.getStatus());

            // Converter java.util.Date para java.sql.Date
            stmt.setDate(6, chamado.getDataCriacao() != null ?
                    new java.sql.Date(chamado.getDataCriacao().getTime()) : null);

            stmt.setDate(7, chamado.getDataResolucao() != null ?
                    new java.sql.Date(chamado.getDataResolucao().getTime()) : null);

            stmt.setString(8, chamado.getSolucao());

            stmt.executeUpdate();

            // Recuperar ID gerado
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    chamado.setId(rs.getInt(1));
                }
            }
        }
    }

    @Override
    public void atualizar(Chamado chamado) throws Exception {
        String sql = "UPDATE chamados SET " +
                "usuario_id = ?, tipo_chamado_id = ?, atendente_id = ?, " +
                "descricao = ?, status = ?, data_criacao = ?, " +
                "data_resolucao = ?, solucao = ? WHERE id = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, chamado.getUsuario().getId());
            stmt.setInt(2, chamado.getTipoChamado().getId());

            // Lidar com atendente que pode ser nulo
            if (chamado.getAtendente() != null) {
                stmt.setInt(3, chamado.getAtendente().getId());
            } else {
                stmt.setNull(3, Types.INTEGER);
            }

            stmt.setString(4, chamado.getDescricao());
            stmt.setString(5, chamado.getStatus());

            stmt.setDate(6, chamado.getDataCriacao() != null ?
                    new java.sql.Date(chamado.getDataCriacao().getTime()) : null);

            stmt.setDate(7, chamado.getDataResolucao() != null ?
                    new java.sql.Date(chamado.getDataResolucao().getTime()) : null);

            stmt.setString(8, chamado.getSolucao());
            stmt.setInt(9, chamado.getId());

            stmt.executeUpdate();
        }
    }

    @Override
    public void excluir(int id) throws Exception {
        String sql = "DELETE FROM chamados WHERE id = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    @Override
    public Chamado buscarPorId(int id) throws Exception {
        Chamado chamado = null;
        String sql = "SELECT * FROM chamados WHERE id = ?";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    chamado = new Chamado();
                    chamado.setId(rs.getInt("id"));

                    // Recuperar objetos relacionados
                    int usuarioId = rs.getInt("usuario_id");
                    chamado.setUsuario(usuarioDao.buscarPorId(usuarioId));

                    int tipoChamadoId = rs.getInt("tipo_chamado_id");
                    chamado.setTipoChamado(tipoChamadoDao.buscarPorId(tipoChamadoId));

                    int atendenteId = rs.getInt("atendente_id");
                    chamado.setAtendente(atendenteId > 0 ? atendenteDao.buscarPorId(atendenteId) : null);

                    chamado.setDescricao(rs.getString("descricao"));
                    chamado.setStatus(rs.getString("status"));
                    chamado.setDataCriacao(rs.getDate("data_criacao"));
                    chamado.setDataResolucao(rs.getDate("data_resolucao"));
                    chamado.setSolucao(rs.getString("solucao"));
                }
            }
        }

        return chamado;
    }

    @Override
    public List<Chamado> buscarTodos() throws Exception {
        List<Chamado> chamados = new ArrayList<>();
        String sql = "SELECT * FROM chamados";

        try (PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Chamado chamado = new Chamado();
                chamado.setId(rs.getInt("id"));

                // Recuperar objetos relacionados
                int usuarioId = rs.getInt("usuario_id");
                chamado.setUsuario(usuarioDao.buscarPorId(usuarioId));

                int tipoChamadoId = rs.getInt("tipo_chamado_id");
                chamado.setTipoChamado(tipoChamadoDao.buscarPorId(tipoChamadoId));

                int atendenteId = rs.getInt("atendente_id");
                chamado.setAtendente(atendenteId > 0 ? atendenteDao.buscarPorId(atendenteId) : null);

                chamado.setDescricao(rs.getString("descricao"));
                chamado.setStatus(rs.getString("status"));
                chamado.setDataCriacao(rs.getDate("data_criacao"));
                chamado.setDataResolucao(rs.getDate("data_resolucao"));
                chamado.setSolucao(rs.getString("solucao"));

                chamados.add(chamado);
            }
        }

        return chamados;
    }
}
