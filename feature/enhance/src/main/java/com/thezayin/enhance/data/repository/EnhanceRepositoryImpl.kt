package com.thezayin.enhance.data.repository

import android.content.Context
import android.graphics.Bitmap
import com.thezayin.enhance.domain.model.EnhancementType
import com.thezayin.enhance.domain.repository.EnhanceRepository
import org.tensorflow.lite.Interpreter
import android.util.Log
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.min

class EnhanceRepositoryImpl(
    private val context: Context
) : EnhanceRepository {

    // Load TFLite models lazily
    private val basicModel: Interpreter by lazy { loadModel("esrgan.tflite") }
//    private val basicModel: Interpreter by lazy { loadModel("srgan.tflite") }
    private val plusModel: Interpreter by lazy { loadModel("edsr_x2.tflite") }
    private val proModel: Interpreter by lazy { loadModel("esrgan.tflite") }
    private val deblurModel: Interpreter by lazy { loadModel("wdsr.tflite") }

    override suspend fun processImage(inputBitmap: Bitmap, type: EnhancementType): Bitmap {
        return when (type) {
            EnhancementType.BASIC -> processBasicModel(inputBitmap)
            EnhancementType.PLUS -> processPlusModel(inputBitmap)
            EnhancementType.PRO -> processProModel(inputBitmap)
            EnhancementType.DEBLUR -> processDeblurModel(inputBitmap)
        }
    }

    private fun loadModel(fileName: String): Interpreter {
        Log.d("EnhanceRepositoryImpl", "Loading TFLite model: $fileName")
        val fileDescriptor = context.assets.openFd(fileName)
        val inputStream = fileDescriptor.createInputStream()
        val byteArray = inputStream.readBytes()
        val byteBuffer = ByteBuffer.allocateDirect(byteArray.size).apply {
            order(ByteOrder.nativeOrder())
            put(byteArray)
        }
        Log.d("EnhanceRepositoryImpl", "Model loaded successfully: $fileName")
        return Interpreter(byteBuffer)
    }

    private fun processBasicModel(inputBitmap: Bitmap): Bitmap {
        Log.d("EnhanceRepositoryImpl", "Processing with BASIC model")
        return enhanceImage(inputBitmap, basicModel)
    }

    private fun processPlusModel(inputBitmap: Bitmap): Bitmap {
        Log.d("EnhanceRepositoryImpl", "Processing with PLUS model")
        return enhanceImage(inputBitmap, plusModel)
    }

    private fun processProModel(inputBitmap: Bitmap): Bitmap {
        Log.d("EnhanceRepositoryImpl", "Processing with PRO model")
        return enhanceImage(inputBitmap, proModel)
    }

    private fun processDeblurModel(inputBitmap: Bitmap): Bitmap {
        Log.d("EnhanceRepositoryImpl", "Processing with DEBLUR model")
        return enhanceImage(inputBitmap, deblurModel)
    }

    private fun enhanceImage(inputBitmap: Bitmap, model: Interpreter): Bitmap {
        Log.d("EnhanceRepositoryImpl", "Starting enhancement using esrgan.tflite")

        // Preprocess the input bitmap
        val inputTensor = preprocessBitmap(inputBitmap)
        Log.d("EnhanceRepositoryImpl", "Input tensor created with size: ${inputTensor.capacity()} bytes")

        // Log input tensor details
        val inputTensorShape = model.getInputTensor(0).shape()
        Log.d("EnhanceRepositoryImpl", "Model input shape: ${inputTensorShape.contentToString()}")

        // Log output tensor details
        val outputTensorShape = model.getOutputTensor(0).shape()
        val outputTensorSize = model.getOutputTensor(0).numBytes()
        Log.d("EnhanceRepositoryImpl", "Model output shape: ${outputTensorShape.contentToString()}, size: $outputTensorSize bytes")

        // Allocate output buffer dynamically
        val outputBuffer = ByteBuffer.allocateDirect(outputTensorSize).apply {
            order(ByteOrder.nativeOrder())
        }
        Log.d("EnhanceRepositoryImpl", "Output buffer created with size: $outputTensorSize bytes")

        // Run inference
        try {
            model.run(inputTensor, outputBuffer)
            Log.d("EnhanceRepositoryImpl", "Inference completed successfully using esrgan.tflite")
        } catch (e: Exception) {
            Log.e("EnhanceRepositoryImpl", "Error during inference", e)
            throw e
        }

        // Postprocess the output buffer
        return postprocessBitmap(outputBuffer, outputTensorShape[1], outputTensorShape[2])
    }


    private fun preprocessBitmap(bitmap: Bitmap): ByteBuffer {
        val inputSize = 1 // Update to match the model's expected input size
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, inputSize, inputSize, true)
        val byteBuffer = ByteBuffer.allocateDirect(inputSize * inputSize * 3 * 4) // 3 channels (RGB), 4 bytes per float
        byteBuffer.order(ByteOrder.nativeOrder())

        for (y in 0 until inputSize) {
            for (x in 0 until inputSize) {
                val pixel = resizedBitmap.getPixel(x, y)
                byteBuffer.putFloat((pixel shr 16 and 0xFF) / 255.0f) // Red
                byteBuffer.putFloat((pixel shr 8 and 0xFF) / 255.0f)  // Green
                byteBuffer.putFloat((pixel and 0xFF) / 255.0f)       // Blue
            }
        }
        return byteBuffer
    }

    private fun postprocessBitmap(byteBuffer: ByteBuffer, outputWidth: Int, outputHeight: Int): Bitmap {
        byteBuffer.rewind()
        val outputBitmap = Bitmap.createBitmap(outputWidth, outputHeight, Bitmap.Config.ARGB_8888)

        for (y in 0 until outputHeight) {
            for (x in 0 until outputWidth) {
                val r = (byteBuffer.float * 255).toInt().coerceIn(0, 255)
                val g = (byteBuffer.float * 255).toInt().coerceIn(0, 255)
                val b = (byteBuffer.float * 255).toInt().coerceIn(0, 255)

                // Debug raw RGB values
                Log.d("PostprocessDebug", "Pixel($x, $y): R=$r, G=$g, B=$b")

                val color = (0xFF shl 24) or (r shl 16) or (g shl 8) or b
                outputBitmap.setPixel(x, y, color)
            }
        }

        Log.d("EnhanceRepositoryImpl", "Postprocessed Bitmap created with dimensions: ${outputBitmap.width}x${outputBitmap.height}")
        return outputBitmap
    }

}
