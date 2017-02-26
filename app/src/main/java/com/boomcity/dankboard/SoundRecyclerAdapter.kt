package com.boomcity.dankboard

import android.app.AlertDialog
import android.content.Context
import android.media.MediaPlayer
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.TextView


class SoundRecyclerAdapter(data: MutableList<SoundClip>) : RecyclerView.Adapter<SoundRecyclerAdapter.ViewHolder>() {
    private var mDataset: MutableList<SoundClip> = data

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {

        var mSoundClip = SoundClip(mDataset[position].Title,mDataset[position].AudioId)

        var text = holder!!.mView.findViewById(R.id.sound_clip_text_view) as TextView
        text.setText(mSoundClip.Title)

        var playButton = holder!!.mView.findViewById(R.id.play_button) as ImageButton
        val mp = MediaPlayer.create(holder!!.mView.context, R.raw.test_sound)
        mp.setOnCompletionListener {
            playButton.setImageResource(android.R.drawable.ic_media_play)
        }

        playButton.setOnClickListener {
            if(mp.isPlaying) {
                mp.pause()
                playButton.setImageResource(android.R.drawable.ic_media_play)
            }
            else
            {
                mp.start()
                playButton.setImageResource(android.R.drawable.ic_media_pause)
            }
        }

        var addToFavoriteButton = holder.mView.findViewById(R.id.favorite_button) as ImageButton
        addToFavoriteButton.setTag(mDataset[position].AudioId)
        addToFavoriteButton.setOnClickListener {
            showTabSelectionDialog(holder.mView.context, mSoundClip)

        }
    }

    fun showTabSelectionDialog(context: Context, soundClip: SoundClip) {
        val builder = AlertDialog.Builder(context)
        builder.setIcon(android.R.drawable.ic_dialog_email)
        builder.setTitle("swag")

        var arrayAdapter = ArrayAdapter<String>(context, android.R.layout.select_dialog_item)
        arrayAdapter.add("Hardik")
        arrayAdapter.add("Archit")
        arrayAdapter.add("Jignesh")
        arrayAdapter.add("Umang")
        arrayAdapter.add("Gatti")

        builder.setNegativeButton("cancel", { dialog, which ->
            dialog.dismiss()
        })

        builder.setAdapter(arrayAdapter, { dialog, which ->

            //var tabName = arrayAdapter.getItem(which)
//            var builderInner = AlertDialog.Builder(context)
//            builderInner.setMessage(strName)
//            builderInner.setTitle("Your Selected Item is")
//            builderInner.setPositiveButton("Ok", { dialog, which ->
//                dialog.dismiss()
//                //val lw = (dialog as AlertDialog).listView
//                //checkedItem = lw.adapter.getItem(lw.checkedItemPosition)
//            })
//            builderInner.show()

            Utils.addClipToFavoriteTab(soundClip, which + 1)



            //this.mDataset = testData
            //this.notifyDataSetChanged()
        })
        builder.show()
    }

    fun addSoundClip(soundClip: SoundClip) {
        mDataset.add(soundClip)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return mDataset.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        var v = LayoutInflater.from(parent!!.getContext()).inflate(R.layout.fragment_sound_clip, parent, false)

        var vh = ViewHolder(v)
        return vh
    }

    class ViewHolder(var mView: View) : RecyclerView.ViewHolder(mView)
}
