import service from '@/utils/request.js'
// 将所有的针对category的请求，都封装到一个对象中
const categoryApi = {
    list(categoryQuery) {
        return service.get('/product/category/list', {params: categoryQuery})
    },
    deleteById(id) {
        return service.delete( `/product/category/deleteById/${id}`)
    },
    deleteAll(ids) {
        return service.delete( `/product/category/deleteAll/${ids}`)
    },
    add(category) {
        //category以JSON形式传递到后台，后端使用@RequestBody接收
        return service.post('/product/category/add', category)
    },
    selectById(id) {
        return service.get(`/product/category/selectById/${id}`)
    },
    updateById(category) {
        return service.put('/product/category/update', category)
    },
    categoryInfo(){
        return service.get('product/category/categoryInfo')
    },
    selectTopCategoryList() {
        return service.get('/product/category/selectTopCategoryList')
    },

    selectSecondCategoryListByParentId(parentId) {
        return service.get(`/product/category/selectSecondCategoryListByParentId/${parentId}`)

    }
}

export default categoryApi
