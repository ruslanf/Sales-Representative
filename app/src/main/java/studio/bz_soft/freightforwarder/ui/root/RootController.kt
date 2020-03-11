package studio.bz_soft.freightforwarder.ui.root

import studio.bz_soft.freightforwarder.data.repository.RepositoryInterface

class RootController(
    private val repository: RepositoryInterface
) : RootInterface {

    override fun deleteToken() {
        repository.deleteToken()
    }

    override fun deleteUserId() {
        repository.deleteUserId()
    }
}