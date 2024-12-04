package br.univates.sistemachamados.persistencia;

import java.util.List;

// Interface DAO genérica
public interface GenericDao<T> {
    void inserir(T objeto) throws Exception;
    void atualizar(T objeto) throws Exception;
    void excluir(int id) throws Exception;
    T buscarPorId(int id) throws Exception;
    List<T> buscarTodos() throws Exception;
}
s