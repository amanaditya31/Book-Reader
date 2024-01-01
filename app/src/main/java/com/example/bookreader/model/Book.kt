package com.example.bookreader.model

data class Book(val totalItems: Int = 0,
                val kind: String = "",
                val items: List<ItemsItem>?)