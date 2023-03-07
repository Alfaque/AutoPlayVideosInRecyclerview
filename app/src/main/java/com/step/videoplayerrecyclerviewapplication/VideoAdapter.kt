package com.step.videoplayerrecyclerviewapplication

//import androidx.lifecycle.LifecycleObserver
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import com.step.videoplayerrecyclerviewapplication.databinding.ItemVideoLayoutBinding

class VideoAdapter(
    var context: Context,
    var list: ArrayList<String>,
    var onHolderCreated: OnHolderCreated
) :
    RecyclerView.Adapter<VideoAdapter.VideoViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        return VideoViewHolder(
            ItemVideoLayoutBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            ),
        )
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {

        val url = list[position]

        holder.bind(context, url, position, onHolderCreated)



        onHolderCreated.onHolderCreatedListener(HolderItem(position, holder))


    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

        val manager = recyclerView.layoutManager;

        if (manager is LinearLayoutManager && itemCount > 0) {
            val llm = manager
            recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    val visiblePosition = llm.findFirstCompletelyVisibleItemPosition();


                    if (visiblePosition > -1) {
                        var v = llm.findViewByPosition(visiblePosition);
                        //do something
                        Log.d("TAG", "onScrolled: visiblePosition:$visiblePosition")
                        onHolderCreated.onitemScrolled(position = visiblePosition)
                    }


                }
            })

        }

    }


    class VideoViewHolder(var binding: ItemVideoLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun getVideoView(): PlayerView {
            return binding.videoExoplayer
        }


        var url = ""
        var player: ExoPlayer? = null
        fun bind(context: Context, url: String, position: Int, onHolderCreated: OnHolderCreated) {

            this.url = url

            val currentItem = 0
            val playbackPosition = 0L

            val trackSelector = DefaultTrackSelector(context)

            player = ExoPlayer.Builder(context)
                .setTrackSelector(trackSelector)
                .build()
                .also { exoPlayer ->

                    val mediaItem = MediaItem.fromUri(url)
                    exoPlayer.setMediaItem(mediaItem)
                    exoPlayer.playWhenReady = false

                    binding.videoExoplayer.player = exoPlayer


                    exoPlayer.seekTo(currentItem, playbackPosition)
                    exoPlayer.addListener(object : Player.Listener {
                        override fun onPlaybackStateChanged(playbackState: Int) {
                            val stateString: String = when (playbackState) {
                                ExoPlayer.STATE_IDLE -> "ExoPlayer.STATE_IDLE      -"
                                ExoPlayer.STATE_BUFFERING -> "ExoPlayer.STATE_BUFFERING -"
                                ExoPlayer.STATE_READY -> "ExoPlayer.STATE_READY     -"
                                ExoPlayer.STATE_ENDED -> "ExoPlayer.STATE_ENDED     -"
                                else -> "UNKNOWN_STATE             -"
                            }
                            Log.d("TAG", "changed state to $stateString")
                        }

                    })
                    exoPlayer.prepare()

                }


            var controlView =
                binding.videoExoplayer.findViewById<CoordinatorLayout>(R.id.custom_controller)
            var retryButton = controlView.findViewById<ImageButton>(R.id.retry)
            var rewind = controlView.findViewById<ImageView>(R.id.exo_rewind)
            var forward = controlView.findViewById<ImageView>(R.id.exo_fastforword)
            var exo_play =
                controlView.findViewById<ImageView>(com.google.android.exoplayer2.R.id.exo_play)

            var centerControl = controlView.findViewById<LinearLayout>(R.id.center_control)


            exo_play.setOnClickListener {
                onHolderCreated.onItemPlayed(position)
                Log.d("TAG", "bind: ")
            }

            retryButton.setOnClickListener {
                centerControl.visibility = View.VISIBLE
                retryButton.visibility = View.GONE

                player!!.seekTo(0)
                player!!.playWhenReady = true
            }

            forward.setOnClickListener {
                if (player != null) {
                    val current = player!!.currentPosition
                    val duration = player!!.duration
                    if (current < duration) {
                        player!!.seekTo(player!!.contentPosition + 15000)
                    }
                }
            }

            rewind.setOnClickListener {
                if (player != null) {
                    val current = player!!.currentPosition
                    if (current > 0) {
                        player!!.seekTo(player!!.contentPosition - 15000)
                    } else {
                        player!!.seekTo(0)
                    }
                }
            }


        }

        fun playVideo() {
            player?.play()
        }

        fun pauseVideo() {
            player?.pause()

        }


    }


    interface OnHolderCreated {
        fun onHolderCreatedListener(holderItem: HolderItem)
        fun onitemScrolled(position: Int)
        fun onItemPlayed(position: Int)
    }

    data class HolderItem(
        var position: Int,
        var holder: VideoViewHolder
    )


}