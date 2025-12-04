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

import com.example.myapplication.databinding.FragmentListaViagensBinding;
import com.google.android.material.snackbar.Snackbar;

public class ListaViagensFragment extends Fragment {

    private ViagemViewModel mViagemViewModel;
    private FragmentListaViagensBinding binding;
    private ViagemListAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentListaViagensBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViagemViewModel = new ViewModelProvider(this).get(ViagemViewModel.class);

        adapter = new ViagemListAdapter();
        binding.recyclerviewViagens.setAdapter(adapter);
        binding.recyclerviewViagens.setLayoutManager(new LinearLayoutManager(getContext()));

        binding.fabAdicionarViagem.setOnClickListener(v -> {
            NavHostFragment.findNavController(this).navigate(R.id.action_lista_para_nova);
        });

        mViagemViewModel.getTodasViagens().observe(getViewLifecycleOwner(), viagens -> {
            adapter.submitList(viagens);

            // 1. Lógica da Viagem Ativa (Card Verde)
            Viagem viagemAtiva = null;
            for (Viagem v : viagens) {
                if (v.getKmFinal() == 0) {
                    viagemAtiva = v;
                    break;
                }
            }

            if (viagemAtiva != null) {
                binding.cardViagemAndamento.setVisibility(View.VISIBLE);
                binding.textRotaAtiva.setText(viagemAtiva.getOrigem() + " -> " + viagemAtiva.getDestino());
                binding.textClienteAtivo.setText("Cliente: " + viagemAtiva.getCliente());

                long idAtivo = viagemAtiva.getId();
                binding.btnVerDetalhesAtivo.setOnClickListener(v -> {
                    Bundle bundle = new Bundle();
                    bundle.putLong("viagemId", idAtivo);
                    NavHostFragment.findNavController(this).navigate(R.id.action_lista_para_detalhe, bundle);
                });
            } else {
                binding.cardViagemAndamento.setVisibility(View.GONE);
            }

            // 2. Lógica do Estado Vazio (Desenho Cinza)
            if (viagens.isEmpty()) {
                binding.recyclerviewViagens.setVisibility(View.GONE);
                binding.layoutEstadoVazio.setVisibility(View.VISIBLE);
                binding.cardViagemAndamento.setVisibility(View.GONE);
            } else {
                binding.recyclerviewViagens.setVisibility(View.VISIBLE);
                binding.layoutEstadoVazio.setVisibility(View.GONE);
            }
        });

        // --- ARRASTAR PARA EXCLUIR ---
        ItemTouchHelper helper = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

                    @Override
                    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                        int position = viewHolder.getAdapterPosition();
                        Viagem viagemParaDeletar = adapter.getViagemAt(position);

                        mViagemViewModel.deletarViagem(viagemParaDeletar);

                        Snackbar.make(binding.getRoot(), "Viagem excluída", Snackbar.LENGTH_SHORT).show();
                    }
                }
        );

        helper.attachToRecyclerView(binding.recyclerviewViagens);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}