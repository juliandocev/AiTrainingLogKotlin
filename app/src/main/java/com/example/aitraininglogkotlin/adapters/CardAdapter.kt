package com.example.aitraininglogkotlin.adapters



import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.aitraininglogkotlin.CARD_ID_EXTRA
import com.example.aitraininglogkotlin.Card
import com.example.aitraininglogkotlin.databinding.CardCellBinding
import com.example.aitraininglogkotlin.ui.DetectionActivity

class CardAdapter(
    private val cardsList: List<Card>,
    private val context: Activity
    ): RecyclerView.Adapter<CardAdapter.CardViewHolder>() {


    inner class CardViewHolder(var binding: CardCellBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val layoutInflater =  LayoutInflater.from(parent.context)
        val view = CardCellBinding.inflate(layoutInflater,parent, false)

        return CardViewHolder(view)
    }

    override fun getItemCount(): Int = cardsList.size

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val current  = cardsList[position]

        holder.binding.ivCardImage.setImageResource(current.icon)
        holder.binding.tvCardTitle.text = current.title

        holder.binding.cvCard.setOnClickListener {
            val intent =  Intent(context, DetectionActivity::class.java)
            intent.putExtra(CARD_ID_EXTRA, current.id)
            context.startActivity(intent)
        }


    }
}