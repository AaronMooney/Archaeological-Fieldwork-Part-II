package org.wit.hillfort.activities

import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.graphics.Palette
import android.view.ViewGroup
import android.widget.ImageView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import org.wit.hillfort.R

class ImageActivity : AppCompatActivity() {

    lateinit var imageView: ImageView
    lateinit var hillfortImage: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.item_image)

        hillfortImage = intent.getStringExtra("image")
        imageView = findViewById(R.id.hillfortImage)

        display(hillfortImage)
    }

    fun display(image: String) {

        Picasso.get()
            .load(image)
            .placeholder(R.drawable.ic_launcher_background)
            .fit()
            .into(imageView, object : Callback {

                override fun onSuccess() {
                    val bitmap = (imageView.drawable as BitmapDrawable).bitmap
                    onPalette(Palette.from(bitmap).generate())
                }

                override fun onError(e: Exception?) {
                }
            })
    }

    fun onPalette(palette: Palette?) {
        if (null != palette) {
            val parent = imageView.parent.parent as ViewGroup
            parent.setBackgroundColor(palette.getDarkVibrantColor(Color.GRAY))
        }
    }
}