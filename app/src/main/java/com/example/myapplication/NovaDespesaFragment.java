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

import com.example.myapplication.databinding.FragmentNovaDespesaBinding;

public class NovaDespesaFragment extends Fragment {

    private FragmentNovaDespesaBinding binding;
    private ViagemViewModel mViagemViewModel;
    private long viagemId;
    private String tipoDespesaSelecionado;

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
        binding = FragmentNovaDespesaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViagemViewModel = new ViewModelProvider(this).get(ViagemViewModel.class);

        setupSpinner();

        binding.inputLayoutLitros.setVisibility(View.GONE);

        binding.buttonSalvarDespesa.setOnClickListener(v -> {
            salvarNovaDespesa();
        });
    }

    private void setupSpinner() {
        Spinner spinner = binding.spinnerTipoDespesa;


        String[] tipos = new String[]{"Pedágio", "Alimentação", "Pernoite", "Taxas", "Outros"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, tipos);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tipoDespesaSelecionado = tipos[position];
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    private void salvarNovaDespesa() {
        String valorStr = binding.editTextValorDespesa.getText().toString().trim();

        if (TextUtils.isEmpty(valorStr)) {
            Toast.makeText(getContext(), "Por favor, insira o Valor", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double valor = Double.parseDouble(valorStr);
            double litros = 0.0;

            Despesa novaDespesa = new Despesa(viagemId, tipoDespesaSelecionado, valor, litros);
            mViagemViewModel.inserirDespesa(novaDespesa);

            Toast.makeText(getContext(), "Despesa salva!", Toast.LENGTH_SHORT).show();
            NavHostFragment.findNavController(this).popBackStack();

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