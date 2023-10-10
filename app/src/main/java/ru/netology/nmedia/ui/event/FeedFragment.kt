package ru.netology.nmedia.ui.event

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.PagerAdapter
import ru.netology.nmedia.databinding.FragmentFeedBinding
import ru.netology.nmedia.ui.user.UserFragment
import ru.netology.nmedia.view.load
import ru.netology.nmedia.viewmodel.AuthViewModel
import ru.netology.nmedia.viewmodel.UserViewModel

@AndroidEntryPoint
class FeedFragment : Fragment() {

    private val authViewModel by viewModels<AuthViewModel>()
    private val userViewModel by viewModels<UserViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentFeedBinding.inflate(inflater, container, false)

        authViewModel.data.observe(viewLifecycleOwner) { authModel ->
            binding.apply {
                if (authViewModel.authorized) {
                    menuAuth.visibility = View.INVISIBLE
                    feedGroup.visibility = View.VISIBLE
                    exit.visibility = View.VISIBLE
                } else {
                    menuAuth.visibility = View.VISIBLE
                    feedGroup.visibility = View.INVISIBLE
                    exit.visibility = View.INVISIBLE
                }
            }

            userViewModel.getUserById(authModel.id)
        }

        userViewModel.user.observe(viewLifecycleOwner) { user ->
            binding.feedName.text = user.name
            user.avatar?.apply {
                binding.feedAvatar.load(this)
            } ?: binding.feedAvatar.setImageResource(R.drawable.ic_empty_avatar)
            binding.feedAvatar.setOnClickListener {
                findNavController().navigate(
                    R.id.action_feedFragment_to_userFragment,
                    bundleOf(UserFragment.USER_ID to user.id)
                )
            }
        }

        binding.menuAuth.setOnClickListener {
            PopupMenu(it.context, it).apply {
                inflate(R.menu.menu_auth)
                setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.login -> {
                            findNavController().navigate(R.id.authFragment)
                            true
                        }
                        R.id.register -> {
                            findNavController().navigate(R.id.regFragment)
                            true
                        }
                        else -> false
                    }
                }
            }.show()
        }

        binding.exit.setOnClickListener {
                    authViewModel.logout()
                    findNavController().navigate(R.id.action_feedFragment_to_authFragment)
                }

        binding.listContainer.adapter = PagerAdapter(childFragmentManager, lifecycle)
        TabLayoutMediator(binding.tabLayout, binding.listContainer) { tab, position ->
            val feedList = listOf(getString(R.string.posts), getString(R.string.events))
            tab.text = feedList[position]
        }.attach()


        binding.fab.setOnClickListener {
            if (!authViewModel.authorized) {
                findNavController().navigate(R.id.action_feedFragment_to_regFragment)
            } else {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.menu_add)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.new_post -> {
                                findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
                                true
                            }
                            R.id.new_event -> {
                                findNavController().navigate(R.id.action_feedFragment_to_newEventFragment)
                                true
                            }
                            else -> false
                        }
                    }
                }.show()
            }
        }

        return binding.root
    }
}