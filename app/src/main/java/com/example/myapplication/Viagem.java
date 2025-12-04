package com.example.myapplication;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "tabela_viagens")
public class Viagem {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private String origem;
    private String destino;
    private String cliente;
    private String descricao;
    private double valorFrete;
    private long dataInicio;
    private boolean concluida;
    private double kmInicial;
    private double kmFinal;
    private double mediaKmL;    // Ex: 4.5
    private double precoDiesel; // Ex: 5.80


     // Construtor ---
    public Viagem(String origem, String destino, String cliente, String descricao,
                  double valorFrete, double kmInicial, double mediaKmL, double precoDiesel) {

        this.origem = origem;
        this.destino = destino;
        this.cliente = cliente;
        this.descricao = descricao;
        this.valorFrete = valorFrete;
        this.kmInicial = kmInicial;
        this.mediaKmL = mediaKmL;
        this.precoDiesel = precoDiesel;

        this.dataInicio = System.currentTimeMillis();
        this.concluida = false;
        this.kmFinal = 0.0;
    }

    // --- Getters e Setters ---

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getOrigem() { return origem; }
    public void setOrigem(String origem) { this.origem = origem; }
    public String getDestino() { return destino; }
    public void setDestino(String destino) { this.destino = destino; }
    public String getCliente() { return cliente; }
    public void setCliente(String cliente) { this.cliente = cliente; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public double getValorFrete() { return valorFrete; }
    public void setValorFrete(double valorFrete) { this.valorFrete = valorFrete; }
    public long getDataInicio() { return dataInicio; }
    public void setDataInicio(long dataInicio) { this.dataInicio = dataInicio; }
    public boolean isConcluida() { return concluida; }
    public void setConcluida(boolean concluida) { this.concluida = concluida; }
    public double getKmInicial() { return kmInicial; }
    public void setKmInicial(double kmInicial) { this.kmInicial = kmInicial; }
    public double getKmFinal() { return kmFinal; }
    public void setKmFinal(double kmFinal) { this.kmFinal = kmFinal; }
    public double getMediaKmL() { return mediaKmL; }
    public void setMediaKmL(double mediaKmL) { this.mediaKmL = mediaKmL; }

    public double getPrecoDiesel() { return precoDiesel; }
    public void setPrecoDiesel(double precoDiesel) { this.precoDiesel = precoDiesel; }
}