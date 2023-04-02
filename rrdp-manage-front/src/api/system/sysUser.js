import request from '@/utils/request'

const BASE_URL = '/admin/system/sysUser'

export default {

  // 获取列表
  getPageList(page, limit, searchObj) {
    return request({
      url: `${BASE_URL}/${page}/${limit}`,
      method: 'get',
      params: searchObj
    })
  },
  // 添加系统用户
  saveSysUser(sysUser) {
    return request({
      url: `${BASE_URL}/saveSysUser`,
      method: 'post',
      data: sysUser
    })
  },
  // 删除用户
  removeSysUser(id) {
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
  // 更新用户信息
  updateSysUser(sysUser) {
    return request({
      url: `${BASE_URL}/updateSysUser`,
      method: 'put',
      data: sysUser
    })
  },
  // 修改用户状态
  updateStatus(id, status) {
    return request({
      url: `${BASE_URL}/updateStatus/${id}/${status}`,
      method: 'get'
    })
  }

}
