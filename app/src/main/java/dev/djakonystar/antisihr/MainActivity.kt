package dev.djakonystar.antisihr

import android.content.Intent
import android.media.session.MediaSession
import android.media.session.MediaSessionManager
import android.net.Uri
import android.os.Bundle
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
import dev.djakonystar.antisihr.databinding.ActivityMainBinding
import dev.djakonystar.antisihr.service.manager.PlayerManager
import dev.djakonystar.antisihr.data.models.AudioStatus
import dev.djakonystar.antisihr.data.models.PlayerManagerListener
import dev.djakonystar.antisihr.data.room.entity.AudioBookmarked
import dev.djakonystar.antisihr.utils.*
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
//        visibilityOfLoadingAnimationView.onEach {
//            if (it) {
//                binding.loadingAnimation.show()
//            } else {
//                delay(500)
//                binding.loadingAnimation.hide()
//            }
//        }.launchIn(lifecycleScope)

        playAudioFlow.onEach {
            playAudio(it.id)
            isFirstTime = false
        }.launchIn(lifecycleScope)
    }

    private fun initListeners() {
        val navHostFragment = supportFragmentManager.findFragmentById(
            R.id.activity_fragment_container
        ) as NavHostFragment
        navController = navHostFragment.navController

        binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        binding.ivClose.clicks().debounce(200).onEach {
            binding.drawerLayout.close()
        }

        binding.navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.about -> {
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                    navController.navigate(R.id.action_mainScreen_to_aboutScreen)
                }
                R.id.privacy_policy -> {
                    val uri = Uri.parse("https://anti-sihr-server.ru/agreement")
                    startActivity(Intent(Intent.ACTION_VIEW, uri))
                }
                R.id.feedback -> {
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                    navController.navigate(R.id.action_mainScreen_to_feedbackScreen)
                }
                R.id.language -> {
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                    navController.navigate(R.id.action_mainScreen_to_languageScreen)
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

    fun setAudioList(list: List<AudioBookmarked>) {
        audioPlayerManager.playlist = list as ArrayList<AudioBookmarked>
    }

    override fun onPreparedAudio(status: AudioStatus) {
    }

    override fun onCompletedAudio() {
        try {
            audioPlayerManager.nextAudio(true)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onPaused(status: AudioStatus) {
    }

    override fun onContinueAudio(status: AudioStatus) {

    }

    override fun onPlaying(status: AudioStatus) {

    }

    override fun onTimeChanged(status: AudioStatus) {}

    override fun onStopped(status: AudioStatus) {}

    override fun onJcpError(throwable: Throwable) {}

    fun playAudio(id: Int, isContinue: Boolean = true) {
        lifecycleScope.launchWhenCreated {
            resetBottomPlayerInfoFlow.emit(Unit)
        }
        audioPlayerManager.playlist.let {
            isFirstTime = false
            val audio = it.find { it.id == id }
            audioPlayerManager.playAudio(audio!!, isContinue)
        }
    }

    fun next() {
        audioPlayerManager.let { player ->
            player.currentAudio?.let {
                lifecycleScope.launchWhenResumed {
                    resetBottomPlayerInfoFlow.emit(Unit)
                }
                try {
                    player.nextAudio(false)
                } catch (e: Exception) {
                    lifecycleScope.launchWhenResumed {
                        errorBottomPlayerInfoFlow.emit(Unit)
                    }
                    e.printStackTrace()
                }
            }
        }
    }

    fun continueAudio() {
        try {
            audioPlayerManager.continueAudio()
        } catch (e: Exception) {
            lifecycleScope.launchWhenResumed {
                errorBottomPlayerInfoFlow.emit(Unit)
            }
            e.printStackTrace()
        }
    }

    fun pause() {
        audioPlayerManager.pauseAudio()
    }

    fun setStatusBarColor(@ColorRes color: Int) {
        window.statusBarColor = ContextCompat.getColor(this, color)
    }

    override fun onDestroy() {
        super.onDestroy()
        audioPlayerManager.kill()
    }
}