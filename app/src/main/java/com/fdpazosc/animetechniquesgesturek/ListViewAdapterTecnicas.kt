package com.fdpazosc.animetechniquesgesturek


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView


class ListViewAdapterTecnicas(private val context: Context) : BaseAdapter() {
    private val nombres: Array<String> = Contantes.getNombresTecnicas(context)
    private val imagenes: IntArray = Contantes.getImagenesTecnicas(context)

    override fun getCount(): Int {
        return nombres.size
    }

    override fun getItem(position: Int): Any {
        return Object()
    }

    override fun getItemId(position: Int): Long {
        return 0
    }


    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val view: View = View.inflate(context, R.layout.item_tecnica, null)
        val nombreTecnica: TextView
        val imagenTecnica: ImageView
        if (nombres.isNotEmpty() && imagenes.isNotEmpty()) {
            nombreTecnica = view.findViewById<View>(R.id.nombre_tecnica) as TextView
            nombreTecnica.text = nombres[p0]
            imagenTecnica = view.findViewById<View>(R.id.imagen_tecnica) as ImageView
            imagenTecnica.setImageResource(imagenes[p0])
        }
        return view
    }
}