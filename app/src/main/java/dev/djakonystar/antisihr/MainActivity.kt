package dev.djakonystar.antisihr

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import dev.djakonystar.antisihr.databinding.ActivityMainBinding
import dev.djakonystar.antisihr.ui.about.AboutScreen
import dev.djakonystar.antisihr.ui.audio.AudioScreen
import dev.djakonystar.antisihr.ui.feedback.FeedbackScreen
import dev.djakonystar.antisihr.ui.language.LanguageScreen
import dev.djakonystar.antisihr.ui.library.LibraryScreen
import dev.djakonystar.antisihr.ui.test.TestScreen
import dev.djakonystar.antisihr.utils.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private var selectedMenuId = R.id.tests


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initListeners()
        initObservers()
    }

    private fun initObservers() {
        visibilityOfBottomNavigationView.onEach {
            if (it) {
                binding.bottomNavigationBar.show()
            } else {
                binding.bottomNavigationBar.hide()
            }
        }.launchIn(lifecycleScope)

        visibilityOfLoadingAnimationView.onEach {
            if (it) {
                binding.loadingAnimation.show()
            } else {
                binding.loadingAnimation.hide()
            }
        }.launchIn(lifecycleScope)

        visibilityAudioPlayer.onEach {
            if (it) {
                binding.layoutMusicPlayer.show()
            } else {
                binding.layoutMusicPlayer.hide()
            }
        }.launchIn(lifecycleScope)

        bottomAudioPlayer.onEach {
            binding.tvName.text = it.name
            binding.tvAuthor.text = it.author
            binding.icImage.setImageWithGlide(this, it.image)
        }.launchIn(lifecycleScope)
    }

    private fun initListeners() {
        val navHostFragment = supportFragmentManager.findFragmentById(
            R.id.nav_host_container_main
        ) as NavHostFragment
        navController = navHostFragment.navController
        val navInflater = navController.navInflater

        val navGraph = navInflater.inflate(R.navigation.test_graph)
        navController.graph = navGraph



        binding.bottomNavigationBar.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.tests -> {
                    if (selectedMenuId != R.id.tests) {
                        val graph = navInflater.inflate(R.navigation.test_graph)
                        navController.graph = graph
                        selectedMenuId = R.id.tests
                    }
                }
                R.id.audio -> {
                    if (selectedMenuId != R.id.audio) {
                        val graph = navInflater.inflate(R.navigation.audio_graph)
                        navController.graph = graph
                        selectedMenuId = R.id.audio
                    }
                }
                R.id.library -> {
                    if (selectedMenuId != R.id.library) {
                        val graph = navInflater.inflate(R.navigation.library_graph)
                        navController.graph = graph
                        selectedMenuId = R.id.library
                    }
                }
                R.id.shop -> {
                    if (selectedMenuId != R.id.shop) {
                        val graph = navInflater.inflate(R.navigation.shop_graph)
                        navController.graph = graph
                        selectedMenuId = R.id.shop
                    }
                }
                R.id.readers -> {
                    toast("READERS")
                }
            }
            true
        }

        binding.navView.setNavigationItemSelectedListener { menuItem ->
            val graph = navInflater.inflate(R.navigation.drawer_menu_graph)
            when (menuItem.itemId) {
                R.id.about -> {
                    binding.bottomNavigationBar.hide()
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                    graph.setStartDestination(R.id.about_screen)
                    navController.graph = graph
                    selectedMenuId = R.id.drawerLayout
                }
                R.id.privacy_policy -> {
                    toast("OPEN PRIVACY POLICY SOON....")
                }
                R.id.feedback -> {
                    binding.bottomNavigationBar.hide()
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                    graph.setStartDestination(R.id.feedback_screen)
                    navController.graph = graph
                    selectedMenuId = R.id.drawerLayout
                }
                R.id.language -> {
                    binding.bottomNavigationBar.hide()
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                    graph.setStartDestination(R.id.language_screen)
                    navController.graph = graph
                    selectedMenuId = R.id.drawerLayout
                }
                R.id.share -> {
                    toast("SHARE CLICKED AND OPENED SHAIR DIALOG")
                }
            }
            true
        }
    }
}