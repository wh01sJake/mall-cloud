<script setup>
import orderApi from "@/api/order.js";
import {reactive, ref} from "vue";
import {ElMessage, ElMessageBox} from "element-plus";
import { View, Edit, Refresh } from '@element-plus/icons-vue'

const list = ref([])
const total = ref(0)

const orderQuery = reactive({
    orderNo: '',
    status: '',
    customerName: '',
    page: 1,
    limit: 10
})

// Order status options
const statusOptions = [
    { label: 'All', value: '' },
    { label: 'Unpaid', value: 1 },
    { label: 'Paid', value: 2 },
    { label: 'Shipped', value: 3 },
    { label: 'Completed', value: 4 },
    { label: 'Cancelled', value: 0 },
    { label: 'Closed', value: 5 }
]

const statusColors = {
    0: 'danger',    // Cancelled
    1: 'warning',   // Unpaid
    2: 'primary',   // Paid
    3: 'info',      // Shipped
    4: 'success',   // Completed
    5: 'info'       // Closed
}

const statusLabels = {
    0: 'Cancelled',
    1: 'Unpaid',
    2: 'Paid',
    3: 'Shipped',
    4: 'Completed',
    5: 'Closed'
}

const loadData = () => {
    orderApi.list(orderQuery).then(result => {
        if (result.code == 0){
            list.value = result.data.records
            total.value = result.data.total
        } else {
            ElMessage.error(result.msg || 'Failed to load orders')
        }
    }).catch(error => {
        console.error('Error loading orders:', error)
        ElMessage.error('Failed to load orders')
    })
}

loadData()

const onSearch = () => {
    orderQuery.page = 1
    loadData()
}

const onReset = () => {
    orderQuery.orderNo = ''
    orderQuery.status = ''
    orderQuery.customerName = ''
    orderQuery.page = 1
    loadData()
}

// Update order status
const updateOrderStatus = (orderNo, newStatus) => {
    ElMessageBox.confirm(
        `Are you sure to update order status to ${statusLabels[newStatus]}?`,
        'Confirm Status Update',
        {
            confirmButtonText: 'Confirm',
            cancelButtonText: 'Cancel',
            type: 'warning',
            lockScroll: false
        }
    ).then(() => {
        orderApi.updateStatus(orderNo, newStatus).then(result => {
            if (result.code === 0) {
                ElMessage.success('Status updated successfully')
                loadData()
            } else {
                ElMessage.error(result.msg || 'Failed to update status')
            }
        }).catch(error => {
            console.error('Error updating status:', error)
            ElMessage.error('Failed to update status')
        })
    })
}

// Order details dialog
const orderDetailsVisible = ref(false)
const orderDetails = ref({})
const orderItems = ref([])
const orderAddress = ref({})
const loadingDetails = ref(false)

// View order details
const viewOrderDetails = (orderNo) => {
    orderDetailsVisible.value = true
    loadingDetails.value = true

    // Load order details
    Promise.all([
        orderApi.getByOrderNo(orderNo),
        orderApi.getOrderItems(orderNo),
        orderApi.getOrderAddress(orderNo)
    ]).then(([detailsResult, itemsResult, addressResult]) => {
        if (detailsResult.code === 0) {
            orderDetails.value = detailsResult.data
        }
        if (itemsResult.code === 0) {
            orderItems.value = itemsResult.data
        }
        if (addressResult.code === 0) {
            orderAddress.value = addressResult.data
        }
        loadingDetails.value = false
    }).catch(error => {
        console.error('Error loading order details:', error)
        ElMessage.error('Failed to load order details')
        loadingDetails.value = false
    })
}

// Format currency
const formatCurrency = (amount) => {
    return `€${(amount || 0).toFixed(2)}`
}

// Format date
const formatDate = (date) => {
    if (!date) return '-'
    return new Date(date).toLocaleDateString('en-IE')
}
</script>

