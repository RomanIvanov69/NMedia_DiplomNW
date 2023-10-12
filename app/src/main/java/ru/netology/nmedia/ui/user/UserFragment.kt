package ru.netology.nmedia.ui.user

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.FeedAdapter
import ru.netology.nmedia.adapter.FeedOnInteractionListener
import ru.netology.nmedia.databinding.FragmentUserBinding
import ru.netology.nmedia.dto.FeedItem
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.enumeration.AttachmentType
import ru.netology.nmedia.ui.ImageFragment
import ru.netology.nmedia.ui.VideoFragment
import ru.netology.nmedia.ui.user.JobsFragment.Companion.USER_ID
import ru.netology.nmedia.view.load
import ru.netology.nmedia.viewmodel.JobViewModel
import ru.netology.nmedia.viewmodel.PostViewModel
import ru.netology.nmedia.viewmodel.UserViewModel

class UserFragment : Fragment() {

    private val userViewModel by activityViewModels<UserViewModel>()
    private val postViewModel by activityViewModels<PostViewModel>()
    private val jobViewModel by activityViewModels<JobViewModel>()


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentUserBinding.inflate(inflater, container, false)

        val userId = requireArguments().getInt(USER_ID)

        userViewModel.getUserById(userId)
        jobViewModel.getJobsByUserId(userId)

        userViewModel.user.observe(viewLifecycleOwner) { user ->
            binding.titleName.text = user.name
            user.avatar?.let { binding.userAvatar.load(it) }
                ?: binding.userAvatar.setImageResource(R.drawable.ic_empty_avatar)
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                jobViewModel.data.collectLatest { jobsList ->
                    if (jobsList.isNotEmpty()) {
                        val job = jobViewModel.getCurrentJob(jobsList)
                        binding.titleWorkName.text = job.position
                        binding.titleWork.text = job.name
                    }
                }
            }
        }

        binding.iconJob.setOnClickListener {
            findNavController().navigate(
                R.id.action_userFragment_to_jobsFragment,
                bundleOf(JobsFragment.USER_ID to userId)
            )
        }

        val adapter = FeedAdapter(object : FeedOnInteractionListener {
            override fun onLike(feedItem: FeedItem) {
                postViewModel.likePostById(feedItem as Post)
            }

            override fun onRemove(id: Int) {
                postViewModel.removeById(id)
            }

            override fun onEdit(feedItem: FeedItem) {
                findNavController().navigate(R.id.action_userFragment_to_newPostFragment)
                postViewModel.edit(feedItem as Post)
            }

            override fun onUser(userId: Int) = Unit

            override fun onPlayPause(feedItem: FeedItem) {
                if (feedItem.attachment?.type == AttachmentType.AUDIO) {
                    feedItem.attachment?.url?.let { }
                }
            }


            override fun onVideo(url: String) {
                findNavController().navigate(
                    R.id.action_userFragment_to_videoFragment,
                    bundleOf(
                        VideoFragment.URL to url
                    )
                )
            }

            override fun onImage(url: String) {
                findNavController().navigate(
                    R.id.action_userFragment_to_imageFragment,
                    bundleOf(
                        ImageFragment.URL to url
                    )
                )
            }
        })
        binding.listContainer.adapter = adapter

//        lifecycleScope.launchWhenCreated {
//            userViewModel.data.collectLatest {
//                adapter.submitData(it)
//            }
//        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                userViewModel.getWall(id)
            }
        }

        userViewModel.userDataState.observe(viewLifecycleOwner) { state ->
            binding.swiperefresh.isRefreshing = state.refreshing
            if (state.error) {
                Snackbar.make(binding.root, R.string.error_loading, Snackbar.LENGTH_LONG)
                    .show()
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                adapter.loadStateFlow.collectLatest { state ->
                    binding.swiperefresh.isRefreshing =
                        state.refresh is LoadState.Loading
                }
            }
        }

        binding.swiperefresh.setOnRefreshListener(adapter::refresh)


        return binding.root
    }

    companion object {
        const val USER_ID = "USER_ID"
    }
}