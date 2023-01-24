package dev.djakonystar.antisihr

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import dev.djakonystar.antisihr.data.local.LocalStorage
import dev.djakonystar.antisihr.databinding.ActivityMainBinding
import dev.djakonystar.antisihr.ui.about.AboutScreen
import dev.djakonystar.antisihr.ui.audio.AudioScreen
import dev.djakonystar.antisihr.ui.feedback.FeedbackScreen
import dev.djakonystar.antisihr.ui.language.LanguageScreen
import dev.djakonystar.antisihr.ui.library.LibraryScreen
import dev.djakonystar.antisihr.ui.test.TestScreen
import dev.djakonystar.antisihr.utils.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var localStorage: LocalStorage
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private var selectedMenuId = R.id.tests


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setAppLocale(localStorage.language.ifEmpty { "ru" }, this)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initListeners()
        initObservers()
    }

    private fun initObservers() {
        visibilityOfBottomNavigationView.onEach {
            if (it) {
                binding.bottomNavigationBar.show()
                changeBottomNavigationSelectedItem(true)
            } else {
                binding.bottomNavigationBar.hide()
            }
        }.launchIn(lifecycleScope)

        visibilityOfLoadingAnimationView.onEach {
            if (it) {
                binding.loadingAnimation.show()
            } else {
                delay(500)
                binding.loadingAnimation.hide()
            }
        }.launchIn(lifecycleScope)


        showBottomNavigationView.onEach {
            binding.bottomNavigationBar.show()
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

        binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);



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
                    if (selectedMenuId != R.id.readers) {
                        val graph = navInflater.inflate(R.navigation.readers_graph)
                        navController.graph = graph
                        selectedMenuId = R.id.readers
                    }
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
                    val uri = Uri.parse("https://anti-sihr-server.ru/agreement")
                    startActivity(Intent(Intent.ACTION_VIEW, uri))
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
                    val intent = Intent(Intent.ACTION_SEND)
                    intent.type = "text/plain"
                    val text = """
             https://play.google.com/store/apps/details?id=${this.packageName}
             """.trimIndent()
                    intent.putExtra(Intent.EXTRA_TEXT, text)
                    startActivity(Intent.createChooser(intent, "Share:"))
                }
            }
            true
        }
    }


    fun changeBottomNavigationSelectedItem(isTestScreen: Boolean) {
        if (isTestScreen) {
            selectedMenuId = R.id.tests
            binding.bottomNavigationBar.selectedItemId = R.id.tests
            val graph = navController.navInflater.inflate(R.navigation.test_graph)
            navController.graph = graph
        } else {
            selectedMenuId = R.id.library
            binding.bottomNavigationBar.selectedItemId = R.id.library
            val graph = navController.navInflater.inflate(R.navigation.library_graph)
            navController.graph = graph
        }
    }

    fun rerun() {
        val refresh = Intent(this, MainActivity::class.java)
        refresh.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        this.intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        this.intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(refresh)
    }
}