package com.fresheats.app

import android.app.Application
import com.google.firebase.FirebaseApp

class FreshEatsApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Inicializa Firebase explícitamente (opcional en muchas configuraciones modernas, 
        // pero buena práctica y útil para tener control total o usar múltiples proyectos)
        FirebaseApp.initializeApp(this)
    }
}
