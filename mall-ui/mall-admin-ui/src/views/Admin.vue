<script setup>

    import adminApi from "@/api/admin.js";
    import {reactive, ref} from "vue";
    import {ElMessage, ElMessageBox} from "element-plus";
    import { Plus } from '@element-plus/icons-vue'

    const list = ref([])

    const total = ref(0)

    const adminQuery = reactive({
        username: '',
        email: '',
        page: 1,
        limit: 10
    })

    const loadData = () => {
        adminApi.list(adminQuery).then(result => {
            if (result.code == 0){
                list.value = result.data.records
                total.value = result.data.total
            }
        })
    }

    loadData()

    const onSearch = () => {
        adminQuery.page = 1
        loadData()
    }

    const onReset = () => {
        adminQuery.username = ''
        adminQuery.email = ''
        adminQuery.page = 1
        loadData()
    }

    // Toggle admin status (activate/block)
    const toggleStatus = (row, newValue) => {
        const originalStatus = row.status
        const newStatus = newValue
        const statusText = newStatus === 1 ? 'activate' : 'block'

        ElMessageBox.confirm(
            `Are you sure to ${statusText} this admin?`,
            'Confirm Status Change',
            {
                confirmButtonText: 'Confirm',
                cancelButtonText: 'Cancel',
                type: 'warning',
                lockScroll: false
            }
        ).then(() => {
            adminApi.updateStatus(row.id, newStatus).then(result => {
                if (result.code === 0) {
                    ElMessage.success(`Admin ${statusText}d successfully`)
                    row.status = newStatus
                } else {
                    ElMessage.error(result.msg)
                    // Revert switch on API error
                    row.status = originalStatus
                }
            }).catch(() => {
                ElMessage.error('Failed to update admin status')
                // Revert switch on network error
                row.status = originalStatus
            })
        }).catch(() => {
            // User clicked cancel - revert the switch
            row.status = originalStatus
        })
    }

    //deleteById
    const deleteById = (id) => {
        ElMessageBox.confirm(
            'Are you sure to delete?',
            'Warning',
            {
                confirmButtonText: 'Confirm',
                cancelButtonText: 'Cancel',
                type: 'warning',
                lockScroll: false // Prevent scrolling
            }
        ).then(() => {
            adminApi.deleteById(id).then(result => {
                if (result.code === 0) {
                    ElMessage.success(result.msg)
                    loadData()
                } else {
                    ElMessage.error(result.msg)
                }
            })
        })
    }

    const ids = ref([])
    const handleSelectionChange = (rows) => {
        console.log(rows)
        ids.value = rows.map(row => row.id)
        console.log(ids.value)
    }

    //deleteAll
    const deleteAll = () => {
        ElMessageBox.confirm(
            'Are you sure to delete selected?',
            'Warning',
            {
                confirmButtonText: 'Confirm',
                cancelButtonText: 'Cancel',
                type: 'warning',
                lockScroll: false // Prevent scrolling
            }
        ).then(() => {
            adminApi.deleteAll(ids.value).then(result => {
                if (result.code === 0) {
                    ElMessage.success(result.msg)
                    loadData()
                } else {
                    ElMessage.error(result.msg)
                }
            })
        })
    }

    // add/edit
    const dialogFormVisible = ref(false)
    const admin = ref({})
    const title = ref('')

    const showAddDialog = () => {
        dialogFormVisible.value = true
        title.value = 'add admin'
        admin.value = { status: 1 } // Default to active status
    }

    const showUpdateDialog = (id) => {
        dialogFormVisible.value = true
        title.value = 'update admin'
        admin.value = {}

        adminApi.selectById(id).then(result => {
            admin.value = result.data
        })
    }

    const addOrUpdate = () => {
        if (admin.value.id) {
            adminApi.updateById(admin.value).then(result => {
                if (result.code === 0) {
                    ElMessage.success(result.msg)
                    dialogFormVisible.value = false
                    loadData()
                } else {
                    ElMessage.error(result.msg)
                }
            })
        } else {
            adminApi.add(admin.value).then(result => {
                if (result.code === 0) {
                    ElMessage.success(result.msg)
                    dialogFormVisible.value = false
                    loadData()
                } else {
                    ElMessage.error(result.msg)
                }
            })
        }
    }

    const handleAvatarSuccess = (result) => {
        console.log('Admin upload result:', result)
        if (result.code === 0 && result.data && result.data.url) {
            admin.value.avatar = result.data.url
            console.log('Set admin avatar to:', admin.value.avatar)
        } else {
            ElMessage.error('Upload failed: ' + (result.msg || 'Unknown error'))
        }
    }

    import {useTokenStore} from '@/store/token.js'

    const tokenStore = useTokenStore()

    const headers = ref({
        Authorization: `Bearer ${tokenStore.token}`
    })

    // Function to get the correct upload URL
    const getUploadUrl = (path) => {
        // In development, use the proxy configuration
        if (import.meta.env.DEV) {
            return `/api${path}`
        }
        // In production, use the full gateway URL
        return `${import.meta.env.VITE_API_BASE_URL || 'https://vapemall-gateway-d8a215a380c5.herokuapp.com/api'}${path}`
    }


</script>

