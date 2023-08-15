package com.example.aitraininglogkotlin

var cardsList = mutableListOf<Card>()
val CARD_ID_EXTRA = "cardExtra"
class Card (
    val icon: Int,
    val title: String,
    val id: Int? = cardsList.size
        )