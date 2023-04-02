import request from '@/utils/request'
const BASE_URL = '/admin/evaluate/shop'

export default {
  // 获取商铺列表
  getShopList(page, limit, searchObj) {
    return request({
      url: `${BASE_URL}/${page}/${limit}`,
      method: 'get',
      params: searchObj
    })
  },
  // 添加商铺
  saveShop(shop) {
    return request({
      url: `${BASE_URL}/save`,
      method: 'post',
      data: shop
    })
  },
  // 删除商铺
  removeShop(id) {
    return request({
      url: `${BASE_URL}/remove/${id}`,
      method: 'delete',
      params: id
    })
  },
  // 根据id查询商铺
  queryById(id) {
    return request({
      url: `${BASE_URL}/${id}`,
      method: 'get',
      params: id
    })
  },
  // 更新商铺
  updateShop(shop) {
    return request({
      url: `${BASE_URL}/update`,
      method: 'post',
      data: shop
    })
  }
}
