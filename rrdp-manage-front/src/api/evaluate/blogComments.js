import request from '@/utils/request'
const BASE_URL = '/admin/evaluate/blogComments'

export default {
  // 获取博客评论列表
  getBlogCommentsList(page, limit, searchObj) {
    return request({
      url: `${BASE_URL}/${page}/${limit}`,
      method: 'get',
      params: searchObj
    })
  }
}
