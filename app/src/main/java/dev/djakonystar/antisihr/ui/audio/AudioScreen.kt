package dev.djakonystar.antisihr.ui.audio

import android.os.Bundle
import android.view.View
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import dev.djakonystar.antisihr.R
import dev.djakonystar.antisihr.data.models.AudioModel
import dev.djakonystar.antisihr.data.models.TestData
import dev.djakonystar.antisihr.databinding.ScreenAudioBinding
import dev.djakonystar.antisihr.databinding.ScreenHomeBinding
import dev.djakonystar.antisihr.ui.adapters.AudioAdapter
import dev.djakonystar.antisihr.utils.hide
import dev.djakonystar.antisihr.utils.show
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.ldralighieri.corbind.view.clicks


@AndroidEntryPoint
class AudioScreen : Fragment(R.layout.screen_audio) {
    private val binding: ScreenAudioBinding by viewBinding(ScreenAudioBinding::bind)

    private var _adapter: AudioAdapter? = null
    private val adapter: AudioAdapter get() = _adapter!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initListeners()


    }

    private fun initListeners() {
        _adapter = AudioAdapter()
        binding.rcAudios.adapter = adapter


        binding.icSearch.clicks().debounce(200).onEach {
            binding.icFavourites.hide()
            binding.icSearch.hide()
            binding.icClose.show()
            binding.tvBody.hide()
            binding.expandableLayout.expand()
        }.launchIn(lifecycleScope)

        binding.icClose.clicks().debounce(200).onEach {
            binding.tvBody.show()
            binding.icClose.hide()
            binding.icFavourites.show()
            binding.icSearch.show()
            binding.expandableLayout.collapse()
        }.launchIn(lifecycleScope)


        val list = listOf<AudioModel>(
            AudioModel("Рукъя от джиннов", "Барахоев Иса", R.drawable.ic_launcher_background),
            AudioModel(
                "Рукъя от сглаза", "Мухаммад аль-Люхайдан", R.drawable.ic_launcher_background
            ),
            AudioModel(
                "От проблем в жизни", "Мухаммад Даутукаев", R.drawable.ic_launcher_background
            ),
            AudioModel("Дуа от Сглаза и порчи", "Адам Тангиев", R.drawable.ic_launcher_background),
            AudioModel("Рукъя от джиннов", "Мишари Ар-Рашид", R.drawable.ic_launcher_background)
        )
        adapter.submitList(list)
    }

    override fun onDestroy() {
        super.onDestroy()
        _adapter = null
    }
}