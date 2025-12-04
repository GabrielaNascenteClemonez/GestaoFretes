package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.databinding.DespesaItemBinding;
import java.text.NumberFormat;
import java.util.Locale;

public class DespesaListAdapter extends ListAdapter<Despesa, DespesaListAdapter.DespesaViewHolder> {

    public DespesaListAdapter() {
        super(DESPESA_COMPARATOR);
    }

    public Despesa getDespesaAt(int position) {
        return getItem(position);
    }

    @NonNull
    @Override
    public DespesaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        DespesaItemBinding binding = DespesaItemBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new DespesaViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull DespesaViewHolder holder, int position) {
        Despesa despesaAtual = getItem(position);
        holder.bind(despesaAtual);
    }

    class DespesaViewHolder extends RecyclerView.ViewHolder {

        private final DespesaItemBinding binding;

        public DespesaViewHolder(DespesaItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Despesa despesa) {
            binding.textDespesaTipo.setText(despesa.getTipo());

            // Formata o valor para R$
            NumberFormat formatadorMoeda = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
            binding.textDespesaValor.setText(formatadorMoeda.format(despesa.getValor()));

            // Mostra os litros apenas se for Diesel
            if (despesa.getLitros() > 0) {
                binding.textDespesaLitros.setText(String.format(Locale.getDefault(), "%.2f L", despesa.getLitros()));
                binding.textDespesaLitros.setVisibility(View.VISIBLE);
            } else {
                binding.textDespesaLitros.setVisibility(View.GONE);
            }
        }
    }

    private static final DiffUtil.ItemCallback<Despesa> DESPESA_COMPARATOR = new DiffUtil.ItemCallback<Despesa>() {
        @Override
        public boolean areItemsTheSame(@NonNull Despesa oldItem, @NonNull Despesa newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Despesa oldItem, @NonNull Despesa newItem) {
            return oldItem.getValor() == newItem.getValor() &&
                    oldItem.getTipo().equals(newItem.getTipo()) &&
                    oldItem.getLitros() == newItem.getLitros();
        }
    };
}