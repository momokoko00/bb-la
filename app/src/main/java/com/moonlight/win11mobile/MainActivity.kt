package com.moonlight.win11mobile

import android.app.WallpaperManager
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.Build
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.widget.ImageView
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.Manifest
import android.view.View

class MainActivity : ComponentActivity() {
    private lateinit var imageView: ImageView
    private val REQUEST_CODE_PERMISSION = 1  // Define a constant for the request code
    private lateinit var thirdRectangle: View
    private lateinit var secondRectangle: View
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        imageView = findViewById(R.id.imageView)
        thirdRectangle = findViewById(R.id.thirdRectangle)
        makeStatusBarAndNavigationBarTransparent()
        checkAndRequestPermissions()
        secondRectangle = findViewById(R.id.secondRectangle)
        setBlendModeToMultiply(secondRectangle)
        val color = getDynamicBackgroundColor()
        setRoundedCornersWithColor(thirdRectangle, color)



        // Adjust for safe area insets
        val frameLayout: View = findViewById(R.id.frameLayout)
        ViewCompat.setOnApplyWindowInsetsListener(frameLayout) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(
                systemBars.left + 5,
                systemBars.top,
                systemBars.right + 5,
                systemBars.bottom + 10 // Adds 10px to the bottom inset
            )
            insets
        }
    }

    private fun makeStatusBarAndNavigationBarTransparent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION)
            window.statusBarColor = Color.TRANSPARENT
            window.navigationBarColor = Color.TRANSPARENT
        }
    }



    private fun checkAndRequestPermissions() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_CODE_PERMISSION
            )
        } else {
            loadWallpaper()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadWallpaper()
            } else {
                // Handle permission denial. Provide feedback!
                imageView.setImageResource(R.drawable.default_wallpaper)
                // Display an error message to the user.
            }
        }
    }

    private fun loadWallpaper() {
        val wallpaperManager = WallpaperManager.getInstance(this)
        try {
            val drawable: Drawable? = wallpaperManager.drawable
            drawable?.let {
                imageView.setImageDrawable(it)
            } ?: run {
                imageView.setImageResource(R.drawable.default_wallpaper)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            imageView.setImageResource(R.drawable.default_wallpaper)
        }
    }

    private fun getDynamicBackgroundColor(): Int {
        return if (resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK == android.content.res.Configuration.UI_MODE_NIGHT_YES) {
            Color.parseColor("#CC202020")
        } else {
            Color.parseColor("#CCF3F3F3")
        }
    }

    private fun setBlendModeToMultiply(view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val background = view.background.mutate()
            background.colorFilter = PorterDuffColorFilter(Color.BLACK, PorterDuff.Mode.MULTIPLY)
            view.background = background
        }
    }

    private fun setRoundedCornersWithColor(view: View, backgroundColor: Int) {
        val radius = dpToPx(12) // Radius in dp
        val background = GradientDrawable().apply {
            cornerRadius = radius.toFloat()
            setColor(backgroundColor)
        }
        view.background = background
    }

    private fun dpToPx(dp: Int): Int {
        val density = resources.displayMetrics.density
        return (dp * density).toInt()
    }

}
