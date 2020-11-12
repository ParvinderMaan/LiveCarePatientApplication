package com.app.patlivecare

import android.content.Context
import com.app.patlivecare.base.SingletonHolder
import com.app.patlivecare.helper.SharedPrefHelper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map


/**
 * You can use like this .
 * val channel = EventBus().asChannel<ItemChangeAction>()
 * launch (UI){
 *   for(action in channel){
 *     // You can use item
 *     action.item
 *   }
 * }
 */

class LiveCareEventBus constructor(var application: LiveCareApplication){
    val bus: BroadcastChannel<Any> = ConflatedBroadcastChannel<Any>()

//    @ExperimentalCoroutinesApi
//    inline fun <reified T> asChannel(): ReceiveChannel<T> {
//        return bus.openSubscription().filter { it is T }.map { it as T }
//    }


    @ExperimentalCoroutinesApi
    final inline fun <reified T> asChannel() = bus.asFlow().drop(1).filter { it is T }.map { it as T }

    suspend fun send(o: Any) {
        bus.send(o)
    }

    companion object : SingletonHolder<LiveCareEventBus, LiveCareApplication>(::LiveCareEventBus)


}