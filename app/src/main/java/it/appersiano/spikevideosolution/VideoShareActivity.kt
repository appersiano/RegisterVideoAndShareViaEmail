package it.appersiano.spikevideosolution

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.core.content.FileProvider
import kotlinx.android.synthetic.main.activity_video.*
import java.io.File
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*

class VideoShareActivity : Activity() {
    private var mShareIntent: Intent? = null
    private var os: OutputStream? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)

        btGo.setOnClickListener {
            askForAvideo()
        }
    }

    val REQUEST_VIDEO_CAPTURE = 564
    lateinit var movieFile: File
    lateinit var movieFileUri: Uri
    private fun askForAvideo() {
        movieFile = createMovieFile()
        val intent = Intent()

        with(intent) {
            action = MediaStore.ACTION_VIDEO_CAPTURE
            putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1)
            putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10)
        }

        movieFileUri = FileProvider.getUriForFile(this, "com.example.fileprovider", movieFile)
        intent.resolveActivity(baseContext.packageManager)?.also {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, movieFileUri)
            startActivityForResult(intent, REQUEST_VIDEO_CAPTURE)
        }
    }

    @Throws(IOException::class)
    private fun createMovieFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())

        val storageDir = File(filesDir, "pdfs")
        storageDir.mkdirs()
//        val storageDir: File = baseContext!!.getExternalFilesDir(Environment.DIRECTORY_MOVIES)!!

        return File.createTempFile(
            "NOISE_${timeStamp}_", /* prefix */
            ".mp4", /* suffix */
            storageDir /* directory */
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            //we have the video share on a email
            val shareEmail = Intent()
            shareEmail.action = Intent.ACTION_SEND
            shareEmail.type = "application/pdf"
            // Assuming it may go via eMail:
            shareEmail.putExtra(Intent.EXTRA_SUBJECT, "Here is a PDF from PdfSend")
            // Attach the PDf as a Uri, since Android can't take it as bytes yet.
            shareEmail.putExtra(Intent.EXTRA_STREAM, movieFileUri)
            startActivity(shareEmail)
        }
    }

}