<template>
    <el-card class="order-management">
        <template #header>
            <div class="header">
                <h3>Order Management</h3>
                <div class="header-actions">
                    <el-button type="primary" :icon="Refresh" @click="loadData">Refresh</el-button>
                </div>
            </div>
        </template>

        <!-- Search Form -->
        <el-form :inline="true" class="search-form">
            <el-form-item label="Order No">
                <el-input 
                    v-model="orderQuery.orderNo" 
                    placeholder="Enter order number" 
                    clearable
                    style="width: 200px"
                />
            </el-form-item>
            <el-form-item label="Status">
                <el-select 
                    v-model="orderQuery.status" 
                    placeholder="Select status"
                    clearable
                    style="width: 150px"
                >
                    <el-option
                        v-for="option in statusOptions"
                        :key="option.value"
                        :label="option.label"
                        :value="option.value"
                    />
                </el-select>
            </el-form-item>
            <el-form-item label="Customer">
                <el-input 
                    v-model="orderQuery.customerName" 
                    placeholder="Enter customer name" 
                    clearable
                    style="width: 200px"
                />
            </el-form-item>
            <el-form-item>
                <el-button type="primary" @click="onSearch">Search</el-button>
                <el-button @click="onReset">Reset</el-button>
            </el-form-item>
        </el-form>

        <!-- Orders Table -->
        <el-table :data="list" style="width: 100%" class="orders-table">
            <el-table-column prop="orderNo" label="Order No" width="180" fixed="left">
                <template #default="{ row }">
                    <el-link type="primary" @click="viewOrderDetails(row.orderNo)">
                        {{ row.orderNo }}
                    </el-link>
                </template>
            </el-table-column>
            
            <el-table-column prop="customerName" label="Customer" width="150"/>
            
            <el-table-column prop="paymentAmount" label="Amount" width="120">
                <template #default="{ row }">
                    {{ formatCurrency(row.paymentAmount) }}
                </template>
            </el-table-column>
            
            <el-table-column prop="status" label="Status" width="120">
                <template #default="{ row }">
                    <el-tag :type="statusColors[row.status]">
                        {{ statusLabels[row.status] }}
                    </el-tag>
                </template>
            </el-table-column>
            
            <el-table-column prop="paymentType" label="Payment" width="120">
                <template #default="{ row }">
                    <span v-if="row.paymentType === 1">Stripe</span>
                    <span v-else-if="row.paymentType === 2">PayPal</span>
                    <span v-else-if="row.paymentType === 3">Credit Card</span>
                    <span v-else-if="row.paymentType === 4">Cash on Delivery</span>
                    <span v-else>-</span>
                </template>
            </el-table-column>
            
            <el-table-column prop="createTime" label="Order Date" width="120">
                <template #default="{ row }">
                    {{ formatDate(row.createTime) }}
                </template>
            </el-table-column>
            
            <el-table-column prop="paymentTime" label="Payment Date" width="120">
                <template #default="{ row }">
                    {{ formatDate(row.paymentTime) }}
                </template>
            </el-table-column>

            <el-table-column align="center" width="200px" fixed="right" label="Actions">
                <template #default="{ row }">
                    <el-button size="small" type="primary" :icon="View" @click="viewOrderDetails(row.orderNo)">
                        View
                    </el-button>
                    <el-dropdown @command="(status) => updateOrderStatus(row.orderNo, status)">
                        <el-button size="small" type="warning" :icon="Edit">
                            Status
                        </el-button>
                        <template #dropdown>
                            <el-dropdown-menu>
                                <el-dropdown-item :command="1">Mark as Unpaid</el-dropdown-item>
                                <el-dropdown-item :command="2">Mark as Paid</el-dropdown-item>
                                <el-dropdown-item :command="3">Mark as Shipped</el-dropdown-item>
                                <el-dropdown-item :command="4">Mark as Completed</el-dropdown-item>
                                <el-dropdown-item :command="0">Cancel Order</el-dropdown-item>
                            </el-dropdown-menu>
                        </template>
                    </el-dropdown>
                </template>
            </el-table-column>
        </el-table>

        <!-- Pagination -->
        <el-pagination
            v-model:current-page="orderQuery.page"
            v-model:page-size="orderQuery.limit"
            :page-sizes="[10, 20, 30, 50]"
            layout="total, sizes, prev, pager, next, jumper"
            :total="total"
            @change="loadData"
            class="pagination"
        />
    </el-card>

    <!-- Order Details Dialog -->
    <el-dialog
        v-model="orderDetailsVisible"
        title="Order Details"
        width="80%"
        :lock-scroll="false"
        class="order-details-dialog"
    >
        <div v-loading="loadingDetails">
            <el-row :gutter="20" v-if="!loadingDetails">
                <!-- Order Information -->
                <el-col :span="12">
                    <el-card class="detail-card">
                        <template #header>
                            <h4>Order Information</h4>
                        </template>
                        <el-descriptions :column="1" border>
                            <el-descriptions-item label="Order No">
                                {{ orderDetails.orderNo }}
                            </el-descriptions-item>
                            <el-descriptions-item label="Status">
                                <el-tag :type="statusColors[orderDetails.status]">
                                    {{ statusLabels[orderDetails.status] }}
                                </el-tag>
                            </el-descriptions-item>
                            <el-descriptions-item label="Payment Amount">
                                {{ formatCurrency(orderDetails.paymentAmount) }}
                            </el-descriptions-item>
                            <el-descriptions-item label="Payment Type">
                                <span v-if="orderDetails.paymentType === 1">Stripe</span>
                                <span v-else-if="orderDetails.paymentType === 2">PayPal</span>
                                <span v-else-if="orderDetails.paymentType === 3">Credit Card</span>
                                <span v-else-if="orderDetails.paymentType === 4">Cash on Delivery</span>
                                <span v-else>-</span>
                            </el-descriptions-item>
                            <el-descriptions-item label="Order Date">
                                {{ formatDate(orderDetails.createTime) }}
                            </el-descriptions-item>
                            <el-descriptions-item label="Payment Date">
                                {{ formatDate(orderDetails.paymentTime) }}
                            </el-descriptions-item>
                        </el-descriptions>
                    </el-card>
                </el-col>

                <!-- Shipping Address -->
                <el-col :span="12">
                    <el-card class="detail-card">
                        <template #header>
                            <h4>Shipping Address</h4>
                        </template>
                        <el-descriptions :column="1" border v-if="orderAddress.id">
                            <el-descriptions-item label="Recipient">
                                {{ orderAddress.receiverName }}
                            </el-descriptions-item>
                            <el-descriptions-item label="Phone">
                                {{ orderAddress.receiverPhone }}
                            </el-descriptions-item>
                            <el-descriptions-item label="Address Line 1">
                                {{ orderAddress.addressLine1 }}
                            </el-descriptions-item>
                            <el-descriptions-item label="Address Line 2" v-if="orderAddress.addressLine2">
                                {{ orderAddress.addressLine2 }}
                            </el-descriptions-item>
                            <el-descriptions-item label="City">
                                {{ orderAddress.townCity }}
                            </el-descriptions-item>
                            <el-descriptions-item label="Eircode">
                                {{ orderAddress.eircode }}
                            </el-descriptions-item>
                        </el-descriptions>
                        <el-empty v-else description="No shipping address" />
                    </el-card>
                </el-col>
            </el-row>

            <!-- Order Items -->
            <el-card class="detail-card" style="margin-top: 20px;" v-if="!loadingDetails">
                <template #header>
                    <h4>Order Items</h4>
                </template>
                <el-table :data="orderItems" style="width: 100%">
                    <el-table-column prop="productName" label="Product Name" />
                    <el-table-column prop="quantity" label="Quantity" width="100" />
                    <el-table-column prop="currentUnitPrice" label="Unit Price" width="120">
                        <template #default="{ row }">
                            {{ formatCurrency(row.currentUnitPrice) }}
                        </template>
                    </el-table-column>
                    <el-table-column prop="totalPrice" label="Total Price" width="120">
                        <template #default="{ row }">
                            {{ formatCurrency(row.totalPrice) }}
                        </template>
                    </el-table-column>
                    <el-table-column prop="productImage" label="Image" width="100">
                        <template #default="{ row }">
                            <img
                                v-if="row.productImage"
                                :src="row.productImage"
                                style="width: 50px; height: 50px; object-fit: cover; border-radius: 4px;"
                            />
                        </template>
                    </el-table-column>
                </el-table>
            </el-card>
        </div>

        <template #footer>
            <el-button @click="orderDetailsVisible = false">Close</el-button>
        </template>
    </el-dialog>
</template>

<style scoped>
.order-management {
    margin: 20px;
}

.header {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.header h3 {
    margin: 0;
    color: #303133;
}

.search-form {
    margin-bottom: 20px;
    padding: 20px;
    background: #f8f9fa;
    border-radius: 8px;
}

.orders-table {
    margin-bottom: 20px;
}

.pagination {
    margin-top: 20px;
    justify-content: flex-end;
}

.order-details-dialog .detail-card {
    margin-bottom: 20px;
}

.order-details-dialog .detail-card h4 {
    margin: 0;
    color: #303133;
}

/* Responsive design */
@media (max-width: 768px) {
    .search-form {
        padding: 15px;
    }
    
    .search-form .el-form-item {
        margin-bottom: 15px;
        width: 100%;
    }
    
    .search-form .el-input,
    .search-form .el-select {
        width: 100% !important;
    }
    
    .header {
        flex-direction: column;
        gap: 15px;
        align-items: flex-start;
    }
}
</style>
