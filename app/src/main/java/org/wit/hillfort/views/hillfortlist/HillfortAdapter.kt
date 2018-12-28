package org.wit.hillfort.views.hillfortlist

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import kotlinx.android.synthetic.main.card_hillfort.view.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.wit.hillfort.R
import org.wit.hillfort.helpers.readImageFromPath
import org.wit.hillfort.main.MainApp
import org.wit.hillfort.models.HillfortModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

interface HillfortListener {
    fun onHillfortClick(hillfort: HillfortModel)
}

class HillfortAdapter constructor(private var hillforts: List<HillfortModel>,
                                  private val listener: HillfortListener, var app: MainApp) : androidx.recyclerview.widget.RecyclerView.Adapter<HillfortAdapter.MainHolder>() {

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
            itemView.imageIcon.setImageBitmap(readImageFromPath(itemView.context, hillfort.image))
            val checkBox: CheckBox = itemView.checkBoxVisited
            checkBox.isChecked = hillfort.visited
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                async(UI) {
                    if (checkBox.isChecked) {
                        hillfort.visited = true
//                        app.currentUser.numVisited++
                        val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                        hillfort.dateVisited = date
                        app.hillforts.update(hillfort)
                    } else {
                        hillfort.visited = false
//                        app.currentUser.numVisited--
                        hillfort.dateVisited = ""
                        app.hillforts.update(hillfort)
                    }
                }
            }
            itemView.setOnClickListener { listener.onHillfortClick(hillfort) }
        }
    }
}