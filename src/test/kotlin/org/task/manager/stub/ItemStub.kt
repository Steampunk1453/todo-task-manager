package org.task.manager.stub

import org.task.manager.web.rest.dto.Item

class ItemStub {

    companion object {
        fun buildItem() = Item(
            id = "tt9077530",
            title = "Mr Robot",
            rank = "1",
            year = "2018",
            type = "TVSeries",
            genres = null,
            officialWebsite = null
        )
        fun buildItemInfo() = Item(
            id = "tt9077530",
            title = "Mr Robot",
            rank = "1",
            year = "2018",
            type = "TVSeries",
            genres = "Action, Adventure, Sci-Fi",
            officialWebsite = "https://www.amazon.com/dp/B07QQQ52B3"
        )
    }

}
