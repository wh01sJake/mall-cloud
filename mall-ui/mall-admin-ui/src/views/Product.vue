<script setup>

    import productApi from "@/api/product.js";
    import {reactive, ref} from "vue";
    import {ElMessage, ElMessageBox} from "element-plus";
    import { Plus } from '@element-plus/icons-vue'

    const list = ref([])

    const total = ref(0)

    const productQuery = reactive({
        name: '',
        categoryId: '',
        page: 1,
        limit: 10
    })

    const loadData = () => {
        productApi.list(productQuery).then(result => {
            if (result.code == 0){
                list.value = result.data.records
                total.value = result.data.total
            }
        })
    }

    loadData()

    const onSearch = () => {
        productQuery.page = 1
        loadData()
    }

    const onReset = () => {
        productQuery.name = ''
        productQuery.categoryId = ''
        selectCategory.value = null
        secondCategoryList.value = []
        productQuery.page = 1
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
            productApi.deleteById(id).then(result => {
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
            productApi.deleteAll(ids.value).then(result => {
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
    const product = ref({})
    const title = ref('')

    const showAddDialog = () => {
        dialogFormVisible.value = true
        title.value = 'add product'
        product.value = {}
    }

    const showUpdateDialog = (id) => {
        dialogFormVisible.value = true
        title.value = 'update product'
        product.value = {}

        productApi.selectById(id).then(result => {
            product.value = result.data
        })
    }

    const addOrUpdate = () => {
        if (product.value.id) {
            productApi.updateById(product.value).then(result => {
                if (result.code === 0) {
                    ElMessage.success(result.msg)
                    dialogFormVisible.value = false
                    loadData()
                } else {
                    ElMessage.error(result.msg)
                }
            })
        } else {
            productApi.add(product.value).then(result => {
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
        if (result.code === 0 && result.data) {
            // Handle enhanced upload response with multiple variants
            if (result.data.urls) {
                // Use medium size for display, but store original URL
                product.value.mainImage = result.data.urls.medium || result.data.urls.original
                console.log('🖼️ Image uploaded with variants:', result.data.urls)
            } else if (result.data.url) {
                // Fallback for basic upload
                product.value.mainImage = result.data.url
            }
        } else {
            ElMessage.error('Upload failed: ' + (result.msg || 'Unknown error'))
        }
    }

    import {useTokenStore} from '@/store/token.js'
    import WangEditor from "@/components/WangEditor.vue";
    import categoryApi from "@/api/category.js";

    const tokenStore = useTokenStore()

    const headers = ref({
        Authorization: `Bearer ${tokenStore.token}`
    })

    const onEditorChange = (detail) => {
      console.log(detail)
      product.value.detail = detail
    }

    // category handle

    const selectCategory = ref(null)

    const topCategoryList = ref([])

    categoryApi.selectTopCategoryList().then(result => {

      if (result.code == 0){
        topCategoryList.value = result.data

      }
    })

    const secondCategoryList = ref([])

    const selectChange = (value) => {

      console.log(value)

      productQuery.categoryId = ''

        if(value){
            categoryApi.selectSecondCategoryListByParentId(value).then(result => {

                if (result.code == 0){
                    secondCategoryList.value = result.data
                }
            })
        }

    }

    const detailDialogVisible = ref(false)

    const detail = ref('')

    const showDetail = (data) => {

        detailDialogVisible.value = true

        detail.value = data
    }

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
    <el-card class="product-management">
        <template #header>
            <div class="header">
                <h3>Product Management</h3>
                <div class="header-actions">
                    <el-button type="primary" @click="showAddDialog">Add Product</el-button>
                    <el-button type="danger" @click="deleteAll">Bulk Delete</el-button>
                </div>
            </div>
        </template>

        <!-- Search Form -->
        <el-form :inline="true" class="search-form">
            <el-form-item label="Name">
                <el-input
                    v-model="productQuery.name"
                    placeholder="Enter product name"
                    clearable
                    style="width: 200px"
                />
            </el-form-item>
            <el-form-item label="Category">
                <el-select
                    v-model="selectCategory"
                    clearable
                    placeholder="Select Top Category"
                    @change="selectChange"
                    style="width: 200px"
                >
                    <el-option
                        v-for="category in topCategoryList"
                        :key="category.id"
                        :label="category.name"
                        :value="category.id"
                    />
                </el-select>
            </el-form-item>
            <el-form-item label="Sub Category">
                <el-select
                    v-model="productQuery.categoryId"
                    clearable
                    placeholder="Select Sub Category"
                    style="width: 200px"
                >
                    <el-option
                        v-for="category in secondCategoryList"
                        :key="category.id"
                        :label="category.name"
                        :value="category.id"
                    />
                </el-select>
            </el-form-item>
            <el-form-item>
                <el-button type="primary" @click="onSearch">Search</el-button>
                <el-button @click="onReset">Reset</el-button>
            </el-form-item>
        </el-form>

        <!-- Product Table -->
        <el-table :data="list" style="width: 100%" ref="multipleTableRef" @selection-change="handleSelectionChange" class="product-table">
        <el-table-column type="selection" width="55" />
        <el-table-column fixed prop="id" label="ID"/>
        <el-table-column prop="name" label="name"/>
        <el-table-column prop="categoryId" label="categoryId"/>
        <el-table-column prop="categoryName" label="categoryName"/>
        <el-table-column prop="price" label="price"/>
        <el-table-column prop="stock" label="stock"/>

          <el-table-column prop="mainImage" label="mainImage">
            <template #default="scope">
                <img v-if="scope.row.mainImage" :src="scope.row.mainImage" style="max-height: 40px; max-width: 120px;" />
            </template>
        </el-table-column>
        <el-table-column align="center" width="200px" fixed="right" label="Operations">
            <template #default="{ row }">
                <el-button size="small" type="primary" @click="showUpdateDialog(row.id)">edit</el-button>
                <el-button size="small" type="danger" @click="deleteById(row.id)">delete</el-button>
                <el-button size="small" type="success" @click="showDetail(row.detail)">detail</el-button>
            </template>
        </el-table-column>
        </el-table>

        <!-- Pagination -->
        <el-pagination
            v-model:current-page="productQuery.page"
            v-model:page-size="productQuery.limit"
            :page-sizes="[10, 20, 30, 50]"
            layout="total, sizes, prev, pager, next, jumper"
            :total="total"
            @change="loadData"
            class="pagination"
        />
    </el-card>

    <!-- Add/Edit Dialog -->
    <el-dialog v-model="dialogFormVisible" :title="Info" width="70%" :lock-scroll="false">
        <el-form :model="product">
            <el-form-item label="name" :label-width="60">
                <el-input v-model="product.name" autocomplete="off" />
            </el-form-item>
            <el-form-item label="categoryId" :label-width="60">
              <el-select v-model="selectCategory" clearable placeholder="Select Top Category" @change="selectChange" style="width: 200px">
                <el-option
                    v-for="category in topCategoryList"
                    :key="category.id"
                    :label="category.name"
                    :value="category.id"
                />
              </el-select>
              <el-select v-model="product.categoryId" clearable placeholder="Select Second Category"  style="width: 200px">
                <el-option
                    v-for="category in secondCategoryList"
                    :key="category.id"
                    :label="category.name"
                    :value="category.id"
                />
              </el-select>

            </el-form-item>
          <el-form-item label="subtitle" :label-width="60">
            <el-input v-model="product.subtitle" autocomplete="off" />
          </el-form-item>
            <el-form-item label="price" :label-width="60">
                <el-input-number v-model="product.price" :min="1" :max="999" />
            </el-form-item>
          <el-form-item label="stock" :label-width="60">
            <el-input-number v-model="product.stock" :min="0" :max="9999" />
          </el-form-item>
            <el-form-item label="mainImage" :label-width="60">
            <el-upload
                class="avatar-uploader"
                :action="getUploadUrl('/upload/product/enhanced')"
                :show-file-list="false"
                :on-success="handleAvatarSuccess"
                :headers="headers"
            >
                <img class="mainImage" v-if="product.mainImage" :src="product.mainImage" />
                <el-icon v-else class="avatar-uploader-icon">
                    <Plus/>
                </el-icon>
            </el-upload>
            </el-form-item>
            <el-form-item label="detail" :label-width="60">
              <WangEditor :initValue="product.detail" @getEditorContent="onEditorChange"
                          v-if="dialogFormVisible" @close="dialogFormVisible = false"></WangEditor>
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

    //Detail Dialog

    <el-dialog v-model="detailDialogVisible" :title="Info" width="500" :lock-scroll="false">
        <div v-html="detail"></div>
        <template #footer>
            <div class="dialog-footer">
                <el-button @click="detailDialogVisible = false">close</el-button>

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

.mainImage {
    width: 178px;
    height: 178px;
    object-fit: cover;
    display: block;
    border-radius: 6px;
    border: 1px solid #dcdfe6;
}
.product-management {
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

.product-table {
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
