package xyz.yhsj.ktor.entity.resp

/**
 * 根据传入的当前多少页
 *
 * @param size
 * @param number
 * @param totalElements
 */
class PageUtil(
    // 每页大小
    var size: Int, number: Int,
    // 总共有多少条数据
    var totalElements: Long
) {

    /**
     * 当前页为第几页
     */
    var number: Int = 0

    /**
     * 是否为第一页
     */
    var isFirst = false

    /**
     * 是否为最后一页
     */
    var isLast = false

    /**
     * 总共有多少页
     */
    var totalPages: Long = 0
        private set

    /**
     * 当前页一共有多少条数据
     */
    var numberOfElements: Int = 0

    /**
     * 数据
     */
    var content: List<Any>? = null
        set(value) {
            this.numberOfElements = value?.size ?: 0
            field = value
        }

    init {

        this.number = if (number < 0) 0 else number

        this.totalPages = if (totalElements % size == 0L) totalElements / size else totalElements / size + 1

        this.isFirst = number == 0

        this.isLast = number + 1 >= this.totalPages

    }

    fun setTotalPages(totalPages: Int) {
        this.totalPages = totalPages.toLong()
    }


}