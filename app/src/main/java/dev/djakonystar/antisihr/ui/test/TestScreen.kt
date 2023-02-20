package dev.djakonystar.antisihr.ui.test

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import dev.djakonystar.antisihr.MainActivity
import dev.djakonystar.antisihr.R
import dev.djakonystar.antisihr.databinding.ScreenHomeBinding
import dev.djakonystar.antisihr.presentation.test.HomeScreenViewModel
import dev.djakonystar.antisihr.presentation.test.impl.HomeScreenViewModelImpl
import dev.djakonystar.antisihr.ui.adapters.TestAdapter
import dev.djakonystar.antisihr.utils.*
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.ldralighieri.corbind.view.clicks


@AndroidEntryPoint
class TestScreen : Fragment(R.layout.screen_home) {
    private val binding: ScreenHomeBinding by viewBinding(ScreenHomeBinding::bind)
    private var _adapter: TestAdapter? = null
    private val adapter: TestAdapter get() = _adapter!!
    private val viewModel: HomeScreenViewModel by viewModels<HomeScreenViewModelImpl>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (requireActivity() as MainActivity).setStatusBarColor(R.color.background_color)

        initAdapters()
        initListeners()
        initObservers()

        lifecycleScope.launchWhenCreated {
//            visibilityOfLoadingAnimationView.emit(true)
            showBottomNavigationView.emit(Unit)
        }
    }

    private fun initObservers() {
        viewModel.getListOfTestsSuccessFlow.onEach {
            adapter.submitList(it)
            visibilityOfLoadingAnimationView.emit(false)
        }.launchIn(lifecycleScope)

        viewModel.messageFlow.onEach {
            visibilityOfLoadingAnimationView.emit(false)
            showSnackBar(requireView(), it)
        }.launchIn(lifecycleScope)

        viewModel.errorFlow.onEach {
            visibilityOfLoadingAnimationView.emit(false)
        }.launchIn(lifecycleScope)
    }

    private fun initAdapters() {
        _adapter = TestAdapter()
        binding.rcTests.adapter = adapter
    }

    private fun initListeners() {
        val drawer = requireActivity().findViewById<DrawerLayout>(R.id.drawerLayout)
        val ivClose = requireActivity().findViewById<AppCompatImageView>(R.id.iv_close)

        drawer.addDrawerListener(
            object : ActionBarDrawerToggle(
                requireActivity(),
                drawer,
                androidx.navigation.ui.R.string.nav_app_bar_open_drawer_description,
                androidx.navigation.ui.R.string.nav_app_bar_navigate_up_description
            ) {
                override fun onDrawerOpened(drawerView: View) {
                    super.onDrawerOpened(drawerView)
                    ivClose.show()
                }

                override fun onDrawerClosed(drawerView: View) {
                    super.onDrawerClosed(drawerView)
                    ivClose.hide()
                }
            }
        )

        adapter.setOnItemClickListener { id, name ->
            findNavController().navigate(
                TestScreenDirections.actionHomeScreenToTestFragment(
                    id,
                    name
                )
            )
        }

        binding.menuBtn.clicks().debounce(200).onEach {
            drawer.open()
        }.launchIn(lifecycleScope)
    }

    override fun onDestroy() {
        super.onDestroy()
        _adapter = null
    }

}