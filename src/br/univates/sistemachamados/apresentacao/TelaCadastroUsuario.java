package br.univates.sistemachamados.apresentacao;

import br.univates.sistemachamados.negocio.Usuario;
import br.univates.sistemachamados.persistencia.UsuarioDao;

import javax.swing.*;
        import javax.swing.table.DefaultTableModel;
import java.awt.*;
        import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.util.List;

public class TelaCadastroUsuario extends JFrame {
    private Connection conexao;
    private UsuarioDao usuarioDao;
    private JTextField txtId, txtNome, txtEmail, txtTelefone;
    private JTable tabelaUsuarios;
    private DefaultTableModel modeloTabela;

    public TelaCadastroUsuario(Connection conexao) {
        this.conexao = conexao;
        this.usuarioDao = new UsuarioDao(conexao);

        setTitle("Cadastro de Usuários");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Layout principal
        setLayout(new BorderLayout());

        // Painel de formulário
        JPanel painelFormulario = new JPanel(new GridLayout(5, 2));
        painelFormulario.add(new JLabel("ID:"));
        txtId = new JTextField();
        txtId.setEditable(false);
        painelFormulario.add(txtId);

        painelFormulario.add(new JLabel("Nome:"));
        txtNome = new JTextField();
        painelFormulario.add(txtNome);

        painelFormulario.add(new JLabel("Email:"));
        txtEmail = new JTextField();
        painelFormulario.add(txtEmail);

        painelFormulario.add(new JLabel("Telefone:"));
        txtTelefone = new JTextField();
        painelFormulario.add(txtTelefone);

        // Botões
        JPanel painelBotoes = new JPanel();
        JButton btnNovo = new JButton("Novo");
        JButton btnSalvar = new JButton("Salvar");
        JButton btnEditar = new JButton("Editar");
        JButton btnExcluir = new JButton("Excluir");

        painelBotoes.add(btnNovo);
        painelBotoes.add(btnSalvar);
        painelBotoes.add(btnEditar);
        painelBotoes.add(btnExcluir);

        // Tabela
        String[] colunas = {"ID", "Nome", "Email", "Telefone"};
        modeloTabela = new DefaultTableModel(colunas, 0);
        tabelaUsuarios = new JTable(modeloTabela);
        JScrollPane scrollPane = new JScrollPane(tabelaUsuarios);

        // Adicionar componentes
        add(painelFormulario, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(painelBotoes, BorderLayout.SOUTH);

        // Eventos
        btnNovo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                limparCampos();
            }
        });

        btnSalvar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                salvarUsuario();
            }
        });

        btnEditar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editarUsuario();
            }
        });

        btnExcluir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                excluirUsuario();
            }
        });

        // Carregar usuários na tabela
        carregarUsuarios();

        // Evento de seleção na tabela
        tabelaUsuarios.getSelectionModel().addListSelectionListener(e -> {
            int linhaSelecionada = tabelaUsuarios.getSelectedRow();
            if (linhaSelecionada != -1) {
                txtId.setText(tabelaUsuarios.getValueAt(linhaSelecionada, 0).toString());
                txtNome.setText(tabelaUsuarios.getValueAt(linhaSelecionada, 1).toString());
                txtEmail.setText(tabelaUsuarios.getValueAt(linhaSelecionada, 2).toString());
                txtTelefone.setText(tabelaUsuarios.getValueAt(linhaSelecionada, 3).toString());
            }
        });
    }

    private void limparCampos() {
        txtId.setText("");
        txtNome.setText("");
        txtEmail.setText("");
        txtTelefone.setText("");
    }

    private void salvarUsuario() {
        try {
            Usuario usuario = new Usuario();

            // Se ID não estiver vazio, é uma atualização
            if (!txtId.getText().isEmpty()) {
                usuario.setId(Integer.parseInt(txtId.getText()));
            }

            usuario.setNome(txtNome.getText());
            usuario.setEmail(txtEmail.getText());
            usuario.setTelefone(txtTelefone.getText());

            if (usuario.getId() > 0) {
                usuarioDao.atualizar(usuario);
            } else {
                usuarioDao.inserir(usuario);
            }

            carregarUsuarios();
            limparCampos();
            JOptionPane.showMessageDialog(this, "Usuário salvo com sucesso!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar usuário: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editarUsuario() {
        if (!txtId.getText().isEmpty()) {
            // O método salvarUsuario() já trata atualização
            salvarUsuario();
        } else {
            JOptionPane.showMessageDialog(this, "Selecione um usuário para editar");
        }
    }

    private void excluirUsuario() {
        if (!txtId.getText().isEmpty()) {
            try {
                int id = Integer.parseInt(txtId.getText());
                usuarioDao.excluir(id);
                carregarUsuarios();
                limparCampos();
                JOptionPane.showMessageDialog(this, "Usuário excluído com sucesso!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao excluir usuário: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Selecione um usuário para excluir");
        }
    }

    private void carregarUsuarios() {
        try {
            // Limpar tabela
            modeloTabela.setRowCount(0);

            // Carregar usuários
            List<Usuario> usuarios = usuarioDao.buscarTodos();
            for (Usuario usuario : usuarios) {
                modeloTabela.addRow(new Object[]{
                        usuario.getId(),
                        usuario.getNome(),
                        usuario.getEmail(),
                        usuario.getTelefone()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar usuários: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}