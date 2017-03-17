package com.boomcity.dankboard

import android.app.AlertDialog
import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.TextView

class SoundRecyclerAdapter(data: MutableList<SoundClip>, val tabPosition: Int) : RecyclerView.Adapter<SoundRecyclerAdapter.ViewHolder>() {
    private var mDataset: MutableList<SoundClip> = data
    var mp: MediaPlayer = MediaPlayer()
    var mediaState = "OK"

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        val mSoundClip = SoundClip(mDataset[position].Title,mDataset[position].AudioId, mDataset[position].Path)

        val text = holder!!.mView.findViewById(R.id.sound_clip_text_view) as TextView
        text.setText(mSoundClip.Title)

        val deleteButton = holder.mView.findViewById(R.id.delete_button) as ImageButton
        deleteButton.setOnClickListener {
            if(tabPosition != 0) {
                DataService.removeSoundClipFromTab(this,mSoundClip, tabPosition)
            }
            else {
                val deleteConfirmation = AlertDialog.Builder(holder.mView.context, R.style.DankAlertDialogStyle)
                deleteConfirmation.setTitle("This will permanently remove the sound clip from the app. Continue? ")
                deleteConfirmation.setNegativeButton(R.string.dialog_cancel, { dialog, which ->
                    dialog.dismiss()
                })
                deleteConfirmation.setPositiveButton(R.string.dialog_yes, {dialog, which ->
                    DataService.removeSoundClipFromApp(mSoundClip)
                    dialog.dismiss()
                })
                deleteConfirmation.show()
            }
        }

        val playButton = holder.mView.findViewById(R.id.play_button) as ImageButton


        playButton.setOnClickListener {
            if(mediaState == "OK" && mp.isPlaying) {
                mp.stop()
                playButton.setImageResource(R.drawable.ic_playbutton)
                mp.release()
                mediaState = "RELEASED"
            }
            else {
                if (mSoundClip.Path != null) {
                    try {
                        mp = MediaPlayer.create(holder.mView.context, Uri.parse(mSoundClip.Path))
                    } catch (ex: Exception) {
                        text.setText(R.string.invalid_path)
                    }
                } else {
                    mp = MediaPlayer.create(holder.mView.context, mSoundClip.AudioId)
                }

                mp.setOnCompletionListener {
                    playButton.setImageResource(R.drawable.ic_playbutton)
                    mp.release()
                    mediaState = "RELEASED"
                }

                mp.start()
                playButton.setImageResource(android.R.drawable.ic_media_pause)
                mediaState = "OK"
            }
        }

        val favoritesExists = holder.mView.findViewById(R.id.favorite_button)
        if (favoritesExists != null) {
            val addToFavoriteButton = holder.mView.findViewById(R.id.favorite_button) as ImageButton
            addToFavoriteButton.setTag(mDataset[position].AudioId)
            addToFavoriteButton.setOnClickListener {
                showTabSelectionDialog(holder.mView.context, mSoundClip)
            }
        }
    }

    fun showTabSelectionDialog(context: Context, soundClip: SoundClip) {
        val builder = AlertDialog.Builder(context, R.style.DankAlertDialogStyle)
        builder.setTitle("Add dank sound to which tab?")

        var arrayAdapter = ArrayAdapter<String>(context, android.R.layout.select_dialog_item)

        for (tab in DataService.getTabsData().tabsList!!) {
            if(tab.position != 0) {
                arrayAdapter.add(tab.name)
            }
        }

        builder.setNegativeButton("Cancel", { dialog, which ->
            dialog.dismiss()
        })

        builder.setAdapter(arrayAdapter, { dialog, which ->

            if (DataService.getTabsData().getTab(which + 1)!!.soundClips.any { clip -> clip.AudioId == soundClip.AudioId }) {
                val errorBuilder = AlertDialog.Builder(context, R.style.DankAlertDialogStyle)
                errorBuilder.setTitle("This dank sound clip is already in that favorite tab bruh!")
                errorBuilder.setNegativeButton(R.string.dialog_aight, { dialog, which ->
                    dialog.dismiss()
                })
                errorBuilder.show()
            }
            else {
                DataService.addClipToFavoriteTab(soundClip, which + 1)
            }
        })

        builder.show()
    }

    override fun getItemCount(): Int {
        return mDataset.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {

        var soundClipFragmentId: Int

        if (tabPosition > 0) {
            soundClipFragmentId = R.layout.fragment_sound_clip
        }
        else {
            soundClipFragmentId = R.layout.fragment_sound_clip_all
        }

        val v = LayoutInflater.from(parent!!.getContext()).inflate(soundClipFragmentId, parent, false)
        val vh = ViewHolder(v)
        return vh
    }

    class ViewHolder(var mView: View) : RecyclerView.ViewHolder(mView)

}
