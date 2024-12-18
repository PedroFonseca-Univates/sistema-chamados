package br.univates.sistemachamados.apresentacao;

import br.univates.sistemachamados.negocio.Atendente;
import br.univates.sistemachamados.persistencia.AtendenteDao;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.util.List;

public class TelaCadastroAtendente extends JFrame {
    private Connection conexao;
    private AtendenteDao atendenteDao;
    private JTextField txtId, txtNome, txtEmail, txtSetor;
    private JTable tabelaAtendentes;
    private DefaultTableModel modeloTabela;

    public TelaCadastroAtendente(Connection conexao) {
        this.conexao = conexao;
        this.atendenteDao = new AtendenteDao(conexao);

        setTitle("Cadastro de Atendentes");
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

        painelFormulario.add(new JLabel("Setor:"));
        txtSetor = new JTextField();
        painelFormulario.add(txtSetor);

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
        String[] colunas = {"ID", "Nome", "Email", "Setor"};
        modeloTabela = new DefaultTableModel(colunas, 0);
        tabelaAtendentes = new JTable(modeloTabela);
        JScrollPane scrollPane = new JScrollPane(tabelaAtendentes);

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
                salvarAtendente();
            }
        });

        btnEditar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editarAtendente();
            }
        });

        btnExcluir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                excluirAtendente();
            }
        });

        // Carregar atendentes na tabela
        carregarAtendentes();

        // Evento de seleção na tabela
        tabelaAtendentes.getSelectionModel().addListSelectionListener(e -> {
            int linhaSelecionada = tabelaAtendentes.getSelectedRow();
            if (linhaSelecionada != -1) {
                txtId.setText(tabelaAtendentes.getValueAt(linhaSelecionada, 0).toString());
                txtNome.setText(tabelaAtendentes.getValueAt(linhaSelecionada, 1).toString());
                txtEmail.setText(tabelaAtendentes.getValueAt(linhaSelecionada, 2).toString());
                txtSetor.setText(tabelaAtendentes.getValueAt(linhaSelecionada, 3).toString());
            }
        });
    }

    private void limparCampos() {
        txtId.setText("");
        txtNome.setText("");
        txtEmail.setText("");
        txtSetor.setText("");
    }

    private void salvarAtendente() {
        try {
            Atendente atendente = new Atendente();

            // Se ID não estiver vazio, é uma atualização
            if (!txtId.getText().isEmpty()) {
                atendente.setId(Integer.parseInt(txtId.getText()));
            }

            atendente.setNome(txtNome.getText());
            atendente.setEmail(txtEmail.getText());
            atendente.setSetor(txtSetor.getText());

            if (atendente.getId() > 0) {
                atendenteDao.atualizar(atendente);
            } else {
                atendenteDao.inserir(atendente);
            }

            carregarAtendentes();
            limparCampos();
            JOptionPane.showMessageDialog(this, "Atendente salvo com sucesso!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar atendente: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editarAtendente() {
        if (!txtId.getText().isEmpty()) {
            // O método salvarAtendente() já trata atualização
            salvarAtendente();
        } else {
            JOptionPane.showMessageDialog(this, "Selecione um atendente para editar");
        }
    }

    private void excluirAtendente() {
        if (!txtId.getText().isEmpty()) {
            try {
                int id = Integer.parseInt(txtId.getText());
                atendenteDao.excluir(id);
                carregarAtendentes();
                limparCampos();
                JOptionPane.showMessageDialog(this, "Atendente excluído com sucesso!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao excluir atendente: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Selecione um atendente para excluir");
        }
    }

    private void carregarAtendentes() {
        try {
            // Limpar tabela
            modeloTabela.setRowCount(0);

            // Carregar atendentes
            List<Atendente> atendentes = atendenteDao.buscarTodos();
            for (Atendente atendente : atendentes) {
                modeloTabela.addRow(new Object[]{
                        atendente.getId(),
                        atendente.getNome(),
                        atendente.getEmail(),
                        atendente.getSetor()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar atendentes: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}