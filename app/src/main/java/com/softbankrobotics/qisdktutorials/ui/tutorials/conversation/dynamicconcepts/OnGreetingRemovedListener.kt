/*
 * Copyright (C) 2018 Softbank Robotics Europe
 * See COPYING for the license
 */

package com.softbankrobotics.qisdktutorials.ui.tutorials.conversation.dynamicconcepts

/**
 * The listener used to handle a greeting removal.
 */
internal interface OnGreetingRemovedListener {
    /**
     * Called when a greeting is removed.
     * @param greeting the greeting
     */
    fun onGreetingRemoved(greeting: String)
}
