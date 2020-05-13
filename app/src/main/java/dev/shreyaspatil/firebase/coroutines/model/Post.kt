package dev.shreyaspatil.firebase.coroutines.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Post(
    val postContent: String? = null,
    val postAuthor: String? = null,
    val postLanguage: String? = null,
    @ServerTimestamp
    val postDate: Date? = null
)
