package ru.netology.nmedia.ui

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import androidx.fragment.app.Fragment
import ru.netology.nmedia.databinding.FragmentVideoBinding

class VideoFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentVideoBinding.inflate(inflater, container, false)

        val url = requireArguments().getString(URL)
        binding.apply {
            videoView.setVideoURI(Uri.parse(url))
            val mediaController = MediaController(requireContext())
            mediaController.setAnchorView(videoView)
            mediaController.setMediaPlayer(videoView)
            videoView.setMediaController(mediaController)
            videoView.start()
        }

        return binding.root
    }

    companion object {
        const val URL = "URL"
    }
}