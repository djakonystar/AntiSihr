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
import dev.djakonystar.antisihr.data.models.reader.CityData
import dev.djakonystar.antisihr.data.models.reader.ReaderData
import dev.djakonystar.antisihr.databinding.ScreenReadersBinding
import dev.djakonystar.antisihr.presentation.readers.ReadersScreenViewModel
import dev.djakonystar.antisihr.presentation.readers.impl.ReadersScreenViewModelImpl
import dev.djakonystar.antisihr.ui.adapters.ReadersAdapter
import dev.djakonystar.antisihr.utils.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import net.cachapa.expandablelayout.ExpandableLayout

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
            viewModel.getReaders()
            viewModel.getAllCities()
        }

        initListeners()
        initObservers()
    }

    private fun initListeners() {
        _adapter = ReadersAdapter()

        lifecycleScope.launchWhenResumed {
            showSearchBar(binding.etSearch.text.toString().isNotEmpty())
        }

        binding.apply {
            rcReaders.adapter = adapter

            etSearch.doAfterTextChanged {
                chipGroupCity.clearCheck()
                if (etSearch.text.toString().isEmpty()) {
                    adapter.models = allReaders
                } else {
                    val newList = allReaders.filter { r ->
                        r.city!!.name.startsWith(
                            etSearch.text.toString(), ignoreCase = true
                        ) || r.name.contains(etSearch.text.toString(), true) || r.surname.contains(
                            etSearch.text.toString(), true
                        ) || r.description.contains(etSearch.text.toString(), true)
                    }
                    adapter.models = newList
                }
            }
        }

        adapter.setOnItemClickListener {
            findNavController().navigate(
                ReadersScreenDirections.actionReadersScreenToReaderDetailBottomFragment(
                    it.id
                )
            )
        }
    }

    private fun initObservers() {
        viewModel.getReadersSuccessFlow.onEach {
            allReaders.clear()
            selectedCities.clear()
            binding.chipGroupCity.clearCheck()
            allReaders.addAll(it)
            val searchValue = binding.etSearch.text.toString()
            if (searchValue.isEmpty() || searchValue.isBlank()) {
                adapter.models = allReaders
            } else {
                adapter.models = allReaders.filter { reader ->
                    reader.name.contains(searchValue, true) || reader.description.contains(
                        searchValue, true
                    )
                }
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

        viewModel.getAllCitiesFlow.onEach {
            it.forEach {
                addNewChip(it)
            }
        }.launchIn(lifecycleScope)
    }

    private fun addNewChip(city: CityData) {
        try {
            binding.apply {
                val inflater = LayoutInflater.from(requireContext())

                val newChip = inflater.inflate(R.layout.item_chip, chipGroupCity, false) as Chip
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


    private fun showSearchBar(show: Boolean) {
        if (show) {
            binding.tvAudio.text = getString(R.string.search)
            binding.tvBody.hide()
        } else {
            binding.tvAudio.text = getString(R.string.readers)
            binding.tvBody.show()
            binding.etSearch.setText("")
            hideKeyboard()
        }
    }

    private fun filterReaders() {
        val newList = if (selectedCities.isNotEmpty()) {
            allReaders.filter { selectedCities.contains(it.city!!.name) }
        } else allReaders
        adapter.models = newList
    }

    override fun onDetach() {
        _adapter = null
        super.onDetach()
    }
}
