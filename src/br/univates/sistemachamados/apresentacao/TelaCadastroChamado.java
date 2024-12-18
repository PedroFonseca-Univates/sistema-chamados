package br.univates.sistemachamados.apresentacao;


import br.univates.sistemachamados.negocio.*;
import br.univates.sistemachamados.persistencia.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.util.Date;
import java.util.List;

public class TelaCadastroChamado extends JFrame {
    private Connection conexao;
    private ChamadoDao chamadoDao;
    private UsuarioDao usuarioDao;
    private TipoChamadoDao tipoChamadoDao;

    private JTextField txtDescricao;
    private JComboBox<Usuario> cbUsuario;
    private JComboBox<TipoChamado> cbTipoChamado;
    private JTable tabelaChamados;
    private DefaultTableModel modeloTabela;

    public TelaCadastroChamado(Connection conexao) {
        this.conexao = conexao;
        this.chamadoDao = new ChamadoDao(conexao);
        this.usuarioDao = new UsuarioDao(conexao);
        this.tipoChamadoDao = new TipoChamadoDao(conexao);

        setTitle("Cadastro de Chamados");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        inicializarComponentes();
        carregarChamados();
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout(10, 10));

        // Painel de Formulário
        JPanel painelFormulario = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Usuário
        gbc.gridx = 0;
        gbc.gridy = 0;
        painelFormulario.add(new JLabel("Usuário:"), gbc);
        gbc.gridx = 1;
        cbUsuario = new JComboBox<>();
        carregarComboUsuarios();
        painelFormulario.add(cbUsuario, gbc);

        // Tipo de Chamado
        gbc.gridx = 0;
        gbc.gridy = 1;
        painelFormulario.add(new JLabel("Tipo de Chamado:"), gbc);
        gbc.gridx = 1;
        cbTipoChamado = new JComboBox<>();
        carregarComboTiposChamado();
        painelFormulario.add(cbTipoChamado, gbc);

        // Descrição
        gbc.gridx = 0;
        gbc.gridy = 2;
        painelFormulario.add(new JLabel("Descrição:"), gbc);
        gbc.gridx = 1;
        txtDescricao = new JTextField(30);
        painelFormulario.add(txtDescricao, gbc);

        // Botões
        JPanel painelBotoes = new JPanel();
        JButton btnNovo = new JButton("Novo");
        JButton btnSalvar = new JButton("Salvar");
        JButton btnExcluir = new JButton("Excluir");

        btnNovo.addActionListener(e -> limparCampos());
        btnSalvar.addActionListener(e -> salvarChamado());
        btnExcluir.addActionListener(e -> excluirChamado());

        painelBotoes.add(btnNovo);
        painelBotoes.add(btnSalvar);
        painelBotoes.add(btnExcluir);

        // Tabela de Chamados
        modeloTabela = new DefaultTableModel(new String[]{"ID", "Usuário", "Tipo", "Descrição", "Status"}, 0);
        tabelaChamados = new JTable(modeloTabela);
        JScrollPane scrollPane = new JScrollPane(tabelaChamados);

        // Adicionar listeners
        tabelaChamados.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    preencherCamposSelecionados();
                }
            }
        });

        // Layout Final
        add(painelFormulario, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(painelBotoes, BorderLayout.SOUTH);
    }

    private void carregarComboUsuarios() {
        try {
            List<Usuario> usuarios = usuarioDao.buscarTodos();
            cbUsuario.removeAllItems();

            for (Usuario usuario : usuarios) {
                cbUsuario.addItem(usuario);
            }

            cbUsuario.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                              boolean isSelected, boolean cellHasFocus) {
                    if (value instanceof Usuario) {
                        value = ((Usuario) value).getNome();
                    }
                    return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                }
            });
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar usuários: " + e.getMessage());
        }
    }

    private void carregarComboTiposChamado() {
        try {
            List<TipoChamado> tiposChamado = tipoChamadoDao.buscarTodos();
            cbTipoChamado.removeAllItems();

            for (TipoChamado tipo : tiposChamado) {
                cbTipoChamado.addItem(tipo);
            }

            cbTipoChamado.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                              boolean isSelected, boolean cellHasFocus) {
                    if (value instanceof TipoChamado) {
                        value = ((TipoChamado) value).getDescricao();
                    }
                    return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                }
            });
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar tipos de chamado: " + e.getMessage());
        }
    }

    private void carregarChamados() {
        try {
            modeloTabela.setRowCount(0);
            List<Chamado> chamados = chamadoDao.buscarTodos();
            for (Chamado chamado : chamados) {
                modeloTabela.addRow(new Object[]{
                        chamado.getId(),
                        chamado.getUsuario().getNome(),
                        chamado.getTipoChamado().getDescricao(),
                        chamado.getDescricao(),
                        chamado.getStatus()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar chamados: " + e.getMessage());
        }
    }

    private void salvarChamado() {
        try {
            Chamado chamado = new Chamado();
            chamado.setUsuario((Usuario) cbUsuario.getSelectedItem());
            chamado.setTipoChamado((TipoChamado) cbTipoChamado.getSelectedItem());
            chamado.setDescricao(txtDescricao.getText());
            chamado.setStatus("Aberto");
            chamado.setDataCriacao(new Date());

            chamadoDao.inserir(chamado);
            carregarChamados();
            limparCampos();
            JOptionPane.showMessageDialog(this, "Chamado salvo com sucesso!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar chamado: " + e.getMessage());
        }
    }

    private void preencherCamposSelecionados() {
        int linhaSelecionada = tabelaChamados.getSelectedRow();
        if (linhaSelecionada != -1) {
            int id = (int) modeloTabela.getValueAt(linhaSelecionada, 0);
            try {
                Chamado chamado = chamadoDao.buscarPorId(id);
                cbUsuario.setSelectedItem(chamado.getUsuario());
                cbTipoChamado.setSelectedItem(chamado.getTipoChamado());
                txtDescricao.setText(chamado.getDescricao());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erro ao carregar chamado: " + e.getMessage());
            }
        }
    }

    private void excluirChamado() {
        int linhaSelecionada = tabelaChamados.getSelectedRow();
        if (linhaSelecionada != -1) {
            int id = (int) modeloTabela.getValueAt(linhaSelecionada, 0);
            int confirmacao = JOptionPane.showConfirmDialog(
                    this,
                    "Tem certeza que deseja excluir este chamado?",
                    "Confirmação",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirmacao == JOptionPane.YES_OPTION) {
                try {
                    chamadoDao.excluir(id);
                    carregarChamados();
                    limparCampos();
                    JOptionPane.showMessageDialog(this, "Chamado excluído com sucesso!");
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Erro ao excluir chamado: " + e.getMessage());
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Selecione um chamado para excluir.");
        }
    }

    private void limparCampos() {
        txtDescricao.setText("");
        cbUsuario.setSelectedIndex(-1);
        cbTipoChamado.setSelectedIndex(-1);
    }
}