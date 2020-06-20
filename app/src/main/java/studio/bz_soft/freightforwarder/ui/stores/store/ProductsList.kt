package studio.bz_soft.freightforwarder.ui.stores.store

class ProductsList {
    companion object Singleton {
        private var products = mutableListOf<Int>()

        fun addProductsToList(product: Int) {
            products.add(product)
        }

        fun getProductsFromList(): List<Int>? = products

        fun clearProductsList() {
            products.clear()
        }
    }
}