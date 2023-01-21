package dev.djakonystar.antisihr.ui.shop

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout.LayoutParams
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayout.Tab
import dagger.hilt.android.AndroidEntryPoint
import dev.djakonystar.antisihr.MainActivity
import dev.djakonystar.antisihr.R
import dev.djakonystar.antisihr.databinding.ScreenShopBinding
import dev.djakonystar.antisihr.utils.hide
import dev.djakonystar.antisihr.utils.show
import dev.djakonystar.antisihr.utils.showBottomNavigationView
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.ldralighieri.corbind.view.clicks
import kotlin.random.Random

@AndroidEntryPoint
class ShopScreen : Fragment(R.layout.screen_shop) {
    private val binding by viewBinding(ScreenShopBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    (requireActivity() as MainActivity).changeBottomNavigationSelectedItem(true)
                }
            })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        lifecycleScope.launchWhenResumed {
            showBottomNavigationView.emit(Unit)
        }
        initListeners()
        initObservers()
    }

    private fun initListeners() {
        binding.apply {
            expandableLayout.duration = 300

            icSearch.clicks().debounce(200).onEach {
                icFavorites.hide()
                icClose.show()
                icSearch.hide()
                expandableLayout.expand()
            }.launchIn(lifecycleScope)

            icClose.clicks().debounce(200).onEach {
                icFavorites.show()
                icClose.hide()
                icSearch.show()
                expandableLayout.collapse()
            }.launchIn(lifecycleScope)

            repeat(5) {
                val tab = tabLayout.newTab()
                val tvTab = TextView(requireContext())
                tab.customView = tvTab
                tvTab.text = Random.nextInt(10000, 99999).toString()
                tvTab.layoutParams =
                    LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
                if (it == 0) {
                    val typeface =
                        ResourcesCompat.getFont(requireContext(), R.font.nunito_extrabold_ttf)
                    tvTab.typeface = typeface
                    tvTab.setTextColor(ContextCompat.getColor(requireContext(), R.color.main_color))
                } else {
                    tvTab.typeface = ResourcesCompat.getFont(requireContext(), R.font.nunito_medium)
                    tvTab.setTextColor(Color.parseColor("#66000000"))
                }

                tabLayout.addTab(tab)
            }

            tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
                override fun onTabSelected(tab: Tab?) {
                    val tvTab = tab?.customView as TextView
                    tvTab.typeface =
                        ResourcesCompat.getFont(requireContext(), R.font.nunito_extrabold_ttf)
                    tvTab.setTextColor(ContextCompat.getColor(requireContext(), R.color.main_color))
                }

                override fun onTabUnselected(tab: Tab?) {
                    val tvTab = tab?.customView as TextView
                    tvTab.typeface = ResourcesCompat.getFont(requireContext(), R.font.nunito_medium)
                    tvTab.setTextColor(Color.parseColor("#66000000"))
                }

                override fun onTabReselected(tab: Tab?) {}
            })
        }
    }

    private fun initObservers() {

    }
}
