package org.wit.hillfort.activities

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.squareup.picasso.Picasso
import org.wit.hillfort.R


interface ImageListener {
    fun onHillfortImageClick(image: String)
}

class ImageGalleryAdapter(val images: List<String>, private val listener: ImageListener)
    : RecyclerView.Adapter<ImageGalleryAdapter.ImageHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageGalleryAdapter.ImageHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val photoView = inflater.inflate(R.layout.item_image, parent, false)
        return ImageHolder(photoView)
    }

    override fun onBindViewHolder(holder: ImageGalleryAdapter.ImageHolder, position: Int) {
        val image = images[position]
        holder.bind(image, listener)
    }

    override fun getItemCount(): Int {
        return images.size
    }

    class ImageHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(image: String, listener: ImageListener) {
            val imageView: ImageView = itemView.findViewById(R.id.hillfortImage)

            Picasso.get()
                .load(image)
                .placeholder(R.drawable.ic_launcher_background)
                .fit()
                .into(imageView)
            itemView.setOnClickListener{listener.onHillfortImageClick(image)}
        }
    }
}