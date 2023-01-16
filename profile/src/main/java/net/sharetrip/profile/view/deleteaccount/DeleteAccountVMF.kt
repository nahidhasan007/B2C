package net.sharetrip.profile.view.deleteaccount

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.IllegalArgumentException

class DeleteAccountVMF(private val repo: DeleteAccountRepo, private val token: String) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DeleteAccountViewModel::class.java))
            return DeleteAccountViewModel(repo, token) as T

        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}