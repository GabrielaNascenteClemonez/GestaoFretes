package com.example.myapplication;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "tabela_despesas")
public class Despesa {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private long viagemId; // Chave para "ligar" esta despesa a uma viagem
    private String tipo;   // "Diesel", "Pedágio", "Alimentação", etc.
    private double valor;  // O custo em R$
    private double litros; // A quantidade de litros

    // --- Construtor ---

    public Despesa(long viagemId, String tipo, double valor, double litros) {
        this.viagemId = viagemId;
        this.tipo = tipo;
        this.valor = valor;
        this.litros = litros;
    }

    // --- Getters e Setters ---

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getViagemId() {
        return viagemId;
    }

    public void setViagemId(long viagemId) {
        this.viagemId = viagemId;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public double getLitros() {
        return litros;
    }

    public void setLitros(double litros) {
        this.litros = litros;
    }
}
