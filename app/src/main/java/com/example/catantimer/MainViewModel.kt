package com.example.catantimer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.GlobalScope
import java.util.*

class MainViewModel : ViewModel() {

    private var value: Long = 120
    private val mElapsedTime = MutableLiveData<Long?>()
    private val myRef = Firebase.database.reference
    private val timeStamp: MutableMap<String, String> = ServerValue.TIMESTAMP
    private val mTimeStamp = Time(timeStamp)
    private val timer = Timer()
    var job: Job? = null


    init {

        job = GlobalScope.launch (Dispatchers.Default) {
            timer.scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    mElapsedTime.postValue(value)
                    value -= 1
                }
            }, 0, 1000)
        }

        GlobalScope.launch (Dispatchers.Default){
                myRef.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        cancelTick()
                        value = 120
                        mElapsedTime.postValue(value)
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }
                })
            }


        if (value > 0) {
            joinTick()

//            timer.scheduleAtFixedRate(object : TimerTask() {
//                override fun run() {
//                    mElapsedTime.postValue(value)
//                    value -= 1
//                }
//            }, ONE_SECOND.toLong(), ONE_SECOND.toLong())
        }
    }

    private fun cancelTick() {
        GlobalScope.launch (Dispatchers.IO) {
            job?.cancelAndJoin()
        }
    }

    private fun joinTick() {
        GlobalScope.launch (Dispatchers.IO) {
            job?.join()
        }
    }


    fun updateTrigger() {
        myRef.child("time").setValue(mTimeStamp)
    }

    fun getElapsedTime(): LiveData<Long?> {
        return mElapsedTime
    }

    companion object {
        private const val ONE_SECOND = 4000
    }
}