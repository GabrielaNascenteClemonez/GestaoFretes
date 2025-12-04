package com.example.myapplication;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.databinding.FragmentResumoBinding;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ResumoFragment extends Fragment {

    private FragmentResumoBinding binding;
    private ViagemViewModel mViagemViewModel;

    private double valorFretes = 0.0;
    private double valorDespesasViagem = 0.0;
    private double valorGastosGerais = 0.0;

    private final NumberFormat formatador = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentResumoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViagemViewModel = new ViewModelProvider(this).get(ViagemViewModel.class);

        // Ações dos Chips
        binding.chipGroupFiltro.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            int id = checkedIds.get(0);

            if (id == R.id.chip_hoje) {
                binding.textPeriodoAtual.setText("Exibindo: Hoje");
                binding.btnAlterarMes.setVisibility(View.GONE);
                carregarDadosFiltrados(getInicioDoDia(), getFimDoDia());
            } else if (id == R.id.chip_semana) {
                binding.textPeriodoAtual.setText("Exibindo: Últimos 7 Dias");
                binding.btnAlterarMes.setVisibility(View.GONE);
                carregarDadosFiltrados(getInicioDaSemana(), getFimDoDia());
            } else if (id == R.id.chip_mes) {
                SimpleDateFormat sdf = new SimpleDateFormat("MMMM/yyyy", new Locale("pt", "BR"));
                String nomeMes = sdf.format(Calendar.getInstance().getTime());
                binding.textPeriodoAtual.setText("Exibindo: " + nomeMes);
                binding.btnAlterarMes.setVisibility(View.VISIBLE);
                carregarDadosFiltrados(getInicioDoMesAtual(), getFimDoMesAtual());
            } else {
                binding.textPeriodoAtual.setText("Exibindo: Total Geral");
                binding.btnAlterarMes.setVisibility(View.GONE);
                carregarDadosFiltrados(0, Long.MAX_VALUE);
            }
        });

        binding.btnAlterarMes.setOnClickListener(v -> abrirSeletorDeMes());

        // Ação do Botão PDF
        binding.btnGerarPdfResumo.setOnClickListener(v -> {
            gerarPdfResumo();
        });

        // Inicialização
        binding.textPeriodoAtual.setText("Exibindo: Total Geral");
        binding.btnAlterarMes.setVisibility(View.GONE);
        carregarDadosFiltrados(0, Long.MAX_VALUE);
    }

    // --- LÓGICA DO PDF DE RESUMO ---
    private void gerarPdfResumo() {
        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);

        Canvas canvas = page.getCanvas();
        Paint paint = new Paint();
        Paint titlePaint = new Paint();

        // Estilos
        titlePaint.setTextSize(26);
        titlePaint.setFakeBoldText(true);
        titlePaint.setColor(getResources().getColor(android.R.color.black, null));

        paint.setTextSize(16);
        paint.setColor(getResources().getColor(android.R.color.black, null));

        int x = 40;
        int y = 60;

        // --- DESENHO ---

        // Título Principal
        canvas.drawText("Relatório de Gestão Financeira", x, y, titlePaint);
        y += 40;

        // Subtítulo
        String periodo = binding.textPeriodoAtual.getText().toString();
        paint.setTextSize(18);
        paint.setColor(getResources().getColor(android.R.color.darker_gray, null));
        canvas.drawText(periodo, x, y, paint);
        y += 50;

        // Linha
        paint.setColor(getResources().getColor(android.R.color.black, null));
        canvas.drawLine(x, y, 550, y, paint);
        y += 40;

        // 1. Receitas
        paint.setTextSize(16);
        canvas.drawText("(+) Total de Fretes (Receita):", x, y, paint);
        canvas.drawText(formatador.format(valorFretes), 350, y, paint);
        y += 30;

        // 2. Despesas Viagem
        canvas.drawText("(-) Despesas de Viagem:", x, y, paint);
        canvas.drawText("- " + formatador.format(valorDespesasViagem), 350, y, paint);
        y += 30;

        // 3. Gastos Gerais
        canvas.drawText("(-) Gastos Gerais do Caminhão:", x, y, paint);
        canvas.drawText("- " + formatador.format(valorGastosGerais), 350, y, paint);
        y += 40;

        // Linha
        canvas.drawLine(x, y, 550, y, paint);
        y += 40;

        // SALDO FINAL
        Paint resultPaint = new Paint();
        resultPaint.setTextSize(22);
        resultPaint.setFakeBoldText(true);

        double saldo = valorFretes - valorDespesasViagem - valorGastosGerais;

        canvas.drawText("SALDO LÍQUIDO:", x, y, resultPaint);
        canvas.drawText(formatador.format(saldo), 350, y, resultPaint);

        // Rodapé
        y = 800;
        paint.setTextSize(12);
        paint.setColor(getResources().getColor(android.R.color.darker_gray, null));
        canvas.drawText("Gerado pelo App Gestão de Fretes", x, y, paint);

        document.finishPage(page);

        // Salvar e Compartilhar
        File file = new File(requireContext().getExternalFilesDir(null), "Relatorio_Financeiro.pdf");

        try {
            document.writeTo(new FileOutputStream(file));
            Toast.makeText(getContext(), "Relatório Gerado!", Toast.LENGTH_SHORT).show();
            compartilharArquivo(file);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Erro ao gerar PDF", Toast.LENGTH_LONG).show();
        }

        document.close();
    }

    private void compartilharArquivo(File file) {
        try {
            Uri uri = FileProvider.getUriForFile(
                    requireContext(),
                    requireContext().getPackageName() + ".provider",
                    file);

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("application/pdf");
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            startActivity(Intent.createChooser(intent, "Compartilhar Resumo Financeiro"));
        } catch (IllegalArgumentException e) {
            Toast.makeText(getContext(), "Erro no FileProvider", Toast.LENGTH_LONG).show();
        }
    }

    // --- MÉTODOS DE FILTRO E DATA ---

    private void abrirSeletorDeMes() {
        Calendar cal = Calendar.getInstance();
        int anoAtual = cal.get(Calendar.YEAR);
        int mesAtual = cal.get(Calendar.MONTH);
        int diaAtual = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePicker = new DatePickerDialog(getContext(),
                (view, year, month, dayOfMonth) -> {
                    Calendar dataEscolhida = Calendar.getInstance();
                    dataEscolhida.set(year, month, 1);

                    SimpleDateFormat sdf = new SimpleDateFormat("MMMM/yyyy", new Locale("pt", "BR"));
                    String nomeMes = sdf.format(dataEscolhida.getTime());
                    binding.textPeriodoAtual.setText("Exibindo: " + nomeMes);

                    binding.chipGroupFiltro.clearCheck();

                    long inicioMes = getInicioDoMesEspecifico(year, month);
                    long fimMes = getFimDoMesEspecifico(year, month);

                    carregarDadosFiltrados(inicioMes, fimMes);

                }, anoAtual, mesAtual, diaAtual);

        datePicker.setTitle("Escolha um dia no mês desejado");
        datePicker.show();
    }

    private void carregarDadosFiltrados(long inicio, long fim) {
        valorFretes = 0.0;
        valorDespesasViagem = 0.0;
        valorGastosGerais = 0.0;
        calcularSaldoFinal();

        mViagemViewModel.getFretesPorData(inicio, fim).observe(getViewLifecycleOwner(), total -> {
            valorFretes = (total != null) ? total : 0.0;
            binding.textTotalFretes.setText(formatador.format(valorFretes));
            calcularSaldoFinal();
        });

        mViagemViewModel.getDespesasViagemPorData(inicio, fim).observe(getViewLifecycleOwner(), total -> {
            valorDespesasViagem = (total != null) ? total : 0.0;
            binding.textTotalDespesasViagem.setText("- " + formatador.format(valorDespesasViagem));
            calcularSaldoFinal();
        });

        mViagemViewModel.getGastosGeraisPorData(inicio, fim).observe(getViewLifecycleOwner(), total -> {
            valorGastosGerais = (total != null) ? total : 0.0;
            binding.textTotalGastosGerais.setText("- " + formatador.format(valorGastosGerais));
            calcularSaldoFinal();
        });
    }

    private void calcularSaldoFinal() {
        double saldo = valorFretes - valorDespesasViagem - valorGastosGerais;
        binding.textSaldoFinal.setText(formatador.format(saldo));

        if (saldo >= 0) {
            // Saldo Positivo: BRANCO
            binding.textSaldoFinal.setTextColor(getResources().getColor(android.R.color.white, null));
        } else {
            // Saldo Negativo: AMARELO
            binding.textSaldoFinal.setTextColor(getResources().getColor(android.R.color.holo_orange_light, null));
        }
    }

    // Datas Helper
    private long getInicioDoDia() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    private long getFimDoDia() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        return cal.getTimeInMillis();
    }

    private long getInicioDaSemana() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -7);
        return cal.getTimeInMillis();
    }

    private long getInicioDoMesAtual() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    private long getFimDoMesAtual() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        return cal.getTimeInMillis();
    }

    private long getInicioDoMesEspecifico(int year, int month) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    private long getFimDoMesEspecifico(int year, int month) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        return cal.getTimeInMillis();
    }
}