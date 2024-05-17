package com.fdpazosc.animetechniquesgesturek

import android.content.Intent
import android.os.Bundle
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    private lateinit var listaTecnicas: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        listaTecnicas = findViewById(R.id.lista_tecnicas)
        listaTecnicas.adapter = ListViewAdapterTecnicas(this)
        listaTecnicas.setOnItemClickListener { _, _, i, _ ->
            val intent = Intent(
                baseContext,
                TecnicasActivity::class.java
            )
            intent.putExtra("ID_TECNICA", i)
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            startActivity(intent)
        }
    }
}