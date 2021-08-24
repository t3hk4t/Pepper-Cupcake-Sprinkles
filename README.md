**Pepper notes by the Cupcake team**
--
 - 08.24.2021 progress description

Conclusion - if you have no experience in android programming, one must certainly go through the android studio tutorials. Here is a link: [Build your first app | Android Basics | Android Developers](https://developer.android.com/training/basics/firstapp)
It is also recommended that you have theoretical knowledge about activities, fragments, async, sync method calls, etc... 
Before starting any programming for the Pepper robot, make sure that you know Pepper principles. [Mastering Focus & Robot lifecycle — QiSDK (softbankrobotics.com)](https://qisdk.softbankrobotics.com/sdk/doc/pepper-sdk/ch2_principles/focus_lifecycle.html)

Once you know the basic terminology and architecture of the basic android app, then you can move on to programming Pepper. During our reserach sessions, we could not make an empty project and run it from this tutorial [Creating a robot application — QiSDK (softbankrobotics.com)](https://qisdk.softbankrobotics.com/sdk/doc/pepper-sdk/ch1_gettingstarted/starting_project.html). Either because of android development illeteracy or some Pepper SDK bugs. Either way, we concluded that using a SoftBank SDK template is good enough for us. You can find a template : [softbankrobotics-labs/App-Template: This is a general structure of an android application built for Pepper. (github.com)](https://github.com/softbankrobotics-labs/App-Template). 

We tested a few basic things like the Say action [Say — QiSDK (aldebaran.com)](https://android.aldebaran.com/sdk/doc/pepper-sdk/ch4_api/conversation/reference/say.html), playing a sound on the robot [audio - Play sound on button click android - Stack Overflow](https://stackoverflow.com/questions/18459122/play-sound-on-button-click-android), as well as translation using Google's ML Kit. Two seperate test functionalities- greeting upon human detection and translating (english -> latvian) text and saying it- have been implemented in their most basic forms in separate branches.

To do next:
- Merge both test functionalities
- Try Google Cloud Translation in place of the local model
- Add initial fragment that will lead to the tutorial activites from the github
