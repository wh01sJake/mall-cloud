<script setup>

    import categoryApi from "@/api/category.js";
    import {reactive, ref} from "vue";
    import {ElMessage, ElMessageBox} from "element-plus";
    import { Plus } from '@element-plus/icons-vue'

    const list = ref([])

    const total = ref(0)

    const categoryQuery = reactive({
        name: '',
        page: 1,
        limit: 10
    })

    const loadData = () => {
        categoryApi.list(categoryQuery).then(result => {
            if (result.code == 0){
                list.value = result.data.records
                total.value = result.data.total
            }
        })
    }

    loadData()

    const onSearch = () => {
        categoryQuery.page = 1
        loadData()
    }

    const onReset = () => {
        categoryQuery.name = ''
        categoryQuery.page = 1
        loadData()
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
            categoryApi.deleteById(id).then(result => {
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
            categoryApi.deleteAll(ids.value).then(result => {
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
    const category = ref({})
    const title = ref('')

    const showAddDialog = () => {
        dialogFormVisible.value = true
        title.value = 'add category'
        category.value = {}
    }

    const showUpdateDialog = (id) => {
        dialogFormVisible.value = true
        title.value = 'update category'
        category.value = {}

        categoryApi.selectById(id).then(result => {
            category.value = result.data
        })
    }

    const addOrUpdate = () => {
        if (category.value.id) {
            categoryApi.updateById(category.value).then(result => {
                if (result.code === 0) {
                    ElMessage.success(result.msg)
                    dialogFormVisible.value = false
                    loadData()
                } else {
                    ElMessage.error(result.msg)
                }
            })
        } else {
            categoryApi.add(category.value).then(result => {
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
        console.log('Category upload result:', result)
        if (result.code === 0 && result.data && result.data.url) {
            category.value.avatar = result.data.url
            console.log('Set category avatar to:', category.value.avatar)
        } else {
            ElMessage.error('Upload failed: ' + (result.msg || 'Unknown error'))
        }
    }

    import {useTokenStore} from '@/store/token.js'
    import WangEditor from "@/components/WangEditor.vue";

    const tokenStore = useTokenStore()

    const headers = ref({
        Authorization: `Bearer ${tokenStore.token}`
    })

    const onEditorChange = (detail) => {
      console.log(detail)
      category.value.detail = detail
    }



</script>

<template>
    <el-card class="category-management">
        <template #header>
            <div class="header">
                <h3>Category Management</h3>
                <div class="header-actions">
                    <el-button type="primary" @click="showAddDialog">Add Category</el-button>
                    <el-button type="danger" @click="deleteAll">Bulk Delete</el-button>
                </div>
            </div>
        </template>

        <!-- Search Form -->
        <el-form :inline="true" class="search-form">
            <el-form-item label="Name">
                <el-input
                    v-model="categoryQuery.name"
                    placeholder="Enter category name"
                    clearable
                    style="width: 200px"
                />
            </el-form-item>
            <el-form-item>
                <el-button type="primary" @click="onSearch">Search</el-button>
                <el-button @click="onReset">Reset</el-button>
            </el-form-item>
        </el-form>

        <!-- Category Table -->
        <el-table :data="list" style="width: 100%" ref="multipleTableRef" @selection-change="handleSelectionChange" class="category-table">
        <el-table-column type="selection" width="55" />
        <el-table-column fixed prop="id" label="ID"/>
        <el-table-column prop="name" label="name"/>
        <el-table-column prop="parentId" label="parentId"/>
        <el-table-column align="center" width="200px" fixed="right" label="Operations">

            <template #default="{ row }">
                <el-button type="primary" @click="showUpdateDialog(row.id)">edit</el-button>
                <el-button type="danger" @click="deleteById(row.id)">delete</el-button>
            </template>
        </el-table-column>
        </el-table>

        <!-- Pagination -->
        <el-pagination
            v-model:current-page="categoryQuery.page"
            v-model:page-size="categoryQuery.limit"
            :page-sizes="[10, 20, 30, 50]"
            layout="total, sizes, prev, pager, next, jumper"
            :total="total"
            @change="loadData"
            class="pagination"
        />
    </el-card>

    <!-- Add/Edit Dialog -->
    <el-dialog v-model="dialogFormVisible" :title="title" width="70%" :lock-scroll="false">
        <el-form :model="category">
            <el-form-item label="name" :label-width="60">
                <el-input v-model="category.name" autocomplete="off" />
            </el-form-item>
            <el-form-item label="parentId" :label-width="60">
                <el-input v-model="category.parentId" autocomplete="off" />
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
.category-management {
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

.category-table {
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
