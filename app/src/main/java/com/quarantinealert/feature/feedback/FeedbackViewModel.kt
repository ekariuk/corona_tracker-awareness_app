package com.quarantinealert.feature.feedback

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.quarantinealert.R
import com.quarantinealert.exception.NetworkException
import com.quarantinealert.feature.base.BaseViewModel
import com.quarantinealert.util.NetworkUtil.isOnline
import com.quarantinealert.util.getErrorMessage
import com.quarantinealert.util.getCountryCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FeedbackViewModel(
    application: Application,
    private val firebaseFirestore: FirebaseFirestore
) : BaseViewModel(application) {

    var showLoadingLiveData = MutableLiveData<Boolean>()
    var sendFeedbackSuccessfulLiveData = MutableLiveData<String>()
    var sendFeedbackFailedLiveData = MutableLiveData<String>()

    fun sendFeedback(email: String, message: String) {
        showLoadingLiveData.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (isOnline(context)) {
                    val feedback: MutableMap<String, String> = HashMap()
                    feedback["email"] = email
                    feedback["message"] = message
                    feedback["country"] =
                        getCountryCode(context)

                    firebaseFirestore.collection("feedback")
                        .add(feedback)
                        .addOnSuccessListener {
                            onSendFeedbackSuccessful()
                        }
                        .addOnFailureListener {
                            val errorMessage = getErrorMessage(context, it)
                            onSendFeedbackFailed(errorMessage)
                            Log.e(
                                javaClass.simpleName,
                                "Error adding document",
                                it
                            )
                        }
                } else {
                    throw NetworkException(context.getString(R.string.network_error_message))
                }
            } catch (ex: Exception) {
                val errorMessage = getErrorMessage(context, ex)
                launch(Dispatchers.Main) {
                    showLoadingLiveData.postValue(false)
                    sendFeedbackFailedLiveData.postValue(errorMessage)
                }
            }
        }
    }

    private fun onSendFeedbackSuccessful() {
        viewModelScope.launch(Dispatchers.Main) {
            showLoadingLiveData.postValue(false)
            sendFeedbackSuccessfulLiveData.postValue(context.getString(R.string.feedback_send_successful))
        }
    }

    private fun onSendFeedbackFailed(message: String) {
        viewModelScope.launch(Dispatchers.Main) {
            showLoadingLiveData.postValue(false)
            sendFeedbackFailedLiveData.postValue(message)
        }
    }
}