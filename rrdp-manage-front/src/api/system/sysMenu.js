import request from '@/utils/request'

const BASE_URL = '/admin/system/sysMenu'

export default {
  // 添加菜单
  saveSysMenu(sysMenu) {
    return request({
      url: `${BASE_URL}/saveSysMenu`,
      method: 'post',
      data: sysMenu
    })
  },
  // 删除菜单
  removeSysMenu(id) {
    return request({
      url: `${BASE_URL}/remove/${id}`,
      method: 'delete',
      params: id
    })
  },
  // 根据id查询菜单
  queryById(id) {
    return request({
      url: `${BASE_URL}/queryById/${id}`,
      method: 'delete',
      params: id
    })
  },
  // 修改菜单
  updateSysMenu(sysMenu) {
    return request({
      url: `${BASE_URL}/updateSysMenu`,
      method: 'put',
      data: sysMenu
    })
  },
  // 获取树形数据
  treeMenu() {
    return request({
      url: `${BASE_URL}/treeMenu`,
      method: 'get'
    })
  },
  // 查看角色分配的菜单数据
  toAssign(roleId) {
    return request({
      url: `${BASE_URL}/toAssign/${roleId}`,
      method: 'get'
    })
  },
  // 根据角色id获取菜单,返回所有的选中的MenuId
  getSelectMenuId(roleId) {
    return request({
      url: `${BASE_URL}/getSelectMenuId/${roleId}`,
      method: 'get'
    })
  },
  // 角色分配菜单
  doAssign(sysRoleMenu) {
    return request({
      url: `${BASE_URL}/doAssign`,
      method: 'post',
      data: sysRoleMenu
    })
  }
}
