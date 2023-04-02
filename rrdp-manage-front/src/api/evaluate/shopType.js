import request from '@/utils/request'
const BASE_URL = '/admin/evaluate/shopType'

export default {
  // 获取商铺类型列表
  getTypeList(page, limit) {
    return request({
      url: `${BASE_URL}/${page}/${limit}`,
      method: 'get'
    })
  },
  // 添加商铺类型
  saveShopType(shopType) {
    return request({
      url: `${BASE_URL}/save`,
      method: 'post',
      data: shopType
    })
  },
  // 更新商铺类型
  updateShopType(shopType) {
    return request({
      url: `${BASE_URL}/update`,
      method: 'post',
      data: shopType
    })
  },
  // 删除商铺类型
  removeShopType(id) {
    return request({
      url: `${BASE_URL}/remove/${id}`,
      method: 'delete',
      params: id
    })
  },
  // 根据id查询商铺详情
  queryById(id) {
    return request({
      url: `${BASE_URL}/${id}`,
      method: 'get',
      params: id
    })
  },
  // 查询所有的商铺类型
  queryAll() {
    return request({
      url: `${BASE_URL}/queryAll`,
      method: 'get'
    })
  }

}
