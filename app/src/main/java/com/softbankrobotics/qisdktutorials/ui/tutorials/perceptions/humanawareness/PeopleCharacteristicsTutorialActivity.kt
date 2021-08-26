/*
 * Copyright (C) 2018 Softbank Robotics Europe
 * See COPYING for the license
 */

package com.softbankrobotics.qisdktutorials.ui.tutorials.perceptions.humanawareness

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import android.util.Log

import com.aldebaran.qi.sdk.QiContext
import com.aldebaran.qi.sdk.QiSDK
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks
import com.aldebaran.qi.sdk.builder.SayBuilder
import com.aldebaran.qi.sdk.`object`.actuation.Frame
import com.aldebaran.qi.sdk.`object`.human.Human
import com.aldebaran.qi.sdk.`object`.humanawareness.HumanAwareness
import com.softbankrobotics.qisdktutorials.R
import com.softbankrobotics.qisdktutorials.ui.conversation.ConversationBinder
import com.softbankrobotics.qisdktutorials.ui.tutorials.TutorialActivity
import kotlinx.android.synthetic.main.activity_people_characteristics_tutorial.*

import kotlin.math.sqrt

private const val TAG = "CharacteristicsActivity"


/**
 * The activity for the People characteristics tutorial.
 */
class PeopleCharacteristicsTutorialActivity : TutorialActivity(), RobotLifecycleCallbacks {

    private lateinit var conversationBinder: ConversationBinder

    private lateinit var humanInfoAdapter: HumanInfoAdapter

    // Store the HumanAwareness service.
    private var humanAwareness: HumanAwareness? = null
    // The QiContext provided by the QiSDK.
    private var qiContext: QiContext? = null

    private val humanInfoList: MutableList<HumanInfo> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layoutManager = LinearLayoutManager(this)
        recycler_view.layoutManager = layoutManager
        recycler_view.addItemDecoration(DividerItemDecoration(this, layoutManager.orientation))
        humanInfoAdapter = HumanInfoAdapter()
        recycler_view.adapter = humanInfoAdapter

        // Find humans around when refresh button clicked.
        refresh_button.setOnClickListener {
            qiContext?.let { findHumansAround(it) }
        }

        // Register the RobotLifecycleCallbacks to this Activity.
        QiSDK.register(this, this)
    }

    override fun onDestroy() {
        // Unregister the RobotLifecycleCallbacks for this Activity.
        QiSDK.unregister(this, this)
        super.onDestroy()
    }

    override val layoutId = R.layout.activity_people_characteristics_tutorial

    override fun onRobotFocusGained(qiContext: QiContext) {
        // Store the provided QiContext.
        this.qiContext = qiContext

        // Bind the conversational events to the view.
        val conversationStatus = qiContext.conversation.status(qiContext.robotContext)
        conversationBinder = conversation_view.bindConversationTo(conversationStatus)

        val say = SayBuilder.with(qiContext)
                .withText("I can display characteristics about the human I'm seeing.")
                .build()

        say.run()

        // Get the HumanAwareness service from the QiContext.
        humanAwareness = qiContext.humanAwareness

        findHumansAround(qiContext)
    }

    override fun onRobotFocusLost() {
        // Remove the QiContext.
        this.qiContext = null

        conversationBinder.unbind()
    }

    override fun onRobotFocusRefused(reason: String) {
        // Nothing here.
    }

    private fun findHumansAround(qiContext: QiContext) {
        val humanAwareness = this.humanAwareness
            // Get the humans around the robot.
            val humansAroundFuture = humanAwareness?.async()?.humansAround

            humansAroundFuture?.andThenConsume {
                Log.i(TAG, it.size.toString() + " human(s) around.")
                retrieveCharacteristics(it, qiContext)
            }
    }

    private fun retrieveCharacteristics(humans: List<Human>, qiContext: QiContext) {
        // Get the Actuation service from the QiContext.
        val actuation = qiContext.actuation

        // Get the robot frame.
        val robotFrame = actuation.robotFrame()
        //we clear memory used for human who are being showed
         humanInfoList.forEach {
            it.clearMemory()
        }

        humanInfoList.clear()
        humans.forEachIndexed { index, human ->
            // Get the characteristics.
            val age = human.estimatedAge.years
            val gender = human.estimatedGender
            val pleasureState = human.emotion.pleasure
            val excitementState = human.emotion.excitement
            val engagementIntentionState = human.engagementIntention
            val smileState = human.facialExpressions.smile
            val attentionState = human.attention
            val humanFrame = human.headFrame

            // Display the characteristics.
            Log.i(TAG, "----- Human $index -----")
            Log.i(TAG, "Age: $age year(s)")
            Log.i(TAG, "Gender: $gender")
            Log.i(TAG, "Pleasure state: $pleasureState")
            Log.i(TAG, "Excitement state: $excitementState")
            Log.i(TAG, "Engagement state: $engagementIntentionState")
            Log.i(TAG, "Smile state: $smileState")
            Log.i(TAG, "Attention state: $attentionState")

            // Compute the distance.
            val distance = robotFrame?.let { computeDistance(humanFrame, it) }
            // Display the distance between the human and the robot.
            Log.i(TAG, "Distance: $distance meter(s).")

            // Get face picture.
            val facePictureBuffer = human.facePicture.image.data
            facePictureBuffer.rewind()
            val pictureBufferSize = facePictureBuffer.remaining()
            val facePictureArray = ByteArray(pictureBufferSize)
            facePictureBuffer.get(facePictureArray)

            var facePicture: Bitmap? = null
            // Test if the robot has an empty picture (this can happen when he detects a human but not the face).
            if (pictureBufferSize != 0) {
                Log.i(TAG, "Picture available")
                facePicture = BitmapFactory.decodeByteArray(facePictureArray, 0, pictureBufferSize)
            } else {
                Log.i(TAG, "Picture not available")
            }

            if (distance != null && facePicture != null) {
                val humanInfo = HumanInfo(age, gender, pleasureState, excitementState, engagementIntentionState, smileState, attentionState, distance, facePicture)
                humanInfoList.add(humanInfo)
            }
        }

        displayHumanInfoList(humanInfoList)
    }

    private fun computeDistance(humanFrame: Frame, robotFrame: Frame): Double {
        // Get the TransformTime between the human frame and the robot frame.
        val transformTime = humanFrame.computeTransform(robotFrame)

        // Get the transform.
        val transform = transformTime.transform

        // Get the translation.
        val translation = transform.translation

        // Get the x and y components of the translation.
        val x = translation.x
        val y = translation.y

        // Compute the distance and return it.
        return sqrt(x * x + y * y)
    }

    private fun displayHumanInfoList(humanInfoList: List<HumanInfo>) {
        runOnUiThread { humanInfoAdapter.updateList(humanInfoList) }
    }
}
