package ru.netology.nmedia.ui.event

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.FeedAdapter
import ru.netology.nmedia.adapter.FeedOnInteractionListener
import ru.netology.nmedia.databinding.FragmentEventsBinding
import ru.netology.nmedia.dto.Event
import ru.netology.nmedia.dto.FeedItem
import ru.netology.nmedia.enumeration.AttachmentType
import ru.netology.nmedia.ui.ImageFragment
import ru.netology.nmedia.ui.MediaLifecycleObserver
import ru.netology.nmedia.ui.VideoFragment
import ru.netology.nmedia.ui.user.UserFragment
import ru.netology.nmedia.viewmodel.EventViewModel

class EventsFragment : Fragment() {

    private val eventViewModel by activityViewModels<EventViewModel>()
    private val mediaObserver = MediaLifecycleObserver()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentEventsBinding.inflate(inflater, container, false)

        val adapter = FeedAdapter(object : FeedOnInteractionListener {
            override fun onLike(feedItem: FeedItem) {
                eventViewModel.likeById(feedItem as Event)
            }

            override fun onRemove(id: Int) {
                eventViewModel.removeById(id)
            }

            override fun onEdit(feedItem: FeedItem) {
                findNavController().navigate(R.id.action_feedFragment_to_newEventFragment)
                eventViewModel.edit(feedItem as Event)
            }

            override fun onUser(userId: Int) {
                findNavController().navigate(
                    R.id.action_feedFragment_to_userFragment,
                    bundleOf(
                        UserFragment.USER_ID to userId
                    )
                )
            }

            override fun onPlayPause(feedItem: FeedItem) {
                if (feedItem.attachment?.type == AttachmentType.AUDIO) {
                    feedItem.attachment?.url?.let { mediaObserver.playPause(it) }
                }
            }

            override fun onVideo(url: String) {
                findNavController().navigate(
                    R.id.action_feedFragment_to_videoFragment,
                    bundleOf(
                        VideoFragment.URL to url
                    )
                )
            }

            override fun onImage(url: String) {
                findNavController().navigate(
                    R.id.action_feedFragment_to_imageFragment,
                    bundleOf(
                        ImageFragment.URL to url
                    )
                )
            }
        })

        binding.listEvents.adapter = adapter

        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                if (positionStart == 0) {
                    binding.listEvents.smoothScrollToPosition(0)
                }
            }
        })

        lifecycleScope.launch {
            eventViewModel.data.collectLatest { data ->
                adapter.submitData(data)
            }
        }

        eventViewModel.dataState.observe(viewLifecycleOwner) { state ->
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
}