import request from '@/utils/request'
const BASE_URL = '/admin/evaluate/voucher'

export default {
  // 添加优惠券
  saveVoucher(voucher) {
    return request({
      url: `${BASE_URL}/saveVoucher`,
      method: 'post',
      data: voucher
    })
  },
  // 删除优惠券
  removeVoucherById(id) {
    return request({
      url: `${BASE_URL}/removeVoucherById/${id}`,
      method: 'delete',
      params: id
    })
  },
  // 根据id查询商铺
  queryVoucherVoById(id) {
    return request({
      url: `${BASE_URL}/queryVoucherVoById/${id}`,
      method: 'get',
      params: id
    })
  },
  // 更新商铺
  updateVoucher(voucher) {
    return request({
      url: `${BASE_URL}/updateVoucher`,
      method: 'post',
      data: voucher
    })
  },
  // 获取优惠券列表
  getVoucherList(page, limit, searchObj) {
    return request({
      url: `${BASE_URL}/${page}/${limit}`,
      method: 'get',
      params: searchObj
    })
  }
}
