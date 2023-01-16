package dev.djakonystar.antisihr.ui.library

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import dev.djakonystar.antisihr.R
import dev.djakonystar.antisihr.data.models.InnerLibraryModel
import dev.djakonystar.antisihr.databinding.ScreenLibraryBinding
import dev.djakonystar.antisihr.ui.adapters.InnerLibraryAdapter
import dev.djakonystar.antisihr.utils.hide
import dev.djakonystar.antisihr.utils.show
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.ldralighieri.corbind.view.clicks


@AndroidEntryPoint
class InnerLibraryScreen : Fragment(R.layout.screen_library) {
    private val binding: ScreenLibraryBinding by viewBinding(ScreenLibraryBinding::bind)

    private var _adapter: InnerLibraryAdapter? = null
    private val adapter: InnerLibraryAdapter get() = _adapter!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initListeners()


    }

    private fun initListeners() {
        _adapter = InnerLibraryAdapter()
        binding.rcLibrary.adapter = adapter


        binding.icSearch.clicks().debounce(200).onEach {
            binding.icMenu.hide()
            binding.icSearch.hide()
            binding.icClose.show()
            binding.tvBody.hide()
            binding.expandableLayout.expand()
        }.launchIn(lifecycleScope)

        binding.icClose.clicks().debounce(200).onEach {
            binding.tvBody.show()
            binding.icClose.hide()
            binding.icMenu.show()
            binding.icSearch.show()
            binding.expandableLayout.collapse()
        }.launchIn(lifecycleScope)


        val list = listOf<InnerLibraryModel>(
            InnerLibraryModel(
                "Пациент",
                "Важная информация для тебя. Азкары, информация про намаз,  его важность и не только."
            ),
            InnerLibraryModel(
                "Вопрос/Ответ",
                "Что такое сглаз? Какие бывают джинны? Когда и из чего были сотворены ...?"
            ),
            InnerLibraryModel(
                "Азкары",
                "Здесь собраны много азкаров, которые необходимо читать каждый день и перед сном!"
            ),
            InnerLibraryModel(
                "Лекарь",
                "Здесь собранна информация,   которую необходимо знать тому кто хочет лечить людей (Чтецу) "
            ),

            )
        adapter.submitList(list)
    }

    override fun onDestroy() {
        super.onDestroy()
        _adapter = null
    }
}