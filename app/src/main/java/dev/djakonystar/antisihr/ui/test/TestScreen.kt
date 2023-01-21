package dev.djakonystar.antisihr.ui.test

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import dev.djakonystar.antisihr.R
import dev.djakonystar.antisihr.databinding.ScreenHomeBinding
import dev.djakonystar.antisihr.presentation.test.HomeScreenViewModel
import dev.djakonystar.antisihr.presentation.test.impl.HomeScreenViewModelImpl
import dev.djakonystar.antisihr.ui.adapters.TestAdapter
import dev.djakonystar.antisihr.utils.showBottomNavigationView
import dev.djakonystar.antisihr.utils.showSnackBar
import dev.djakonystar.antisihr.utils.visibilityOfLoadingAnimationView
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
        initAdapters()
        initListeners()
        initObservers()

        lifecycleScope.launchWhenCreated {
            visibilityOfLoadingAnimationView.emit(true)
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
            Log.d("TTTT", "HOMESCREEN ERROR MESSAGE: ${it.message}")
        }.launchIn(lifecycleScope)
    }

    private fun initAdapters() {
        _adapter = TestAdapter()
        binding.rcTests.adapter = adapter
    }

    private fun initListeners() {
        adapter.setOnItemClickListener { id, name->
            findNavController().navigate(TestScreenDirections.actionHomeScreenToTestFragment(id,name))
        }

        binding.menuBtn.clicks().debounce(200).onEach {
            requireActivity().findViewById<DrawerLayout>(R.id.drawerLayout).open()
        }.launchIn(lifecycleScope)

    }

    override fun onDestroy() {
        super.onDestroy()
        _adapter = null
    }

}