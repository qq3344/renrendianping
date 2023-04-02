import request from '@/utils/request'

const BASE_URL = '/admin/system/sysLoginLog'

export default {
  // 分页查询登录日志
  getPageList(page, limit, searchObj) {
    return request({
      url: `${BASE_URL}/${page}/${limit}`,
      method: 'get',
      params: searchObj
    })
  },
  // 获取详情页
  queryById(id) {
    return request({
      url: `${BASE_URL}/queryById/${id}`,
      method: 'get'
    })
  }
}
