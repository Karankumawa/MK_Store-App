package com.example.mkstore.data.remote

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.serializer.KotlinXSerializer
import io.ktor.client.engine.okhttp.OkHttp
import kotlinx.serialization.json.Json

object SupabaseClientProvider {

    const val SUPABASE_URL = "https://heuetnnzuhqvjmxlbltx.supabase.co"
    const val SUPABASE_ANON_KEY = "sb_publishable_ydYq_Qj3UKlQOvWX2MsGzw_lfJfdUd0"

    val client by lazy {
        createSupabaseClient(
            supabaseUrl = SUPABASE_URL,
            supabaseKey = SUPABASE_ANON_KEY
        ) {
            httpEngine = OkHttp.create()
            install(Postgrest) {
                serializer = KotlinXSerializer(
                    Json {
                        ignoreUnknownKeys = true
                        coerceInputValues = true
                    }
                )
            }
        }
    }
}
