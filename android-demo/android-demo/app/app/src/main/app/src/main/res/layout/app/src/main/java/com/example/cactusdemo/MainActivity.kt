package com.example.cactusdemo

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.cactus.Cactus
import com.cactus.CompletionResult
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

    private var cactus: Cactus? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val inputPrompt = findViewById<EditText>(R.id.inputPrompt)
        val runBtn = findViewById<Button>(R.id.runBtn)
        val outputText = findViewById<TextView>(R.id.outputText)

        val modelPath = filesDir.absolutePath + "/model.bin"

        // Load model in background
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                cactus = Cactus.create(modelPath)

                withContext(Dispatchers.Main) {
                    outputText.text = "Model loaded. Ready!"
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    outputText.text = "Init error: ${e.message}"
                }
            }
        }

        runBtn.setOnClickListener {
            val prompt = inputPrompt.text.toString()

            if (prompt.isBlank()) {
                outputText.text = "Enter a prompt"
                return@setOnClickListener
            }

            runBtn.isEnabled = false
            outputText.text = "Running..."

            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val result: CompletionResult? = cactus?.complete(prompt)

                    withContext(Dispatchers.Main) {
                        outputText.text = result?.text ?: "No result"
                        runBtn.isEnabled = true
                    }

                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        outputText.text = "Error: ${e.message}"
                        runBtn.isEnabled = true
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        cactus?.close()
        super.onDestroy()
    }
}
