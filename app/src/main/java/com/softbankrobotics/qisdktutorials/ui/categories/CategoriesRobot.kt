/*
 * Copyright (C) 2018 Softbank Robotics Europe
 * See COPYING for the license
 */

package com.softbankrobotics.qisdktutorials.ui.categories

import androidx.annotation.StringRes
import android.util.Log

import com.aldebaran.qi.Future
import com.aldebaran.qi.sdk.QiContext
import com.aldebaran.qi.sdk.QiSDK
import android.media.MediaPlayer
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks
import com.aldebaran.qi.sdk.`object`.conversation.QiChatVariable
import com.aldebaran.qi.sdk.`object`.conversation.QiChatbot
import com.aldebaran.qi.sdk.`object`.conversation.Say
import com.aldebaran.qi.sdk.`object`.conversation.TopicStatus
import com.aldebaran.qi.sdk.`object`.human.Human
import com.aldebaran.qi.sdk.`object`.humanawareness.EngageHuman
import com.aldebaran.qi.sdk.`object`.humanawareness.HumanAwareness
import com.aldebaran.qi.sdk.builder.*
import com.softbankrobotics.qisdktutorials.R
import com.softbankrobotics.qisdktutorials.model.data.Tutorial
import com.softbankrobotics.qisdktutorials.model.data.TutorialCategory
import com.softbankrobotics.qisdktutorials.model.data.TutorialLevel
import kotlin.coroutines.*
import com.softbankrobotics.interactionsample.utils.HumanEngager
import java.util.*
import com.aldebaran.qi.sdk.builder.SayBuilder





private const val TAG = "CategoriesRobot"

private const val LEVEL_BASIC = "basic"
private const val LEVEL_ADVANCED = "advanced"

/**
 * The robot for the tutorial categories.
 */
internal class CategoriesRobot(private val presenter: CategoriesContract.Presenter) : CategoriesContract.Robot, RobotLifecycleCallbacks {
    private var talkTopicStatus: TopicStatus? = null
    private var moveTopicStatus: TopicStatus? = null
    private var smartTopicStatus: TopicStatus? = null
    private var qiChatbot: QiChatbot? = null
    private var chatFuture: Future<Void>? = null
    private var something = false;
    private var selectedCategory = TutorialCategory.TALK
    private var say1: Future<Say>? = null
    private var say2: Future<Say>? = null
    private var selectedLevel = TutorialLevel.BASIC
    private var levelVariable: QiChatVariable? = null
    private var mediaPlayer: MediaPlayer? = null
    private var isFirstIntro = true
    private var engaging = false
    private var awareness: HumanAwareness? = null
    private var qiContext: QiContext? = null
    private var humanengager: HumanEngager? = null

    override fun register(activity: CategoriesActivity) {
        QiSDK.register(activity, this)
    }

    override fun unregister(activity: CategoriesActivity) {
        QiSDK.unregister(activity, this)
    }

    override fun stopDiscussion(tutorial: Tutorial) {
        val chatFuture = chatFuture
        if (chatFuture != null) {
            chatFuture.thenConsume {
                if (it.isCancelled) {
                    presenter.goToTutorial(tutorial)
                }
            }
            chatFuture.requestCancellation()
        } else {
            presenter.goToTutorial(tutorial)
        }
        this.chatFuture = chatFuture

    }

    override fun selectTopic(category: TutorialCategory) {
        selectedCategory = category

        val topicsAreReady = talkTopicStatus != null && moveTopicStatus != null && smartTopicStatus != null
        if (topicsAreReady) {
            enableTopic(category)
        }
    }

    override fun selectLevel(level: TutorialLevel) {
        selectedLevel = level

        if (levelVariable != null) {
            enableLevel(level)
        }
    }

