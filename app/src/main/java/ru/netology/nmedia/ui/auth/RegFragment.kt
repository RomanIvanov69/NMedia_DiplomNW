package ru.netology.nmedia.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.github.dhaval2404.imagepicker.constant.ImageProvider
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentRegBinding
import ru.netology.nmedia.enumeration.AttachmentType
import ru.netology.nmedia.util.AndroidUtils
import ru.netology.nmedia.viewmodel.RegisterViewModel

@AndroidEntryPoint
class RegistrationFragment : Fragment() {

    private val viewModel: RegisterViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentRegBinding.inflate(inflater, container, false)

        val pickPhotoLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                when (it.resultCode) {
                    ImagePicker.RESULT_ERROR -> {
                        Snackbar.make(binding.root, R.string.error_loading, Snackbar.LENGTH_LONG)
                            .show()
                    }

                    else -> {
                        val uri = it.data?.data ?: return@registerForActivityResult
                        viewModel.addPhoto(uri, uri.toFile(), AttachmentType.IMAGE)
                    }
                }
            }
        binding.pickPhoto.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(2048)
                .provider(ImageProvider.GALLERY)
                .galleryMimeTypes(
                    arrayOf(
                        "image/png",
                        "image/jpeg",
                    )
                )
                .createIntent(pickPhotoLauncher::launch)
        }
        binding.takePhoto.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(2048)
                .provider(ImageProvider.CAMERA)
                .createIntent(pickPhotoLauncher::launch)
        }

        with(binding) {

            btSignUp.setOnClickListener {
                val login = loginInput.text.toString()
                val password = passwordInput.text.toString()
                val confirmPassword = passwordConfirmInput.text.toString()
                val name = nameInput.text.toString()

                if (password == confirmPassword) {
                    viewModel.registration(login, password, name)
                    AndroidUtils.hideKeyboard(requireView())
                } else {
                    Snackbar.make(
                        binding.root,
                        R.string.match_passwords,
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }

            close.setOnClickListener {
                findNavController().navigateUp()
            }
        }
        viewModel.state.observe(viewLifecycleOwner) { state ->

            binding.btSignUp.isEnabled = !state.loading

            if (state.register) {
                findNavController().navigateUp()
            }

            if (state.isEmpty) {
                Snackbar.make(
                    binding.root,
                    R.string.emptyRegister,
                    Snackbar.LENGTH_LONG
                ).show()
            }

            if (state.error) {
                Snackbar.make(
                    binding.root,
                    R.string.error_loading,
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
        binding.removePhoto.setOnClickListener {
            viewModel.clearPhoto()
        }
        return binding.root
    }
}


