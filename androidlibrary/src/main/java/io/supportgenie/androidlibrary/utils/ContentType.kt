package io.supportgenie.androidlibrary.utils

fun isMimeTypeText(mimeType : String) : Boolean {
    return mimeType.startsWith("text/")
}

fun isMimeTypeImage(mimeType : String) : Boolean {
    return mimeType.startsWith("image/")
}

fun isMimeTypeVideo(mimeType : String) : Boolean {
    return mimeType.startsWith("video/")
}

fun isMimeTypeDocument(mimeType : String) : Boolean {
    return (mimeType.startsWith("document/") || mimeType.startsWith("application/pdf"))
}

fun isMimeTypeAdaptiveCard(mimeType : String) : Boolean {
    return mimeType == "application/vnd.microsoft.card.adaptive"
}

fun isMimeTypeMarkdown(mimeType : String) : Boolean {
    return mimeType.contains("markdown")
}

