package dev.djakonystar.antisihr.ui.about

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import dev.djakonystar.antisihr.R
import dev.djakonystar.antisihr.databinding.ScreenAboutBinding
import dev.djakonystar.antisihr.databinding.ScreenHomeBinding
import dev.djakonystar.antisihr.utils.visibilityOfBottomNavigationView
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.ldralighieri.corbind.view.clicks


@AndroidEntryPoint
class AboutScreen : Fragment(R.layout.screen_about) {
    private val binding: ScreenAboutBinding by viewBinding(ScreenAboutBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()
    }

    private fun initListeners(){
        binding.icBack.clicks().debounce(200).onEach {
            visibilityOfBottomNavigationView.emit(true)
        }.launchIn(lifecycleScope)
    }
}