package dev.djakonystar.antisihr.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.djakonystar.antisihr.R
import dev.djakonystar.antisihr.data.room.entity.AudioBookmarked
import dev.djakonystar.antisihr.databinding.ItemAudioBinding
import dev.djakonystar.antisihr.utils.setImageWithGlide

class AudioAdapter : RecyclerView.Adapter<AudioAdapter.ViewHolder>() {

    private var onItemClickListener: ((AudioBookmarked) -> Unit)? = null
    private var onPlayClickListener: ((AudioBookmarked) -> Unit)? = null

    fun setOnItemClickListener(block: (AudioBookmarked) -> Unit) {
        onItemClickListener = block
    }

    fun setOnPlayClickListener(block: (AudioBookmarked) -> Unit) {
        onPlayClickListener = block
    }

    var audios = listOf<AudioBookmarked>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }


    override fun getItemCount() = audios.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AudioAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_audio, parent, false)
        val binding = ItemAudioBinding.bind(v)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AudioAdapter.ViewHolder, position: Int) {
        holder.bind(audios[position])
    }

    inner class ViewHolder(private val binding: ItemAudioBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(d: AudioBookmarked) {
            binding.apply {
                tvTitle.text = d.name
                tvBody.text = d.author
                icImage.setImageWithGlide(binding.root.context, d.image)

                root.setOnClickListener {
                    onItemClickListener?.invoke(d)
                }

                icPlay.setOnClickListener {
                    onPlayClickListener?.invoke(d)
                }
            }

        }


    }
}