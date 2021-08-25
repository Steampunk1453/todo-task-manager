package org.task.manager.client

import org.task.manager.web.rest.dto.TitleResponse
import org.task.manager.web.rest.dto.Item

interface TitleClient {
    fun getItems(filter: String): TitleResponse
    fun getItemInfo(filter: String, titleId: String?): Item
}
