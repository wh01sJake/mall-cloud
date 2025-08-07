import service from '@/utils/request.js'
// 将所有的针对admin的请求，都封装到一个对象中
const adminApi = {
    list(adminQuery) {
        return service.get('/admin/list', {params: adminQuery})
    },
    deleteById(id) {
        return service.delete( `/admin/deleteById/${id}`)
    },
    deleteAll(ids) {
        return service.delete( `/admin/deleteAll/${ids}`)
    },
    add(admin) {
        //admin以JSON形式传递到后台，后端使用@RequestBody接收
        return service.post('/admin/add', admin)
    },
    selectById(id) {
        return service.get(`/admin/selectById/${id}`)
    },
    updateById(admin) {
        return service.put('/admin/update', admin)
    },
    login(admin){
        return service.post('/admin/login', admin)
    },
    adminInfo(){
        return service.get('/admin/adminInfo')
    },
    resetPassword(adminPasswordDTO){
        return service.put('/admin/resetPassword',adminPasswordDTO)
    },
    updateStatus(id, status) {
        return service.put(`/admin/updateStatus/${id}/${status}`)
    }
}

export default adminApi
