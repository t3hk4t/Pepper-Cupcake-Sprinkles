# Using Google translation services with Pepper

WIP 

TODO: add a Google Cloud service guide

---

## Introduction 

This document describes third-party translation service options provided by Google. In addition, it provides a general guide to their implementation. 

It must be noted that Google is not the only available provider for such services. For example, the Microsoft cloud service Azure provides similar functionality. However, official documentation is more easily available for implementation of Google services. As such, these services are the focus of this document.

This guide is still a work in progress. As such, a section for Google Cloud services has not yet been written. 

## Service options

There are two options for translation services from Google.

- [ML Kit](https://developers.google.com/ml-kit)

  The ML Kit provides ML-based services which are optimized for mobile use. In addition, tasks are performed on the device, which means that no cloud usage in necessary. However, in the case of translation, local models tend to be less accurate than their cloud counterparts.            

- [Google Cloud Translation](https://cloud.google.com/)

  Google Cloud provides paid cloud services. Similarly to Azure, Google Cloud uses a pay-as-you-go model. Free credits are available for 90 days, and most of their products have a [free tier](https://cloud.google.com/free). As such, this is a great option for small-scale and short-term projects.



## Usage

### ML Kit

Google provides a comprehensive and easy to use [guide](https://developers.google.com/ml-kit/language/translation/android) for translation model usage. 

**Note:** There is an error in two of the code snippet examples. The OnSuccessListener class takes a template parameter, which is missing in their guide. The class parameter corresponds to the argument passed to the onSuccess method.

Keep in mind that any Pepper actions used in listener classes for the Translator must be created asynchronously. Otherwise, the code will compile but fail upon execution.

TODO: add code snippet



### Google Cloud

To be added.

