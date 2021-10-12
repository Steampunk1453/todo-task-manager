package org.task.manager.client

interface TitleClient {
    fun getItems(filter: String): TitleResponse
    fun getItemInfo(filter: String, titleId: String?): Item
}
