package br.univates.sistemachamados.apresentacao;

import br.univates.sistemachamados.negocio.Chamado;
import br.univates.sistemachamados.persistencia.ChamadoDao;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class TelaConsultaChamados extends JFrame {
    private Connection conexao;
    private ChamadoDao chamadoDao;

    private JTable tabelaChamados;
    private DefaultTableModel modeloTabela;
    private JComboBox<String> cbStatus;
    private JRadioButton rbDataCriacao;
    private JRadioButton rbDataResolucao;
    private JDateChooser dataInicial;
    private JDateChooser dataFinal;

    public TelaConsultaChamados(Connection conexao) {
        this.conexao = conexao;
        this.chamadoDao = new ChamadoDao(conexao);

        setTitle("Consulta de Chamados");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        inicializarComponentes();
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout(10, 10));

        // Painel de Filtros
        JPanel painelFiltros = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Status
        gbc.gridx = 0;
        gbc.gridy = 0;
        painelFiltros.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1;
        cbStatus = new JComboBox<>(new String[]{"Todos", "Aberto", "Resolvido"});
        painelFiltros.add(cbStatus, gbc);

        // Tipo de Data
        ButtonGroup grupoData = new ButtonGroup();
        rbDataCriacao = new JRadioButton("Data Criação");
        rbDataResolucao = new JRadioButton("Data Resolução");
        grupoData.add(rbDataCriacao);
        grupoData.add(rbDataResolucao);
        rbDataCriacao.setSelected(true);

        gbc.gridx = 0;
        gbc.gridy = 1;
        painelFiltros.add(new JLabel("Tipo Data:"), gbc);
        gbc.gridx = 1;
        JPanel painelRadio = new JPanel();
        painelRadio.add(rbDataCriacao);
        painelRadio.add(rbDataResolucao);
        painelFiltros.add(painelRadio, gbc);

        // Datas
        gbc.gridx = 0;
        gbc.gridy = 2;
        painelFiltros.add(new JLabel("Data Inicial:"), gbc);
        gbc.gridx = 1;
        dataInicial = new JDateChooser();
        painelFiltros.add(dataInicial, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        painelFiltros.add(new JLabel("Data Final:"), gbc);
        gbc.gridx = 1;
        dataFinal = new JDateChooser();
        painelFiltros.add(dataFinal, gbc);

        // Botão de Pesquisa
        JButton btnPesquisar = new JButton("Pesquisar");
        btnPesquisar.addActionListener(e -> pesquisarChamados());

        gbc.gridx = 1;
        gbc.gridy = 4;
        painelFiltros.add(btnPesquisar, gbc);

        // Tabela de Resultados
        modeloTabela = new DefaultTableModel(new String[]{
                "ID", "Usuário", "Tipo Chamado", "Descrição",
                "Status", "Data Criação", "Data Resolução", "Solução"
        }, 0);
        tabelaChamados = new JTable(modeloTabela);
        JScrollPane scrollPane = new JScrollPane(tabelaChamados);

        // Layout
        add(painelFiltros, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void pesquisarChamados() {
        try {
            // Limpar tabela
            modeloTabela.setRowCount(0);

            // Buscar todos os chamados
            List<Chamado> chamados = chamadoDao.buscarTodos();

            // Filtrar por status
            String statusSelecionado = (String) cbStatus.getSelectedItem();
            chamados = filtrarPorStatus(chamados, statusSelecionado);

            // Filtrar por data
            Date dataInicialSelecionada = dataInicial.getDate();
            Date dataFinalSelecionada = dataFinal.getDate();

            if (dataInicialSelecionada != null && dataFinalSelecionada != null) {
                chamados = filtrarPorData(chamados, dataInicialSelecionada, dataFinalSelecionada);
            }

            // Preencher tabela
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            for (Chamado chamado : chamados) {
                modeloTabela.addRow(new Object[]{
                        chamado.getId(),
                        chamado.getUsuario().getNome(),
                        chamado.getTipoChamado().getDescricao(),
                        chamado.getDescricao(),
                        chamado.getStatus(),
                        chamado.getDataCriacao() != null ? sdf.format(chamado.getDataCriacao()) : "",
                        chamado.getDataResolucao() != null ? sdf.format(chamado.getDataResolucao()) : "",
                        chamado.getSolucao() != null ? chamado.getSolucao() : ""
                });
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao pesquisar chamados: " + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private List<Chamado> filtrarPorStatus(List<Chamado> chamados, String status) {
        if ("Todos".equals(status)) {
            return chamados;
        }
        return chamados.stream()
                .filter(c -> status.equals(c.getStatus()))
                .collect(Collectors.toList());
    }

    private List<Chamado> filtrarPorData(List<Chamado> chamados, Date dataInicio, Date dataFim) {
        return chamados.stream()
                .filter(c -> {
                    Date dataParaComparar = rbDataCriacao.isSelected() ?
                            c.getDataCriacao() : c.getDataResolucao();

                    return dataParaComparar != null &&
                            !dataParaComparar.before(dataInicio) &&
                            !dataParaComparar.after(dataFim);
                })
                .collect(Collectors.toList());
    }
}

// Classe simples para o DateChooser (pode ser substituída por bibliotecas como JCalendar)
class JDateChooser extends JPanel {
    private JTextField txtData;
    private JButton btnEscolher;
    private Date selectedDate;

    public JDateChooser() {
        setLayout(new BorderLayout());

        txtData = new JTextField(10);
        txtData.setEditable(false);

        btnEscolher = new JButton("...");
        btnEscolher.addActionListener(e -> escolherData());

        add(txtData, BorderLayout.CENTER);
        add(btnEscolher, BorderLayout.EAST);
    }

    private void escolherData() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Escolher Data", true);
        dialog.setLayout(new BorderLayout());

        // Calendário
        JCalendar calendario = new JCalendar();
        dialog.add(calendario, BorderLayout.CENTER);

        // Painel de botões
        JPanel painelBotoes = new JPanel();
        JButton btnSelecionar = new JButton("Selecionar");
        JButton btnCancelar = new JButton("Cancelar");

        btnSelecionar.addActionListener(e -> {
            selectedDate = calendario.getData();
            if (selectedDate != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                txtData.setText(sdf.format(selectedDate));
            }
            dialog.dispose();
        });

        btnCancelar.addActionListener(e -> dialog.dispose());

        painelBotoes.add(btnSelecionar);
        painelBotoes.add(btnCancelar);
        dialog.add(painelBotoes, BorderLayout.SOUTH);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    public Date getDate() {
        return selectedDate;
    }

    public void setDate(Date date) {
        this.selectedDate = date;
        if (date != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            txtData.setText(sdf.format(date));
        } else {
            txtData.setText("");
        }
    }

    // Classe interna para o calendário
    private class JCalendar extends JPanel {
        private JComboBox<String> cbMes;
        private JComboBox<Integer> cbAno;
        private JPanel painelDias;
        private Date selectedDate;

        public JCalendar() {
            setLayout(new BorderLayout());

            // Painel de seleção de mês e ano
            JPanel painelSelecao = new JPanel();
            String[] meses = {"Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
                    "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"};
            cbMes = new JComboBox<>(meses);

            // Gerar anos de 1950 a 2050
            Integer[] anos = new Integer[101];
            for (int i = 0; i < anos.length; i++) {
                anos[i] = 1950 + i;
            }
            cbAno = new JComboBox<>(anos);

            // Definir mês e ano atuais
            Calendar cal = Calendar.getInstance();
            cbMes.setSelectedIndex(cal.get(Calendar.MONTH));
            cbAno.setSelectedItem(cal.get(Calendar.YEAR));

            painelSelecao.add(new JLabel("Mês:"));
            painelSelecao.add(cbMes);
            painelSelecao.add(new JLabel("Ano:"));
            painelSelecao.add(cbAno);

            add(painelSelecao, BorderLayout.NORTH);

            // Painel de dias
            painelDias = new JPanel(new GridLayout(0, 7));
            add(new JScrollPane(painelDias), BorderLayout.CENTER);

            // Adicionar listeners para atualizar os dias
            cbMes.addActionListener(e -> atualizarDias());
            cbAno.addActionListener(e -> atualizarDias());

            // Adicionar cabeçalho dos dias da semana
            String[] diasSemana = {"Dom", "Seg", "Ter", "Qua", "Qui", "Sex", "Sáb"};
            for (String dia : diasSemana) {
                painelDias.add(new JLabel(dia, SwingConstants.CENTER));
            }

            atualizarDias();
        }

        private void atualizarDias() {
            // Limpar dias anteriores
            while (painelDias.getComponentCount() > 7) {
                painelDias.remove(7);
            }

            // Calcular dias do mês
            int mes = cbMes.getSelectedIndex();
            int ano = (int) cbAno.getSelectedItem();

            Calendar cal = Calendar.getInstance();
            cal.set(ano, mes, 1);

            // Adicionar espaços em branco antes do primeiro dia
            int diaSemana = cal.get(Calendar.DAY_OF_WEEK);
            for (int i = 1; i < diaSemana; i++) {
                painelDias.add(new JLabel(""));
            }

            // Adicionar dias do mês
            int ultimoDia = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
            for (int dia = 1; dia <= ultimoDia; dia++) {
                JButton botaoDia = new JButton(String.valueOf(dia));
                int finalDia = dia;
                botaoDia.addActionListener(e -> {
                    cal.set(ano, mes, finalDia);
                    selectedDate = cal.getTime();

                    // Destacar o dia selecionado
                    for (Component comp : painelDias.getComponents()) {
                        if (comp instanceof JButton) {
                            ((JButton) comp).setBackground(null);
                        }
                    }
                    botaoDia.setBackground(Color.LIGHT_GRAY);
                });
                painelDias.add(botaoDia);
            }

            // Revalidar e repintar
            revalidate();
            repaint();
        }

        public Date getData() {
            return selectedDate;
        }
    }
}