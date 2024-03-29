package org.wit.hillfort.views.hillfortlist

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.card_hillfort.view.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.image
import org.wit.hillfort.R
import org.wit.hillfort.helpers.readImageFromPath
import org.wit.hillfort.main.MainApp
import org.wit.hillfort.models.HillfortModel
import java.text.SimpleDateFormat
import java.util.*



interface HillfortListener {
    fun onHillfortClick(hillfort: HillfortModel)
}

class HillfortAdapter constructor(private var hillforts: List<HillfortModel>,
                                  private val listener: HillfortListener, var app: MainApp) : RecyclerView.Adapter<HillfortAdapter.MainHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        return MainHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.card_hillfort,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val hillfort = hillforts[holder.adapterPosition]
        holder.bind(hillfort, listener, app)
    }

    override fun getItemCount(): Int = hillforts.size

    class MainHolder constructor(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        fun bind(hillfort: HillfortModel, listener : HillfortListener, app:MainApp) {
            itemView.hillfortName.text = hillfort.name
            itemView.description.text = hillfort.description
            itemView.rating.text = hillfort.rating.toString()
            Glide.with(itemView.context).load(hillfort.image).into(itemView.imageIcon)

            if (!hillfort.favorite) {
                itemView.item_favorite.image = ContextCompat.getDrawable(app.applicationContext, android.R.drawable.star_big_off)
            } else {
                itemView.item_favorite.image = ContextCompat.getDrawable(app.applicationContext, android.R.drawable.star_big_on)
            }

            val checkBox: CheckBox = itemView.checkBoxVisited
            checkBox.isChecked = hillfort.visited
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                async(UI) {
                    if (checkBox.isChecked) {
                        hillfort.visited = true
                        val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                        hillfort.dateVisited = date
                        app.hillforts.update(hillfort)
                    } else {
                        hillfort.visited = false
                        hillfort.dateVisited = ""
                        app.hillforts.update(hillfort)
                    }
                }
            }
            itemView.setOnClickListener { listener.onHillfortClick(hillfort) }
        }
    }
}