package com.example.myapplication;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.databinding.FragmentDetalheViagemBinding;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;

public class DetalheViagemFragment extends Fragment {

    private FragmentDetalheViagemBinding binding;
    private ViagemViewModel mViagemViewModel;
    private DespesaListAdapter adapter;
    private long viagemId;

    private Viagem viagemAtual;
    private double totalGasto = 0.0;

    private final NumberFormat formatadorMoeda = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            viagemId = getArguments().getLong("viagemId");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDetalheViagemBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViagemViewModel = new ViewModelProvider(this).get(ViagemViewModel.class);

        setupDespesaRecyclerView();
        observarDadosDaViagem();

        // Botão Adicionar Despesa
        binding.fabAdicionarDespesa.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putLong("viagemId", viagemId);
            NavHostFragment.findNavController(this).navigate(R.id.action_detalhe_para_nova_despesa, bundle);
        });

        // Botão Finalizar Viagem
        binding.buttonFinalizarViagem.setOnClickListener(v -> {
            if (viagemAtual != null && viagemAtual.getKmFinal() == 0) {
                mostrarDialogoKmFinal();
            }
        });

        // Botão Gerar PDF
        binding.buttonGerarPdf.setOnClickListener(v -> {
            if (viagemAtual != null) {
                gerarECompartilharPdf();
            }
        });
    }

    // --- LÓGICA DO PDF ---

    private void gerarECompartilharPdf() {
        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);

        Canvas canvas = page.getCanvas();
        Paint paint = new Paint();
        Paint titlePaint = new Paint();

        titlePaint.setTextSize(24);
        titlePaint.setFakeBoldText(true);
        titlePaint.setColor(getResources().getColor(android.R.color.black, null));

        paint.setTextSize(14);
        paint.setColor(getResources().getColor(android.R.color.darker_gray, null));

        int x = 40;
        int y = 50;

        // Título
        canvas.drawText("Relatório de Viagem", x, y, titlePaint);
        y += 40;

        // Dados
        paint.setColor(getResources().getColor(android.R.color.black, null));
        paint.setTextSize(16);

        canvas.drawText("Rota: " + viagemAtual.getOrigem() + " -> " + viagemAtual.getDestino(), x, y, paint);
        y += 25;
        canvas.drawText("Cliente: " + viagemAtual.getCliente(), x, y, paint);
        y += 25;

        if (viagemAtual.getDescricao() != null && !viagemAtual.getDescricao().isEmpty()) {
            canvas.drawText("Descrição: " + viagemAtual.getDescricao(), x, y, paint);
            y += 25;
        }

        y += 10;
        canvas.drawLine(x, y, 550, y, paint);
        y += 30;

        // Financeiro
        titlePaint.setTextSize(18);
        canvas.drawText("Resumo Financeiro", x, y, titlePaint);
        y += 30;

        paint.setTextSize(14);
        canvas.drawText("Valor do Frete: " + formatadorMoeda.format(viagemAtual.getValorFrete()), x, y, paint);
        y += 25;
        canvas.drawText("Total de Despesas: - " + formatadorMoeda.format(totalGasto), x, y, paint);
        y += 25;

        Paint resultPaint = new Paint();
        resultPaint.setTextSize(16);
        resultPaint.setFakeBoldText(true);
        double lucro = viagemAtual.getValorFrete() - totalGasto;
        canvas.drawText("LUCRO LÍQUIDO: " + formatadorMoeda.format(lucro), x, y, resultPaint);

        y = 800;
        paint.setTextSize(10);
        paint.setColor(getResources().getColor(android.R.color.darker_gray, null));
        canvas.drawText("Gerado pelo App Gestão de Fretes", x, y, paint);

        document.finishPage(page);

        // Salvar
        File file = new File(requireContext().getExternalFilesDir(null), "Relatorio_Viagem.pdf");

        try {
            document.writeTo(new FileOutputStream(file));
            Toast.makeText(getContext(), "PDF Gerado!", Toast.LENGTH_SHORT).show();
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
                    "com.example.myapplication.provider",
                    file);

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("application/pdf");
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            startActivity(Intent.createChooser(intent, "Compartilhar Relatório"));
        } catch (IllegalArgumentException e) {
            Toast.makeText(getContext(), "Erro no FileProvider: Verifique o AndroidManifest", Toast.LENGTH_LONG).show();
        }
    }

    private void setupDespesaRecyclerView() {
        adapter = new DespesaListAdapter();
        binding.recyclerviewDespesas.setAdapter(adapter);
        binding.recyclerviewDespesas.setLayoutManager(new LinearLayoutManager(getContext()));

        ItemTouchHelper helper = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                    @Override
                    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) { return false; }

                    @Override
                    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                        int position = viewHolder.getAdapterPosition();
                        Despesa despesaParaDeletar = adapter.getDespesaAt(position);
                        mViagemViewModel.deletarDespesa(despesaParaDeletar);
                        Snackbar.make(binding.getRoot(), "Despesa excluída", Snackbar.LENGTH_SHORT).show();
                    }
                }
        );
        helper.attachToRecyclerView(binding.recyclerviewDespesas);
    }

    private void observarDadosDaViagem() {
        mViagemViewModel.getViagemPorId(viagemId).observe(getViewLifecycleOwner(), viagem -> {
            if (viagem == null) return;
            viagemAtual = viagem;
            atualizarCamposDaViagem();
            calcularLucro();
        });
        mViagemViewModel.getSomaTotalDespesas(viagemId).observe(getViewLifecycleOwner(), soma -> {
            totalGasto = (soma != null) ? soma : 0.0;
            calcularLucro();
        });
        mViagemViewModel.getDespesasDaViagem(viagemId).observe(getViewLifecycleOwner(), despesas -> {
            adapter.submitList(despesas);
        });
    }

    private void atualizarCamposDaViagem() {
        if (viagemAtual == null) return;
        String rota = viagemAtual.getOrigem() + " -> " + viagemAtual.getDestino();
        binding.textDetalheRota.setText(rota);
        binding.textValorRecebido.setText(formatadorMoeda.format(viagemAtual.getValorFrete()));

        if (viagemAtual.getKmFinal() > 0) {
            binding.buttonFinalizarViagem.setEnabled(false);
            binding.buttonFinalizarViagem.setText("Viagem Finalizada");
            binding.textValorMedia.setVisibility(View.VISIBLE);
            binding.textLabelMedia.setVisibility(View.VISIBLE);

            // Recalcula média ao abrir se já finalizada
            double kmRodados = viagemAtual.getKmFinal() - viagemAtual.getKmInicial();
            double litros = kmRodados / viagemAtual.getMediaKmL();
            double media = kmRodados / litros;
            binding.textValorMedia.setText(String.format(Locale.getDefault(), "%.2f km/L", media));

        } else {
            binding.buttonFinalizarViagem.setEnabled(true);
            binding.buttonFinalizarViagem.setText("Finalizar Viagem (Calcular Diesel)");
            binding.textValorMedia.setVisibility(View.GONE);
            binding.textLabelMedia.setVisibility(View.GONE);
        }
    }

    private void calcularLucro() {
        if (viagemAtual == null) return;
        double valorFrete = viagemAtual.getValorFrete();
        double lucroLiquido = valorFrete - totalGasto;

        binding.textValorGasto.setText("- " + formatadorMoeda.format(totalGasto));
        binding.textValorLucro.setText(formatadorMoeda.format(lucroLiquido));

        if (lucroLiquido < 0) {
            binding.textValorLucro.setTextColor(getResources().getColor(android.R.color.holo_red_dark, null));
        } else {
            binding.textValorLucro.setTextColor(getResources().getColor(android.R.color.holo_green_dark, null));
        }
    }

    private void mostrarDialogoKmFinal() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Quilometragem Final");
        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        input.setHint(String.valueOf(viagemAtual.getKmInicial()));
        builder.setView(input);

        builder.setPositiveButton("Salvar", (dialog, which) -> {
            String kmFinalStr = input.getText().toString();
            try {
                double kmFinal = Double.parseDouble(kmFinalStr);
                if (kmFinal <= viagemAtual.getKmInicial()) {
                    Toast.makeText(getContext(), "Km Final deve ser maior que o Km Inicial", Toast.LENGTH_LONG).show();
                    return;
                }
                viagemAtual.setKmFinal(kmFinal);
                double kmRodados = kmFinal - viagemAtual.getKmInicial();
                double litrosConsumidos = kmRodados / viagemAtual.getMediaKmL();
                double custoDiesel = litrosConsumidos * viagemAtual.getPrecoDiesel();

                Despesa despesaDiesel = new Despesa(viagemId, "Diesel (Calculado)", custoDiesel, litrosConsumidos);
                mViagemViewModel.inserirDespesa(despesaDiesel);
                mViagemViewModel.atualizarViagem(viagemAtual);

                Toast.makeText(getContext(), "Viagem finalizada!", Toast.LENGTH_LONG).show();
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Valor inválido", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
