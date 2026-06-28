import { ref } from 'vue'

/**
 * 统一分页组合函数。
 * 后端返回 PageResult<T> = { records, total, page, size }，前端统一解析。
 *
 * @param {Function} fetchFn  API 调用函数，签名为 (params) => Promise，params 含 page, size
 * @param {Object}   options  可选配置
 * @param {number}   options.defaultSize  默认每页条数，默认 20
 * @param {number}   options.defaultPage  默认页码，默认 1
 * @returns {{ list, total, loading, page, size, fetchData, refresh, handlePageChange, handleSizeChange }}
 *
 * @example
 * const { list, total, loading, page, size, fetchData } = usePagination(getUsers)
 * fetchData()              // 首次加载
 * fetchData({ keyword })   // 带搜索条件
 * refresh()                // 回到第1页重新加载
 */
export function usePagination(fetchFn, { defaultSize = 20, defaultPage = 1 } = {}) {
  const list = ref([])
  const total = ref(0)
  const loading = ref(false)
  const page = ref(defaultPage)
  const size = ref(defaultSize)

  /** 执行数据加载，extraParams 会与分页参数合并传给 fetchFn */
  const fetchData = async (extraParams = {}) => {
    loading.value = true
    try {
      const res = await fetchFn({
        page: page.value - 1,  // 后端 page 从 0 开始
        size: size.value,
        ...extraParams
      })
      // 统一解析后端 PageResult<T> 格式
      list.value = res?.records || res?.data || []
      total.value = res?.total || 0
    } catch (e) { /* 调用方自行处理错误 */ }
    finally { loading.value = false }
  }

  /** 切换页码 */
  const handlePageChange = (val) => {
    page.value = val
    fetchData()
  }

  /** 切换每页条数，回到第 1 页 */
  const handleSizeChange = (val) => {
    size.value = val
    page.value = 1
    fetchData()
  }

  /** 回到第 1 页重新加载 */
  const refresh = () => {
    page.value = 1
    fetchData()
  }

  return { list, total, loading, page, size, fetchData, refresh, handlePageChange, handleSizeChange }
}
