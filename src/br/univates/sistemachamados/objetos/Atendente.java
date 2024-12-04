package br.univates.sistemachamados.objetos;

public class Atendente {
    private int id;
    private String nome;
    private String email;
    private String setor;

    // Construtor padrão
    public Atendente() {}

    // Construtor com parâmetros
    public Atendente(int id, String nome, String email, String setor) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.setor = setor;
    }

    // Getters e setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSetor() { return setor; }
    public void setSetor(String setor) { this.setor = setor; }
}