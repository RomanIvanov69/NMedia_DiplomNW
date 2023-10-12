package ru.netology.nmedia.ui.posts

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.snackbar.Snackbar
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentNewEventBinding
import ru.netology.nmedia.enumeration.AttachmentType
import ru.netology.nmedia.util.AndroidUtils
import ru.netology.nmedia.viewmodel.PostViewModel

class NewPostFragment: Fragment() {

    private val viewModel: PostViewModel by activityViewModels()
    private var imageLauncher: ActivityResultLauncher<Intent>? = null

    private val onBackPressedCallback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            viewModel.clearEdited()
            findNavController().navigateUp()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentNewEventBinding.inflate(inflater, container, false)
        binding.eventGroup.isVisible = false

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            onBackPressedCallback
        )

        viewModel.getEditPost()?.let { post ->
            binding.editNewPost.setText(post.content)
            binding.editTextLink.setText(post.link)
            if (post.attachment?.type == AttachmentType.IMAGE) {
                binding.textViewImage.text = post.attachment.url
            }
        }

        binding.createPost.setOnClickListener {
            val content = binding.editNewPost.text.toString()
            val link = AndroidUtils.checkLink(binding.editTextLink.text.toString())
            if (link == "") {
                Snackbar.make(binding.root, R.string.invalid_link, Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }
            viewModel.changeContent(content,link)
            viewModel.save()
            AndroidUtils.hideKeyboard(requireView())
            findNavController().navigate(R.id.feedFragment)
        }

        viewModel.postCreated.observe(viewLifecycleOwner) {
            findNavController().navigateUp()
        }

        imageLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                when (it.resultCode) {
                    ImagePicker.RESULT_ERROR -> {
                        Snackbar.make(binding.root, R.string.error_loading, Snackbar.LENGTH_LONG)
                            .show()
                    }
                    else -> {
                        val uri = it.data?.data ?: return@registerForActivityResult
                        viewModel.addMedia(uri, uri.toFile(), AttachmentType.IMAGE)
                    }
                }
            }
        binding.addImageButton.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(2048)
                .createIntent(imageLauncher!!::launch)
        }
        binding.clearImage.setOnClickListener {
            viewModel.clearMedia()
        }
        viewModel.media.observe(viewLifecycleOwner) { media ->
            media?.let {
                binding.textViewImage.text = media.file.name
            }
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        imageLauncher = null
    }
}