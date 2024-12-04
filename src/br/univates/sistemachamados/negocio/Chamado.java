package br.univates.sistemachamados.negocio;

import java.util.Date;

public class Chamado {
    private int id;
    private Usuario usuario;
    private TipoChamado tipoChamado;
    private Atendente atendente;
    private String descricao;
    private String status; // pode ser "Aberto", "Em Andamento", "Resolvido"
    private Date dataCriacao;
    private Date dataResolucao;
    private String solucao;

    // Construtor padrão
    public Chamado() {}

    // Construtor com parâmetros
    public Chamado(int id, Usuario usuario, TipoChamado tipoChamado,
                   Atendente atendente, String descricao, String status,
                   Date dataCriacao, Date dataResolucao, String solucao) {
        this.id = id;
        this.usuario = usuario;
        this.tipoChamado = tipoChamado;
        this.atendente = atendente;
        this.descricao = descricao;
        this.status = status;
        this.dataCriacao = dataCriacao;
        this.dataResolucao = dataResolucao;
        this.solucao = solucao;
    }

    // Getters e setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public TipoChamado getTipoChamado() { return tipoChamado; }
    public void setTipoChamado(TipoChamado tipoChamado) { this.tipoChamado = tipoChamado; }
    public Atendente getAtendente() { return atendente; }
    public void setAtendente(Atendente atendente) { this.atendente = atendente; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Date getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(Date dataCriacao) { this.dataCriacao = dataCriacao; }
    public Date getDataResolucao() { return dataResolucao; }
    public void setDataResolucao(Date dataResolucao) { this.dataResolucao = dataResolucao; }
    public String getSolucao() { return solucao; }
    public void setSolucao(String solucao) { this.solucao = solucao; }
}
