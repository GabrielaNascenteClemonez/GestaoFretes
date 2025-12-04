package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.databinding.FragmentListaGastosBinding;
import com.google.android.material.snackbar.Snackbar;

public class ListaGastosFragment extends Fragment {

    private ViagemViewModel mViagemViewModel;
    private FragmentListaGastosBinding binding;
    private GastoGeralListAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentListaGastosBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViagemViewModel = new ViewModelProvider(this).get(ViagemViewModel.class);

        adapter = new GastoGeralListAdapter();
        binding.recyclerviewGastosGerais.setAdapter(adapter);
        binding.recyclerviewGastosGerais.setLayoutManager(new LinearLayoutManager(getContext()));

        binding.fabAdicionarGastoGeral.setOnClickListener(v -> {
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_lista_gastos_para_novo_gasto);
        });

        mViagemViewModel.getTodosGastosGerais().observe(getViewLifecycleOwner(), gastos -> {
            adapter.submitList(gastos);
        });


        // Cria o helper para o gesto de "arrastar"
        ItemTouchHelper helper = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {


                    @Override
                    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                        // Pega a posição do item
                        int position = viewHolder.getAdapterPosition();
                        // Pega o objeto GastoGeral
                        GastoGeral gastoParaDeletar = adapter.getGastoAt(position);

                        // Manda o ViewModel deletar
                        mViagemViewModel.deletarGastoGeral(gastoParaDeletar);

                        // Mostra uma mensagem de confirmação
                        Snackbar.make(binding.getRoot(), "Gasto excluído", Snackbar.LENGTH_SHORT).show();
                    }
                }
        );

        helper.attachToRecyclerView(binding.recyclerviewGastosGerais);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
