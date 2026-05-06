package com.cute.timer

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.*
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.cute.timer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var countDownTimer: CountDownTimer? = null
    private var totalMillis: Long = 0L
    private var remainingMillis: Long = 0L
    private var isRunning = false
    private var isPaused = false

    // Bounce animation for the mascot
    private var bounceAnimator: ObjectAnimator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
        updateTimerDisplay(0L)
        updateUI(TimerState.IDLE)
    }

    private fun setupClickListeners() {
        binding.btnStart.setOnClickListener {
            if (!isRunning && !isPaused) {
                startTimer()
            }
        }

        binding.btnPause.setOnClickListener {
            if (isRunning) pauseTimer()
            else if (isPaused) resumeTimer()
        }

        binding.btnReset.setOnClickListener {
            resetTimer()
        }

        // Number picker buttons
        binding.btnMinutesUp.setOnClickListener { adjustMinutes(1) }
        binding.btnMinutesDown.setOnClickListener { adjustMinutes(-1) }
        binding.btnSecondsUp.setOnClickListener { adjustSeconds(10) }
        binding.btnSecondsDown.setOnClickListener { adjustSeconds(-10) }

        // Preset buttons
        binding.btn1min.setOnClickListener { setPreset(1, 0) }
        binding.btn3min.setOnClickListener { setPreset(3, 0) }
        binding.btn5min.setOnClickListener { setPreset(5, 0) }
        binding.btn10min.setOnClickListener { setPreset(10, 0) }
    }

    private fun adjustMinutes(delta: Int) {
        if (isRunning) return
        val mins = binding.tvMinutes.text.toString().toIntOrNull() ?: 0
        val newMins = (mins + delta).coerceIn(0, 99)
        binding.tvMinutes.text = String.format("%02d", newMins)
        bounceButton(if (delta > 0) binding.btnMinutesUp else binding.btnMinutesDown)
    }

    private fun adjustSeconds(delta: Int) {
        if (isRunning) return
        val secs = binding.tvSeconds.text.toString().toIntOrNull() ?: 0
        val newSecs = (secs + delta).coerceIn(0, 59)
        binding.tvSeconds.text = String.format("%02d", newSecs)
        bounceButton(if (delta > 0) binding.btnSecondsUp else binding.btnSecondsDown)
    }

    private fun setPreset(minutes: Int, seconds: Int) {
        if (isRunning) return
        binding.tvMinutes.text = String.format("%02d", minutes)
        binding.tvSeconds.text = String.format("%02d", seconds)
        punchMascot()
    }

    private fun startTimer() {
        val mins = binding.tvMinutes.text.toString().toIntOrNull() ?: 0
        val secs = binding.tvSeconds.text.toString().toIntOrNull() ?: 0
        totalMillis = ((mins * 60) + secs) * 1000L

        if (totalMillis == 0L) {
            shakeView(binding.tvMinutes)
            return
        }

        remainingMillis = totalMillis
        beginCountdown()
        updateUI(TimerState.RUNNING)
        startMascotBounce()
    }

    private fun resumeTimer() {
        beginCountdown()
        updateUI(TimerState.RUNNING)
        startMascotBounce()
    }

    private fun beginCountdown() {
        isRunning = true
        isPaused = false

        countDownTimer = object : CountDownTimer(remainingMillis, 50) {
            override fun onTick(millisUntilFinished: Long) {
                remainingMillis = millisUntilFinished
                updateTimerDisplay(millisUntilFinished)
                updateProgressRing(millisUntilFinished)
            }

            override fun onFinish() {
                remainingMillis = 0L
                isRunning = false
                updateTimerDisplay(0L)
                updateProgressRing(0L)
                updateUI(TimerState.FINISHED)
                onTimerFinished()
            }
        }.start()
    }

    private fun pauseTimer() {
        countDownTimer?.cancel()
        isRunning = false
        isPaused = true
        updateUI(TimerState.PAUSED)
        stopMascotBounce()
    }

    private fun resetTimer() {
        countDownTimer?.cancel()
        isRunning = false
        isPaused = false
        remainingMillis = 0L

        binding.tvMinutes.text = "00"
        binding.tvSeconds.text = "00"
        updateTimerDisplay(0L)
        updateProgressRing(totalMillis) // reset ring to full... actually clear it
        binding.progressRing.progress = 0
        updateUI(TimerState.IDLE)
        stopMascotBounce()

        // Reset mascot
        binding.ivMascot.animate()
            .scaleX(1f).scaleY(1f)
            .rotation(0f)
            .setDuration(300)
            .start()
    }

    private fun updateTimerDisplay(millis: Long) {
        val mins = (millis / 1000) / 60
        val secs = (millis / 1000) % 60
        binding.tvCountdownMins.text = String.format("%02d", mins)
        binding.tvCountdownSecs.text = String.format("%02d", secs)
    }

    private fun updateProgressRing(millisRemaining: Long) {
        if (totalMillis > 0) {
            val progress = ((millisRemaining.toFloat() / totalMillis.toFloat()) * 100).toInt()
            binding.progressRing.progress = progress
        }
    }

    private fun onTimerFinished() {
        stopMascotBounce()
        celebrateMascot()
        playAlertSound()
        vibrateDevice()
    }

    private fun updateUI(state: TimerState) {
        when (state) {
            TimerState.IDLE -> {
                binding.layoutInput.visibility = View.VISIBLE
                binding.layoutCountdown.visibility = View.GONE
                binding.btnStart.visibility = View.VISIBLE
                binding.btnPause.visibility = View.GONE
                binding.btnReset.visibility = View.GONE
                binding.tvStatus.text = "Set your timer! 🐟"
                binding.progressRing.progress = 0
            }
            TimerState.RUNNING -> {
                binding.layoutInput.visibility = View.GONE
                binding.layoutCountdown.visibility = View.VISIBLE
                binding.btnStart.visibility = View.GONE
                binding.btnPause.visibility = View.VISIBLE
                binding.btnPause.text = "⏸ Pause"
                binding.btnReset.visibility = View.VISIBLE
                binding.tvStatus.text = "Tick tock... ⏰"
            }
            TimerState.PAUSED -> {
                binding.btnPause.text = "▶ Resume"
                binding.tvStatus.text = "Paused~ 💤"
            }
            TimerState.FINISHED -> {
                binding.layoutInput.visibility = View.VISIBLE
                binding.layoutCountdown.visibility = View.GONE
                binding.btnStart.visibility = View.VISIBLE
                binding.btnPause.visibility = View.GONE
                binding.btnReset.visibility = View.GONE
                binding.tvStatus.text = "Time's up! 🎉✨"
            }
        }
    }

    // ─── Animations ────────────────────────────────────────────────────────────

    private fun startMascotBounce() {
        bounceAnimator?.cancel()
        bounceAnimator = ObjectAnimator.ofFloat(binding.ivMascot, "translationY", 0f, -18f, 0f).apply {
            duration = 700
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.RESTART
            start()
        }
    }

    private fun stopMascotBounce() {
        bounceAnimator?.cancel()
        binding.ivMascot.animate().translationY(0f).setDuration(200).start()
    }

    private fun celebrateMascot() {
        val scaleX = ObjectAnimator.ofFloat(binding.ivMascot, "scaleX", 1f, 1.3f, 1f, 1.2f, 1f)
        val scaleY = ObjectAnimator.ofFloat(binding.ivMascot, "scaleY", 1f, 1.3f, 1f, 1.2f, 1f)
        val rotate = ObjectAnimator.ofFloat(binding.ivMascot, "rotation", 0f, -15f, 15f, -10f, 0f)
        AnimatorSet().apply {
            playTogether(scaleX, scaleY, rotate)
            duration = 800
            interpolator = OvershootInterpolator()
            start()
        }
    }

    private fun punchMascot() {
        binding.ivMascot.animate()
            .scaleX(1.15f).scaleY(1.15f)
            .setDuration(100)
            .withEndAction {
                binding.ivMascot.animate()
                    .scaleX(1f).scaleY(1f)
                    .setDuration(150)
                    .start()
            }.start()
    }

    private fun bounceButton(view: View) {
        view.animate()
            .scaleX(0.85f).scaleY(0.85f)
            .setDuration(80)
            .withEndAction {
                view.animate().scaleX(1f).scaleY(1f).setDuration(120).start()
            }.start()
    }

    private fun shakeView(view: View) {
        val shake = ObjectAnimator.ofFloat(view, "translationX", 0f, -12f, 12f, -8f, 8f, 0f)
        shake.duration = 400
        shake.start()
    }

    private fun playAlertSound() {
        try {
            val toneGen = ToneGenerator(AudioManager.STREAM_ALARM, 100)
            toneGen.startTone(ToneGenerator.TONE_PROP_BEEP2, 1200)
            Handler(Looper.getMainLooper()).postDelayed({
                toneGen.release()
            }, 1500)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun vibrateDevice() {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vm = getSystemService(VIBRATOR_MANAGER_SERVICE) as android.os.VibratorManager
            vm.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(VIBRATOR_SERVICE) as Vibrator
        }
        val pattern = longArrayOf(0, 200, 100, 200, 100, 400)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(pattern, -1)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
        bounceAnimator?.cancel()
    }

    enum class TimerState { IDLE, RUNNING, PAUSED, FINISHED }
}
