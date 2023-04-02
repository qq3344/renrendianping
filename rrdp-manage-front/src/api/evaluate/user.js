import request from '@/utils/request'
const BASE_URL = '/admin/evaluate/user'

export default {
  // 获取用户列表
  getPageList(page, limit, searchObj) {
    return request({
      url: `${BASE_URL}/${page}/${limit}`,
      method: 'get',
      params: searchObj
    })
  },
  // 添加系统用户,暂时用不到
  saveUser(sysUser) {
    return request({
      url: `${BASE_URL}/saveSysUser`,
      method: 'post',
      data: sysUser
    })
  },
  // 删除用户
  removeUser(id) {
    return request({
      url: `${BASE_URL}/remove/${id}`,
      method: 'delete',
      params: id
    })
  },
  // 根据id查询系统用户
  queryById(id) {
    return request({
      url: `${BASE_URL}/queryById/${id}`,
      method: 'get',
      params: id
    })
  },
  // 根据id查询用户信息详情
  queryInfoById(id) {
    return request({
      url: `${BASE_URL}/queryInfoById/${id}`,
      method: 'get',
      params: id
    })
  }
}
