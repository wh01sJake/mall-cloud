import service from '@/utils/request.js'

const customerApi = {
    list(customerQuery) {
        return service.get('/customer/list', {params: customerQuery})
    },
    deleteById(id) {
        return service.delete(`/customer/deleteById/${id}`)
    },
    deleteAll(ids) {
        return service.delete(`/customer/deleteAll/${ids}`)
    },
    add(customer) {
        return service.post('/customer/add', customer)
    },
    selectById(id) {
        return service.get(`/customer/selectById/${id}`)
    },
    updateById(customer) {
        return service.put('/customer/update', customer)
    },
    updateStatus(id, status) {
        return service.put(`/customer/updateStatus/${id}/${status}`)
    }
}

export default customerApi