<template>
    <el-card class="admin-management">
        <template #header>
            <div class="header">
                <h3>Admin Management</h3>
                <div class="header-actions">
                    <el-button type="primary" @click="showAddDialog">Add Admin</el-button>
                    <el-button type="danger" @click="deleteAll">Bulk Delete</el-button>
                </div>
            </div>
        </template>

        <!-- Search Form -->
        <el-form :inline="true" class="search-form">
            <el-form-item label="Username">
                <el-input
                    v-model="adminQuery.username"
                    placeholder="Enter username"
                    clearable
                    style="width: 200px"
                />
            </el-form-item>
            <el-form-item label="Email">
                <el-input
                    v-model="adminQuery.email"
                    placeholder="Enter email"
                    clearable
                    style="width: 200px"
                />
            </el-form-item>
            <el-form-item>
                <el-button type="primary" @click="onSearch">Search</el-button>
                <el-button @click="onReset">Reset</el-button>
            </el-form-item>
        </el-form>

        <!-- Admin Table -->
        <el-table :data="list" style="width: 100%" ref="multipleTableRef" @selection-change="handleSelectionChange" class="admin-table">
        <el-table-column type="selection" width="55" />
        <el-table-column fixed prop="id" label="ID"/>
        <el-table-column prop="username" label="username"/>
        <el-table-column prop="password" label="password"/>
        <el-table-column prop="email" label="email"/>
        <el-table-column prop="avatar" label="avatar">
            <template #default="scope">
                <img v-if="scope.row.avatar" :src="scope.row.avatar" style="max-height: 40px; max-width: 120px;" />
            </template>
        </el-table-column>
        <el-table-column prop="status" label="Status" width="120" align="center">
            <template #default="scope">
                <el-switch
                    v-model="scope.row.status"
                    :active-value="1"
                    :inactive-value="0"
                    active-color="#67C23A"
                    inactive-color="#F56C6C"
                    @change="(value) => toggleStatus(scope.row, value)"
                    :loading="false"
                    style="--el-switch-on-color: #67C23A; --el-switch-off-color: #F56C6C;"
                />
            </template>
        </el-table-column>

        <el-table-column align="center" width="200px" fixed="right" label="Operations">
            <template #default="{ row }">
                <el-button size="small" type="primary" @click="showUpdateDialog(row.id)">edit</el-button>
                <el-button size="small" type="danger" @click="deleteById(row.id)">delete</el-button>
            </template>
        </el-table-column>
        </el-table>

        <!-- Pagination -->
        <el-pagination
            v-model:current-page="adminQuery.page"
            v-model:page-size="adminQuery.limit"
            :page-sizes="[10, 20, 30, 50]"
            layout="total, sizes, prev, pager, next, jumper"
            :total="total"
            @change="loadData"
            class="pagination"
        />
    </el-card>

    <!-- Add/Edit Dialog -->
    <el-dialog v-model="dialogFormVisible" :title="title" width="500" :lock-scroll="false">
        <el-form :model="admin">
            <el-form-item label="name" :label-width="60">
                <el-input v-model="admin.username" autocomplete="off" />
            </el-form-item>
            <el-form-item label="password" :label-width="60">
                <el-input v-model="admin.password" autocomplete="off" />
            </el-form-item>
            <el-form-item label="email" :label-width="60">
                <el-input v-model="admin.email" autocomplete="off" />
            </el-form-item>
            <el-form-item label="status" :label-width="60">
                <el-switch
                    v-model="admin.status"
                    :active-value="1"
                    :inactive-value="0"
                    active-color="#67C23A"
                    inactive-color="#F56C6C"
                    style="--el-switch-on-color: #67C23A; --el-switch-off-color: #F56C6C;"
                />
            </el-form-item>
            <el-form-item label="avatar" :label-width="60">
            <el-upload
                class="avatar-uploader"
                :action="getUploadUrl('/upload/user')"
                :show-file-list="false"
                :on-success="handleAvatarSuccess"
                :headers="headers"
            >
                <img class="avatar" v-if="admin.avatar" :src="admin.avatar" />
                <el-icon v-else class="avatar-uploader-icon">
                    <Plus/>
                </el-icon>
            </el-upload>
            </el-form-item>
        </el-form>
        <template #footer>
            <div class="dialog-footer">
                <el-button @click="dialogFormVisible = false">cancel</el-button>
                <el-button type="primary" @click="addOrUpdate">
                    confirm
                </el-button>
            </div>
        </template>
    </el-dialog>




</template>


<style>
.avatar-uploader .avatar {
    width: 178px;
    height: 178px;
    display: block;
}
</style>

<style scoped>
.admin-management {
    margin: 20px;
}

/* Status switch colors */
:deep(.el-switch.is-checked .el-switch__core) {
    background-color: #67C23A !important;
    border-color: #67C23A !important;
}

:deep(.el-switch .el-switch__core) {
    background-color: #F56C6C !important;
    border-color: #F56C6C !important;
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

.admin-table {
    margin-bottom: 20px;
}

.pagination {
    margin-top: 20px;
    justify-content: flex-end;
}

.avatar-uploader .el-upload {
    border: 1px dashed var(--el-border-color);
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    transition: var(--el-transition-duration-fast);
}

.avatar-uploader .el-upload:hover {
    border-color: var(--el-color-primary);
}

.el-icon.avatar-uploader-icon {
    font-size: 28px;
    color: #8c939d;
    width: 178px;
    height: 178px;
    text-align: center;
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

    .search-form .el-input {
        width: 100% !important;
    }

    .header {
        flex-direction: column;
        gap: 15px;
        align-items: flex-start;
    }
}
</style>
