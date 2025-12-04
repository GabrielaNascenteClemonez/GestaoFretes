package com.example.myapplication;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.myapplication.databinding.FragmentNovoGastoGeralBinding;

public class NovoGastoGeralFragment extends Fragment {

    private FragmentNovoGastoGeralBinding binding;
    private ViagemViewModel mViagemViewModel;
    private String tipoGastoSelecionado;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentNovoGastoGeralBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViagemViewModel = new ViewModelProvider(this).get(ViagemViewModel.class);

        setupSpinner();

        binding.buttonSalvarGasto.setOnClickListener(v -> {
            salvarNovoGasto();
        });
    }

    private void setupSpinner() {
        Spinner spinner = binding.spinnerTipoGasto;

        // Lista de tipos de gastos gerais
        String[] tipos = new String[]{"Abastecimento", "Manutenção", "Troca de Pneu", "Peças", "Óleo", "Outros"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, tipos);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tipoGastoSelecionado = tipos[position];
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    private void salvarNovoGasto() {
        String valorStr = binding.editTextValorGasto.getText().toString().trim();
        String descricao = binding.editTextDescricaoGasto.getText().toString().trim();

        if (TextUtils.isEmpty(valorStr) || TextUtils.isEmpty(descricao)) {
            Toast.makeText(getContext(), "Preencha o Valor e a Descrição", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double valor = Double.parseDouble(valorStr);

            // Cria o objeto GastoGeral
            GastoGeral novoGasto = new GastoGeral(tipoGastoSelecionado, valor, descricao);

            // Manda o ViewModel salvar
            mViagemViewModel.inserirGastoGeral(novoGasto);

            Toast.makeText(getContext(), "Gasto salvo!", Toast.LENGTH_SHORT).show();
            NavHostFragment.findNavController(this).popBackStack(); // Volta para a lista de gastos

        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Valor numérico inválido", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
