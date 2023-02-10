package dev.djakonystar.antisihr.ui.language

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import dev.djakonystar.antisihr.MainActivity
import dev.djakonystar.antisihr.R
import dev.djakonystar.antisihr.data.local.LocalStorage
import dev.djakonystar.antisihr.databinding.ScreenLanguageBinding
import dev.djakonystar.antisihr.utils.visibilityOfBottomNavigationView
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.ldralighieri.corbind.view.clicks
import javax.inject.Inject


@AndroidEntryPoint
class LanguageScreen : Fragment(R.layout.screen_language) {
    @Inject
    lateinit var localStorage: LocalStorage

    private val binding: ScreenLanguageBinding by viewBinding(ScreenLanguageBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this,
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

        checkCurrentLanguage()
    }

    private fun initListeners() {
        binding.icBack.clicks().debounce(200).onEach {
            visibilityOfBottomNavigationView.emit(true)
        }.launchIn(lifecycleScope)

        binding.tvSave.clicks().debounce(200).onEach {
            localStorage.language = getLangCode()
            (requireActivity() as MainActivity).setNewLocale()
            visibilityOfBottomNavigationView.emit(true)
        }.launchIn(lifecycleScope)
    }

    private fun checkCurrentLanguage() {
        binding.apply {
            rgLang.clearCheck()
            rgLang.check(
                when (localStorage.language) {
                    "ru" -> R.id.rb_ru
                    "ar" -> R.id.rb_ar
                    "es" -> R.id.rb_es
                    "it" -> R.id.rb_it
                    "zh" -> R.id.rb_zh
                    "ko" -> R.id.rb_ko
                    "de" -> R.id.rb_de
                    "tr" -> R.id.rb_tr
                    "fr" -> R.id.rb_fr
                    "sv" -> R.id.rb_sv
                    "ja" -> R.id.rb_ja
                    else -> R.id.rb_en
                }
            )
        }
    }

    private fun getLangCode(): String {
        binding.apply {
            println(rgLang.checkedRadioButtonId)
            println(R.id.rb_ru)
            return when (rgLang.checkedRadioButtonId) {
                R.id.rb_ru -> "ru"
                R.id.rb_ar -> "ar"
                R.id.rb_es -> "es"
                R.id.rb_it -> "it"
                R.id.rb_zh -> "zh"
                R.id.rb_ko -> "ko"
                R.id.rb_de -> "de"
                R.id.rb_tr -> "tr"
                R.id.rb_fr -> "fr"
                R.id.rb_sv -> "sv"
                R.id.rb_ja -> "ja"
                else -> "en"
            }
        }
    }
}
