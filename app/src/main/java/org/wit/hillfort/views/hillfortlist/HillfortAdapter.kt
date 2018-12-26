package org.wit.hillfort.views.hillfortlist

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import kotlinx.android.synthetic.main.card_hillfort.view.*
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

    class MainHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(hillfort: HillfortModel, listener : HillfortListener, app:MainApp) {
            itemView.hillfortName.text = hillfort.name
            itemView.description.text = hillfort.description
            if (hillfort.images.isNotEmpty()) {
                itemView.imageIcon.setImageBitmap((readImageFromPath(itemView.context, hillfort.images.first())))
            }
            val checkBox: CheckBox = itemView.checkBoxVisited
            checkBox.isChecked = hillfort.visited
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                if (checkBox.isChecked){
                    hillfort.visited = true
                    app.numHillfortsVisited ++
                    val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                    hillfort.dateVisited = date
                    app.users.updateUser(app.currentUser.copy())
                } else {
                    hillfort.visited = false
                    app.numHillfortsVisited --
                    hillfort.dateVisited = ""
                    app.users.updateUser(app.currentUser.copy())
                }
            }
            itemView.setOnClickListener { listener.onHillfortClick(hillfort) }
        }
    }
}