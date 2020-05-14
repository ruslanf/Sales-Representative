package studio.bz_soft.freightforwarder.ui.stores

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.cell_trade_point.view.*
import studio.bz_soft.freightforwarder.R
import studio.bz_soft.freightforwarder.data.models.StorePointModel
import studio.bz_soft.freightforwarder.root.delegated.AdapterDelegateInterface
import studio.bz_soft.freightforwarder.root.delegated.BaseHolder

sealed class TradePointElement {
    data class TradePointItem(val tradePoint: StorePointModel): TradePointElement()
}

class SimulatorItemHolder(v: View, val onClick: (StorePointModel) -> Unit): BaseHolder<TradePointElement>(v) {

    override fun bindModel(item: TradePointElement) {
        super.bindModel(item)
        when (item) {
            is TradePointElement.TradePointItem -> itemView.apply {
                typeTV.text = item.tradePoint.type
                tradePointNameTV.text = item.tradePoint.storePoint
                setOnClickListener { onClick(item.tradePoint) }
            }
        }
    }
}

class TradePointItemDelegate(private val onClick: (StorePointModel) -> Unit):
    AdapterDelegateInterface<TradePointElement> {

    override fun isForViewType(items: List<TradePointElement>, position: Int): Boolean {
        return items[position] is TradePointElement.TradePointItem
    }

    override fun createViewHolder(parent: ViewGroup): BaseHolder<TradePointElement> {
        return SimulatorItemHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.cell_trade_point, parent, false),
            onClick
        )
    }

    override fun bindViewHolder(items: List<TradePointElement>, position: Int, holder: BaseHolder<TradePointElement>) {
        holder.bindModel(items[position])
    }
}