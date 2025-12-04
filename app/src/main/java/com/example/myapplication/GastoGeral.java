package com.example.myapplication;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "tabela_gastos_gerais")
public class GastoGeral {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private String tipo;      // Ex: "Abastecimento", "Manutenção", "Troca de Pneu"
    private double valor;     // Ex: 500.00
    private String descricao; // Ex: "Posto X" ou "Troca óleo e filtro"
    private long data;        // Data do gasto

    public GastoGeral(String tipo, double valor, String descricao) {
        this.tipo = tipo;
        this.valor = valor;
        this.descricao = descricao;
        this.data = System.currentTimeMillis();
    }

    // --- Getters e Setters ---

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public double getValor() { return valor; }
    public void setValor(double valor) { this.valor = valor; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public long getData() { return data; }
    public void setData(long data) { this.data = data; }
}
