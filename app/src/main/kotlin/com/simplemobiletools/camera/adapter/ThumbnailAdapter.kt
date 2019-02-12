package com.simplemobiletools.camera.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.simplemobiletools.camera.R
import androidx.recyclerview.widget.RecyclerView
import com.simplemobiletools.camera.interfaces.FilterListFragmentListener
import com.zomato.photofilters.utils.ThumbnailItem
import kotlinx.android.synthetic.main.thumbnail_list_item.view.*

class ThumbnailAdapter(
<<<<<<< HEAD
    private val context: Context,
    private val thumbnailItemList: List<ThumbnailItem>,
    private val listener: FilterListFragmentListener
=======
        private val context: Context,
        private val thumbnailItemList: List<ThumbnailItem>,
        private val listener: FilterListFragmentListener
>>>>>>> f492d52d5beb9f7411c618f910d25d6ef53168b3
) : RecyclerView.Adapter<ThumbnailAdapter.MyViewholder>() {

    private var selectedIndex = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewholder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.thumbnail_list_item, parent, false)
        return MyViewholder(itemView)
    }

    override fun getItemCount(): Int {
        return thumbnailItemList.size
    }

    override fun onBindViewHolder(holder: MyViewholder, position: Int) {
        val thumbnailItem: ThumbnailItem = thumbnailItemList[position]
            holder.thumbNail.setImageBitmap(thumbnailItem.image)
            holder.thumbNail.setOnClickListener {
                listener.onFilterSelected(thumbnailItem.filter)
                selectedIndex = position
                notifyDataSetChanged()
            }
    }

    class MyViewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var thumbNail: ImageView
        var filterName: TextView
<<<<<<< HEAD
        init {
=======
        init{
>>>>>>> f492d52d5beb9f7411c618f910d25d6ef53168b3
            thumbNail = itemView.thumbnail
            filterName = itemView.filter_name
        }
    }
}
