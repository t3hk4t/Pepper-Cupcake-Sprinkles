package com.softbankrobotics.interactionsample.utils

import android.media.MediaPlayer
import com.aldebaran.qi.Consumer
import com.aldebaran.qi.Future
import com.aldebaran.qi.sdk.QiContext
import com.aldebaran.qi.sdk.`object`.conversation.Say
import com.aldebaran.qi.sdk.`object`.human.Human
import com.aldebaran.qi.sdk.`object`.humanawareness.EngageHuman
import com.aldebaran.qi.sdk.`object`.humanawareness.HumanAwareness
import com.aldebaran.qi.sdk.builder.EngageHumanBuilder
import com.aldebaran.qi.sdk.builder.SayBuilder
import com.softbankrobotics.qisdktutorials.R
import java.util.*

/*
* A helper that engages the recommended human, and can notify on "interacting" state.
*
* This wraps standard practice for creating a "high priority" engagement with the human
* recommended by QiSDK's HumanAwareness (i.e. the human standing in the right position that
* looks interested in the robot.
*
* If the engaged human leaves but another one is recommended, that one will be engaged.
*
* An outside observer can add an "onInteracting" callback to be notified the robot is interacting,
* meaning that either there is an engaged human, or there was one very recently (the goal is to
* consider a quick sequence of humans as a "single interaction", to filter out noise caused by
* some humans being temporarily not detected - we don't want to reset the interaction if that
* happens).
*
* The timeout for how long the robot needs to be alone before the interaction is considered
* "finished" is passed to the constructor.
*/
class HumanEngager(
    private val qiContext: QiContext, // Inner state, from which state is calculated
    private val unengageTimeMs: Int
) {
    private val awareness: HumanAwareness

    // Inner working of engaging system
    private var engaging = false
    private var mediaPlayer: MediaPlayer? = null
    private var queuedRecommendedHuman: Human? = null
    private var disengageTimerTask: TimerTask? = null
    var onInteracting: Consumer<Boolean>? = null

    /* Internal; notify listener of "isInteracting" state.
     */
    private fun setIsInteracting(isInteracting: Boolean) {
        if (onInteracting != null) {
            try {
                onInteracting!!.consume(isInteracting)
            } catch (throwable: Throwable) {
                throwable.printStackTrace()
            }
        }
    }

    /* Internal; processes a recommended candidate for engagement by creating an Engage action.
     */
    private fun tryToEngageHuman(human: Human?) {
        val say: Say = SayBuilder.with(qiContext)
            .withText("Hello!")
            .build()

        if (human != null) {
            engaging = true
            //Log.i(TAG,"Building engage");
            val engage: EngageHuman = EngageHumanBuilder.with(qiContext).withHuman(human).build()
            engage.addOnHumanIsEngagedListener(EngageHuman.OnHumanIsEngagedListener {
                say.run()
                setIsInteracting(
                    true
                )
            })
            engage.addOnHumanIsDisengagingListener {
                mediaPlayer = MediaPlayer.create(qiContext, R.raw.bye)
                mediaPlayer?.start()
            }
            engage.async().run().thenConsume(Consumer<Future<Void?>> { fut: Future<Void?>? ->
                engaging = false
                // Try again with a new human
                tryToEngageHuman(queuedRecommendedHuman)
                queuedRecommendedHuman = null
                // This listener could never be called any more, but leaving it risks a memory leak
                engage.removeAllOnHumanIsEngagedListeners()
            })
        } else {
            // No human to engage - BUT we give a timeout
            disengageTimerTask = object : TimerTask() {
                override fun run() {
                    setIsInteracting(false)
                }
            }
            Timer("disengage").schedule(disengageTimerTask, unengageTimeMs.toLong())
        }
    }

    /* Start tracking and engaging humans.
     */
    fun start() {
        awareness.async().addOnRecommendedHumanToEngageChangedListener { recommendedHuman: Human? ->
            if (!engaging) {
                tryToEngageHuman(recommendedHuman)
            } else {
                queuedRecommendedHuman = recommendedHuman
            }
        }
        awareness.async().getRecommendedHumanToEngage()
            .andThenConsume(Consumer<Human> { human: Human? ->
                tryToEngageHuman(
                    human
                )
            })
    }

    /* Start tracking and engaging humans.
     */
    fun stop() {
        awareness.removeAllOnRecommendedHumanToEngageChangedListeners()
        if (disengageTimerTask != null) {
            disengageTimerTask!!.cancel()
        }
    } // Internal API

    init {
        awareness = qiContext.humanAwareness
    }
}