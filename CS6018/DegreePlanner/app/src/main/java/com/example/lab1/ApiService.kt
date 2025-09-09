package com.example.lab1.network

import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import com.google.gson.Gson
import com.example.lab1.data.DegreePlansResponse
import com.example.lab1.data.DegreeRequirementsResponse

class ApiService {

    private val client = HttpClient(Android)
    private val json = Gson()

    // Fetch the list of all available degree plans
    suspend fun fetchDegreePlans(): Result<DegreePlansResponse> {
        return try {
            // Make an HTTP GET request to the given URL
            //downloads the JSON file as plain text
            val jsonString = client
                .get("https://msd2025.github.io/degreePlans/degreePlans.json")
                .bodyAsText()

            //convert the raw JSON string into a DegreePlansResponse object
            //"fromJson" = parse JSON → Kotlin class
            val result = json.fromJson(jsonString, DegreePlansResponse::class.java)

            //wrap the parsed result in a "success" Result
            Result.success(result)
        } catch (e: Exception) {
            //any error happens
            Result.failure(e)
        }
    }

    // Fetch the requirements for a specific degree plan
    suspend fun fetchDegreeRequirements(planPath: String): Result<DegreeRequirementsResponse> {
        return try {
            //build full URL for the requirements file
            // https://msd2025.github.io/degreePlans/cs.json
            val jsonString = client
                .get("https://msd2025.github.io/degreePlans/$planPath")
                .bodyAsText()

            //parse JSON into DegreeRequirementsResponse object
            val result = json.fromJson(jsonString, DegreeRequirementsResponse::class.java)

            //wrap the parsed object in a success Result
            Result.success(result)
        } catch (e: Exception) {
            //any error
            Result.failure(e)
        }
    }

    fun close() {
        client.close()
    }
}