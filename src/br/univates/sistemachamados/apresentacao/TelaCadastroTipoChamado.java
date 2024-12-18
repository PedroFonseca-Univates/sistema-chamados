package br.univates.sistemachamados.apresentacao;

import br.univates.sistemachamados.negocio.TipoChamado;
import br.univates.sistemachamados.persistencia.TipoChamadoDao;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.util.List;

public class TelaCadastroTipoChamado extends JFrame {
    private Connection conexao;
    private TipoChamadoDao tipoChamadoDao;
    private JTextField txtId, txtDescricao;
    private JTable tabelaTiposChamado;
    private DefaultTableModel modeloTabela;

    public TelaCadastroTipoChamado(Connection conexao) {
        this.conexao = conexao;
        this.tipoChamadoDao = new TipoChamadoDao(conexao);

        setTitle("Cadastro de Tipos de Chamado");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Layout principal
        setLayout(new BorderLayout());

        // Painel de formulário
        JPanel painelFormulario = new JPanel(new GridLayout(3, 2));
        painelFormulario.add(new JLabel("ID:"));
        txtId = new JTextField();
        txtId.setEditable(false);
        painelFormulario.add(txtId);

        painelFormulario.add(new JLabel("Descrição:"));
        txtDescricao = new JTextField();
        painelFormulario.add(txtDescricao);

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
        String[] colunas = {"ID", "Descrição"};
        modeloTabela = new DefaultTableModel(colunas, 0);
        tabelaTiposChamado = new JTable(modeloTabela);
        JScrollPane scrollPane = new JScrollPane(tabelaTiposChamado);

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
                salvarTipoChamado();
            }
        });

        btnEditar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editarTipoChamado();
            }
        });

        btnExcluir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                excluirTipoChamado();
            }
        });

        // Carregar tipos de chamado na tabela
        carregarTiposChamado();

        // Evento de seleção na tabela
        tabelaTiposChamado.getSelectionModel().addListSelectionListener(e -> {
            int linhaSelecionada = tabelaTiposChamado.getSelectedRow();
            if (linhaSelecionada != -1) {
                txtId.setText(tabelaTiposChamado.getValueAt(linhaSelecionada, 0).toString());
                txtDescricao.setText(tabelaTiposChamado.getValueAt(linhaSelecionada, 1).toString());
            }
        });
    }

    private void limparCampos() {
        txtId.setText("");
        txtDescricao.setText("");
    }

    private void salvarTipoChamado() {
        try {
            TipoChamado tipoChamado = new TipoChamado();

            // Se ID não estiver vazio, é uma atualização
            if (!txtId.getText().isEmpty()) {
                tipoChamado.setId(Integer.parseInt(txtId.getText()));
            }

            tipoChamado.setDescricao(txtDescricao.getText());

            if (tipoChamado.getId() > 0) {
                tipoChamadoDao.atualizar(tipoChamado);
            } else {
                tipoChamadoDao.inserir(tipoChamado);
            }

            carregarTiposChamado();
            limparCampos();
            JOptionPane.showMessageDialog(this, "Tipo de Chamado salvo com sucesso!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar tipo de chamado: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editarTipoChamado() {
        if (!txtId.getText().isEmpty()) {
            // O método salvarTipoChamado() já trata atualização
            salvarTipoChamado();
        } else {
            JOptionPane.showMessageDialog(this, "Selecione um tipo de chamado para editar");
        }
    }

    private void excluirTipoChamado() {
        if (!txtId.getText().isEmpty()) {
            try {
                int id = Integer.parseInt(txtId.getText());
                tipoChamadoDao.excluir(id);
                carregarTiposChamado();
                limparCampos();
                JOptionPane.showMessageDialog(this, "Tipo de Chamado excluído com sucesso!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao excluir tipo de chamado: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Selecione um tipo de chamado para excluir");
        }
    }

    private void carregarTiposChamado() {
        try {
            // Limpar tabela
            modeloTabela.setRowCount(0);

            // Carregar tipos de chamado
            List<TipoChamado> tiposChamado = tipoChamadoDao.buscarTodos();
            for (TipoChamado tipoChamado : tiposChamado) {
                modeloTabela.addRow(new Object[]{
                        tipoChamado.getId(),
                        tipoChamado.getDescricao()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar tipos de chamado: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}