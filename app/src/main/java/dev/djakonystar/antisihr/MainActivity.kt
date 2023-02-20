package dev.djakonystar.antisihr

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import dagger.hilt.android.AndroidEntryPoint
import dev.djakonystar.antisihr.app.App
import dev.djakonystar.antisihr.data.local.LocalStorage
import dev.djakonystar.antisihr.data.models.AudioResultData
import dev.djakonystar.antisihr.databinding.ActivityMainBinding
import dev.djakonystar.antisihr.service.manager.PlayerManager
import dev.djakonystar.antisihr.data.models.AudioStatus
import dev.djakonystar.antisihr.data.models.PlayerManagerListener
import dev.djakonystar.antisihr.service.notification.MusicService
import dev.djakonystar.antisihr.utils.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.ldralighieri.corbind.view.clicks
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), PlayerManagerListener {
    @Inject
    lateinit var localStorage: LocalStorage
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private var selectedMenuId = R.id.tests
    val audioPlayerManager: PlayerManager by lazy {
        PlayerManager.getInstance(App.instance).get()!!
    }
    var isClickedFavourite = false
    var isFirstTime = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setAppLocale(localStorage.language.ifEmpty { "ru" }, this)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        audioPlayerManager.jcPlayerManagerListener = this

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


        playAudioFlow.onEach {
            binding.layoutMusicPlayer.expand()
            playAudio(it.id)
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

        binding.ivClose.clicks().debounce(200).onEach {
            binding.drawerLayout.close()
        }

        binding.btnPause.clicks().debounce(200).onEach {
            if (audioPlayerManager.isPlaying()) {
                pause()
                binding.icSkipForward.hide()
                binding.icClose.show()
            } else {
                continueAudio()
                binding.icClose.hide()
                binding.icSkipForward.show()
            }
        }.launchIn(lifecycleScope)

        binding.icSkipForward.clicks().debounce(200).onEach {
            next()
        }.launchIn(lifecycleScope)

        binding.icClose.clicks().debounce(200).onEach {
            binding.layoutMusicPlayer.collapse()
            val intent = Intent(this, MusicService::class.java)
            stopService(intent)
        }.launchIn(lifecycleScope)


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

    fun setNewLocale() {
        val refresh = Intent(this, MainActivity::class.java)
        refresh.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        this.intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        this.intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(refresh)
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

    fun setAudioList(list: List<AudioResultData>) {
        audioPlayerManager.playlist = list as ArrayList<AudioResultData>
    }

    override fun onPreparedAudio(status: AudioStatus) {
        binding.icPause.show()
        binding.icPlay.hide()
        resetPlayerInfo()
        onUpdateTitle(status.audio)
    }

    private fun onUpdateTitle(audio: AudioResultData?) {
        audio?.let {
            binding.tvName.text = it.name
            binding.tvAuthor.text = it.author
            binding.icImage.setImageWithGlide(this, it.image)
            binding.icSkipForward.show()
            binding.icClose.hide()
            if (navController.currentDestination?.id == R.id.audio_screen) {
                binding.layoutMusicPlayer.expand()
            }
            binding.btnPause.show()
            binding.pbLoadingBar.hide()
            binding.icImage.show()
        }
    }

    override fun onCompletedAudio() {
        resetPlayerInfo()
        try {
            audioPlayerManager.nextAudio(true)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun resetPlayerInfo() {
        binding.tvName.text = ""
        binding.tvAuthor.text = ""
        binding.icSkipForward.hide()
        binding.icClose.hide()
        binding.pbLoadingBar.show()
        binding.icImage.invisible()
        binding.btnPause.hide()
    }

    override fun onPaused(status: AudioStatus) {
        binding.icPause.hide()
        binding.icPlay.show()
        binding.icSkipForward.hide()
        binding.icClose.show()
    }

    override fun onContinueAudio(status: AudioStatus) {
        binding.icPause.show()
        binding.icPlay.hide()
        binding.icSkipForward.show()
        binding.icClose.hide()
    }

    override fun onPlaying(status: AudioStatus) {
        binding.icPlay.hide()
        binding.icPause.show()
        binding.icSkipForward.show()
        binding.icClose.hide()
    }

    override fun onTimeChanged(status: AudioStatus) {}

    override fun onStopped(status: AudioStatus) {}

    override fun onJcpError(throwable: Throwable) {}

    fun playAudio(id: Int, isContinue: Boolean = true) {
        audioPlayerManager.playlist.let {
            resetPlayerInfo()
            val audio = it.find { it.id == id }
            audioPlayerManager.playAudio(audio!!, isContinue)
        }
    }

    fun next() {
        audioPlayerManager.let { player ->
            player.currentAudio?.let {
                resetPlayerInfo()
                try {
                    player.nextAudio(false)
                } catch (e: Exception) {
                    binding.icPause.show()
                    binding.icPlay.hide()
                    binding.icSkipForward.hide()
                    binding.icClose.hide()
                    e.printStackTrace()
                }
            }
        }
    }

    fun continueAudio() {
        try {
            audioPlayerManager.continueAudio()
        } catch (e: Exception) {
            binding.icPause.show()
            binding.icPlay.hide()
            binding.icSkipForward.hide()
            binding.icClose.hide()
            e.printStackTrace()
        }
    }

    fun pause() {
        audioPlayerManager.pauseAudio()
        binding.icPause.hide()
        binding.icPlay.show()
        binding.icSkipForward.show()
        binding.icClose.hide()
    }


    fun previous() {
        resetPlayerInfo()
        try {
            audioPlayerManager.previousAudio()
        } catch (e: Exception) {
            binding.icPause.show()
            binding.icPlay.hide()
            binding.icSkipForward.hide()
            binding.icClose.hide()
            e.printStackTrace()
        }
    }

    fun visibilityMediaPlayer(visibility: Int) {
        if (visibility == View.VISIBLE) {
            binding.layoutMusicPlayer.expand()
        } else {
            binding.layoutMusicPlayer.collapse()
        }
    }

    fun setStatusBarColor(@ColorRes color: Int) {
        window.statusBarColor = ContextCompat.getColor(this, color)
    }

    override fun onDestroy() {
        super.onDestroy()
        audioPlayerManager.kill()
    }
}