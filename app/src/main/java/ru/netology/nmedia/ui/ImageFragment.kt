package ru.netology.nmedia.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ru.netology.nmedia.databinding.FragmentImageBinding
import ru.netology.nmedia.view.loadAttachment

class ImageFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentImageBinding.inflate(inflater, container, false)

        requireArguments().getString(VideoFragment.URL)?.let {
            binding.ImageFullScreen.loadAttachment(it)
        }

        return binding.root
    }

    companion object {
        const val URL = "URL"
    }
}