package com.example.zdklogoprj;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ImageOptionAdapter extends RecyclerView.Adapter<ImageOptionAdapter.ViewHolder> {
    private List<Integer> imageResIds;
    private int checkedPosition = -1;
    private Context context;
    private int selectedImageResId = -1;
    private OnImageSelectedListener listener;

    public ImageOptionAdapter(Context context, List<Integer> imageResIds,OnImageSelectedListener listener) {
        this.context = context;
        this.imageResIds = imageResIds;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_image_option, parent, false);
        return new ViewHolder(view);
    }

    // 获取当前选中项的图片资源ID
    public int getSelectedImageResId() {
        if (checkedPosition != -1) {
            return imageResIds.get(checkedPosition);
        }
        return -1; // 如果没有选择，则返回-1或其他适当的默认值
    }

    public interface OnImageSelectedListener {
        void onImageSelected(int imageResId);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.imageView.setImageResource(imageResIds.get(position));
        holder.radioButton.setChecked(position == checkedPosition);
        int imageResId = imageResIds.get(position);
        holder.imageView.setImageResource(imageResId);
        holder.itemView.setOnClickListener(v -> {
            checkedPosition = position;
            selectedImageResId = position; // 更新当前选中的图片资源ID
            if (listener != null) {
                listener.onImageSelected(selectedImageResId); // 通知监听器选中的图片资源ID
            }
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return imageResIds.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        RadioButton radioButton;
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            radioButton = itemView.findViewById(R.id.radio_button);
            imageView = itemView.findViewById(R.id.image_view);
        }
    }
}
