package com.example.bookreader.model

data class AccessInfo(val accessViewStatus: String = "",
                      val country: String = "",
                      val viewability: String = "",
                      val pdf: Pdf,
                      val webReaderLink: String = "",
                      val epub: Epub,
                      val publicDomain: Boolean = false,
                      val quoteSharingAllowed: Boolean = false,
                      val embeddable: Boolean = false,
                      val textToSpeechPermission: String = "")