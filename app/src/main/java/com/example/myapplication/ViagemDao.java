package com.example.myapplication;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ViagemDao {

    // --- VIAGENS ---
    @Insert
    void inserirViagem(Viagem viagem);

    @Update
    void atualizarViagem(Viagem viagem);

    @Delete
    void deletarViagem(Viagem viagem);

    @Query("SELECT * FROM tabela_viagens ORDER BY dataInicio DESC")
    LiveData<List<Viagem>> getTodasViagens();

    @Query("SELECT * FROM tabela_viagens WHERE id = :idDaViagem")
    LiveData<Viagem> getViagemPorId(long idDaViagem);

    // --- DESPESAS (CRUD) ---
    @Insert
    void inserirDespesa(Despesa despesa);

    @Update
    void atualizarDespesa(Despesa despesa);

    @Delete
    void deletarDespesa(Despesa despesa);

    @Query("SELECT * FROM tabela_despesas WHERE viagemId = :idDaViagem")
    LiveData<List<Despesa>> getDespesasDaViagem(long idDaViagem);

    // --- CÁLCULOS POR VIAGEM ---
    @Query("SELECT SUM(valor) FROM tabela_despesas WHERE viagemId = :idDaViagem")
    LiveData<Double> getSomaTotalDespesas(long idDaViagem);

    @Query("SELECT SUM(litros) FROM tabela_despesas WHERE viagemId = :idDaViagem AND tipo = 'Diesel'")
    LiveData<Double> getSomaTotalLitrosDiesel(long idDaViagem);

    // --- GASTOS GERAIS (CRUD) ---
    @Insert
    void inserirGastoGeral(GastoGeral gasto);

    @Query("SELECT * FROM tabela_gastos_gerais ORDER BY data DESC")
    LiveData<List<GastoGeral>> getTodosGastosGerais();

    @Delete
    void deletarGastoGeral(GastoGeral gasto);

    // --- CÁLCULOS GERAIS (Para o Resumo) ---

    // 1. Soma de Fretes por período
    @Query("SELECT SUM(valorFrete) FROM tabela_viagens WHERE dataInicio BETWEEN :inicio AND :fim")
    LiveData<Double> getFretesPorData(long inicio, long fim);

    // 2. Soma de Gastos Gerais por período
    @Query("SELECT SUM(valor) FROM tabela_gastos_gerais WHERE data BETWEEN :inicio AND :fim")
    LiveData<Double> getGastosGeraisPorData(long inicio, long fim);

    // 3. Soma de Despesas de Viagem por período (Juntando tabelas)
    @Query("SELECT SUM(d.valor) FROM tabela_despesas d " +
            "INNER JOIN tabela_viagens v ON d.viagemId = v.id " +
            "WHERE v.dataInicio BETWEEN :inicio AND :fim")
    LiveData<Double> getDespesasViagemPorData(long inicio, long fim);
}