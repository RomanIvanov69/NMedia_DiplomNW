package ru.netology.nmedia.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentAuthBinding
import ru.netology.nmedia.viewmodel.AuthViewModel

@AndroidEntryPoint
class AuthFragment : Fragment() {

    private val authViewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentAuthBinding.inflate(inflater, container, false)

        with(binding) {

            btSignIn.setOnClickListener {
                val login = login.text.toString()
                val pass = password.text.toString()
                authViewModel.authentication(login, pass)
            }

            authViewModel.state.observe(viewLifecycleOwner) { state ->

                binding.btSignIn.isEnabled = !state.loading

                if (state.register) {
                    findNavController().navigateUp()
                }

                if (state.isEmpty) {
                    Snackbar.make(
                        binding.root,
                        R.string.emptyAuth,
                        Snackbar.LENGTH_LONG
                    ).show()
                }

                if (state.incorrectLoginOrPass) {
                    Snackbar.make(
                        binding.root,
                        R.string.incorrect_auth,
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

            return binding.root
        }
    }
}