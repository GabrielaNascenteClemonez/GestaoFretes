package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.databinding.GastoGeralItemBinding;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GastoGeralListAdapter extends ListAdapter<GastoGeral, GastoGeralListAdapter.GastoViewHolder> {

    public GastoGeralListAdapter() {
        super(GASTO_COMPARATOR);
    }

    public GastoGeral getGastoAt(int position) {
        return getItem(position);
    }

    @NonNull
    @Override
    public GastoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        GastoGeralItemBinding binding = GastoGeralItemBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new GastoViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull GastoViewHolder holder, int position) {
        GastoGeral gastoAtual = getItem(position);
        holder.bind(gastoAtual);
    }

    class GastoViewHolder extends RecyclerView.ViewHolder {

        private final GastoGeralItemBinding binding;
        private final NumberFormat formatadorMoeda = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        private final SimpleDateFormat formatadorData = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        public GastoViewHolder(GastoGeralItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(GastoGeral gasto) {
            binding.textGastoTipo.setText(gasto.getTipo());
            binding.textGastoDescricao.setText(gasto.getDescricao());
            binding.textGastoValor.setText("- " + formatadorMoeda.format(gasto.getValor()));
            binding.textGastoData.setText(formatadorData.format(new Date(gasto.getData())));
        }
    }

    private static final DiffUtil.ItemCallback<GastoGeral> GASTO_COMPARATOR = new DiffUtil.ItemCallback<GastoGeral>() {
        @Override
        public boolean areItemsTheSame(@NonNull GastoGeral oldItem, @NonNull GastoGeral newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull GastoGeral oldItem, @NonNull GastoGeral newItem) {
            return oldItem.getValor() == newItem.getValor() &&
                    oldItem.getTipo().equals(newItem.getTipo()) &&
                    oldItem.getDescricao().equals(newItem.getDescricao());
        }
    };
}
