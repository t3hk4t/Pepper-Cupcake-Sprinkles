package com.softbankrobotics.qisdktutorials.ui.categories

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.softbankrobotics.qisdktutorials.R
import android.os.Environment
import android.view.View
import android.widget.VideoView
import android.net.Uri;
import android.widget.Button
import android.view.Menu;
import android.widget.MediaController;


class VIdeoPlayerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_player)
        val path = "android.resource://" + packageName + "/" + R.raw.video
        val videoView = findViewById<View>(R.id.videoView) as VideoView
        //Creating MediaController
        val mediaController = MediaController(this)
        mediaController.setAnchorView(videoView)
        //specify the location of media file
        val uri: Uri = Uri.parse(path)
        //Setting MediaController and URI, then starting the videoView
        videoView.setMediaController(mediaController)
        videoView.setVideoURI(uri)
        videoView.requestFocus()
        videoView.start()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        //menuInflater.inflate(R.menu.activity_main, menu)
        return true
    }
}