package br.univates.sistemachamados.apresentacao;

import br.univates.sistemachamados.negocio.Atendente;
import br.univates.sistemachamados.negocio.Chamado;
import br.univates.sistemachamados.persistencia.AtendenteDao;
import br.univates.sistemachamados.persistencia.ChamadoDao;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.util.Date;
import java.util.List;

public class TelaResolverChamado extends JFrame {
    private Connection conexao;
    private ChamadoDao chamadoDao;
    private AtendenteDao atendenteDao;
    private Atendente atendenteLogado;

    private JTable tabelaChamados;
    private DefaultTableModel modeloTabela;
    private JTextArea txtSolucao;
    private JButton btnResolver;

    public TelaResolverChamado(Connection conexao) {
        this.conexao = conexao;
        this.chamadoDao = new ChamadoDao(conexao);
        this.atendenteDao = new AtendenteDao(conexao);

        setTitle("Resolver Chamados Técnicos");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        inicializarComponentes();
        carregarChamadosPendentes();
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout(10, 10));

        // Tabela de Chamados Pendentes
        modeloTabela = new DefaultTableModel(new String[]{
                "ID", "Usuário", "Tipo Chamado", "Descrição", "Data Criação"
        }, 0);
        tabelaChamados = new JTable(modeloTabela);
        JScrollPane scrollPane = new JScrollPane(tabelaChamados);

        // Seleção de chamado
        tabelaChamados.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                habilitarBotaoResolver();
            }
        });

        // Área de solução
        JPanel painelSolucao = new JPanel(new BorderLayout());
        JLabel lblSolucao = new JLabel("Descrição da Solução:");
        txtSolucao = new JTextArea(5, 40);
        txtSolucao.setLineWrap(true);
        txtSolucao.setWrapStyleWord(true);
        JScrollPane scrollSolucao = new JScrollPane(txtSolucao);

        painelSolucao.add(lblSolucao, BorderLayout.NORTH);
        painelSolucao.add(scrollSolucao, BorderLayout.CENTER);

        // Botão de resolver
        btnResolver = new JButton("Resolver Chamado");
        btnResolver.setEnabled(false);
        btnResolver.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resolverChamado();
            }
        });

        // Layout
        JPanel painelBotoes = new JPanel();
        painelBotoes.add(btnResolver);

        add(scrollPane, BorderLayout.CENTER);
        add(painelSolucao, BorderLayout.SOUTH);
        add(painelBotoes, BorderLayout.EAST);
    }

    private void carregarChamadosPendentes() {
        try {
            modeloTabela.setRowCount(0);
            List<Chamado> chamados = chamadoDao.buscarTodos();
            for (Chamado chamado : chamados) {
                if ("Aberto".equals(chamado.getStatus())) {
                    modeloTabela.addRow(new Object[]{
                            chamado.getId(),
                            chamado.getUsuario().getNome(),
                            chamado.getTipoChamado().getDescricao(),
                            chamado.getDescricao(),
                            chamado.getDataCriacao()
                    });
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao carregar chamados pendentes: " + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void habilitarBotaoResolver() {
        int linhaSelecionada = tabelaChamados.getSelectedRow();
        btnResolver.setEnabled(linhaSelecionada != -1);

        if (linhaSelecionada != -1) {
            txtSolucao.setEnabled(true);
        } else {
            txtSolucao.setEnabled(false);
        }
    }

    private void resolverChamado() {
        int linhaSelecionada = tabelaChamados.getSelectedRow();
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this,
                    "Selecione um chamado para resolver.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        String solucao = txtSolucao.getText().trim();
        if (solucao.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Descreva a solução do chamado.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        try {
            // Recuperar o chamado selecionado
            int idChamado = (int) modeloTabela.getValueAt(linhaSelecionada, 0);
            Chamado chamado = chamadoDao.buscarPorId(idChamado);

            // Atualizar dados do chamado
            chamado.setStatus("Resolvido");
            chamado.setSolucao(solucao);
            chamado.setAtendente(atendenteLogado);
            chamado.setDataResolucao(new Date());

            // Salvar alterações
            chamadoDao.atualizar(chamado);

            // Atualizar tabela e limpar campos
            carregarChamadosPendentes();
            txtSolucao.setText("");
            btnResolver.setEnabled(false);

            JOptionPane.showMessageDialog(this,
                    "Chamado resolvido com sucesso!",
                    "Sucesso",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao resolver chamado: " + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}