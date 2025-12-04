package com.example.myapplication;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.myapplication.databinding.FragmentNovaViagemBinding;

public class NovaViagemFragment extends Fragment {

    private FragmentNovaViagemBinding binding;
    private ViagemViewModel mViagemViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentNovaViagemBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViagemViewModel = new ViewModelProvider(this).get(ViagemViewModel.class);

        binding.buttonSalvar.setOnClickListener(v -> {
            salvarNovaViagem();
        });
    }

    private void salvarNovaViagem() {
        // Pega os dados dos campos de texto
        String origem = binding.editTextOrigem.getText().toString().trim();
        String destino = binding.editTextDestino.getText().toString().trim();
        String cliente = binding.editTextCliente.getText().toString().trim();
        String descricao = binding.editTextDescricao.getText().toString().trim();
        String valorFreteStr = binding.editTextValorFrete.getText().toString().trim();
        String kmInicialStr = binding.editTextKmInicial.getText().toString().trim();


        String mediaKmLStr = binding.editTextMediaKml.getText().toString().trim();
        String precoDieselStr = binding.editTextPrecoDiesel.getText().toString().trim();

        // Validação
        if (TextUtils.isEmpty(origem) || TextUtils.isEmpty(destino) ||
                TextUtils.isEmpty(cliente) || TextUtils.isEmpty(valorFreteStr) ||
                TextUtils.isEmpty(kmInicialStr) || TextUtils.isEmpty(mediaKmLStr) ||
                TextUtils.isEmpty(precoDieselStr)) {

            Toast.makeText(getContext(), "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Converter os números
            double valorFrete = Double.parseDouble(valorFreteStr);
            double kmInicial = Double.parseDouble(kmInicialStr);
            double mediaKmL = Double.parseDouble(mediaKmLStr);
            double precoDiesel = Double.parseDouble(precoDieselStr);

            // Criar o objeto Viagem
            Viagem novaViagem = new Viagem(origem, destino, cliente, descricao,
                    valorFrete, kmInicial, mediaKmL, precoDiesel);

            // Mandar o ViewModel salvar
            mViagemViewModel.inserirViagem(novaViagem);

            // Voltar para a tela anterior
            Toast.makeText(getContext(), "Viagem salva!", Toast.LENGTH_SHORT).show();
            NavHostFragment.findNavController(this).popBackStack();

        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Valores numéricos inválidos", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
