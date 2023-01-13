package dev.djakonystar.antisihr.ui.test

import android.os.Bundle
import android.view.View
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import dev.djakonystar.antisihr.R
import dev.djakonystar.antisihr.databinding.ScreenHomeBinding
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.ldralighieri.corbind.view.clicks


@AndroidEntryPoint
class HomeScreen : Fragment(R.layout.screen_home) {
    private val binding: ScreenHomeBinding by viewBinding(ScreenHomeBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.menuBtn.clicks().debounce(200).onEach {
            requireActivity().findViewById<DrawerLayout>(R.id.drawerLayout).open()
        }.launchIn(lifecycleScope)
    }

}