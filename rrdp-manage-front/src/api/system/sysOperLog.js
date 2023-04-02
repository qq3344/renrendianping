import request from '@/utils/request'
const BASE_URL = '/admin/system/sysOperLog'

export default {
  getPageList(page, limit, searchObj) {
    return request({
      url: `${BASE_URL}/${page}/${limit}`,
      method: 'get',
      params: searchObj
    })
  },
  queryById(id) {
    return request({
      url: `${BASE_URL}/queryById/${id}`,
      method: 'get'
    })
  }
}
