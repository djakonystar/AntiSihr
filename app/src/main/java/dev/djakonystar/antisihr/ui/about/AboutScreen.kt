package dev.djakonystar.antisihr.ui.about

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import dev.djakonystar.antisihr.R
import dev.djakonystar.antisihr.databinding.ScreenAboutBinding
import dev.djakonystar.antisihr.databinding.ScreenHomeBinding
import dev.djakonystar.antisihr.presentation.drawer.MainViewModel
import dev.djakonystar.antisihr.presentation.drawer.impl.MainViewModelImpl
import dev.djakonystar.antisihr.utils.setImageWithGlide
import dev.djakonystar.antisihr.utils.toPhoneNumber
import dev.djakonystar.antisihr.utils.toPhoneType
import dev.djakonystar.antisihr.utils.visibilityOfBottomNavigationView
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.ldralighieri.corbind.view.clicks


@AndroidEntryPoint
class AboutScreen : Fragment(R.layout.screen_about) {
    private val binding: ScreenAboutBinding by viewBinding(ScreenAboutBinding::bind)
    private val viewModel: MainViewModel by viewModels<MainViewModelImpl>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    lifecycleScope.launchWhenResumed {
                        visibilityOfBottomNavigationView.emit(true)
                    }
                }
            })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()
        initObservers()
        lifecycleScope.launchWhenCreated {
            viewModel.getInfoAboutApp()
        }
    }

    private fun initListeners() {
        binding.icBack.clicks().debounce(200).onEach {
            visibilityOfBottomNavigationView.emit(true)
        }.launchIn(lifecycleScope)
    }

    private fun initObservers() {
        viewModel.infoAboutAppFlow.onEach {
            val info = it.result!!.first()
            binding.ivLogo.setImageWithGlide(requireContext(), info.image)
            binding.tvAbout.text = info.description
            binding.tvAddress.text = getString(R.string.address_with_dots, info.address)
            val phone = getString(R.string.phone_number, info.phone.toPhoneNumber)
//            val spanned = SpannableString(phone)
//            val start = spanned.indexOf('\n')
//            spanned.setSpan(
//                ForegroundColorSpan(Color.parseColor("#0048FF")),
//                start,
//                phone.length,
//                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
//            )
//            spanned.setSpan(
//                UnderlineSpan(),
//                start,
//                phone.length,
//                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
//            )
            binding.tvPhone.text = phone
            binding.tvPhone.clicks().debounce(200).onEach {
                val intent = Intent(
                    Intent.ACTION_DIAL,
                    Uri.parse("tel:" + Uri.encode(info.phone.toPhoneNumber))
                )
                startActivity(intent)
            }.launchIn(lifecycleScope)
        }.launchIn(lifecycleScope)
    }


}