    override fun onRobotFocusGained(qiContext: QiContext) {
        this.qiContext = qiContext
        awareness = qiContext.humanAwareness


        humanengager = HumanEngager(qiContext, 5000)
        humanengager!!.start()

        /*say1 = SayBuilder.with(qiContext)
            .withText("Hello")
            .buildAsync()

        say2 = SayBuilder.with(qiContext)
            .withText("Goodbye")
            .buildAsync()

        awareness!!.async().addOnRecommendedHumanToEngageChangedListener { recommendedHuman: Human? ->
            if (!engaging) {
                tryToEngageHuman(recommendedHuman, say1!!, say2!!)
            }
        }
*/
        /*SayBuilder.with(qiContext)
                .withText(qiContext.getString(introSentenceRes()))
                .build()
                .run()

        val commonTopic = TopicBuilder.with(qiContext)
                .withResource(R.raw.common)
                .build()

        val talkTopic = TopicBuilder.with(qiContext)
                .withResource(R.raw.talk_tutorials)
                .build()

        val moveTopic = TopicBuilder.with(qiContext)
                .withResource(R.raw.move_tutorials)
                .build()

        val smartTopic = TopicBuilder.with(qiContext)
                .withResource(R.raw.smart_tutorials)
                .build()

        val qiChatbot = QiChatbotBuilder.with(qiContext)
                .withTopics(listOf(commonTopic, talkTopic, moveTopic, smartTopic))
                .build()
                .also { this.qiChatbot = it }

        val chat = ChatBuilder.with(qiContext)
                .withChatbot(qiChatbot)
                .build()

        talkTopicStatus = qiChatbot.topicStatus(talkTopic)
        moveTopicStatus = qiChatbot.topicStatus(moveTopic)
        smartTopicStatus = qiChatbot.topicStatus(smartTopic)

        levelVariable = qiChatbot.variable("level")

        enableLevel(selectedLevel)
        enableTopic(selectedCategory)

        qiChatbot.addOnBookmarkReachedListener {
            when (it.name) {
                "talk" -> {
                    presenter.loadTutorials(TutorialCategory.TALK)
                    selectTopic(TutorialCategory.TALK)
                }
                "move" -> {
                    presenter.loadTutorials(TutorialCategory.MOVE)
                    selectTopic(TutorialCategory.MOVE)
                }
                "smart" -> {
                    presenter.loadTutorials(TutorialCategory.SMART)
                    selectTopic(TutorialCategory.SMART)
                }
                "basic" -> {
                    presenter.loadTutorials(TutorialLevel.BASIC)
                    selectLevel(TutorialLevel.BASIC)
                }
                "advanced" -> {
                    presenter.loadTutorials(TutorialLevel.ADVANCED)
                    selectLevel(TutorialLevel.ADVANCED)
                }
            }
        }

        qiChatbot.addOnEndedListener { presenter.goToTutorialForQiChatbotId(it) }
        this.qiChatbot = qiChatbot
        chatFuture = chat.async().run()*/
    }

    override fun onRobotFocusLost() {
        this.qiContext = null
        qiChatbot?.let {
            it.removeAllOnBookmarkReachedListeners()
            it.removeAllOnEndedListeners()
            qiChatbot = null
        }
        chatFuture = null

        talkTopicStatus = null
        moveTopicStatus = null
        smartTopicStatus = null
    }

    override fun onRobotFocusRefused(reason: String) {
        Log.i(TAG, "onRobotFocusRefused: $reason")
    }

    /**
     * Enable the topic corresponding to the specified tutorial category.
     * @param category the tutorial category
     */

    private fun tryToEngageHuman(human: Human?, say1 : Future<Say>, say2 : Future<Say>) {
        if (human != null) {
            engaging = true;
            val engage: EngageHuman = EngageHumanBuilder.with(qiContext).withHuman(human).build()
            engage.addOnHumanIsEngagedListener {
                mediaPlayer = MediaPlayer.create(qiContext, R.raw.hello)
                mediaPlayer?.start()
                val say: Say = say1.get()
                say.run()
            }

            engage.addOnHumanIsDisengagingListener {
                mediaPlayer = MediaPlayer.create(qiContext, R.raw.bye)
                mediaPlayer?.start()
                val say: Say = say2.get()
                say.run()
                engaging = false;
            }


        }else{
            engaging = false;
        }
    }


    private fun enableTopic(category: TutorialCategory) {
        val talkFuture = talkTopicStatus?.async()?.setEnabled(false)
        val moveFuture = moveTopicStatus?.async()?.setEnabled(false)
        val smartFuture = smartTopicStatus?.async()?.setEnabled(false)

        Future.waitAll(talkFuture, moveFuture, smartFuture)
                .andThenConsume {
                    when (category) {
                        TutorialCategory.TALK -> talkTopicStatus?.enabled = true
                        TutorialCategory.MOVE -> moveTopicStatus?.enabled = true
                        TutorialCategory.SMART -> smartTopicStatus?.enabled = true
                    }
                }
    }

    /**
     * Enable the specified level.
     * @param level the tutorial level
     */
    private fun enableLevel(level: TutorialLevel) {
        val value = levelValueFromLevel(level)
        levelVariable?.async()?.setValue(value)
    }

    /**
     * Provides the level variable value from the specified tutorial level.
     * @param level the tutorial level
     * @return The level variable value.
     */
    private fun levelValueFromLevel(level: TutorialLevel): String {
        return when (level) {
            TutorialLevel.BASIC -> LEVEL_BASIC
            TutorialLevel.ADVANCED -> LEVEL_ADVANCED
        }
    }

    @StringRes
    private fun introSentenceRes(): Int {
        return if (isFirstIntro) R.string.categories_intro_sentence else R.string.categories_intro_sentence_variant
    }

}
