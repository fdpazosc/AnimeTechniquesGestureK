package com.fdpazosc.animetechniquesgesturek

import android.content.Context


class Contantes {
    companion object {
        private lateinit var imagenesTecnicas: IntArray
        private lateinit var nombresTecnicas: Array<String>

        private fun cargarDatosTecnicas(contexto: Context) {
            imagenesTecnicas = intArrayOf(
                R.drawable.bolt_solid,
                R.drawable.bolt_solid,
                R.drawable.bolt_solid,
                R.drawable.bolt_solid,
                R.drawable.bolt_solid,
                R.drawable.bolt_solid,
                R.drawable.bolt_solid,
                R.drawable.bolt_solid,
                R.drawable.bolt_solid
            )
            nombresTecnicas = contexto.resources.getStringArray(R.array.tecnicas_anime)
        }

        fun getImagenesTecnicas(contexto: Context): IntArray {
            cargarDatosTecnicas(contexto)
            if (imagenesTecnicas.size == nombresTecnicas.size) return imagenesTecnicas
            return intArrayOf()
        }

        fun getNombresTecnicas(contexto: Context): Array<String> {
            cargarDatosTecnicas(contexto)
            if (imagenesTecnicas.size == nombresTecnicas.size) return nombresTecnicas
            return arrayOf()
        }
    }
}