import request from '@/utils/request'
const BASE_URL = '/admin/evaluate/voucherOrder'

export default {
  // 获取优惠券订单列表
  getVoucherOrderList(page, limit, searchObj) {
    return request({
      url: `${BASE_URL}/${page}/${limit}`,
      method: 'get',
      params: searchObj
    })
  }
}
