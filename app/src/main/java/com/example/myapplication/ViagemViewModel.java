package com.example.myapplication;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import java.util.List;

public class ViagemViewModel extends AndroidViewModel {

    private ViagemDao mDao;

    private LiveData<List<Viagem>> mTodasViagens;
    private LiveData<List<GastoGeral>> mTodosGastosGerais;

    public ViagemViewModel(Application application) {
        super(application);
        AppDatabase db = AppDatabase.getDatabase(application);
        mDao = db.viagemDao();

        mTodasViagens = mDao.getTodasViagens();
        mTodosGastosGerais = mDao.getTodosGastosGerais();
    }

    // --- LISTAS ---
    public LiveData<List<Viagem>> getTodasViagens() { return mTodasViagens; }
    public LiveData<List<GastoGeral>> getTodosGastosGerais() { return mTodosGastosGerais; }

    // --- DETALHES ---
    public LiveData<Viagem> getViagemPorId(long id) { return mDao.getViagemPorId(id); }
    public LiveData<List<Despesa>> getDespesasDaViagem(long viagemId) { return mDao.getDespesasDaViagem(viagemId); }
    public LiveData<Double> getSomaTotalDespesas(long viagemId) { return mDao.getSomaTotalDespesas(viagemId); }
    public LiveData<Double> getSomaTotalLitrosDiesel(long viagemId) { return mDao.getSomaTotalLitrosDiesel(viagemId); }

    // --- RESUMO COM FILTRO ---
    public LiveData<Double> getFretesPorData(long inicio, long fim) {
        return mDao.getFretesPorData(inicio, fim);
    }

    public LiveData<Double> getGastosGeraisPorData(long inicio, long fim) {
        return mDao.getGastosGeraisPorData(inicio, fim);
    }

    public LiveData<Double> getDespesasViagemPorData(long inicio, long fim) {
        return mDao.getDespesasViagemPorData(inicio, fim);
    }

    // --- ESCRITA ---
    public void inserirViagem(Viagem viagem) { AppDatabase.databaseWriteExecutor.execute(() -> mDao.inserirViagem(viagem)); }
    public void atualizarViagem(Viagem viagem) { AppDatabase.databaseWriteExecutor.execute(() -> mDao.atualizarViagem(viagem)); }
    public void deletarViagem(Viagem viagem) { AppDatabase.databaseWriteExecutor.execute(() -> mDao.deletarViagem(viagem)); }

    public void inserirDespesa(Despesa despesa) { AppDatabase.databaseWriteExecutor.execute(() -> mDao.inserirDespesa(despesa)); }
    public void atualizarDespesa(Despesa despesa) { AppDatabase.databaseWriteExecutor.execute(() -> mDao.atualizarDespesa(despesa)); }
    public void deletarDespesa(Despesa despesa) { AppDatabase.databaseWriteExecutor.execute(() -> mDao.deletarDespesa(despesa)); }

    public void inserirGastoGeral(GastoGeral gasto) { AppDatabase.databaseWriteExecutor.execute(() -> mDao.inserirGastoGeral(gasto)); }
    public void deletarGastoGeral(GastoGeral gasto) { AppDatabase.databaseWriteExecutor.execute(() -> mDao.deletarGastoGeral(gasto)); }
}