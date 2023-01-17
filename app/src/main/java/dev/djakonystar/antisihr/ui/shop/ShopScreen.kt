package dev.djakonystar.antisihr.ui.shop

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import dev.djakonystar.antisihr.R
import dev.djakonystar.antisihr.databinding.ScreenShopBinding

@AndroidEntryPoint
class ShopScreen: Fragment(R.layout.screen_shop) {
    private val binding by viewBinding(ScreenShopBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    }
}