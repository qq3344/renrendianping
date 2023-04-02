import request from '@/utils/request'

const BASE_URL = '/admin/system/sysRole'

export default {
  // 获取列表
  getPageList(page, limit, searchObj) {
    return request({
      url: `${BASE_URL}/${page}/${limit}`,
      method: 'get',
      params: searchObj
    })
  },
  // 删除单个角色
  removeById(id) {
    return request({
      url: `${BASE_URL}/remove/${id}`,
      method: 'delete',
      params: id
    })
  },
  // 添加角色
  saveSysRole(sysRole) {
    return request({
      url: `${BASE_URL}/save`,
      method: 'post',
      data: sysRole
    })
  },
  // 单个查询角色
  queryById(id) {
    return request({
      url: `${BASE_URL}/queryById/${id}`,
      method: 'get',
      params: id
    })
  },
  // 更新角色信息
  updateSysRole(sysRole) {
    return request({
      url: `${BASE_URL}/update`,
      method: 'put',
      data: sysRole
    })
  },
  // 批量删除角色
  batchRemove(ids) {
    return request({
      url: `${BASE_URL}/batchRemove`,
      method: 'delete',
      data: ids
    })
  },
  // 获取用户角色信息
  getRolesByUserId(userId) {
    return request({
      url: `${BASE_URL}/toAssign/${userId}`,
      method: 'get',
      params: userId
    })
  },
  // 分配角色
  assignRoles(assignRoleVo) {
    return request({
      url: `${BASE_URL}/doAssign`,
      method: 'post',
      data: assignRoleVo
    })
  }
}
