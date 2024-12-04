package br.univates.sistemachamados.negocio;

public class TipoChamado {
    private int id;
    private String descricao;

    // Construtor padrão
    public TipoChamado() {}

    // Construtor com parâmetros
    public TipoChamado(int id, String descricao) {
        this.id = id;
        this.descricao = descricao;
    }

    // Getters e setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
}
