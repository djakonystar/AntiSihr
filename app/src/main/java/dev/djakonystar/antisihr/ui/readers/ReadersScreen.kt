package dev.djakonystar.antisihr.ui.readers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import dev.djakonystar.antisihr.R
import dev.djakonystar.antisihr.data.models.reader.City
import dev.djakonystar.antisihr.data.models.reader.ReaderData
import dev.djakonystar.antisihr.databinding.ScreenReadersBinding
import dev.djakonystar.antisihr.presentation.readers.ReadersScreenViewModel
import dev.djakonystar.antisihr.presentation.readers.impl.ReadersScreenViewModelImpl
import dev.djakonystar.antisihr.ui.adapters.ReadersAdapter
import dev.djakonystar.antisihr.utils.toast
import dev.djakonystar.antisihr.utils.visibilityOfBottomNavigationView
import dev.djakonystar.antisihr.utils.visibilityOfLoadingAnimationView
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class ReadersScreen : Fragment(R.layout.screen_readers) {
    private val binding by viewBinding(ScreenReadersBinding::bind)
    private val viewModel: ReadersScreenViewModel by viewModels<ReadersScreenViewModelImpl>()
    private val selectedCities = mutableListOf<String>()
    private val allReaders = mutableListOf<ReaderData>()

    private var _adapter: ReadersAdapter? = null
    private val adapter: ReadersAdapter get() = _adapter!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        lifecycleScope.launchWhenResumed {
            visibilityOfBottomNavigationView.emit(true)
            visibilityOfLoadingAnimationView.emit(true)
            viewModel.getReaders()
        }

        initListeners()
        initObservers()
    }

    private fun initListeners() {
        _adapter = ReadersAdapter()
        binding.apply {
            rcReaders.adapter = adapter

            etSearch.doAfterTextChanged {
                chipGroupCity.clearCheck()
                if (etSearch.text.toString().isEmpty()) {
                    adapter.submitList(allReaders)
                } else {
                    val newList = adapter.currentList.filter { r ->
                        r.city.name.contains(etSearch.text.toString())
                    }
                    adapter.submitList(newList)
                }
            }
        }

        adapter.setOnItemClickListener {
            findNavController().navigate(
                ReadersScreenDirections.actionReadersScreenToReaderDetailDialog(
                    it.id
                )
            )
        }
    }

    private fun initObservers() {
        viewModel.getReadersSuccessFlow.onEach {
            adapter.submitList(it)
            allReaders.clear()
            allReaders.addAll(it)
            selectedCities.clear()
            binding.chipGroupCity.clearCheck()
            it.forEach { reader ->
                addNewChip(reader.city)
            }
            visibilityOfLoadingAnimationView.emit(false)
        }.launchIn(lifecycleScope)

        viewModel.messageFlow.onEach {
            visibilityOfLoadingAnimationView.emit(false)
            toast(it)
        }.launchIn(lifecycleScope)

        viewModel.errorFlow.onEach {
            visibilityOfLoadingAnimationView.emit(false)
            it.localizedMessage?.let { message -> toast(message) }
        }.launchIn(lifecycleScope)
    }

    private fun addNewChip(city: City) {
        try {
            binding.apply {
                val inflater = LayoutInflater.from(requireContext())

                val newChip =
                    inflater.inflate(R.layout.item_chip, chipGroupCity, false) as Chip
                newChip.text = city.name

                chipGroupCity.addView(newChip)

                newChip.setOnCheckedChangeListener { buttonView, isChecked ->
                    if (isChecked) {
                        chipGroupCity.check((buttonView as Chip).id)
                        if (!selectedCities.contains(city.name)) {
                            selectedCities.add(city.name)
                        }
                    } else {
                        if (selectedCities.contains(city.name)) {
                            selectedCities.remove(city.name)
                        }
                    }
                    filterReaders()
                }
            }
        } catch (e: Exception) {
            e.localizedMessage?.let { toast(it) }
        }
    }

    private fun filterReaders() {
        val newList = if (selectedCities.isNotEmpty()) {
            allReaders.filter { selectedCities.contains(it.city.name) }
        } else allReaders
        adapter.submitList(newList)
    }

    override fun onDetach() {
        _adapter = null
        super.onDetach()
    }
}
