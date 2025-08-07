import service from '@/utils/request.js'
// 将所有的针对product的请求，都封装到一个对象中
const productApi = {
    list(productQuery) {
        return service.get('/product/list', {params: productQuery})
    },
    deleteById(id) {
        return service.delete( `/product/deleteById/${id}`)
    },
    deleteAll(ids) {
        return service.delete( `/product/deleteAll/${ids}`)
    },
    add(product) {
        //product以JSON形式传递到后台，后端使用@RequestBody接收
        return service.post('/product/add', product)
    },
    selectById(id) {
        return service.get(`/product/selectById/${id}`)
    },
    updateById(product) {
        return service.put('/product/update', product)
    },
    productInfo(){
        return service.get('product/productInfo')
    }

}

export default productApi
