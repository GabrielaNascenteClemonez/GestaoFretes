package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.databinding.ViagemItemBinding;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ViagemListAdapter extends ListAdapter<Viagem, ViagemListAdapter.ViagemViewHolder> {

    public ViagemListAdapter() {
        super(VIAGEM_COMPARATOR);
    }

    @NonNull
    @Override
    public ViagemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViagemItemBinding binding = ViagemItemBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new ViagemViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViagemViewHolder holder, int position) {
        Viagem viagemAtual = getItem(position);
        holder.bind(viagemAtual);
    }

    // --- MÉTODOS PÚBLICOS ---
    public Viagem getViagemAt(int position) {
        return getItem(position);
    }

    class ViagemViewHolder extends RecyclerView.ViewHolder {

        private final ViagemItemBinding binding;
        // Formatador de Data
        private final SimpleDateFormat formatadorData = new SimpleDateFormat("dd/MM/yyyy", new Locale("pt", "BR"));

        public ViagemViewHolder(ViagemItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.getRoot().setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Viagem viagemClicada = getItem(position);
                    Bundle bundle = new Bundle();
                    bundle.putLong("viagemId", viagemClicada.getId());
                    Navigation.findNavController(v).navigate(R.id.action_lista_para_detalhe, bundle);
                }
            });
        }

        public void bind(Viagem viagem) {
            String rota = viagem.getOrigem() + " -> " + viagem.getDestino();
            binding.textViewRota.setText(rota);

            // --- MOSTRAR A DATA ---
            String dataFormatada = formatadorData.format(new Date(viagem.getDataInicio()));
            binding.textViewData.setText(dataFormatada);

            binding.textViewCliente.setText("Cliente: " + viagem.getCliente());

            String descricao = viagem.getDescricao();
            if (descricao != null && !descricao.trim().isEmpty()) {
                binding.textViewDescricao.setText("Descrição: " + descricao);
                binding.textViewDescricao.setVisibility(View.VISIBLE);
            } else {
                binding.textViewDescricao.setVisibility(View.GONE);
            }
        }
    }

    private static final DiffUtil.ItemCallback<Viagem> VIAGEM_COMPARATOR = new DiffUtil.ItemCallback<Viagem>() {
        @Override
        public boolean areItemsTheSame(@NonNull Viagem oldItem, @NonNull Viagem newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Viagem oldItem, @NonNull Viagem newItem) {
            return oldItem.getId() == newItem.getId() &&
                    oldItem.getOrigem().equals(newItem.getOrigem()) &&
                    oldItem.getDestino().equals(newItem.getDestino()) &&
                    oldItem.getCliente().equals(newItem.getCliente()) &&
                    (oldItem.getDescricao() != null ? oldItem.getDescricao().equals(newItem.getDescricao()) : newItem.getDescricao() == null) &&
                    oldItem.getValorFrete() == newItem.getValorFrete() &&
                    oldItem.getKmInicial() == newItem.getKmInicial() &&
                    oldItem.getKmFinal() == newItem.getKmFinal() &&
                    oldItem.getMediaKmL() == newItem.getMediaKmL() &&
                    oldItem.getPrecoDiesel() == newItem.getPrecoDiesel();
        }
    };
}