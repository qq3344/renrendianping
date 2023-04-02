import request from '@/utils/request'
const BASE_URL = '/admin/evaluate/blog'

export default {
  // 获取博客列表
  getBlogList(page, limit, searchObj) {
    return request({
      url: `${BASE_URL}/${page}/${limit}`,
      method: 'get',
      params: searchObj
    })
  },

  // 删除博客
  removeBlog(id) {
    return request({
      url: `${BASE_URL}/removeBlogById/${id}`,
      method: 'delete',
      params: id
    })
  },
  // 根据id查询博客
  queryById(id) {
    return request({
      url: `${BASE_URL}/${id}`,
      method: 'get',
      params: id
    })
  }
}

